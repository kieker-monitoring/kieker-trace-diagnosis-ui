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
import kieker.diagnosis.domain.AggregatedOperationCall;
import kieker.diagnosis.gui.AbstractController;
import kieker.diagnosis.gui.main.MainController;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.service.export.CSVData;
import kieker.diagnosis.util.CSVDataCollector;
import kieker.diagnosis.util.Context;
import kieker.diagnosis.util.ContextKey;
import kieker.diagnosis.util.FilterUtility;
import kieker.diagnosis.util.NameConverter;

/**
 * @author Nils Christian Ehmke
 */
public final class AggregatedCallsController extends AbstractController<AggregatedCallsView> implements AggregatedCallsControllerIfc {

	private FilteredList<AggregatedOperationCall> ivFilteredData;

	private final SimpleObjectProperty<Optional<AggregatedOperationCall>> ivSelection = new SimpleObjectProperty<>( Optional.empty( ) );
	private MainController ivMainController;

	public AggregatedCallsController( final Context aContext ) {
		super( aContext );
	}

	@Override
	public void doInitialize( ) {
		final DataModel dataModel = DataModel.getInstance( );

		ivFilteredData = new FilteredList<>( dataModel.getAggregatedOperationCalls( ) );
		ivFilteredData.addListener( (ListChangeListener<AggregatedOperationCall>) change -> ivSelection.set( Optional.empty( ) ) );

		final SortedList<AggregatedOperationCall> sortedData = new SortedList<>( ivFilteredData );
		sortedData.comparatorProperty( ).bind( getView( ).getTable( ).comparatorProperty( ) );
		getView( ).getTable( ).setItems( sortedData );

		final Object filterContent = getContext( ).get( ContextKey.FILTER_CONTENT );
		if ( filterContent instanceof FilterContent ) {
			loadFilterContent( (FilterContent) filterContent );
			useFilter( );
		}

		ivSelection.addListener( e -> updateDetailPanel( ) );

		getView( ).getCounter( ).textProperty( ).bind( Bindings.createStringBinding(
				( ) -> sortedData.size( ) + " " + getView( ).getResourceBundle( ).getString( "AggregatedCallsView.lblCounter.text" ), sortedData ) );
	}

