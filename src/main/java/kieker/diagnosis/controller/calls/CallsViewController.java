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

package kieker.diagnosis.controller.calls;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import jfxtras.scene.control.CalendarTimeTextField;
import kieker.diagnosis.controller.AbstractController;
import kieker.diagnosis.controller.MainController;
import kieker.diagnosis.domain.AggregatedOperationCall;
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.util.CSVData;
import kieker.diagnosis.util.CSVDataCollector;
import kieker.diagnosis.util.Context;
import kieker.diagnosis.util.ContextKey;
import kieker.diagnosis.util.ErrorHandling;
import kieker.diagnosis.util.FilterUtility;
import kieker.diagnosis.util.NameConverter;

/**
 * @author Nils Christian Ehmke
 */
public final class CallsViewController extends AbstractController {

	private final SimpleObjectProperty<Optional<OperationCall>> ivSelection = new SimpleObjectProperty<>( Optional.empty( ) );

	private FilteredList<OperationCall> ivFilteredData;

	@FXML
	private TableView<OperationCall> ivTable;

	@FXML
	private RadioButton ivShowAllButton;
	@FXML
	private RadioButton ivShowJustFailedButton;
	@FXML
	private RadioButton ivShowJustSuccessful;

	@FXML
	private TextField ivFilterContainer;
	@FXML
	private TextField ivFilterComponent;
	@FXML
	private TextField ivFilterOperation;
	@FXML
	private TextField ivFilterTraceID;
	@FXML
	private TextField ivFilterException;

	@FXML
	private DatePicker ivFilterLowerDate;
	@FXML
	private CalendarTimeTextField ivFilterLowerTime;
	@FXML
	private DatePicker ivFilterUpperDate;
	@FXML
	private CalendarTimeTextField ivFilterUpperTime;

	@FXML
	private TextField ivContainer;
	@FXML
	private TextField ivComponent;
	@FXML
	private TextField ivOperation;
	@FXML
	private TextField ivTimestamp;
	@FXML
	private TextField ivDuration;
	@FXML
	private TextField ivTraceID;
	@FXML
	private TextField ivFailed;

	@FXML
	private TextField ivCounter;

	@FXML
	private ResourceBundle resources;

	public CallsViewController( final Context aContext ) {
		super( aContext );
	}

	@ErrorHandling
	public void initialize( ) {
		final DataModel dataModel = DataModel.getInstance( );

		ivFilteredData = new FilteredList<>( dataModel.getOperationCalls( ) );
		ivFilteredData.addListener( (ListChangeListener<OperationCall>) change -> ivSelection.set( Optional.empty( ) ) );

		final SortedList<OperationCall> sortedData = new SortedList<>( ivFilteredData );
		sortedData.comparatorProperty( ).bind( ivTable.comparatorProperty( ) );
		ivTable.setItems( sortedData );

		final Object filterContent = getContext( ).get( ContextKey.FILTER_CONTENT );
		if ( filterContent instanceof FilterContent ) {
			loadFilterContent( (FilterContent) filterContent );
			useFilter( );
		}

		ivSelection.addListener( e -> updateDetailPanel( ) );

		ivCounter.textProperty( )
				.bind( Bindings.createStringBinding( ( ) -> sortedData.size( ) + " " + resources.getString( "CallsView.lbCounter.text" ), sortedData ) );

		final Object call = super.getContext( ).get( ContextKey.AGGREGATED_OPERATION_CALL );
		if ( call instanceof AggregatedOperationCall ) {
			jumpToCalls( (AggregatedOperationCall) call );
		}

	}

	private void jumpToCalls( final AggregatedOperationCall aCall ) {
		// Clear all filters (as the view might be cached)
		ivFilterLowerDate.setValue( null );
		ivFilterLowerTime.setCalendar( null );
		ivFilterUpperDate.setValue( null );
		ivFilterUpperTime.setCalendar( null );
		ivFilterTraceID.setText( null );
		ivFilterException.setText( null );
		ivShowAllButton.setSelected( true );

		// Now use the values from the given aggregated call for the filters
		ivFilterContainer.setText( aCall.getContainer( ) );
		ivFilterComponent.setText( aCall.getComponent( ) );
		ivFilterOperation.setText( aCall.getOperation( ) );

		if ( aCall.getFailedCause( ) != null ) {
			ivFilterException.setText( aCall.getFailedCause( ) );
		}
		else {
			ivShowJustSuccessful.setSelected( true );
		}

		useFilter( );
	}

