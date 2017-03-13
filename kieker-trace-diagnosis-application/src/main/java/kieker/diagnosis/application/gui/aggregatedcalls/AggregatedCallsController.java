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

package kieker.diagnosis.application.gui.aggregatedcalls;

import kieker.diagnosis.application.gui.main.MainController;
import kieker.diagnosis.application.service.data.DataService;
import kieker.diagnosis.application.service.data.domain.AggregatedOperationCall;
import kieker.diagnosis.application.service.export.CSVData;
import kieker.diagnosis.application.service.export.CSVDataCollector;
import kieker.diagnosis.application.service.filter.FilterService;
import kieker.diagnosis.application.service.nameconverter.NameConverterService;
import kieker.diagnosis.application.service.properties.TimeUnitProperty;
import kieker.diagnosis.architecture.exception.BusinessException;
import kieker.diagnosis.architecture.gui.AbstractController;
import kieker.diagnosis.architecture.service.properties.PropertiesService;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The controller for the aggregated calls.
 *
 * @author Nils Christian Ehmke
 */
@Component
public class AggregatedCallsController extends AbstractController<AggregatedCallsView> {

	private FilteredList<AggregatedOperationCall> ivFilteredData;

	private final SimpleObjectProperty<Optional<AggregatedOperationCall>> ivSelection = new SimpleObjectProperty<>( Optional.empty( ) );

	@Autowired
	private NameConverterService ivNameConverterService;

	@Autowired
	private PropertiesService ivPropertiesService;

	@Autowired
	private FilterService ivFilterService;

	@Autowired
	private DataService ivDataService;

	@Autowired
	private MainController ivMainController;

	@Override
	protected void doInitialize( final boolean aFirstInitialization, final Optional<?> aParameter ) {
		if ( aFirstInitialization ) {
			ivFilteredData = new FilteredList<>( ivDataService.getAggregatedOperationCalls( ) );
			ivFilteredData.addListener( (ListChangeListener<AggregatedOperationCall>) change -> ivSelection.set( Optional.empty( ) ) );

			final SortedList<AggregatedOperationCall> sortedData = new SortedList<>( ivFilteredData );
			sortedData.comparatorProperty( ).bind( getView( ).getTable( ).comparatorProperty( ) );
			getView( ).getTable( ).setItems( sortedData );

			ivSelection.addListener( e -> updateDetailPanel( ) );

			getView( ).getCounter( ).textProperty( ).bind( Bindings.createStringBinding(
					( ) -> sortedData.size( ) + " " + getResourceBundle( ).getString( "AggregatedCallsView.lblCounter.text" ), sortedData ) );
		}

		if ( aParameter.isPresent( ) && ( aParameter.get( ) instanceof AggregatedCallsFilter ) ) {
			final AggregatedCallsFilter aggregatedCallsFilter = (AggregatedCallsFilter) aParameter.get( );
			updateView( aggregatedCallsFilter );
			performUseFilter( );
		}
	}

	@Override
	public void doRefresh( ) {
		getView( ).getTable( ).refresh( );
	}

	private void updateDetailPanel( ) {
		if ( ivSelection.get( ).isPresent( ) ) {
			final AggregatedOperationCall call = ivSelection.get( ).get( );
			updateView( call );
		} else {
			updateView( (AggregatedOperationCall) null );
		}
	}

	public void performSelectCall( final InputEvent aEvent ) {
		final int clicked;

		if ( aEvent instanceof MouseEvent ) {
			clicked = ( (MouseEvent) aEvent ).getClickCount( );
		} else {
			clicked = 1;
		}

		if ( clicked == 1 ) {
			ivSelection.set( Optional.ofNullable( getView( ).getTable( ).getSelectionModel( ).getSelectedItem( ) ) );
		} else if ( clicked == 2 ) {
			jumpToCalls( );
		}
	}