	private void updateDetailPanel( ) {
		if ( ivSelection.get( ).isPresent( ) ) {
			final AggregatedOperationCall call = ivSelection.get( ).get( );
			final TimeUnit sourceTimeUnit = DataModel.getInstance( ).getTimeUnit( );
			final TimeUnit targetTimeUnit = PropertiesModel.getInstance( ).getTimeUnit( );

			getView( ).getContainer( ).setText( call.getContainer( ) );
			getView( ).getComponent( ).setText( call.getComponent( ) );
			getView( ).getOperation( ).setText( call.getOperation( ) );
			getView( ).getMinimalDuration( ).setText( NameConverter.toDurationString( call.getMinDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getMaximalDuration( ).setText( NameConverter.toDurationString( call.getMaxDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getMedianDuration( ).setText( NameConverter.toDurationString( call.getMedianDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getTotalDuration( ).setText( NameConverter.toDurationString( call.getTotalDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getMeanDuration( ).setText( NameConverter.toDurationString( call.getMeanDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getCalls( ).setText( Integer.toString( call.getCalls( ) ) );
			getView( ).getFailed( ).setText( call.getFailedCause( ) != null ? call.getFailedCause( ) : "N/A" );
		}
		else {
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

	@Override
	public void selectCall( final InputEvent aEvent ) throws Exception {
		final int clicked;
		if ( aEvent instanceof MouseEvent ) {
			clicked = ((MouseEvent) aEvent).getClickCount( );
		}
		else {
			clicked = 1;
		}

		if ( clicked == 1 ) {
			ivSelection.set( Optional.ofNullable( getView( ).getTable( ).getSelectionModel( ).getSelectedItem( ) ) );
		}
		else if ( clicked == 2 ) {
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
	public void useFilter( ) {
		final Predicate<AggregatedOperationCall> predicate1 = FilterUtility.useFilter( getView( ).getShowAllButton( ), getView( ).getShowJustSuccessful( ),
				getView( ).getShowJustFailedButton( ), AggregatedOperationCall::isFailed );
		final Predicate<AggregatedOperationCall> predicate2 = FilterUtility.useFilter( getView( ).getFilterContainer( ),
				AggregatedOperationCall::getContainer );
		final Predicate<AggregatedOperationCall> predicate3 = FilterUtility.useFilter( getView( ).getFilterComponent( ),
				AggregatedOperationCall::getComponent );
		final Predicate<AggregatedOperationCall> predicate4 = FilterUtility.useFilter( getView( ).getFilterOperation( ),
				AggregatedOperationCall::getOperation );
		final Predicate<AggregatedOperationCall> predicate5 = FilterUtility.useFilter( getView( ).getFilterException( ),
				(call -> call.isFailed( ) ? call.getFailedCause( ) : "") );

		final Predicate<AggregatedOperationCall> predicate = predicate1.and( predicate2 ).and( predicate3 ).and( predicate4 ).and( predicate5 );
		ivFilteredData.setPredicate( predicate );
	}

	@Override
	public void exportToCSV( ) throws IOException {
		ivMainController.exportToCSV( new AggregatedCallsCSVDataCollector( ) );
	}

	@Override
	public void saveAsFavorite( ) {
		ivMainController.saveAsFavorite( saveFilterContent( ), AggregatedCallsController.class );
	}

	private FilterContent saveFilterContent( ) {
		final FilterContent filterContent = new FilterContent( );

		filterContent.setFilterComponent( getView( ).getFilterComponent( ).getText( ) );
		filterContent.setFilterContainer( getView( ).getFilterContainer( ).getText( ) );
		filterContent.setFilterException( getView( ).getFilterException( ).getText( ) );
		filterContent.setFilterOperation( getView( ).getFilterOperation( ).getText( ) );
		filterContent.setShowAllButton( getView( ).getShowAllButton( ).isSelected( ) );
		filterContent.setShowJustFailedButton( getView( ).getShowJustFailedButton( ).isSelected( ) );
		filterContent.setShowJustSuccessful( getView( ).getShowJustSuccessful( ).isSelected( ) );

		return filterContent;
	}

	private void loadFilterContent( final FilterContent aFilterContent ) {
		getView( ).getFilterComponent( ).setText( aFilterContent.getFilterComponent( ) );
		getView( ).getFilterContainer( ).setText( aFilterContent.getFilterContainer( ) );
		getView( ).getFilterException( ).setText( aFilterContent.getFilterException( ) );
		getView( ).getFilterOperation( ).setText( aFilterContent.getFilterOperation( ) );
		getView( ).getShowAllButton( ).setSelected( aFilterContent.isShowAllButton( ) );
		getView( ).getShowJustFailedButton( ).setSelected( aFilterContent.isShowJustFailedButton( ) );
		getView( ).getShowJustSuccessful( ).setSelected( aFilterContent.isShowJustSuccessful( ) );
	}

	private class FilterContent {

		private boolean ivShowAllButton;
		private boolean ivShowJustSuccessful;
		private boolean ivShowJustFailedButton;

		private String ivFilterContainer;
		private String ivFilterComponent;
		private String ivFilterOperation;
		private String ivFilterException;

		public boolean isShowAllButton( ) {
			return ivShowAllButton;
		}

		public void setShowAllButton( final boolean showAllButton ) {
			ivShowAllButton = showAllButton;
		}

		public boolean isShowJustSuccessful( ) {
			return ivShowJustSuccessful;
		}

		public void setShowJustSuccessful( final boolean showJustSuccessful ) {
			ivShowJustSuccessful = showJustSuccessful;
		}

		public boolean isShowJustFailedButton( ) {
			return ivShowJustFailedButton;
		}

		public void setShowJustFailedButton( final boolean showJustFailedButton ) {
			ivShowJustFailedButton = showJustFailedButton;
		}

		public String getFilterContainer( ) {
			return ivFilterContainer;
		}

		public void setFilterContainer( final String filterContainer ) {
			ivFilterContainer = filterContainer;
		}

		public String getFilterComponent( ) {
			return ivFilterComponent;
		}

		public void setFilterComponent( final String filterComponent ) {
			ivFilterComponent = filterComponent;
		}

		public String getFilterOperation( ) {
			return ivFilterOperation;
		}

		public void setFilterOperation( final String filterOperation ) {
			ivFilterOperation = filterOperation;
		}

		public String getFilterException( ) {
			return ivFilterException;
		}

		public void setFilterException( final String filterException ) {
			ivFilterException = filterException;
		}

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

			final CSVData result = new CSVData( );
			result.setHeader( header );
			result.setRows( rows );
			return result;
		}

	}

}