	private void updateDetailPanel( ) {
		if ( ivSelection.get( ).isPresent( ) ) {
			final OperationCall call = ivSelection.get( ).get( );
			final TimeUnit sourceTimeUnit = DataModel.getInstance( ).getTimeUnit( );
			final TimeUnit targetTimeUnit = PropertiesModel.getInstance( ).getTimeUnit( );

			ivContainer.setText( call.getContainer( ) );
			ivComponent.setText( call.getComponent( ) );
			ivOperation.setText( call.getOperation( ) );
			ivTimestamp.setText( NameConverter.toTimestampString( call.getTimestamp( ), sourceTimeUnit ) + " (" + call.getTimestamp( ) + ")" );
			ivDuration.setText( NameConverter.toDurationString( call.getDuration( ), sourceTimeUnit, targetTimeUnit ) );
			ivTraceID.setText( Long.toString( call.getTraceID( ) ) );
			ivFailed.setText( call.getFailedCause( ) != null ? call.getFailedCause( ) : "N/A" );
		}
		else {
			ivContainer.setText( "N/A" );
			ivComponent.setText( "N/A" );
			ivOperation.setText( "N/A" );
			ivTimestamp.setText( "N/A" );
			ivDuration.setText( "N/A" );
			ivTraceID.setText( "N/A" );
			ivFailed.setText( "N/A" );
		}
	}

	@ErrorHandling
	public void selectCall( final InputEvent aEvent ) throws Exception {
		final int clicked;
		if ( aEvent instanceof MouseEvent ) {
			clicked = ((MouseEvent) aEvent).getClickCount( );
		}
		else {
			clicked = 1;
		}

		if ( clicked == 1 ) {
			ivSelection.set( Optional.ofNullable( ivTable.getSelectionModel( ).getSelectedItem( ) ) );
		}
		else if ( clicked == 2 ) {
			jumpToTrace( );
		}
	}

	private void jumpToTrace( ) throws Exception {
		if ( ivSelection.get( ).isPresent( ) ) {
			final OperationCall call = ivSelection.get( ).get( );
			MainController.instance( ).jumpToTrace( call );
		}
	}

	@ErrorHandling
	public void useFilter( ) {
		final Predicate<OperationCall> predicate1 = FilterUtility.useFilter( ivShowAllButton, ivShowJustSuccessful, ivShowJustFailedButton,
				OperationCall::isFailed );
		final Predicate<OperationCall> predicate2 = FilterUtility.useFilter( ivFilterContainer, OperationCall::getContainer );
		final Predicate<OperationCall> predicate3 = FilterUtility.useFilter( ivFilterComponent, OperationCall::getComponent );
		final Predicate<OperationCall> predicate4 = FilterUtility.useFilter( ivFilterOperation, OperationCall::getOperation );
		final Predicate<OperationCall> predicate5 = FilterUtility.useFilter( ivFilterTraceID, (call -> Long.toString( call.getTraceID( ) )) );
		final Predicate<OperationCall> predicate6 = FilterUtility.useFilter( ivFilterLowerDate, OperationCall::getTimestamp, true );
		final Predicate<OperationCall> predicate7 = FilterUtility.useFilter( ivFilterUpperDate, OperationCall::getTimestamp, false );
		final Predicate<OperationCall> predicate8 = FilterUtility.useFilter( ivFilterLowerTime, OperationCall::getTimestamp, true );
		final Predicate<OperationCall> predicate9 = FilterUtility.useFilter( ivFilterUpperTime, OperationCall::getTimestamp, false );
		final Predicate<OperationCall> predicate10 = FilterUtility.useFilter( ivFilterException, (call -> call.isFailed( ) ? call.getFailedCause( ) : "") );

		final Predicate<OperationCall> predicate = predicate1.and( predicate2 ).and( predicate3 ).and( predicate4 ).and( predicate5 ).and( predicate6 )
				.and( predicate7 ).and( predicate8 ).and( predicate9 ).and( predicate10 );
		ivFilteredData.setPredicate( predicate );
	}

	@ErrorHandling
	public void exportToCSV( ) throws IOException {
		MainController.instance( ).exportToCSV( new CallsCSVDataCollector( ) );
	}

	@ErrorHandling
	public void saveAsFavorite( ) {
		MainController.instance( ).saveAsFavorite( saveFilterContent( ), CallsViewController.class );
	}

	private FilterContent saveFilterContent( ) {
		final FilterContent filterContent = new FilterContent( );

		filterContent.setFilterComponent( ivFilterComponent.getText( ) );
		filterContent.setFilterContainer( ivFilterContainer.getText( ) );
		filterContent.setFilterException( ivFilterException.getText( ) );
		filterContent.setFilterOperation( ivFilterOperation.getText( ) );
		filterContent.setFilterLowerDate( ivFilterLowerDate.getValue( ) );
		filterContent.setFilterLowerTime( ivFilterLowerTime.getCalendar( ) );
		filterContent.setFilterTraceID( ivFilterTraceID.getText( ) );
		filterContent.setFilterUpperDate( ivFilterUpperDate.getValue( ) );
		filterContent.setFilterUpperTime( ivFilterUpperTime.getCalendar( ) );
		filterContent.setShowAllButton( ivShowAllButton.isSelected( ) );
		filterContent.setShowJustFailedButton( ivShowJustFailedButton.isSelected( ) );
		filterContent.setShowJustSuccessful( ivShowJustSuccessful.isSelected( ) );

		return filterContent;
	}

