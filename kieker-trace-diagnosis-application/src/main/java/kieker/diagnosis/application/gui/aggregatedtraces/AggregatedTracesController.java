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

package kieker.diagnosis.application.gui.aggregatedtraces;

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

			final DataService dataModel = ivDataService;
			dataModel.getAggregatedTraces( ).addListener( ( final Change<? extends AggregatedTrace> aChange ) -> reloadTreetable( ) );

			ivSelection.addListener( e -> updateDetailPanel( ) );
		}

		if ( aParameter.isPresent( ) && ( aParameter.get( ) instanceof AggregatedTracesFilter ) ) {
			final AggregatedTracesFilter aggregatedTracesFilter = (AggregatedTracesFilter) aParameter.get( );
			loadFilterContent( aggregatedTracesFilter );
			useFilter( );
		}
	}

	@Override
	public void doRefresh( ) {
		reloadTreetable( );
	}

	private void updateDetailPanel( ) {
		final String notAvailable = getResourceBundle( ).getString( "notAvailable" );
		if ( ivSelection.get( ).isPresent( ) ) {
			final AggregatedOperationCall call = ivSelection.get( ).get( );
			final TimeUnit sourceTimeUnit = ivDataService.getTimeUnit( );
			final TimeUnit targetTimeUnit = ivPropertiesService.loadApplicationProperty( TimeUnitProperty.class );

			getView( ).getContainer( ).setText( call.getContainer( ) );
			getView( ).getComponent( ).setText( call.getComponent( ) );
			getView( ).getOperation( ).setText( call.getOperation( ) );
			getView( ).getMinDuration( ).setText( ivNameConverterService.toDurationString( call.getMinDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getMaxDuration( ).setText( ivNameConverterService.toDurationString( call.getMaxDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getMedianDuration( ).setText( ivNameConverterService.toDurationString( call.getMedianDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getTotalDuration( ).setText( ivNameConverterService.toDurationString( call.getTotalDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getAvgDuration( ).setText( ivNameConverterService.toDurationString( call.getMeanDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getCalls( ).setText( Integer.toString( call.getCalls( ) ) );
			getView( ).getTraceDepth( ).setText( Integer.toString( call.getStackDepth( ) ) );
			getView( ).getTraceSize( ).setText( Integer.toString( call.getStackSize( ) ) );
			getView( ).getFailed( ).setText( call.getFailedCause( ) != null ? call.getFailedCause( ) : notAvailable );
		} else {
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

	public void selectCall( ) {
		final TreeItem<AggregatedOperationCall> selectedItem = getView( ).getTreetable( ).getSelectionModel( ).getSelectedItem( );
		if ( selectedItem != null ) {
			ivSelection.set( Optional.ofNullable( selectedItem.getValue( ) ) );
		}
	}

	public void useFilter( ) {
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

	private void reloadTreetable( ) {
		ivSelection.set( Optional.empty( ) );

		final DataService dataModel = ivDataService;
		final List<AggregatedTrace> traces = dataModel.getAggregatedTraces( );
		final TreeItem<AggregatedOperationCall> root = new TreeItem<>( );
		final ObservableList<TreeItem<AggregatedOperationCall>> rootChildren = root.getChildren( );
		getView( ).getTreetable( ).setRoot( root );
		getView( ).getTreetable( ).setShowRoot( false );

		traces.stream( ).map( trace -> trace.getRootOperationCall( ) ).filter( ivPredicate )
				.forEach( call -> rootChildren.add( new LazyAggregatedOperationCallTreeItem( call ) ) );

		getView( ).getCounter( ).textProperty( ).set( rootChildren.size( ) + " " + getResourceBundle( ).getString( "AggregatedTracesView.lblCounter.text" ) );
	}

	public void saveAsFavorite( ) throws BusinessException {
		ivMainController.saveAsFavorite( saveFilterContent( ), AggregatedTracesController.class );
	}

	private AggregatedTracesFilter saveFilterContent( ) {
		final AggregatedTracesFilter filterContent = new AggregatedTracesFilter( );

		filterContent.setFilterComponent( getView( ).getFilterComponent( ).getText( ) );
		filterContent.setFilterContainer( getView( ).getFilterContainer( ).getText( ) );
		filterContent.setFilterException( getView( ).getFilterException( ).getText( ) );
		filterContent.setFilterOperation( getView( ).getFilterOperation( ).getText( ) );
		filterContent.setShowAllButton( getView( ).getShowAllButton( ).isSelected( ) );
		filterContent.setShowJustFailedButton( getView( ).getShowJustFailedButton( ).isSelected( ) );
		filterContent.setShowJustSuccessful( getView( ).getShowJustSuccessful( ).isSelected( ) );
		filterContent.setShowJustFailureContainingButton( getView( ).getShowJustFailureContainingButton( ).isSelected( ) );

		return filterContent;
	}

	private void loadFilterContent( final AggregatedTracesFilter aFilterContent ) {
		getView( ).getFilterComponent( ).setText( aFilterContent.getFilterComponent( ) );
		getView( ).getFilterContainer( ).setText( aFilterContent.getFilterContainer( ) );
		getView( ).getFilterException( ).setText( aFilterContent.getFilterException( ) );
		getView( ).getFilterOperation( ).setText( aFilterContent.getFilterOperation( ) );
		getView( ).getShowAllButton( ).setSelected( aFilterContent.isShowAllButton( ) );
		getView( ).getShowJustFailedButton( ).setSelected( aFilterContent.isShowJustFailedButton( ) );
		getView( ).getShowJustSuccessful( ).setSelected( aFilterContent.isShowJustSuccessful( ) );
		getView( ).getShowJustFailureContainingButton( ).setSelected( aFilterContent.isShowJustFailureContainingButton( ) );
	}

}
