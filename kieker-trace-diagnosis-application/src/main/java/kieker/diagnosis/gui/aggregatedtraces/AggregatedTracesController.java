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

package kieker.diagnosis.gui.aggregatedtraces;

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
import kieker.diagnosis.gui.components.treetable.LazyAggregatedOperationCallTreeItem;
import kieker.diagnosis.gui.main.MainController;
import kieker.diagnosis.service.InjectService;
import kieker.diagnosis.service.data.DataService;
import kieker.diagnosis.service.data.domain.AggregatedOperationCall;
import kieker.diagnosis.service.data.domain.AggregatedTrace;
import kieker.diagnosis.service.filter.FilterService;
import kieker.diagnosis.service.nameconverter.NameConverterService;
import kieker.diagnosis.service.properties.PropertiesService;
import kieker.diagnosis.service.properties.SearchInEntireTraceProperty;
import kieker.diagnosis.service.properties.TimeUnitProperty;

/**
 * @author Nils Christian Ehmke
 */
public final class AggregatedTracesController extends AbstractController<AggregatedTracesView> implements AggregatedTracesControllerIfc {

	private final SimpleObjectProperty<Optional<AggregatedOperationCall>> ivSelection = new SimpleObjectProperty<>( Optional.empty( ) );

	private Predicate<AggregatedOperationCall> ivPredicate = x -> true;

	@InjectService
	private NameConverterService ivNameConverterService;

	@InjectService
	private PropertiesService ivPropertiesService;

	@InjectService
	private FilterService ivFilterService;

	@InjectService
	private DataService ivDataService;

	private MainController ivMainController;

	public AggregatedTracesController( final Context aContext ) {
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

		final DataService dataModel = ivDataService;
		dataModel.getAggregatedTraces( ).addListener( ( final Change<? extends AggregatedTrace> aChange ) -> reloadTreetable( ) );

		ivSelection.addListener( e -> updateDetailPanel( ) );
	}

	private void updateDetailPanel( ) {
		final String notAvailable = getView( ).getResourceBundle( ).getString( "notAvailable" );
		if ( ivSelection.get( ).isPresent( ) ) {
			final AggregatedOperationCall call = ivSelection.get( ).get( );
			final TimeUnit sourceTimeUnit = ivDataService.getTimeUnit( );
			final TimeUnit targetTimeUnit = ivPropertiesService.loadProperty( TimeUnitProperty.class );

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

	@Override
	public void selectCall( ) {
		final TreeItem<AggregatedOperationCall> selectedItem = getView( ).getTreetable( ).getSelectionModel( ).getSelectedItem( );
		if ( selectedItem != null ) {
			ivSelection.set( Optional.ofNullable( selectedItem.getValue( ) ) );
		}
	}

	@Override
	public void useFilter( ) {
		final boolean searchInEntireTrace = ivPropertiesService.loadPrimitiveProperty( SearchInEntireTraceProperty.class );

		final Predicate<AggregatedOperationCall> predicate1 = FilterService.useFilter( getView( ).getShowAllButton( ), getView( ).getShowJustSuccessful( ),
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

		getView( ).getCounter( ).textProperty( )
				.set( rootChildren.size( ) + " " + getView( ).getResourceBundle( ).getString( "AggregatedTracesView.lblCounter.text" ) );
	}

	@Override
	public void saveAsFavorite( ) {
		ivMainController.saveAsFavorite( saveFilterContent( ), AggregatedTracesController.class );
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
		filterContent.setShowJustFailureContainingButton( getView( ).getShowJustFailureContainingButton( ).isSelected( ) );

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
		getView( ).getShowJustFailureContainingButton( ).setSelected( aFilterContent.isShowJustFailureContainingButton( ) );
	}

	/**
	 * @author Nils Christian Ehmke
	 */
	private static class FilterContent {

		private boolean ivShowAllButton;
		private boolean ivShowJustSuccessful;
		private boolean ivShowJustFailedButton;
		private boolean ivShowJustFailureContainingButton;

		private String ivFilterContainer;
		private String ivFilterComponent;
		private String ivFilterOperation;
		private String ivFilterException;

		public boolean isShowAllButton( ) {
			return ivShowAllButton;
		}

		public void setShowAllButton( final boolean aShowAllButton ) {
			ivShowAllButton = aShowAllButton;
		}

		public boolean isShowJustSuccessful( ) {
			return ivShowJustSuccessful;
		}

		public void setShowJustSuccessful( final boolean aShowJustSuccessful ) {
			ivShowJustSuccessful = aShowJustSuccessful;
		}

		public boolean isShowJustFailedButton( ) {
			return ivShowJustFailedButton;
		}

		public void setShowJustFailedButton( final boolean aShowJustFailedButton ) {
			ivShowJustFailedButton = aShowJustFailedButton;
		}

		public boolean isShowJustFailureContainingButton( ) {
			return ivShowJustFailureContainingButton;
		}

		public void setShowJustFailureContainingButton( final boolean aShowJustFailureContainingButton ) {
			ivShowJustFailureContainingButton = aShowJustFailureContainingButton;
		}

		public String getFilterContainer( ) {
			return ivFilterContainer;
		}

		public void setFilterContainer( final String aFilterContainer ) {
			ivFilterContainer = aFilterContainer;
		}

		public String getFilterComponent( ) {
			return ivFilterComponent;
		}

		public void setFilterComponent( final String aFilterComponent ) {
			ivFilterComponent = aFilterComponent;
		}

		public String getFilterOperation( ) {
			return ivFilterOperation;
		}

		public void setFilterOperation( final String aFilterOperation ) {
			ivFilterOperation = aFilterOperation;
		}

		public String getFilterException( ) {
			return ivFilterException;
		}

		public void setFilterException( final String aFilterException ) {
			ivFilterException = aFilterException;
		}

	}

}
