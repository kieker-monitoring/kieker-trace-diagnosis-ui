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

package kieker.diagnosis.gui.calls;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Calendar;
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
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.gui.AbstractController;
import kieker.diagnosis.gui.main.MainController;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.util.CSVData;
import kieker.diagnosis.util.CSVDataCollector;
import kieker.diagnosis.util.Context;
import kieker.diagnosis.util.ContextKey;
import kieker.diagnosis.util.FilterUtility;
import kieker.diagnosis.util.NameConverter;

/**
 * @author Nils Christian Ehmke
 */
public final class CallsController extends AbstractController<CallsView> implements CallsControllerIfc {

	private final SimpleObjectProperty<Optional<OperationCall>> ivSelection = new SimpleObjectProperty<>( Optional.empty( ) );

	private FilteredList<OperationCall> ivFilteredData;

	public CallsController( final Context aContext ) {
		super( aContext );
	}

	@Override
	public void doInitialize( ) {
		final DataModel dataModel = DataModel.getInstance( );

		ivFilteredData = new FilteredList<>( dataModel.getOperationCalls( ) );
		ivFilteredData.addListener( (ListChangeListener<OperationCall>) change -> ivSelection.set( Optional.empty( ) ) );

		final SortedList<OperationCall> sortedData = new SortedList<>( ivFilteredData );
		sortedData.comparatorProperty( ).bind( getView( ).getTable( ).comparatorProperty( ) );
		getView( ).getTable( ).setItems( sortedData );

		final Object filterContent = getContext( ).get( ContextKey.FILTER_CONTENT );
		if ( filterContent instanceof FilterContent ) {
			loadFilterContent( (FilterContent) filterContent );
			useFilter( );
		}

		ivSelection.addListener( e -> updateDetailPanel( ) );

		getView( ).getCounter( ).textProperty( ).bind( Bindings
				.createStringBinding( ( ) -> sortedData.size( ) + " " + getView( ).getResourceBundle( ).getString( "CallsView.lbCounter.text" ), sortedData ) );

		final Object call = super.getContext( ).get( ContextKey.AGGREGATED_OPERATION_CALL );
		if ( call instanceof AggregatedOperationCall ) {
			jumpToCalls( (AggregatedOperationCall) call );
		}

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
		}
		else {
			getView( ).getShowJustSuccessful( ).setSelected( true );
		}

		useFilter( );
	}

	private void updateDetailPanel( ) {
		if ( ivSelection.get( ).isPresent( ) ) {
			final OperationCall call = ivSelection.get( ).get( );
			final TimeUnit sourceTimeUnit = DataModel.getInstance( ).getTimeUnit( );
			final TimeUnit targetTimeUnit = PropertiesModel.getInstance( ).getTimeUnit( );

			getView( ).getContainer( ).setText( call.getContainer( ) );
			getView( ).getComponent( ).setText( call.getComponent( ) );
			getView( ).getOperation( ).setText( call.getOperation( ) );
			getView( ).getTimestamp( ).setText( NameConverter.toTimestampString( call.getTimestamp( ), sourceTimeUnit ) + " (" + call.getTimestamp( ) + ")" );
			getView( ).getDuration( ).setText( NameConverter.toDurationString( call.getDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getTraceID( ).setText( Long.toString( call.getTraceID( ) ) );
			getView( ).getFailed( ).setText( call.getFailedCause( ) != null ? call.getFailedCause( ) : "N/A" );
		}
		else {
			getView( ).getContainer( ).setText( "N/A" );
			getView( ).getComponent( ).setText( "N/A" );
			getView( ).getOperation( ).setText( "N/A" );
			getView( ).getTimestamp( ).setText( "N/A" );
			getView( ).getDuration( ).setText( "N/A" );
			getView( ).getTraceID( ).setText( "N/A" );
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
			jumpToTrace( );
		}
	}

	private void jumpToTrace( ) throws Exception {
		if ( ivSelection.get( ).isPresent( ) ) {
			final OperationCall call = ivSelection.get( ).get( );
			MainController.instance( ).jumpToTrace( call );
		}
	}

	@Override
	public void useFilter( ) {
		final Predicate<OperationCall> predicate1 = FilterUtility.useFilter( getView( ).getShowAllButton( ), getView( ).getShowJustSuccessful( ),
				getView( ).getShowJustFailedButton( ), OperationCall::isFailed );
		final Predicate<OperationCall> predicate2 = FilterUtility.useFilter( getView( ).getFilterContainer( ), OperationCall::getContainer );
		final Predicate<OperationCall> predicate3 = FilterUtility.useFilter( getView( ).getFilterComponent( ), OperationCall::getComponent );
		final Predicate<OperationCall> predicate4 = FilterUtility.useFilter( getView( ).getFilterOperation( ), OperationCall::getOperation );
		final Predicate<OperationCall> predicate5 = FilterUtility.useFilter( getView( ).getFilterTraceID( ), (call -> Long.toString( call.getTraceID( ) )) );
		final Predicate<OperationCall> predicate6 = FilterUtility.useFilter( getView( ).getFilterLowerDate( ), OperationCall::getTimestamp, true );
		final Predicate<OperationCall> predicate7 = FilterUtility.useFilter( getView( ).getFilterUpperDate( ), OperationCall::getTimestamp, false );
		final Predicate<OperationCall> predicate8 = FilterUtility.useFilter( getView( ).getFilterLowerTime( ), OperationCall::getTimestamp, true );
		final Predicate<OperationCall> predicate9 = FilterUtility.useFilter( getView( ).getFilterUpperTime( ), OperationCall::getTimestamp, false );
		final Predicate<OperationCall> predicate10 = FilterUtility.useFilter( getView( ).getFilterException( ),
				(call -> call.isFailed( ) ? call.getFailedCause( ) : "") );

		final Predicate<OperationCall> predicate = predicate1.and( predicate2 ).and( predicate3 ).and( predicate4 ).and( predicate5 ).and( predicate6 )
				.and( predicate7 ).and( predicate8 ).and( predicate9 ).and( predicate10 );
		ivFilteredData.setPredicate( predicate );
	}

	@Override
	public void exportToCSV( ) throws IOException {
		MainController.instance( ).exportToCSV( new CallsCSVDataCollector( ) );
	}

	@Override
	public void saveAsFavorite( ) {
		MainController.instance( ).saveAsFavorite( saveFilterContent( ), CallsController.class );
	}

	private FilterContent saveFilterContent( ) {
		final FilterContent filterContent = new FilterContent( );

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

	private void loadFilterContent( final FilterContent aFilterContent ) {
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