	private void loadFilterContent( final FilterContent aFilterContent ) {
		ivFilterComponent.setText( aFilterContent.getFilterComponent( ) );
		ivFilterContainer.setText( aFilterContent.getFilterContainer( ) );
		ivFilterException.setText( aFilterContent.getFilterException( ) );
		ivFilterOperation.setText( aFilterContent.getFilterOperation( ) );
		ivFilterTraceID.setText( aFilterContent.getFilterTraceID( ) );
		ivFilterLowerDate.setValue( aFilterContent.getFilterLowerDate( ) );
		ivFilterUpperDate.setValue( aFilterContent.getFilterUpperDate( ) );
		ivFilterLowerTime.setCalendar( aFilterContent.getFilterLowerTime( ) );
		ivFilterUpperTime.setCalendar( aFilterContent.getFilterUpperTime( ) );
		ivShowAllButton.setSelected( aFilterContent.isShowAllButton( ) );
		ivShowJustFailedButton.setSelected( aFilterContent.isShowJustFailedButton( ) );
		ivShowJustSuccessful.setSelected( aFilterContent.isShowJustSuccessful( ) );
	}

	private final class CallsCSVDataCollector implements CSVDataCollector {

		@Override
		public CSVData collectData( ) {
			final ObservableList<OperationCall> items = ivTable.getItems( );
			final ObservableList<TableColumn<OperationCall, ?>> columns = ivTable.getVisibleLeafColumns( );

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

			final CSVData result = new CSVData( );
			result.setHeader( header );
			result.setRows( rows );
			return result;
		}

	}

	private class FilterContent {

		private String ivFilterComponent;
		private String ivFilterContainer;
		private String ivFilterException;
		private String ivFilterOperation;
		private String ivFilterTraceID;
		private LocalDate ivFilterLowerDate;
		private LocalDate ivFilterUpperDate;
		private Calendar ivFilterLowerTime;
		private Calendar ivFilterUpperTime;
		private boolean ivShowAllButton;
		private boolean ivShowJustFailedButton;
		private boolean ivShowJustSuccessful;

		public String getFilterComponent( ) {
			return ivFilterComponent;
		}

		public void setFilterComponent( final String filterComponent ) {
			ivFilterComponent = filterComponent;
		}

		public String getFilterContainer( ) {
			return ivFilterContainer;
		}

		public void setFilterContainer( final String filterContainer ) {
			ivFilterContainer = filterContainer;
		}

		public String getFilterException( ) {
			return ivFilterException;
		}

		public void setFilterException( final String filterException ) {
			ivFilterException = filterException;
		}

		public String getFilterOperation( ) {
			return ivFilterOperation;
		}

		public void setFilterOperation( final String filterOperation ) {
			ivFilterOperation = filterOperation;
		}

		public String getFilterTraceID( ) {
			return ivFilterTraceID;
		}

		public void setFilterTraceID( final String filterTraceID ) {
			ivFilterTraceID = filterTraceID;
		}

		public LocalDate getFilterLowerDate( ) {
			return ivFilterLowerDate;
		}

		public void setFilterLowerDate( final LocalDate filterLowerDate ) {
			ivFilterLowerDate = filterLowerDate;
		}

		public LocalDate getFilterUpperDate( ) {
			return ivFilterUpperDate;
		}

		public void setFilterUpperDate( final LocalDate filterUpperDate ) {
			ivFilterUpperDate = filterUpperDate;
		}

		public Calendar getFilterLowerTime( ) {
			return ivFilterLowerTime;
		}

		public void setFilterLowerTime( final Calendar filterLowerTime ) {
			ivFilterLowerTime = filterLowerTime;
		}

		public Calendar getFilterUpperTime( ) {
			return ivFilterUpperTime;
		}

		public void setFilterUpperTime( final Calendar filterUpperTime ) {
			ivFilterUpperTime = filterUpperTime;
		}

		public boolean isShowAllButton( ) {
			return ivShowAllButton;
		}

		public void setShowAllButton( final boolean showAllButton ) {
			ivShowAllButton = showAllButton;
		}

		public boolean isShowJustFailedButton( ) {
			return ivShowJustFailedButton;
		}

		public void setShowJustFailedButton( final boolean showJustFailedButton ) {
			ivShowJustFailedButton = showJustFailedButton;
		}

		public boolean isShowJustSuccessful( ) {
			return ivShowJustSuccessful;
		}

		public void setShowJustSuccessful( final boolean showJustSuccessful ) {
			ivShowJustSuccessful = showJustSuccessful;
		}

	}

}
