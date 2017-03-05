/***************************************************************************
 * Copyright 2015-2016 Kieker Project (http://kieker-monitoring.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/

package kieker.diagnosis.gui.aggregatedcalls;

import java.io.IOException;
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
import kieker.diagnosis.gui.AbstractController;
import kieker.diagnosis.gui.Context;
import kieker.diagnosis.gui.ContextKey;
import kieker.diagnosis.gui.main.MainController;
import kieker.diagnosis.service.InjectService;
import kieker.diagnosis.service.data.DataService;
import kieker.diagnosis.service.data.domain.AggregatedOperationCall;
import kieker.diagnosis.service.export.CSVData;
import kieker.diagnosis.service.export.CSVDataCollector;
import kieker.diagnosis.service.filter.FilterService;
import kieker.diagnosis.service.nameconverter.NameConverterService;
import kieker.diagnosis.service.properties.PropertiesService;

/**
 * The controller for the aggregated calls.
 *
 * @author Nils Christian Ehmke
 */
public final class AggregatedCallsController extends AbstractController<AggregatedCallsView> implements AggregatedCallsControllerIfc {

	private FilteredList<AggregatedOperationCall> ivFilteredData;

	private final SimpleObjectProperty<Optional<AggregatedOperationCall>> ivSelection = new SimpleObjectProperty<>( Optional.empty( ) );

	@InjectService
	private NameConverterService ivNameConverterService;

	@InjectService
	private PropertiesService ivPropertiesService;

	@InjectService
	private FilterService ivFilterService;

	@InjectService
	private DataService ivDataService;

	private MainController ivMainController;

	public AggregatedCallsController( final Context aContext ) {
		super( aContext );
	}

	@Override
	public void doInitialize( ) {
		ivFilteredData = new FilteredList<>( ivDataService.getAggregatedOperationCalls( ) );
		ivFilteredData.addListener( (ListChangeListener<AggregatedOperationCall>) change -> ivSelection.set( Optional.empty( ) ) );

		final SortedList<AggregatedOperationCall> sortedData = new SortedList<>( ivFilteredData );
		sortedData.comparatorProperty( ).bind( getView( ).getTable( ).comparatorProperty( ) );
		getView( ).getTable( ).setItems( sortedData );

		final Object filterContent = getContext( ).get( ContextKey.FILTER_CONTENT );
		if ( filterContent instanceof AggregatedCallsFilterContent ) {
			updateView( (AggregatedCallsFilterContent) filterContent );
			performUseFilter( );
		}

		ivSelection.addListener( e -> updateDetailPanel( ) );

		getView( ).getCounter( ).textProperty( ).bind( Bindings.createStringBinding(
				( ) -> sortedData.size( ) + " " + getView( ).getResourceBundle( ).getString( "AggregatedCallsView.lblCounter.text" ), sortedData ) );
	}

	private void updateDetailPanel( ) {
		if ( ivSelection.get( ).isPresent( ) ) {
			final AggregatedOperationCall call = ivSelection.get( ).get( );
			updateView( call );
		} else {
			updateView( (AggregatedOperationCall) null );
		}
	}

	@Override
	public void performSelectCall( final InputEvent aEvent ) throws Exception {
		final int clicked;
		if ( aEvent instanceof MouseEvent ) {
			clicked = ((MouseEvent) aEvent).getClickCount( );
		} else {
			clicked = 1;
		}

		if ( clicked == 1 ) {
			ivSelection.set( Optional.ofNullable( getView( ).getTable( ).getSelectionModel( ).getSelectedItem( ) ) );
		} else if ( clicked == 2 ) {
			jumpToCalls( );
		}
	}

	private void jumpToCalls( ) throws Exception {
		if ( ivSelection.get( ).isPresent( ) ) {
			final AggregatedOperationCall call = ivSelection.get( ).get( );
			ivMainController.jumpToCalls( call );
		}
	}

	@Override
	public void performUseFilter( ) {
		final Predicate<AggregatedOperationCall> predicate1 = ivFilterService.useFilter( getView( ).getShowAllButton( ), getView( ).getShowJustSuccessful( ),
				getView( ).getShowJustFailedButton( ), AggregatedOperationCall::isFailed );
		final Predicate<AggregatedOperationCall> predicate2 = ivFilterService.useFilter( getView( ).getFilterContainer( ),
				AggregatedOperationCall::getContainer );
		final Predicate<AggregatedOperationCall> predicate3 = ivFilterService.useFilter( getView( ).getFilterComponent( ),
				AggregatedOperationCall::getComponent );
		final Predicate<AggregatedOperationCall> predicate4 = ivFilterService.useFilter( getView( ).getFilterOperation( ),
				AggregatedOperationCall::getOperation );
		final Predicate<AggregatedOperationCall> predicate5 = ivFilterService.useFilter( getView( ).getFilterException( ),
				(call -> call.isFailed( ) ? call.getFailedCause( ) : "") );

		final Predicate<AggregatedOperationCall> predicate = predicate1.and( predicate2 ).and( predicate3 ).and( predicate4 ).and( predicate5 );
		ivFilteredData.setPredicate( predicate );
	}

