/***************************************************************************
 * Copyright 2015-2017 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.application.gui.traces;

import kieker.diagnosis.application.gui.components.treetable.LazyOperationCallTreeItem;
import kieker.diagnosis.application.gui.main.MainController;
import kieker.diagnosis.application.service.data.DataService;
import kieker.diagnosis.application.service.data.domain.OperationCall;
import kieker.diagnosis.application.service.data.domain.Trace;
import kieker.diagnosis.application.service.filter.FilterService;
import kieker.diagnosis.application.service.nameconverter.NameConverterService;
import kieker.diagnosis.application.service.properties.MethodCallAggregationProperty;
import kieker.diagnosis.application.service.properties.PercentCalculationProperty;
import kieker.diagnosis.application.service.properties.SearchInEntireTraceProperty;
import kieker.diagnosis.application.service.properties.ShowUnmonitoredTimeProperty;
import kieker.diagnosis.application.service.properties.Threshold;
import kieker.diagnosis.application.service.properties.ThresholdProperty;
import kieker.diagnosis.application.service.properties.TimeUnitProperty;
import kieker.diagnosis.architecture.exception.BusinessException;
import kieker.diagnosis.architecture.gui.AbstractController;
import kieker.diagnosis.architecture.service.properties.PropertiesService;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The sub-controller responsible for the sub-view presenting the available traces.
 *
 * @author Nils Christian Ehmke
 */
@Component
public class TracesController extends AbstractController<TracesView> {

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

	private final SimpleObjectProperty<Optional<OperationCall>> ivSelection = new SimpleObjectProperty<>( Optional.empty( ) );

	private Predicate<OperationCall> ivPredicate = x -> true;

	@Override
	protected void doInitialize( final boolean aFirstInitialization, final Optional<?> aParameter ) {
		if ( aFirstInitialization ) {
			reloadTreetable( );

			final ObservableList<Trace> traces = ivDataService.getTraces( );
			traces.addListener( ( final Change<? extends Trace> aChange ) -> reloadTreetable( ) );

			ivSelection.addListener( e -> updateDetailPanel( ) );
		}

		if ( aParameter.isPresent( ) ) {
			final Object parameter = aParameter.get( );
			if ( parameter instanceof TracesFilter ) {
				final TracesFilter tracesFilter = (TracesFilter) parameter;
				loadFilterContent( tracesFilter );
				useFilter( );
			} else if ( parameter instanceof OperationCall ) {
				final OperationCall operationCall = (OperationCall) parameter;
				jumpToCall( operationCall );
			}
		}
	}

	@Override
	public void doRefresh( ) {
		reloadTreetable( );
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

	public void selectCall( ) {
		final TreeItem<OperationCall> selectedItem = getView( ).getTreetable( ).getSelectionModel( ).getSelectedItem( );
		if ( selectedItem != null ) {
			ivSelection.setValue( Optional.ofNullable( selectedItem.getValue( ) ) );
		}
	}

	public void useFilter( ) {
		final boolean searchInEntireTrace = ivPropertiesService.loadBooleanApplicationProperty( SearchInEntireTraceProperty.class );

		final Predicate<OperationCall> predicate1 = ivFilterService.useFilter( getView( ).getShowAllButton( ), getView( ).getShowJustSuccessful( ),
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

	public void saveAsFavorite( ) throws BusinessException {
		ivMainController.saveAsFavorite( saveFilterContent( ), TracesController.class );
	}

	private TracesFilter saveFilterContent( ) {
		final TracesFilter filterContent = new TracesFilter( );

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

	private void loadFilterContent( final TracesFilter aFilterContent ) {
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

		final boolean showUnmonitoredTime = ivPropertiesService.loadBooleanApplicationProperty( ShowUnmonitoredTimeProperty.class );
		final boolean percentCalculation = ivPropertiesService.loadBooleanApplicationProperty( PercentCalculationProperty.class );
		final boolean methodCallAggregation = ivPropertiesService.loadBooleanApplicationProperty( MethodCallAggregationProperty.class );
		final Threshold threshold = ivPropertiesService.loadApplicationProperty( ThresholdProperty.class );

		traces.stream( ).map( trace -> trace.getRootOperationCall( ) ).filter( ivPredicate ).forEach(
				call -> rootChildren.add( new LazyOperationCallTreeItem( call, showUnmonitoredTime, percentCalculation, methodCallAggregation, threshold ) ) );

		getView( ).getCounter( ).textProperty( ).set( rootChildren.size( ) + " " + getResourceBundle( ).getString( "TracesView.lblCounter.text" ) );
	}

}
