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

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import kieker.diagnosis.gui.AbstractController;
import kieker.diagnosis.gui.Context;
import kieker.diagnosis.gui.ContextKey;
import kieker.diagnosis.gui.components.treetable.LazyOperationCallTreeItem;
import kieker.diagnosis.gui.main.MainController;
import kieker.diagnosis.service.InjectService;
import kieker.diagnosis.service.data.DataService;
import kieker.diagnosis.service.data.domain.OperationCall;
import kieker.diagnosis.service.data.domain.Trace;
import kieker.diagnosis.service.filter.FilterService;
import kieker.diagnosis.service.nameconverter.NameConverterService;
import kieker.diagnosis.service.properties.PropertiesService;
import kieker.diagnosis.service.properties.SearchInEntireTraceProperty;
import kieker.diagnosis.service.properties.TimeUnitProperty;

/**
 * The sub-controller responsible for the sub-view presenting the available traces.
 *
 * @author Nils Christian Ehmke
 */
public final class TracesController extends AbstractController<TracesView> implements TracesControllerIfc {

	private final SimpleObjectProperty<Optional<OperationCall>> ivSelection = new SimpleObjectProperty<>( Optional.empty( ) );

	private Predicate<OperationCall> ivPredicate = x -> true;

	@InjectService
	private NameConverterService ivNameConverterService;

	@InjectService
	private PropertiesService ivPropertiesService;

	@InjectService
	private FilterService ivFilterService;

	@InjectService
	private DataService ivDataService;

	private MainController ivMainController;

	public TracesController( final Context aContext ) {
		super( aContext );
	}

	@Override
	public void doInitialize( ) {
		final Object filterContent = getContext( ).get( ContextKey.FILTER_CONTENT );
		if ( filterContent instanceof FilterContent ) {
			loadFilterContent( (FilterContent) filterContent );
			useFilter( );
		} else {
			reloadTreetable( );
		}

		final ObservableList<Trace> traces = ivDataService.getTraces( );
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
		final String notAvailable = getView( ).getResourceBundle( ).getString( "notAvailable" );

		if ( ivSelection.get( ).isPresent( ) ) {
			final OperationCall call = ivSelection.get( ).get( );
			final TimeUnit sourceTimeUnit = ivDataService.getTimeUnit( );
			final TimeUnit targetTimeUnit = ivPropertiesService.loadProperty( TimeUnitProperty.class );

			getView( ).getContainer( ).setText( call.getContainer( ) );
			getView( ).getComponent( ).setText( call.getComponent( ) );
			getView( ).getOperation( ).setText( call.getOperation( ) );
			getView( ).getTimestamp( )
					.setText( ivNameConverterService.toTimestampString( call.getTimestamp( ), sourceTimeUnit ) + " (" + call.getTimestamp( ) + ")" );
			getView( ).getDuration( ).setText( ivNameConverterService.toDurationString( call.getDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getTraceID( ).setText( Long.toString( call.getTraceID( ) ) );
			getView( ).getTraceDepth( ).setText( Integer.toString( call.getStackDepth( ) ) );
			getView( ).getTraceSize( ).setText( Integer.toString( call.getStackSize( ) ) );
			getView( ).getPercent( ).setText( call.getPercent( ) + " %" );
			getView( ).getFailed( ).setText( call.getFailedCause( ) != null ? call.getFailedCause( ) : notAvailable );
		} else {
			getView( ).getContainer( ).setText( notAvailable );
			getView( ).getComponent( ).setText( notAvailable );
			getView( ).getOperation( ).setText( notAvailable );
			getView( ).getTimestamp( ).setText( notAvailable );
			getView( ).getDuration( ).setText( notAvailable );
			getView( ).getTraceID( ).setText( notAvailable );
			getView( ).getPercent( ).setText( notAvailable );
			getView( ).getFailed( ).setText( notAvailable );
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
		final boolean searchInEntireTrace = ivPropertiesService.loadPrimitiveProperty( SearchInEntireTraceProperty.class );

		final Predicate<OperationCall> predicate1 = FilterService.useFilter( getView( ).getShowAllButton( ), getView( ).getShowJustSuccessful( ),
				getView( ).getShowJustFailedButton( ), getView( ).getShowJustFailureContainingButton( ), OperationCall::isFailed,
				OperationCall::containsFailure );
		final Predicate<OperationCall> predicate2 = ivFilterService.useFilter( getView( ).getFilterContainer( ), OperationCall::getContainer,
				searchInEntireTrace );
		final Predicate<OperationCall> predicate3 = ivFilterService.useFilter( getView( ).getFilterComponent( ), OperationCall::getComponent,
				searchInEntireTrace );
		final Predicate<OperationCall> predicate4 = ivFilterService.useFilter( getView( ).getFilterOperation( ), OperationCall::getOperation,
				searchInEntireTrace );
		final Predicate<OperationCall> predicate5 = ivFilterService.useFilter( getView( ).getFilterTraceID( ), call -> Long.toString( call.getTraceID( ) ),
				searchInEntireTrace );
		final Predicate<OperationCall> predicate6 = ivFilterService.useFilter( getView( ).getFilterLowerDate( ), OperationCall::getTimestamp, true,
				searchInEntireTrace );
		final Predicate<OperationCall> predicate7 = ivFilterService.useFilter( getView( ).getFilterUpperDate( ), OperationCall::getTimestamp, false,
				searchInEntireTrace );
		final Predicate<OperationCall> predicate8 = ivFilterService.useFilter( getView( ).getFilterLowerTime( ), OperationCall::getTimestamp, true,
				searchInEntireTrace );
		final Predicate<OperationCall> predicate9 = ivFilterService.useFilter( getView( ).getFilterUpperTime( ), OperationCall::getTimestamp, false,
				searchInEntireTrace );
		final Predicate<OperationCall> predicate10 = ivFilterService.useFilter( getView( ).getFilterException( ),
				call -> call.isFailed( ) ? call.getFailedCause( ) : "", searchInEntireTrace );

		ivPredicate = predicate1.and( predicate2 ).and( predicate3 ).and( predicate4 ).and( predicate5 ).and( predicate6 ).and( predicate7 ).and( predicate8 )
				.and( predicate9 ).and( predicate10 );
		reloadTreetable( );
	}

	@Override
	public void saveAsFavorite( ) {
		ivMainController.saveAsFavorite( saveFilterContent( ), TracesController.class );
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

		final List<Trace> traces = ivDataService.getTraces( );
		final TreeItem<OperationCall> root = new TreeItem<>( );
		final ObservableList<TreeItem<OperationCall>> rootChildren = root.getChildren( );
		getView( ).getTreetable( ).setRoot( root );
		getView( ).getTreetable( ).setShowRoot( false );

		traces.stream( ).map( trace -> trace.getRootOperationCall( ) ).filter( ivPredicate )
				.forEach( call -> rootChildren.add( new LazyOperationCallTreeItem( call ) ) );

		getView( ).getCounter( ).textProperty( ).set( rootChildren.size( ) + " " + getView( ).getResourceBundle( ).getString( "TracesView.lblCounter.text" ) );
	}

}
