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

package kieker.diagnosis.gui.traces;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import kieker.diagnosis.components.treetable.LazyOperationCallTreeItem;
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.domain.Trace;
import kieker.diagnosis.gui.AbstractController;
import kieker.diagnosis.gui.MainController;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.util.Context;
import kieker.diagnosis.util.ContextKey;
import kieker.diagnosis.util.FilterUtility;
import kieker.diagnosis.util.NameConverter;

/**
 * The sub-controller responsible for the sub-view presenting the available traces.
 *
 * @author Nils Christian Ehmke
 */
public final class TracesController extends AbstractController<TracesView> implements TracesControllerIfc {

	private final DataModel ivDataModel = DataModel.getInstance( );

	private final SimpleObjectProperty<Optional<OperationCall>> ivSelection = new SimpleObjectProperty<>( Optional.empty( ) );

	private Predicate<OperationCall> ivPredicate = FilterUtility.alwaysTrue( );

	public TracesController( final Context aContext ) {
		super( aContext );
	}

	@Override
	public void doInitialize( ) {
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
		getView( ).getFilterComponent( ).setText( null );
		getView( ).getFilterContainer( ).setText( null );
		getView( ).getFilterException( ).setText( null );
		getView( ).getFilterOperation( ).setText( null );
		getView( ).getFilterLowerDate( ).setValue( null );
		getView( ).getFilterLowerTime( ).setCalendar( null );
		getView( ).getFilterUpperDate( ).setValue( null );
		getView( ).getFilterUpperTime( ).setCalendar( null );
		getView( ).getFilterTraceID( ).setText( null );
		getView( ).getFilterException( ).setText( null );
		getView( ).getShowAllButton( ).setSelected( true );
		useFilter( );

		final TreeItem<OperationCall> root = getView( ).getTreetable( ).getRoot( );

		final Optional<TreeItem<OperationCall>> traceRoot = findTraceRoot( root, aCall );
		if ( traceRoot.isPresent( ) ) {
			final TreeItem<OperationCall> treeItem = findCall( traceRoot.get( ), aCall );
			if ( treeItem != null ) {
				getView( ).getTreetable( ).getSelectionModel( ).select( treeItem );
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

			getView( ).getContainer( ).setText( call.getContainer( ) );
			getView( ).getComponent( ).setText( call.getComponent( ) );
			getView( ).getOperation( ).setText( call.getOperation( ) );
			getView( ).getTimestamp( ).setText( NameConverter.toTimestampString( call.getTimestamp( ), sourceTimeUnit ) + " (" + call.getTimestamp( ) + ")" );
			getView( ).getDuration( ).setText( NameConverter.toDurationString( call.getDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getTraceID( ).setText( Long.toString( call.getTraceID( ) ) );
			getView( ).getTraceDepth( ).setText( Integer.toString( call.getStackDepth( ) ) );
			getView( ).getTraceSize( ).setText( Integer.toString( call.getStackSize( ) ) );
			getView( ).getPercent( ).setText( call.getPercent( ) + " %" );
			getView( ).getFailed( ).setText( call.getFailedCause( ) != null ? call.getFailedCause( ) : "N/A" );
		}
		else {
			getView( ).getContainer( ).setText( "N/A" );
			getView( ).getComponent( ).setText( "N/A" );
			getView( ).getOperation( ).setText( "N/A" );
			getView( ).getTimestamp( ).setText( "N/A" );
			getView( ).getDuration( ).setText( "N/A" );
			getView( ).getTraceID( ).setText( "N/A" );
			getView( ).getPercent( ).setText( "N/A" );
			getView( ).getFailed( ).setText( "N/A" );
		}
	}

	@Override
	public void selectCall( ) {
		final TreeItem<OperationCall> selectedItem = getView( ).getTreetable( ).getSelectionModel( ).getSelectedItem( );
		if ( selectedItem != null ) {
			ivSelection.setValue( Optional.ofNullable( selectedItem.getValue( ) ) );
		}
	}

	@Override
	public void useFilter( ) {
		final Predicate<OperationCall> predicate1 = FilterUtility.useFilter( getView( ).getShowAllButton( ), getView( ).getShowJustSuccessful( ),
				getView( ).getShowJustFailedButton( ), getView( ).getShowJustFailureContainingButton( ), OperationCall::isFailed,
				OperationCall::containsFailure );
		final Predicate<OperationCall> predicate2 = FilterUtility.useFilter( getView( ).getFilterContainer( ), OperationCall::getContainer,
				PropertiesModel.getInstance( ).isSearchInEntireTrace( ) );
		final Predicate<OperationCall> predicate3 = FilterUtility.useFilter( getView( ).getFilterComponent( ), OperationCall::getComponent,
				PropertiesModel.getInstance( ).isSearchInEntireTrace( ) );
		final Predicate<OperationCall> predicate4 = FilterUtility.useFilter( getView( ).getFilterOperation( ), OperationCall::getOperation,
				PropertiesModel.getInstance( ).isSearchInEntireTrace( ) );
		final Predicate<OperationCall> predicate5 = FilterUtility.useFilter( getView( ).getFilterTraceID( ), (call -> Long.toString( call.getTraceID( ) )),
				PropertiesModel.getInstance( ).isSearchInEntireTrace( ) );
		final Predicate<OperationCall> predicate6 = FilterUtility.useFilter( getView( ).getFilterLowerDate( ), OperationCall::getTimestamp, true,
				PropertiesModel.getInstance( ).isSearchInEntireTrace( ) );
		final Predicate<OperationCall> predicate7 = FilterUtility.useFilter( getView( ).getFilterUpperDate( ), OperationCall::getTimestamp, false,
				PropertiesModel.getInstance( ).isSearchInEntireTrace( ) );
		final Predicate<OperationCall> predicate8 = FilterUtility.useFilter( getView( ).getFilterLowerTime( ), OperationCall::getTimestamp, true,
				PropertiesModel.getInstance( ).isSearchInEntireTrace( ) );
		final Predicate<OperationCall> predicate9 = FilterUtility.useFilter( getView( ).getFilterUpperTime( ), OperationCall::getTimestamp, false,
				PropertiesModel.getInstance( ).isSearchInEntireTrace( ) );
		final Predicate<OperationCall> predicate10 = FilterUtility.useFilter( getView( ).getFilterException( ),
				(call -> call.isFailed( ) ? call.getFailedCause( ) : ""), PropertiesModel.getInstance( ).isSearchInEntireTrace( ) );

		ivPredicate = predicate1.and( predicate2 ).and( predicate3 ).and( predicate4 ).and( predicate5 ).and( predicate6 ).and( predicate7 ).and( predicate8 )
				.and( predicate9 ).and( predicate10 );
		reloadTreetable( );
	}

	@Override
	public void saveAsFavorite( ) {
		MainController.instance( ).saveAsFavorite( saveFilterContent( ), TracesController.class );
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
		filterContent.setShowJustFailureContainingButton( getView( ).getShowJustFailureContainingButton( ).isSelected( ) );

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
		getView( ).getShowJustFailureContainingButton( ).setSelected( aFilterContent.isShowJustFailureContainingButton( ) );
	}

	private void reloadTreetable( ) {
		ivSelection.set( Optional.empty( ) );

		final List<Trace> traces = ivDataModel.getTraces( );
		final TreeItem<OperationCall> root = new TreeItem<>( );
		final ObservableList<TreeItem<OperationCall>> rootChildren = root.getChildren( );
		getView( ).getTreetable( ).setRoot( root );
		getView( ).getTreetable( ).setShowRoot( false );

		traces.stream( ).map( trace -> trace.getRootOperationCall( ) ).filter( ivPredicate )
				.forEach( call -> rootChildren.add( new LazyOperationCallTreeItem( call ) ) );

		getView( ).getCounter( ).textProperty( ).set( rootChildren.size( ) + " " + getView( ).getResourceBundle( ).getString( "TracesView.lblCounter.text" ) );
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