	private void jumpToCalls( ) {
		if ( ivSelection.get( ).isPresent( ) ) {
			final AggregatedOperationCall call = ivSelection.get( ).get( );
			ivMainController.jumpToCalls( call );
		}
	}

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
				call -> call.isFailed( ) ? call.getFailedCause( ) : "" );

		final Predicate<AggregatedOperationCall> predicate = predicate1.and( predicate2 ).and( predicate3 ).and( predicate4 ).and( predicate5 );
		ivFilteredData.setPredicate( predicate );
	}

	public void performExportToCSV( ) throws IOException {
		ivMainController.exportToCSV( new AggregatedCallsCSVDataCollector( ) );
	}

	public void performSaveAsFavorite( ) throws BusinessException {
		final AggregatedCallsFilter filterContent = saveView( new AggregatedCallsFilter( ) );
		ivMainController.saveAsFavorite( filterContent, AggregatedCallsController.class );
	}

	private void updateView( final AggregatedOperationCall aCall ) {
		final String notAvailable = getResourceBundle( ).getString( "notAvailable" );

		if ( aCall != null ) {
			final TimeUnit sourceTimeUnit = ivDataService.getTimeUnit( );
			final TimeUnit targetTimeUnit = ivPropertiesService.loadApplicationProperty( TimeUnitProperty.class );

			getView( ).getContainer( ).setText( aCall.getContainer( ) );
			getView( ).getComponent( ).setText( aCall.getComponent( ) );
			getView( ).getOperation( ).setText( aCall.getOperation( ) );
			getView( ).getMinimalDuration( ).setText( ivNameConverterService.toDurationString( aCall.getMinDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getMaximalDuration( ).setText( ivNameConverterService.toDurationString( aCall.getMaxDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getMedianDuration( ).setText( ivNameConverterService.toDurationString( aCall.getMedianDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getTotalDuration( ).setText( ivNameConverterService.toDurationString( aCall.getTotalDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getMeanDuration( ).setText( ivNameConverterService.toDurationString( aCall.getMeanDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getCalls( ).setText( Integer.toString( aCall.getCalls( ) ) );
			getView( ).getFailed( ).setText( aCall.getFailedCause( ) != null ? aCall.getFailedCause( ) : notAvailable );
		} else {
			getView( ).getContainer( ).setText( notAvailable );
			getView( ).getComponent( ).setText( notAvailable );
			getView( ).getOperation( ).setText( notAvailable );
			getView( ).getMinimalDuration( ).setText( notAvailable );
			getView( ).getMaximalDuration( ).setText( notAvailable );
			getView( ).getMedianDuration( ).setText( notAvailable );
			getView( ).getTotalDuration( ).setText( notAvailable );
			getView( ).getMeanDuration( ).setText( notAvailable );
			getView( ).getCalls( ).setText( notAvailable );
			getView( ).getFailed( ).setText( notAvailable );
		}
	}

	private void updateView( final AggregatedCallsFilter aFilterContent ) {
		getView( ).getFilterComponent( ).setText( aFilterContent.getFilterComponent( ) );
		getView( ).getFilterContainer( ).setText( aFilterContent.getFilterContainer( ) );
		getView( ).getFilterException( ).setText( aFilterContent.getFilterException( ) );
		getView( ).getFilterOperation( ).setText( aFilterContent.getFilterOperation( ) );
		getView( ).getShowAllButton( ).setSelected( aFilterContent.isShowAllButton( ) );
		getView( ).getShowJustFailedButton( ).setSelected( aFilterContent.isShowJustFailedButton( ) );
		getView( ).getShowJustSuccessful( ).setSelected( aFilterContent.isShowJustSuccessful( ) );
	}

	private AggregatedCallsFilter saveView( final AggregatedCallsFilter aFilterContent ) {
		aFilterContent.setFilterComponent( getView( ).getFilterComponent( ).getText( ) );
		aFilterContent.setFilterContainer( getView( ).getFilterContainer( ).getText( ) );
		aFilterContent.setFilterException( getView( ).getFilterException( ).getText( ) );
		aFilterContent.setFilterOperation( getView( ).getFilterOperation( ).getText( ) );
		aFilterContent.setShowAllButton( getView( ).getShowAllButton( ).isSelected( ) );
		aFilterContent.setShowJustFailedButton( getView( ).getShowJustFailedButton( ).isSelected( ) );
		aFilterContent.setShowJustSuccessful( getView( ).getShowJustSuccessful( ).isSelected( ) );

		return aFilterContent;
	}

	/**
	 * @author Nils Christian Ehmke
	 */
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
