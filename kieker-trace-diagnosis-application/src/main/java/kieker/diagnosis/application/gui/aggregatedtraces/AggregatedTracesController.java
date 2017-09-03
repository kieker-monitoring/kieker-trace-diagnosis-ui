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

package kieker.diagnosis.application.gui.aggregatedtraces;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import kieker.diagnosis.application.gui.components.treetable.LazyAggregatedOperationCallTreeItem;
import kieker.diagnosis.application.gui.main.MainController;
import kieker.diagnosis.application.service.data.DataService;
import kieker.diagnosis.application.service.data.domain.AggregatedOperationCall;
import kieker.diagnosis.application.service.data.domain.AggregatedTrace;
import kieker.diagnosis.application.service.filter.FilterService;
import kieker.diagnosis.application.service.nameconverter.NameConverterService;
import kieker.diagnosis.application.service.properties.SearchInEntireTraceProperty;
import kieker.diagnosis.application.service.properties.TimeUnitProperty;
import kieker.diagnosis.architecture.exception.BusinessException;
import kieker.diagnosis.architecture.gui.AbstractController;
import kieker.diagnosis.architecture.service.properties.PropertiesService;

/**
 * The controller for the aggregated traces.
 *
 * @author Nils Christian Ehmke
 */
@Component
public class AggregatedTracesController extends AbstractController<AggregatedTracesView> {

	private final SimpleObjectProperty<Optional<AggregatedOperationCall>> ivSelection = new SimpleObjectProperty<>( Optional.empty( ) );

	private Predicate<AggregatedOperationCall> ivPredicate = x -> true;

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
			reloadTreetable( );

			// If the data changes, we have to reload the whole treetable
			ivDataService.getAggregatedTraces( ).addListener( ( final Change<? extends AggregatedTrace> aChange ) -> reloadTreetable( ) );

