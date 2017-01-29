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

package kieker.diagnosis.controller.traces;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import jfxtras.scene.control.CalendarTimeTextField;
import kieker.diagnosis.components.treetable.LazyOperationCallTreeItem;
import kieker.diagnosis.controller.AbstractController;
import kieker.diagnosis.controller.MainController;
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.domain.Trace;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.util.Context;
import kieker.diagnosis.util.ContextKey;
import kieker.diagnosis.util.ErrorHandling;
import kieker.diagnosis.util.FilterUtility;
import kieker.diagnosis.util.NameConverter;

/**
 * The sub-controller responsible for the sub-view presenting the available traces.
 *
 * @author Nils Christian Ehmke
 */
public final class TracesViewController extends AbstractController {

	private final DataModel ivDataModel = DataModel.getInstance( );

	private final SimpleObjectProperty<Optional<OperationCall>> ivSelection = new SimpleObjectProperty<>( Optional.empty( ) );

	@FXML
	private TreeTableView<OperationCall> ivTreetable;

	@FXML
	private RadioButton ivShowAllButton;
	@FXML
	private RadioButton ivShowJustFailedButton;
	@FXML
	private RadioButton ivShowJustFailureContainingButton;
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
	private TextField ivTraceDepth;
	@FXML
	private TextField ivTraceSize;
	@FXML
	private TextField ivTimestamp;
	@FXML
	private TextField ivContainer;
	@FXML
	private TextField ivComponent;
	@FXML
	private TextField ivOperation;
	@FXML
	private TextField ivDuration;
	@FXML
	private TextField ivPercent;
	@FXML
	private TextField ivTraceID;
	@FXML
	private TextField ivFailed;

	@FXML
	private TextField ivCounter;

	@FXML
	private ResourceBundle resources;

	private Predicate<OperationCall> ivPredicate = FilterUtility.alwaysTrue( );

	public TracesViewController( final Context aContext ) {
		super( aContext );
	}

	@ErrorHandling
	public void initialize( ) {
		final Object filterContent = getContext( ).get( ContextKey.FILTER_CONTENT );
		if ( filterContent instanceof FilterContent ) {
			loadFilterContent( (FilterContent) filterContent );
			useFilter( );
		}
		else {
			reloadTreetable( );
		}

		final ObservableList<Trace> traces = ivDataModel.getTraces( );
		traces.addListener( ( final Change<? extends Trace> aChange ) -> reloadTreetable( ) );

		ivSelection.addListener( e -> updateDetailPanel( ) );

		final Object call = super.getContext( ).get( ContextKey.OPERATION_CALL );
		if ( call instanceof OperationCall ) {
			jumpToCall( (OperationCall) call );
		}
	}

	private void jumpToCall( final OperationCall aCall ) {
		// Clear all filters (as the view might be cached)
		ivFilterComponent.setText( null );
		ivFilterContainer.setText( null );
		ivFilterException.setText( null );
		ivFilterOperation.setText( null );
		ivFilterLowerDate.setValue( null );
		ivFilterLowerTime.setCalendar( null );
		ivFilterUpperDate.setValue( null );
		ivFilterUpperTime.setCalendar( null );
		ivFilterTraceID.setText( null );
		ivFilterException.setText( null );
		ivShowAllButton.setSelected( true );
		useFilter( );

		final TreeItem<OperationCall> root = ivTreetable.getRoot( );

		final Optional<TreeItem<OperationCall>> traceRoot = findTraceRoot( root, aCall );
		if ( traceRoot.isPresent( ) ) {
			final TreeItem<OperationCall> treeItem = findCall( traceRoot.get( ), aCall );
			if ( treeItem != null ) {
				ivTreetable.getSelectionModel( ).select( treeItem );
				ivSelection.set( Optional.ofNullable( treeItem.getValue( ) ) );
			}
		}
	}

	private Optional<TreeItem<OperationCall>> findTraceRoot( final TreeItem<OperationCall> aRoot, final OperationCall aCall ) {
		return aRoot.getChildren( ).stream( ).filter( t -> t.getValue( ).getTraceID( ) == aCall.getTraceID( ) ).findFirst( );
	}