	@Override
	public void performExportToCSV( ) throws IOException {
		ivMainController.exportToCSV( new AggregatedCallsCSVDataCollector( ) );
	}

	@Override
	public void performSaveAsFavorite( ) {
		final AggregatedCallsFilterContent filterContent = saveView( new AggregatedCallsFilterContent( ) );
		ivMainController.saveAsFavorite( filterContent, AggregatedCallsController.class );
	}

	private void updateView( final AggregatedOperationCall aCall ) {
		if ( aCall != null ) {
			final TimeUnit sourceTimeUnit = ivDataService.getTimeUnit( );
			final TimeUnit targetTimeUnit = ivPropertiesService.getTimeUnit( );

			getView( ).getContainer( ).setText( aCall.getContainer( ) );
			getView( ).getComponent( ).setText( aCall.getComponent( ) );
			getView( ).getOperation( ).setText( aCall.getOperation( ) );
			getView( ).getMinimalDuration( ).setText( ivNameConverterService.toDurationString( aCall.getMinDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getMaximalDuration( ).setText( ivNameConverterService.toDurationString( aCall.getMaxDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getMedianDuration( ).setText( ivNameConverterService.toDurationString( aCall.getMedianDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getTotalDuration( ).setText( ivNameConverterService.toDurationString( aCall.getTotalDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getMeanDuration( ).setText( ivNameConverterService.toDurationString( aCall.getMeanDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getCalls( ).setText( Integer.toString( aCall.getCalls( ) ) );
			getView( ).getFailed( ).setText( aCall.getFailedCause( ) != null ? aCall.getFailedCause( ) : "N/A" );
		} else {
			getView( ).getContainer( ).setText( "N/A" );
			getView( ).getComponent( ).setText( "N/A" );
			getView( ).getOperation( ).setText( "N/A" );
			getView( ).getMinimalDuration( ).setText( "N/A" );
			getView( ).getMaximalDuration( ).setText( "N/A" );
			getView( ).getMedianDuration( ).setText( "N/A" );
			getView( ).getTotalDuration( ).setText( "N/A" );
			getView( ).getMeanDuration( ).setText( "N/A" );
			getView( ).getCalls( ).setText( "N/A" );
			getView( ).getFailed( ).setText( "N/A" );
		}
	}

	private void updateView( final AggregatedCallsFilterContent aFilterContent ) {
		getView( ).getFilterComponent( ).setText( aFilterContent.getFilterComponent( ) );
		getView( ).getFilterContainer( ).setText( aFilterContent.getFilterContainer( ) );
		getView( ).getFilterException( ).setText( aFilterContent.getFilterException( ) );
		getView( ).getFilterOperation( ).setText( aFilterContent.getFilterOperation( ) );
		getView( ).getShowAllButton( ).setSelected( aFilterContent.isShowAllButton( ) );
		getView( ).getShowJustFailedButton( ).setSelected( aFilterContent.isShowJustFailedButton( ) );
		getView( ).getShowJustSuccessful( ).setSelected( aFilterContent.isShowJustSuccessful( ) );
	}

	private AggregatedCallsFilterContent saveView( final AggregatedCallsFilterContent aFilterContent ) {
		aFilterContent.setFilterComponent( getView( ).getFilterComponent( ).getText( ) );
		aFilterContent.setFilterContainer( getView( ).getFilterContainer( ).getText( ) );
		aFilterContent.setFilterException( getView( ).getFilterException( ).getText( ) );
		aFilterContent.setFilterOperation( getView( ).getFilterOperation( ).getText( ) );
		aFilterContent.setShowAllButton( getView( ).getShowAllButton( ).isSelected( ) );
		aFilterContent.setShowJustFailedButton( getView( ).getShowJustFailedButton( ).isSelected( ) );
		aFilterContent.setShowJustSuccessful( getView( ).getShowJustSuccessful( ).isSelected( ) );

		return aFilterContent;
	}

	private final class AggregatedCallsCSVDataCollector implements CSVDataCollector {

		@Override
		public CSVData collectData( ) {
			final ObservableList<AggregatedOperationCall> items = getView( ).getTable( ).getItems( );
			final ObservableList<TableColumn<AggregatedOperationCall, ?>> columns = getView( ).getTable( ).getVisibleLeafColumns( );

			final String[][] rows = new String[items.size( )][columns.size( )];
			final String[] header = new String[columns.size( )];

			for ( int i = 0; i < columns.size( ); i++ ) {
				final TableColumn<AggregatedOperationCall, ?> column = columns.get( i );

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
