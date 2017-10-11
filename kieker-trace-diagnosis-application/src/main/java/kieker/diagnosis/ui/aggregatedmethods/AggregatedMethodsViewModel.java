package kieker.diagnosis.ui.aggregatedmethods;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import com.google.inject.Singleton;

import javafx.collections.FXCollections;
import kieker.diagnosis.architecture.exception.BusinessException;
import kieker.diagnosis.architecture.ui.ViewModelBase;
import kieker.diagnosis.service.aggregatedmethods.AggregatedMethodsFilter;
import kieker.diagnosis.service.data.AggregatedMethodCall;
import kieker.diagnosis.service.pattern.PatternService;

@Singleton
class AggregatedMethodsViewModel extends ViewModelBase<AggregatedMethodsView> {

	public void updatePresentationDurationColumnHeader( final String aSuffix ) {
		getView( ).getColumnMinDuration( ).setText( getLocalizedString( "columnMinDuration" ) + aSuffix );
		getView( ).getColumnAvgDuration( ).setText( getLocalizedString( "columnAvgDuration" ) + aSuffix );
		getView( ).getColumnMedianDuration( ).setText( getLocalizedString( "columnMedianDuration" ) + aSuffix );
		getView( ).getColumnMaxDuration( ).setText( getLocalizedString( "columnMaxDuration" ) + aSuffix );
		getView( ).getColumnTotalDuration( ).setText( getLocalizedString( "columnTotalDuration" ) + aSuffix );

	}

	public void updatePresentationDetails( final AggregatedMethodCall aMethodCall ) {
		final String noDataAvailable = getLocalizedString( "noDataAvailable" );

		if ( aMethodCall != null ) {
			getView( ).getDetailsCount( ).setText( Integer.toString( aMethodCall.getCount( ) ) );
			getView( ).getDetailsHost( ).setText( aMethodCall.getHost( ) );
			getView( ).getDetailsClass( ).setText( aMethodCall.getClazz( ) );
			getView( ).getDetailsMethod( ).setText( aMethodCall.getMethod( ) );
			getView( ).getDetailsException( ).setText( aMethodCall.getException( ) != null ? aMethodCall.getException( ) : noDataAvailable );
			getView( ).getDetailsMinDuration( ).setText( String.format( "%d [ns]", aMethodCall.getMinDuration( ) ) );
			getView( ).getDetailsAvgDuration( ).setText( String.format( "%d [ns]", aMethodCall.getAvgDuration( ) ) );
			getView( ).getDetailsMedianDuration( ).setText( String.format( "%d [ns]", aMethodCall.getMedianDuration( ) ) );
			getView( ).getDetailsMaxDuration( ).setText( String.format( "%d [ns]", aMethodCall.getMaxDuration( ) ) );
			getView( ).getDetailsTotalDuration( ).setText( String.format( "%d [ns]", aMethodCall.getTotalDuration( ) ) );
		} else {
			getView( ).getDetailsCount( ).setText( noDataAvailable );
			getView( ).getDetailsHost( ).setText( noDataAvailable );
			getView( ).getDetailsClass( ).setText( noDataAvailable );
			getView( ).getDetailsMethod( ).setText( noDataAvailable );
			getView( ).getDetailsException( ).setText( noDataAvailable );
			getView( ).getDetailsMinDuration( ).setText( noDataAvailable );
			getView( ).getDetailsAvgDuration( ).setText( noDataAvailable );
			getView( ).getDetailsMedianDuration( ).setText( noDataAvailable );
			getView( ).getDetailsMaxDuration( ).setText( noDataAvailable );
			getView( ).getDetailsTotalDuration( ).setText( noDataAvailable );
		}
	}

	public void updatePresentationMethods( final List<AggregatedMethodCall> aMethods ) {
		getView( ).getTableView( ).setItems( FXCollections.observableArrayList( aMethods ) );
		getView( ).getTableView( ).refresh( );
	}

	public void updatePresentationStatus( final int aMethods, final int aTotalMethods ) {
		final NumberFormat decimalFormat = DecimalFormat.getInstance( );
		getView( ).getStatusLabel( ).setText( String.format( getLocalizedString( "statusLabel" ), decimalFormat.format( aMethods ), decimalFormat.format( aTotalMethods ) ) );
	}

	public void updatePresentationFilter( final AggregatedMethodsFilter aFilter ) {
		getView( ).getFilterHost( ).setText( aFilter.getHost( ) );
		getView( ).getFilterClass( ).setText( aFilter.getClazz( ) );
		getView( ).getFilterMethod( ).setText( aFilter.getMethod( ) );
		getView( ).getFilterException( ).setText( aFilter.getException( ) );
		getView( ).getFilterUseRegExpr( ).setSelected( aFilter.isUseRegExpr( ) );
		getView( ).getFilterSearchType( ).setValue( aFilter.getSearchType( ) );
	}

	public AggregatedMethodsFilter savePresentationFilter( ) throws BusinessException {
		final AggregatedMethodsFilter filter = new AggregatedMethodsFilter( );

		filter.setHost( trimToNull( getView( ).getFilterHost( ).getText( ) ) );
		filter.setClazz( trimToNull( getView( ).getFilterClass( ).getText( ) ) );
		filter.setMethod( trimToNull( getView( ).getFilterMethod( ).getText( ) ) );
		filter.setException( trimToNull( getView( ).getFilterException( ).getText( ) ) );
		filter.setUseRegExpr( getView( ).getFilterUseRegExpr( ).isSelected( ) );
		filter.setSearchType( getView( ).getFilterSearchType( ).getValue( ) );

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

	public AggregatedMethodCall getSelected( ) {
		return getView( ).getTableView( ).getSelectionModel( ).getSelectedItem( );
	}

}
