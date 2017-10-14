package kieker.diagnosis.ui.methods;

import java.util.List;

import com.google.inject.Singleton;

import kieker.diagnosis.architecture.exception.BusinessException;
import kieker.diagnosis.architecture.exception.BusinessRuntimeException;
import kieker.diagnosis.architecture.ui.ControllerBase;
import kieker.diagnosis.service.data.AggregatedMethodCall;
import kieker.diagnosis.service.data.MethodCall;
import kieker.diagnosis.service.export.CSVData;
import kieker.diagnosis.service.methods.MethodsFilter;
import kieker.diagnosis.service.methods.MethodsService;
import kieker.diagnosis.service.methods.SearchType;
import kieker.diagnosis.service.settings.SettingsService;
import kieker.diagnosis.ui.main.MainController;

@Singleton
class MethodsController extends ControllerBase<MethodsViewModel> {

	private List<MethodCall> ivMethods;
	private String ivDurationSuffix;
	private int ivTotalMethods;

	/**
	 * This action is performed once during the application's start.
	 */
	public void performInitialize( ) {
		getViewModel( ).updatePresentationDetails( null );
		getViewModel( ).updatePresentationStatus( 0, 0 );
		getViewModel( ).updatePresentationFilter( new MethodsFilter( ) );
	}

	public void performSearch( ) {
		try {
			// Get the filter input from the user
			final MethodsFilter filter = getViewModel( ).savePresentationFilter( );

			// Find the methods to display
			final MethodsService methodsService = getService( MethodsService.class );
			final List<MethodCall> methods = methodsService.searchMethods( filter );
			final int totalMethods = methodsService.countMethods( );

			// Update the view
			getViewModel( ).updatePresentationMethods( methods );
			getViewModel( ).updatePresentationStatus( methods.size( ), totalMethods );
		} catch ( final BusinessException ex ) {
			throw new BusinessRuntimeException( ex );
		}

	}

	public void performPrepareRefresh( ) {
		final MethodsService methodsService = getService( MethodsService.class );
		ivMethods = methodsService.searchMethods( new MethodsFilter( ) );
		ivTotalMethods = methodsService.countMethods( );

		final SettingsService settingsService = getService( SettingsService.class );
		ivDurationSuffix = settingsService.getCurrentDurationSuffix( );
	}

	/**
	 * This action is performed, when a refresh of the view is required
	 */
	public void performRefresh( ) {
		// Reset the filter
		final MethodsFilter filter = new MethodsFilter( );
		getViewModel( ).updatePresentationFilter( filter );

		// Update the table
		getViewModel( ).updatePresentationMethods( ivMethods );
		getViewModel( ).updatePresentationStatus( ivMethods.size( ), ivTotalMethods );

		// Update the column header of the table
		getViewModel( ).updatePresentationDurationColumnHeader( ivDurationSuffix );
	}

	/**
	 * This action is performed, when the selection of the table changes
	 */
	public void performSelectionChange( ) {
		final MethodCall methodCall = getViewModel( ).getSelected( );
		getViewModel( ).updatePresentationDetails( methodCall );
	}

	public void performJumpToTrace( ) {
		final MethodCall methodCall = getViewModel( ).getSelected( );

		if ( methodCall != null ) {
			getController( MainController.class ).performJumpToTrace( methodCall );
		}
	}

	public void performSaveAsFavorite( ) {
		try {
			final MethodsFilter filter = getViewModel( ).savePresentationFilter( );
			getController( MainController.class ).performSaveAsFavorite( MethodsView.class, filter );
		} catch ( final BusinessException ex ) {
			throw new BusinessRuntimeException( ex );
		}
	}

	public void performSetParameter( final Object aParameter ) {
		if ( aParameter instanceof AggregatedMethodCall ) {
			final AggregatedMethodCall methodCall = (AggregatedMethodCall) aParameter;

			// We have to prepare a filter which maches only the method call
			final MethodsFilter filter = new MethodsFilter( );
			filter.setHost( methodCall.getHost( ) );
			filter.setClazz( methodCall.getClazz( ) );
			filter.setMethod( methodCall.getMethod( ) );
			filter.setException( methodCall.getException( ) );
			filter.setSearchType( methodCall.getException( ) != null ? SearchType.ONLY_FAILED : SearchType.ONLY_SUCCESSFUL );

			getViewModel( ).updatePresentationFilter( filter );

			// Now we can perform the actual search
			performSearch( );
		}

		if ( aParameter instanceof MethodsFilter ) {
			final MethodsFilter filter = (MethodsFilter) aParameter;
			getViewModel( ).updatePresentationFilter( filter );

			performSearch( );
		}
	}

	public void performExportToCSV( ) {
		final CSVData csvData = getViewModel( ).savePresentationAsCSV( );
		getController( MainController.class ).performExportToCSV( csvData );
	}

}
