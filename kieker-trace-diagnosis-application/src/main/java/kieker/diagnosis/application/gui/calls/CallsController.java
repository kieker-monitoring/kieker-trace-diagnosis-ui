package kieker.diagnosis.application.gui.calls;

import kieker.diagnosis.application.gui.main.MainController;
import kieker.diagnosis.application.service.data.DataService;
import kieker.diagnosis.application.service.data.domain.AggregatedOperationCall;
import kieker.diagnosis.application.service.data.domain.OperationCall;
import kieker.diagnosis.application.service.export.CSVData;
import kieker.diagnosis.application.service.export.CSVDataCollector;
import kieker.diagnosis.application.service.filter.FilterService;
import kieker.diagnosis.application.service.nameconverter.NameConverterService;
import kieker.diagnosis.application.service.properties.TimeUnitProperty;
import kieker.diagnosis.architecture.exception.BusinessException;
import kieker.diagnosis.architecture.gui.AbstractController;
import kieker.diagnosis.architecture.service.properties.PropertiesService;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableColumn;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CallsController extends AbstractController<CallsView> {

	private final SimpleObjectProperty<Optional<OperationCall>> ivSelection = new SimpleObjectProperty<>( Optional.empty( ) );

	private FilteredList<OperationCall> ivFilteredData;

	@Autowired
	private MainController ivMainController;

	@Autowired
	private NameConverterService ivNameConverterService;

	@Autowired
	private PropertiesService ivPropertiesService;

	@Autowired
	private FilterService ivFilterService;

	@Autowired
	private DataService ivDataService;

	@Override
	protected void doInitialize( final boolean aFirstInitialization, final Optional<?> aParameter ) {
		if ( aFirstInitialization ) {
			ivFilteredData = new FilteredList<>( ivDataService.getOperationCalls( ) );
			ivFilteredData.addListener( (ListChangeListener<OperationCall>) change -> ivSelection.set( Optional.empty( ) ) );

			final SortedList<OperationCall> sortedData = new SortedList<>( ivFilteredData );
			sortedData.comparatorProperty( ).bind( getView( ).getTable( ).comparatorProperty( ) );
			getView( ).getTable( ).setItems( sortedData );

			ivSelection.addListener( e -> updateDetailPanel( ) );

			getView( ).getCounter( ).textProperty( )
					.bind( Bindings.createStringBinding( ( ) -> sortedData.size( ) + " " + getResourceBundle( ).getString( "counter" ), sortedData ) );
		}

		if ( aParameter.isPresent( ) ) {
			if ( aParameter.get( ) instanceof CallsFilter ) {
				loadFilterContent( (CallsFilter) aParameter.get( ) );
				performUseFilter( );
			} else if ( aParameter.get( ) instanceof AggregatedOperationCall ) {
				jumpToCalls( (AggregatedOperationCall) aParameter.get( ) );
			}
		}
	}

	@Override
	public void doRefresh( ) {
		getView( ).getTable( ).refresh( );
	}

	public void performUseFilter( ) {
		final Predicate<OperationCall> predicate1 = ivFilterService.useFilter( getView( ).getShowAllButton( ), getView( ).getShowJustSuccessful( ),
				getView( ).getShowJustFailedButton( ), OperationCall::isFailed );
		final Predicate<OperationCall> predicate2 = ivFilterService.useFilter( getView( ).getFilterContainer( ), OperationCall::getContainer );
		final Predicate<OperationCall> predicate3 = ivFilterService.useFilter( getView( ).getFilterComponent( ), OperationCall::getComponent );
		final Predicate<OperationCall> predicate4 = ivFilterService.useFilter( getView( ).getFilterOperation( ), OperationCall::getOperation );
		final Predicate<OperationCall> predicate5 = ivFilterService.useFilter( getView( ).getFilterTraceID( ), call -> Long.toString( call.getTraceID( ) ) );
		final Predicate<OperationCall> predicate6 = ivFilterService.useFilter( getView( ).getFilterLowerDate( ), OperationCall::getTimestamp, true );
		final Predicate<OperationCall> predicate7 = ivFilterService.useFilter( getView( ).getFilterUpperDate( ), OperationCall::getTimestamp, false );
		final Predicate<OperationCall> predicate8 = ivFilterService.useFilter( getView( ).getFilterLowerTime( ), OperationCall::getTimestamp, true );
		final Predicate<OperationCall> predicate9 = ivFilterService.useFilter( getView( ).getFilterUpperTime( ), OperationCall::getTimestamp, false );
		final Predicate<OperationCall> predicate10 = ivFilterService.useFilter( getView( ).getFilterException( ),
				call -> call.isFailed( ) ? call.getFailedCause( ) : "" );

		final Predicate<OperationCall> predicate = predicate1.and( predicate2 ).and( predicate3 ).and( predicate4 ).and( predicate5 ).and( predicate6 )
				.and( predicate7 ).and( predicate8 ).and( predicate9 ).and( predicate10 );
		ivFilteredData.setPredicate( predicate );
	}

	public void performSelectCall( final InputEvent aEvent ) {
		final int clicked = aEvent instanceof MouseEvent ? ( (MouseEvent) aEvent ).getClickCount( ) : 1;

		if ( clicked == 1 ) {
			// If the user clicks once, we simply change the current selection
			ivSelection.set( Optional.ofNullable( getView( ).getTable( ).getSelectionModel( ).getSelectedItem( ) ) );
		} else if ( ( clicked == 2 ) && ivSelection.get( ).isPresent( ) ) {
			// If the user clicks twice, we jump to the call in the corresponding trace
			final OperationCall call = ivSelection.get( ).get( );
			ivMainController.jumpToTrace( call );
		}
	}

	public void performSaveAsFavorite( ) throws BusinessException {
		ivMainController.saveAsFavorite( saveFilterContent( ), CallsController.class );
	}

	public void performExportToCSV( ) {
		ivMainController.exportToCSV( new CallsCSVDataCollector( ) );
	}

	private void jumpToCalls( final AggregatedOperationCall aCall ) {
		// Clear all filters (as the view might be cached)
		getView( ).getFilterLowerDate( ).setValue( null );
		getView( ).getFilterLowerTime( ).setCalendar( null );
		getView( ).getFilterUpperDate( ).setValue( null );
		getView( ).getFilterUpperTime( ).setCalendar( null );
		getView( ).getFilterTraceID( ).setText( null );
		getView( ).getFilterException( ).setText( null );
		getView( ).getShowAllButton( ).setSelected( true );

		// Now use the values from the given aggregated call for the filters
		getView( ).getFilterContainer( ).setText( aCall.getContainer( ) );
		getView( ).getFilterComponent( ).setText( aCall.getComponent( ) );
		getView( ).getFilterOperation( ).setText( aCall.getOperation( ) );

		if ( aCall.getFailedCause( ) != null ) {
			getView( ).getFilterException( ).setText( aCall.getFailedCause( ) );
		} else {
			getView( ).getShowJustSuccessful( ).setSelected( true );
		}

		performUseFilter( );
	}

	private void updateDetailPanel( ) {
		final String notAvailable = getResourceBundle( ).getString( "notAvailable" );
		if ( ivSelection.get( ).isPresent( ) ) {
			final OperationCall call = ivSelection.get( ).get( );
			final TimeUnit sourceTimeUnit = ivDataService.getTimeUnit( );
			final TimeUnit targetTimeUnit = ivPropertiesService.loadApplicationProperty( TimeUnitProperty.class );

			getView( ).getContainer( ).setText( call.getContainer( ) );
			getView( ).getComponent( ).setText( call.getComponent( ) );
			getView( ).getOperation( ).setText( call.getOperation( ) );
			getView( ).getTimestamp( )
					.setText( ivNameConverterService.toTimestampString( call.getTimestamp( ), sourceTimeUnit ) + " (" + call.getTimestamp( ) + ")" );
			getView( ).getDuration( ).setText( ivNameConverterService.toDurationString( call.getDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getTraceID( ).setText( Long.toString( call.getTraceID( ) ) );
			getView( ).getFailed( ).setText( call.getFailedCause( ) != null ? call.getFailedCause( ) : notAvailable );
		} else {
			getView( ).getContainer( ).setText( notAvailable );
			getView( ).getComponent( ).setText( notAvailable );
			getView( ).getOperation( ).setText( notAvailable );
			getView( ).getTimestamp( ).setText( notAvailable );
			getView( ).getDuration( ).setText( notAvailable );
			getView( ).getTraceID( ).setText( notAvailable );
			getView( ).getFailed( ).setText( notAvailable );
		}
	}

	private CallsFilter saveFilterContent( ) {
		final CallsFilter filterContent = new CallsFilter( );

		filterContent.setFilterComponent( getView( ).getFilterComponent( ).getText( ) );
		filterContent.setFilterContainer( getView( ).getFilterContainer( ).getText( ) );
		filterContent.setFilterException( getView( ).getFilterException( ).getText( ) );
		filterContent.setFilterOperation( getView( ).getFilterOperation( ).getText( ) );
		filterContent.setFilterLowerDate( getView( ).getFilterLowerDate( ).getValue( ) );
		filterContent.setFilterLowerTime( getView( ).getFilterLowerTime( ).getCalendar( ) );
		filterContent.setFilterTraceID( getView( ).getFilterTraceID( ).getText( ) );
		filterContent.setFilterUpperDate( getView( ).getFilterUpperDate( ).getValue( ) );
		filterContent.setFilterUpperTime( getView( ).getFilterUpperTime( ).getCalendar( ) );
		filterContent.setShowAllButton( getView( ).getShowAllButton( ).isSelected( ) );
		filterContent.setShowJustFailedButton( getView( ).getShowJustFailedButton( ).isSelected( ) );
		filterContent.setShowJustSuccessful( getView( ).getShowJustSuccessful( ).isSelected( ) );

		return filterContent;
	}

	private void loadFilterContent( final CallsFilter aFilterContent ) {
		getView( ).getFilterComponent( ).setText( aFilterContent.getFilterComponent( ) );
		getView( ).getFilterContainer( ).setText( aFilterContent.getFilterContainer( ) );
		getView( ).getFilterException( ).setText( aFilterContent.getFilterException( ) );
		getView( ).getFilterOperation( ).setText( aFilterContent.getFilterOperation( ) );
		getView( ).getFilterTraceID( ).setText( aFilterContent.getFilterTraceID( ) );
		getView( ).getFilterLowerDate( ).setValue( aFilterContent.getFilterLowerDate( ) );
		getView( ).getFilterUpperDate( ).setValue( aFilterContent.getFilterUpperDate( ) );
		getView( ).getFilterLowerTime( ).setCalendar( aFilterContent.getFilterLowerTime( ) );
		getView( ).getFilterUpperTime( ).setCalendar( aFilterContent.getFilterUpperTime( ) );
		getView( ).getShowAllButton( ).setSelected( aFilterContent.isShowAllButton( ) );
		getView( ).getShowJustFailedButton( ).setSelected( aFilterContent.isShowJustFailedButton( ) );
		getView( ).getShowJustSuccessful( ).setSelected( aFilterContent.isShowJustSuccessful( ) );
	}

	/**
	 * @author Nils Christian Ehmke
	 */
	private final class CallsCSVDataCollector implements CSVDataCollector {

		@Override
		public CSVData collectData( ) {
			final ObservableList<OperationCall> items = getView( ).getTable( ).getItems( );
			final ObservableList<TableColumn<OperationCall, ?>> columns = getView( ).getTable( ).getVisibleLeafColumns( );

			final String[][] rows = new String[items.size( )][columns.size( )];
			final String[] header = new String[columns.size( )];

			for ( int i = 0; i < columns.size( ); i++ ) {
				final TableColumn<OperationCall, ?> column = columns.get( i );

				header[i] = column.getText( );

				for ( int j = 0; j < items.size( ); j++ ) {
					final Object cellData = column.getCellData( j );
					rows[j][i] = cellData != null ? cellData.toString( ) : null;
				}
			}

			return new CSVData( header, rows );
		}

	}

}