			// If the current selection changes, we want to show information about the selection
			ivSelection.addListener( e -> updateDetailPanel( ) );
		}

		// If we get a filter as parameter, we have to update our view accordingly
		if ( aParameter.isPresent( ) && aParameter.get( ) instanceof AggregatedTracesFilter ) {
			final AggregatedTracesFilter aggregatedTracesFilter = (AggregatedTracesFilter) aParameter.get( );
			updateView( aggregatedTracesFilter );
			performUseFilter( );
		}
	}

	@Override
	public void doRefresh( ) {
		reloadTreetable( );
	}

	private void reloadTreetable( ) {
		ivSelection.set( Optional.empty( ) );

		final List<AggregatedTrace> traces = ivDataService.getAggregatedTraces( );
		final TreeItem<AggregatedOperationCall> root = new TreeItem<>( );
		final ObservableList<TreeItem<AggregatedOperationCall>> rootChildren = root.getChildren( );

		// The addAll-operation has to be performed sequentially or we will raise a concurrent modification exception.
		final List<LazyAggregatedOperationCallTreeItem> filteredChildren = traces.parallelStream( ).map( trace -> trace.getRootOperationCall( ) )
				.filter( ivPredicate ).map( call -> new LazyAggregatedOperationCallTreeItem( call ) ).collect( Collectors.toList( ) );
		rootChildren.addAll( filteredChildren );

		// Set the root after everything is prepared, or the whole GUI will be busy updating for each single element.
		getView( ).getTreetable( ).setRoot( root );
		getView( ).getTreetable( ).setShowRoot( false );
		getView( ).getCounter( ).textProperty( ).set( rootChildren.size( ) + " " + getResourceBundle( ).getString( "counter" ) );
	}

	private void updateDetailPanel( ) {
		// If we have a selection, we have to update the detail view with it. If we do not have a selection, we still have to update the detail view, because it
		// will show otherwise outdated information. The update method can handle a null value.
		final Optional<AggregatedOperationCall> selection = ivSelection.get( );
		updateView( selection.orElse( null ) );
	}

	public void performSelectCall( ) {
		final TreeItem<AggregatedOperationCall> selectedItem = getView( ).getTreetable( ).getSelectionModel( ).getSelectedItem( );
		if ( selectedItem != null ) {
			ivSelection.set( Optional.ofNullable( selectedItem.getValue( ) ) );
		}
	}

	public void performUseFilter( ) {
		final boolean searchInEntireTrace = ivPropertiesService.loadBooleanApplicationProperty( SearchInEntireTraceProperty.class );

		final Predicate<AggregatedOperationCall> predicate1 = ivFilterService.useFilter( getView( ).getShowAllButton( ), getView( ).getShowJustSuccessful( ),
				getView( ).getShowJustFailedButton( ), getView( ).getShowJustFailureContainingButton( ), AggregatedOperationCall::isFailed,
				AggregatedOperationCall::containsFailure );
		final Predicate<AggregatedOperationCall> predicate2 = ivFilterService.useFilter( getView( ).getFilterContainer( ),
				AggregatedOperationCall::getContainer, searchInEntireTrace );
		final Predicate<AggregatedOperationCall> predicate3 = ivFilterService.useFilter( getView( ).getFilterComponent( ),
				AggregatedOperationCall::getComponent, searchInEntireTrace );
		final Predicate<AggregatedOperationCall> predicate4 = ivFilterService.useFilter( getView( ).getFilterOperation( ),
				AggregatedOperationCall::getOperation, searchInEntireTrace );
		final Predicate<AggregatedOperationCall> predicate5 = ivFilterService.useFilter( getView( ).getFilterException( ),
				call -> call.isFailed( ) ? call.getFailedCause( ) : "", searchInEntireTrace );

		ivPredicate = predicate1.and( predicate2 ).and( predicate3 ).and( predicate4 ).and( predicate5 );
		reloadTreetable( );
	}

	public void performSaveAsFavorite( ) throws BusinessException {
		ivMainController.saveAsFavorite( saveView( new AggregatedTracesFilter( ) ), AggregatedTracesController.class );
	}

	private void updateView( final AggregatedOperationCall aCall ) {
		final String notAvailable = getResourceBundle( ).getString( "notAvailable" );

		if ( aCall != null ) {
			// If there is a call given, we update the fields with its content
			final TimeUnit sourceTimeUnit = ivDataService.getTimeUnit( );
			final TimeUnit targetTimeUnit = ivPropertiesService.loadApplicationProperty( TimeUnitProperty.class );

			getView( ).getContainer( ).setText( aCall.getContainer( ) );
			getView( ).getComponent( ).setText( aCall.getComponent( ) );
			getView( ).getOperation( ).setText( aCall.getOperation( ) );
			getView( ).getMinDuration( ).setText( ivNameConverterService.toDurationString( aCall.getMinDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getMaxDuration( ).setText( ivNameConverterService.toDurationString( aCall.getMaxDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getMedianDuration( ).setText( ivNameConverterService.toDurationString( aCall.getMedianDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getTotalDuration( ).setText( ivNameConverterService.toDurationString( aCall.getTotalDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getAvgDuration( ).setText( ivNameConverterService.toDurationString( aCall.getMeanDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getCalls( ).setText( Integer.toString( aCall.getCalls( ) ) );
			getView( ).getTraceDepth( ).setText( Integer.toString( aCall.getStackDepth( ) ) );
			getView( ).getTraceSize( ).setText( Integer.toString( aCall.getStackSize( ) ) );
			getView( ).getFailed( ).setText( aCall.getFailedCause( ) != null ? aCall.getFailedCause( ) : notAvailable );
		} else {
			// If there is no call given, we clear all fields
			getView( ).getContainer( ).setText( notAvailable );
			getView( ).getComponent( ).setText( notAvailable );
			getView( ).getOperation( ).setText( notAvailable );
			getView( ).getMinDuration( ).setText( notAvailable );
			getView( ).getMaxDuration( ).setText( notAvailable );
			getView( ).getMedianDuration( ).setText( notAvailable );
			getView( ).getTotalDuration( ).setText( notAvailable );
			getView( ).getAvgDuration( ).setText( notAvailable );
			getView( ).getCalls( ).setText( notAvailable );
			getView( ).getTraceDepth( ).setText( notAvailable );
			getView( ).getTraceSize( ).setText( notAvailable );
			getView( ).getFailed( ).setText( notAvailable );
		}
	}

	private void updateView( final AggregatedTracesFilter aFilterContent ) {
		getView( ).getFilterComponent( ).setText( aFilterContent.getFilterComponent( ) );
		getView( ).getFilterContainer( ).setText( aFilterContent.getFilterContainer( ) );
		getView( ).getFilterException( ).setText( aFilterContent.getFilterException( ) );
		getView( ).getFilterOperation( ).setText( aFilterContent.getFilterOperation( ) );
		getView( ).getShowAllButton( ).setSelected( aFilterContent.isShowAllButton( ) );
		getView( ).getShowJustFailedButton( ).setSelected( aFilterContent.isShowJustFailedButton( ) );
		getView( ).getShowJustSuccessful( ).setSelected( aFilterContent.isShowJustSuccessful( ) );
		getView( ).getShowJustFailureContainingButton( ).setSelected( aFilterContent.isShowJustFailureContainingButton( ) );
	}

	private AggregatedTracesFilter saveView( final AggregatedTracesFilter aFilterContent ) {
		aFilterContent.setFilterComponent( getView( ).getFilterComponent( ).getText( ) );
		aFilterContent.setFilterContainer( getView( ).getFilterContainer( ).getText( ) );
		aFilterContent.setFilterException( getView( ).getFilterException( ).getText( ) );
		aFilterContent.setFilterOperation( getView( ).getFilterOperation( ).getText( ) );
		aFilterContent.setShowAllButton( getView( ).getShowAllButton( ).isSelected( ) );
		aFilterContent.setShowJustFailedButton( getView( ).getShowJustFailedButton( ).isSelected( ) );
		aFilterContent.setShowJustSuccessful( getView( ).getShowJustSuccessful( ).isSelected( ) );
		aFilterContent.setShowJustFailureContainingButton( getView( ).getShowJustFailureContainingButton( ).isSelected( ) );

		return aFilterContent;
	}

}
