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
import kieker.diagnosis.components.treetable.LazyAggregatedOperationCallTreeItem;
import kieker.diagnosis.domain.AggregatedOperationCall;
import kieker.diagnosis.domain.AggregatedTrace;
import kieker.diagnosis.gui.AbstractController;
import kieker.diagnosis.gui.main.MainController;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.util.Context;
import kieker.diagnosis.util.ContextKey;
import kieker.diagnosis.util.FilterUtility;
import kieker.diagnosis.util.NameConverter;

/**
 * @author Nils Christian Ehmke
 */
public final class AggregatedTracesController extends AbstractController<AggregatedTracesView> implements AggregatedTracesControllerIfc {

	private final SimpleObjectProperty<Optional<AggregatedOperationCall>> ivSelection = new SimpleObjectProperty<>( Optional.empty( ) );

	private Predicate<AggregatedOperationCall> ivPredicate = FilterUtility.alwaysTrue( );

	public AggregatedTracesController( final Context aContext ) {
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

		final DataModel dataModel = DataModel.getInstance( );
		dataModel.getAggregatedTraces( ).addListener( ( final Change<? extends AggregatedTrace> c ) -> reloadTreetable( ) );

		ivSelection.addListener( e -> updateDetailPanel( ) );
	}

	private void updateDetailPanel( ) {
		if ( ivSelection.get( ).isPresent( ) ) {
			final AggregatedOperationCall call = ivSelection.get( ).get( );
			final TimeUnit sourceTimeUnit = DataModel.getInstance( ).getTimeUnit( );
			final TimeUnit targetTimeUnit = PropertiesModel.getInstance( ).getTimeUnit( );

			getView( ).getContainer( ).setText( call.getContainer( ) );
			getView( ).getComponent( ).setText( call.getComponent( ) );
			getView( ).getOperation( ).setText( call.getOperation( ) );
			getView( ).getMinDuration( ).setText( NameConverter.toDurationString( call.getMinDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getMaxDuration( ).setText( NameConverter.toDurationString( call.getMaxDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getMedianDuration( ).setText( NameConverter.toDurationString( call.getMedianDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getTotalDuration( ).setText( NameConverter.toDurationString( call.getTotalDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getAvgDuration( ).setText( NameConverter.toDurationString( call.getMeanDuration( ), sourceTimeUnit, targetTimeUnit ) );
			getView( ).getCalls( ).setText( Integer.toString( call.getCalls( ) ) );
			getView( ).getTraceDepth( ).setText( Integer.toString( call.getStackDepth( ) ) );
			getView( ).getTraceSize( ).setText( Integer.toString( call.getStackSize( ) ) );
			getView( ).getFailed( ).setText( call.getFailedCause( ) != null ? call.getFailedCause( ) : "N/A" );
		}
		else {
			getView( ).getContainer( ).setText( "N/A" );
			getView( ).getComponent( ).setText( "N/A" );
			getView( ).getOperation( ).setText( "N/A" );
			getView( ).getMinDuration( ).setText( "N/A" );
			getView( ).getMaxDuration( ).setText( "N/A" );
			getView( ).getMedianDuration( ).setText( "N/A" );
			getView( ).getTotalDuration( ).setText( "N/A" );
			getView( ).getAvgDuration( ).setText( "N/A" );
			getView( ).getCalls( ).setText( "N/A" );
			getView( ).getTraceDepth( ).setText( "N/A" );
			getView( ).getTraceSize( ).setText( "N/A" );
			getView( ).getFailed( ).setText( "N/A" );
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
		final Predicate<AggregatedOperationCall> predicate1 = FilterUtility.useFilter( getView( ).getShowAllButton( ), getView( ).getShowJustSuccessful( ),
				getView( ).getShowJustFailedButton( ), getView( ).getShowJustFailureContainingButton( ), AggregatedOperationCall::isFailed,
				AggregatedOperationCall::containsFailure );
		final Predicate<AggregatedOperationCall> predicate2 = FilterUtility.useFilter( getView( ).getFilterContainer( ), AggregatedOperationCall::getContainer,
				PropertiesModel.getInstance( ).isSearchInEntireTrace( ) );
		final Predicate<AggregatedOperationCall> predicate3 = FilterUtility.useFilter( getView( ).getFilterComponent( ), AggregatedOperationCall::getComponent,
				PropertiesModel.getInstance( ).isSearchInEntireTrace( ) );
		final Predicate<AggregatedOperationCall> predicate4 = FilterUtility.useFilter( getView( ).getFilterOperation( ), AggregatedOperationCall::getOperation,
				PropertiesModel.getInstance( ).isSearchInEntireTrace( ) );
		final Predicate<AggregatedOperationCall> predicate5 = FilterUtility.useFilter( getView( ).getFilterException( ),
				(call -> call.isFailed( ) ? call.getFailedCause( ) : ""), PropertiesModel.getInstance( ).isSearchInEntireTrace( ) );

		ivPredicate = predicate1.and( predicate2 ).and( predicate3 ).and( predicate4 ).and( predicate5 );
		reloadTreetable( );
	}

	private void reloadTreetable( ) {
		ivSelection.set( Optional.empty( ) );

		final DataModel dataModel = DataModel.getInstance( );
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
		MainController.instance( ).saveAsFavorite( saveFilterContent( ), AggregatedTracesController.class );
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

	private class FilterContent {

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

		public boolean isShowJustFailureContainingButton( ) {
			return ivShowJustFailureContainingButton;
		}

		public void setShowJustFailureContainingButton( final boolean ivShowJustFailureContainingButton ) {
			this.ivShowJustFailureContainingButton = ivShowJustFailureContainingButton;
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

}