	private TreeItem<OperationCall> findCall( final TreeItem<OperationCall> aRoot, final OperationCall aCall ) {
		if ( aRoot.getValue( ) == aCall ) {
			aRoot.setExpanded( true );
			return aRoot;
		}

		for ( final TreeItem<OperationCall> child : aRoot.getChildren( ) ) {
			final TreeItem<OperationCall> item = findCall( child, aCall );
			if ( item != null ) {
				aRoot.setExpanded( true );
				return item;
			}
		}

		return null;
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
			ivTraceDepth.setText( Integer.toString( call.getStackDepth( ) ) );
			ivTraceSize.setText( Integer.toString( call.getStackSize( ) ) );
			ivPercent.setText( call.getPercent( ) + " %" );
			ivFailed.setText( call.getFailedCause( ) != null ? call.getFailedCause( ) : "N/A" );
		}
		else {
			ivContainer.setText( "N/A" );
			ivComponent.setText( "N/A" );
			ivOperation.setText( "N/A" );
			ivTimestamp.setText( "N/A" );
			ivDuration.setText( "N/A" );
			ivTraceID.setText( "N/A" );
			ivPercent.setText( "N/A" );
			ivFailed.setText( "N/A" );
		}
	}

	@ErrorHandling
	public void selectCall( ) {
		final TreeItem<OperationCall> selectedItem = ivTreetable.getSelectionModel( ).getSelectedItem( );
		if ( selectedItem != null ) {
			ivSelection.set( Optional.ofNullable( selectedItem.getValue( ) ) );
		}
	}

	@ErrorHandling
	public void useFilter( ) {
		final Predicate<OperationCall> predicate1 = FilterUtility.useFilter( ivShowAllButton, ivShowJustSuccessful, ivShowJustFailedButton,
				ivShowJustFailureContainingButton, OperationCall::isFailed, OperationCall::containsFailure );
		final Predicate<OperationCall> predicate2 = FilterUtility.useFilter( ivFilterContainer, OperationCall::getContainer,
				PropertiesModel.getInstance( ).isSearchInEntireTrace( ) );
		final Predicate<OperationCall> predicate3 = FilterUtility.useFilter( ivFilterComponent, OperationCall::getComponent,
				PropertiesModel.getInstance( ).isSearchInEntireTrace( ) );
		final Predicate<OperationCall> predicate4 = FilterUtility.useFilter( ivFilterOperation, OperationCall::getOperation,
				PropertiesModel.getInstance( ).isSearchInEntireTrace( ) );
		final Predicate<OperationCall> predicate5 = FilterUtility.useFilter( ivFilterTraceID, (call -> Long.toString( call.getTraceID( ) )),
				PropertiesModel.getInstance( ).isSearchInEntireTrace( ) );
		final Predicate<OperationCall> predicate6 = FilterUtility.useFilter( ivFilterLowerDate, OperationCall::getTimestamp, true,
				PropertiesModel.getInstance( ).isSearchInEntireTrace( ) );
		final Predicate<OperationCall> predicate7 = FilterUtility.useFilter( ivFilterUpperDate, OperationCall::getTimestamp, false,
				PropertiesModel.getInstance( ).isSearchInEntireTrace( ) );
		final Predicate<OperationCall> predicate8 = FilterUtility.useFilter( ivFilterLowerTime, OperationCall::getTimestamp, true,
				PropertiesModel.getInstance( ).isSearchInEntireTrace( ) );
		final Predicate<OperationCall> predicate9 = FilterUtility.useFilter( ivFilterUpperTime, OperationCall::getTimestamp, false,
				PropertiesModel.getInstance( ).isSearchInEntireTrace( ) );
		final Predicate<OperationCall> predicate10 = FilterUtility.useFilter( ivFilterException, (call -> call.isFailed( ) ? call.getFailedCause( ) : ""),
				PropertiesModel.getInstance( ).isSearchInEntireTrace( ) );

		ivPredicate = predicate1.and( predicate2 ).and( predicate3 ).and( predicate4 ).and( predicate5 ).and( predicate6 ).and( predicate7 ).and( predicate8 )
				.and( predicate9 ).and( predicate10 );
		reloadTreetable( );
	}

	@ErrorHandling
	public void saveAsFavorite( ) {
		MainController.instance( ).saveAsFavorite( saveFilterContent( ), TracesViewController.class );
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
		filterContent.setShowJustFailureContainingButton( ivShowJustFailureContainingButton.isSelected( ) );

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
		ivShowJustFailureContainingButton.setSelected( aFilterContent.isShowJustFailureContainingButton( ) );
	}

	private void reloadTreetable( ) {
		ivSelection.set( Optional.empty( ) );

		final List<Trace> traces = ivDataModel.getTraces( );
		final TreeItem<OperationCall> root = new TreeItem<>( );
		final ObservableList<TreeItem<OperationCall>> rootChildren = root.getChildren( );
		ivTreetable.setRoot( root );
		ivTreetable.setShowRoot( false );

		traces.stream( ).map( trace -> trace.getRootOperationCall( ) ).filter( ivPredicate )
				.forEach( call -> rootChildren.add( new LazyOperationCallTreeItem( call ) ) );

		ivCounter.textProperty( ).set( rootChildren.size( ) + " " + resources.getString( "TracesView.lblCounter.text" ) );
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
		private boolean ivShowJustFailureContainingButton;

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

		public boolean isShowJustFailureContainingButton( ) {
			return ivShowJustFailureContainingButton;
		}

		public void setShowJustFailureContainingButton( final boolean ivShowJustFailureContainingButton ) {
			this.ivShowJustFailureContainingButton = ivShowJustFailureContainingButton;
		}

	}

}
