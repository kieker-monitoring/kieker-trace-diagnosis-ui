package kieker.diagnosis.ui.methods;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import com.google.inject.Singleton;

import javafx.collections.FXCollections;
import kieker.diagnosis.architecture.exception.BusinessException;
import kieker.diagnosis.architecture.ui.ViewModelBase;
import kieker.diagnosis.service.data.MethodCall;
import kieker.diagnosis.service.methods.MethodsFilter;
import kieker.diagnosis.service.pattern.PatternService;

@Singleton
class MethodsViewModel extends ViewModelBase<MethodsView> {

	public void updatePresentationMethods( final List<MethodCall> aMethods ) {
		getView( ).getTableView( ).setItems( FXCollections.observableArrayList( aMethods ) );
		getView( ).getTableView( ).refresh( );
	}

	public void updatePresentationDetails( final MethodCall aMethodCall ) {
		final String noDataAvailable = getLocalizedString( "noDataAvailable" );

		if ( aMethodCall != null ) {
			getView( ).getDetailsHost( ).setText( aMethodCall.getHost( ) );
			getView( ).getDetailsClass( ).setText( aMethodCall.getClazz( ) );
			getView( ).getDetailsMethod( ).setText( aMethodCall.getMethod( ) );
			getView( ).getDetailsException( ).setText( aMethodCall.getException( ) != null ? aMethodCall.getException( ) : noDataAvailable );
			getView( ).getDetailsDuration( ).setText( String.format( "%d [ns]", aMethodCall.getDuration( ) ) );
			getView( ).getDetailsTimestamp( ).setText( Long.toString( aMethodCall.getTimestamp( ) ) );
			getView( ).getDetailsTraceId( ).setText( Long.toString( aMethodCall.getTraceId( ) ) );
		} else {
			getView( ).getDetailsHost( ).setText( noDataAvailable );
			getView( ).getDetailsClass( ).setText( noDataAvailable );
			getView( ).getDetailsMethod( ).setText( noDataAvailable );
			getView( ).getDetailsException( ).setText( noDataAvailable );
			getView( ).getDetailsDuration( ).setText( noDataAvailable );
			getView( ).getDetailsTimestamp( ).setText( noDataAvailable );
			getView( ).getDetailsTraceId( ).setText( noDataAvailable );
		}
	}

	public void updatePresentationDurationColumnHeader( final String aSuffix ) {
		getView( ).getDurationColumn( ).setText( getLocalizedString( "columnDuration" ) + " " + aSuffix );
	}

	public void updatePresentationStatus( final int aMethods, final int aTotalMethods ) {
		final NumberFormat decimalFormat = DecimalFormat.getInstance( );
		getView( ).getStatusLabel( ).setText( String.format( getLocalizedString( "statusLabel" ), decimalFormat.format( aMethods ), decimalFormat.format( aTotalMethods ) ) );
	}

	public void updatePresentationFilter( final MethodsFilter aFilter ) {
		getView( ).getFilterHost( ).setText( aFilter.getHost( ) );
		getView( ).getFilterClass( ).setText( aFilter.getClazz( ) );
		getView( ).getFilterMethod( ).setText( aFilter.getMethod( ) );
		getView( ).getFilterException( ).setText( aFilter.getException( ) );
		getView( ).getFilterSearchType( ).setValue( aFilter.getSearchType( ) );
		getView( ).getFilterTraceId( ).setText( aFilter.getTraceId( ) != null ? Long.toString( aFilter.getTraceId( ) ) : null );
		getView( ).getFilterUseRegExpr( ).setSelected( aFilter.isUseRegExpr( ) );
		getView( ).getFilterLowerDate( ).setValue( aFilter.getLowerDate( ) );
		getView( ).getFilterLowerTime( ).setCalendar( aFilter.getLowerTime( ) );
		getView( ).getFilterUpperDate( ).setValue( aFilter.getUpperDate( ) );
		getView( ).getFilterUpperTime( ).setCalendar( aFilter.getUpperTime( ) );

	}

	public MethodsFilter savePresentationFilter( ) throws BusinessException {
		final MethodsFilter filter = new MethodsFilter( );

		filter.setHost( trimToNull( getView( ).getFilterHost( ).getText( ) ) );
		filter.setClazz( trimToNull( getView( ).getFilterClass( ).getText( ) ) );
		filter.setMethod( trimToNull( getView( ).getFilterMethod( ).getText( ) ) );
		filter.setException( trimToNull( getView( ).getFilterException( ).getText( ) ) );
		filter.setSearchType( getView( ).getFilterSearchType( ).getValue( ) );
		filter.setUseRegExpr( getView( ).getFilterUseRegExpr( ).isSelected( ) );
		filter.setLowerDate( getView( ).getFilterLowerDate( ).getValue( ) );
		filter.setLowerTime( getView( ).getFilterLowerTime( ).getCalendar( ) );
		filter.setUpperDate( getView( ).getFilterUpperDate( ).getValue( ) );
		filter.setUpperTime( getView( ).getFilterUpperTime( ).getCalendar( ) );

		try {
			final String traceId = trimToNull( getView( ).getFilterTraceId( ).getText( ) );
			filter.setTraceId( traceId != null ? Long.valueOf( traceId ) : null );
		} catch ( final NumberFormatException ex ) {
			throw new BusinessException( getLocalizedString( "errorMessageTraceId" ) );
		}

		// If we are using regular expressions, we should check them
		if ( filter.isUseRegExpr( ) ) {
			final PatternService patternService = getService( PatternService.class );

			if ( !patternService.isValidPattern( filter.getHost( ) ) ) {
				throw new BusinessException( String.format( getLocalizedString( "errorMessageRegExpr" ), filter.getHost( ) ) );
			}

			if ( !patternService.isValidPattern( filter.getClazz( ) ) ) {
				throw new BusinessException( String.format( getLocalizedString( "errorMessageRegExpr" ), filter.getClazz( ) ) );
			}

			if ( !patternService.isValidPattern( filter.getMethod( ) ) ) {
				throw new BusinessException( String.format( getLocalizedString( "errorMessageRegExpr" ), filter.getMethod( ) ) );
			}

			if ( !patternService.isValidPattern( filter.getException( ) ) ) {
				throw new BusinessException( String.format( getLocalizedString( "errorMessageRegExpr" ), filter.getException( ) ) );
			}
		}

		return filter;
	}

	public MethodCall getSelected( ) {
		return getView( ).getTableView( ).getSelectionModel( ).getSelectedItem( );
	}

}
