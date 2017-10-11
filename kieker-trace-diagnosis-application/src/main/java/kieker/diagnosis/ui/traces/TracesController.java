package kieker.diagnosis.ui.traces;

import java.util.List;

import com.google.inject.Singleton;

import kieker.diagnosis.architecture.exception.BusinessException;
import kieker.diagnosis.architecture.exception.BusinessRuntimeException;
import kieker.diagnosis.architecture.ui.ControllerBase;
import kieker.diagnosis.service.data.MethodCall;
import kieker.diagnosis.service.settings.SettingsService;
import kieker.diagnosis.service.traces.TracesFilter;
import kieker.diagnosis.service.traces.TracesService;
import kieker.diagnosis.ui.main.MainController;

@Singleton
class TracesController extends ControllerBase<TracesViewModel> {

	private List<MethodCall> ivTraceRoots;
	private int ivTotalTraces;
	private String ivDurationSuffix;

	/**
	 * This action is performed once during the application's start.
	 */
	public void performInitialize( ) {
		getViewModel( ).updatePresentationDetails( null );
		getViewModel( ).updatePresentationStatus( 0, 0 );
		getViewModel( ).updatePresentationFilter( new TracesFilter( ) );
	}

	public void performPrepareRefresh( ) {
		// Find the trace roots to display
		final TracesService tracesService = getService( TracesService.class );
		ivTraceRoots = tracesService.searchTraces( new TracesFilter( ) );
		ivTotalTraces = tracesService.countTraces( );

		final SettingsService settingsService = getService( SettingsService.class );
		ivDurationSuffix = settingsService.getCurrentDurationSuffix( );
	}

	/**
	 * This action is performed, when a refresh of the view is required
	 */
	public void performRefresh( ) {
		// Reset the filter
		final TracesFilter filter = new TracesFilter( );
		getViewModel( ).updatePresentationFilter( filter );

		// Update the view
		getViewModel( ).updatePresentationTraces( ivTraceRoots );
		getViewModel( ).updatePresentationStatus( ivTraceRoots.size( ), ivTotalTraces );

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

	public void performSearch( ) {
		try {
			// Get the filter input from the user
			final TracesFilter filter = getViewModel( ).savePresentationFilter( );

			// Find the trace roots to display
			final TracesService tracesService = getService( TracesService.class );
			final List<MethodCall> traceRoots = tracesService.searchTraces( filter );

			final int totalTraces = tracesService.countTraces( );

			// Update the view
			getViewModel( ).updatePresentationTraces( traceRoots );
			getViewModel( ).updatePresentationStatus( traceRoots.size( ), totalTraces );
		} catch ( final BusinessException ex ) {
			throw new BusinessRuntimeException( ex );
		}

	}

	public void performSetParameter( final Object aParameter ) {
		if ( aParameter instanceof MethodCall ) {
			final MethodCall methodCall = (MethodCall) aParameter;

			// We are supposed to jump to the method call
			final TracesFilter filter = new TracesFilter( );
			filter.setTraceId( methodCall.getTraceId( ) );
			getViewModel( ).updatePresentationFilter( filter );
			performSearch( );

			// Now let us see, whether we can find the method call
			getViewModel( ).select( methodCall );
		}

		if ( aParameter instanceof TracesFilter ) {
			final TracesFilter filter = (TracesFilter) aParameter;
			getViewModel( ).updatePresentationFilter( filter );

			performSearch( );
		}
	}

	public void performSaveAsFavorite( ) {
		try {
			final TracesFilter filter = getViewModel( ).savePresentationFilter( );
			getController( MainController.class ).performSaveAsFavorite( TracesView.class, filter );
		} catch ( final BusinessException ex ) {
			throw new BusinessRuntimeException( ex );
		}
	}

}
