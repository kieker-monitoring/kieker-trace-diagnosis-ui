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

package kieker.diagnosis.controller.aggregatedtraces;

import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import kieker.diagnosis.components.treetable.LazyAggregatedOperationCallTreeItem;
import kieker.diagnosis.controller.AbstractController;
import kieker.diagnosis.controller.MainController;
import kieker.diagnosis.domain.AggregatedOperationCall;
import kieker.diagnosis.domain.AggregatedTrace;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.util.Context;
import kieker.diagnosis.util.ContextKey;
import kieker.diagnosis.util.ErrorHandling;
import kieker.diagnosis.util.FilterUtility;
import kieker.diagnosis.util.NameConverter;

/**
 * @author Nils Christian Ehmke
 */
public final class AggregatedTracesViewController extends AbstractController {

	private final SimpleObjectProperty<Optional<AggregatedOperationCall>> ivSelection = new SimpleObjectProperty<>( Optional.empty( ) );

	@FXML
	private TreeTableView<AggregatedOperationCall> ivTreetable;

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
	private TextField ivFilterException;

	@FXML
	private TextField ivMedianDuration;
	@FXML
	private TextField ivTotalDuration;
	@FXML
	private TextField ivMinDuration;
	@FXML
	private TextField ivAvgDuration;
	@FXML
	private TextField ivMaxDuration;
	@FXML
	private TextField ivTraceDepth;
	@FXML
	private TextField ivTraceSize;
	@FXML
	private TextField ivContainer;
	@FXML
	private TextField ivComponent;
	@FXML
	private TextField ivOperation;
	@FXML
	private TextField ivFailed;
	@FXML
	private TextField ivCalls;

	@FXML
	private TextField ivCounter;

	@FXML
	private ResourceBundle resources;

	private Predicate<AggregatedOperationCall> ivPredicate = FilterUtility.alwaysTrue( );

	public AggregatedTracesViewController( final Context aContext ) {
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

		final DataModel dataModel = DataModel.getInstance( );
		dataModel.getAggregatedTraces( ).addListener( ( final Change<? extends AggregatedTrace> c ) -> reloadTreetable( ) );

		ivSelection.addListener( e -> updateDetailPanel( ) );
	}

	private void updateDetailPanel( ) {
		if ( ivSelection.get( ).isPresent( ) ) {
			final AggregatedOperationCall call = ivSelection.get( ).get( );
			final TimeUnit sourceTimeUnit = DataModel.getInstance( ).getTimeUnit( );
			final TimeUnit targetTimeUnit = PropertiesModel.getInstance( ).getTimeUnit( );

			ivContainer.setText( call.getContainer( ) );
			ivComponent.setText( call.getComponent( ) );
			ivOperation.setText( call.getOperation( ) );
			ivMinDuration.setText( NameConverter.toDurationString( call.getMinDuration( ), sourceTimeUnit, targetTimeUnit ) );
			ivMaxDuration.setText( NameConverter.toDurationString( call.getMaxDuration( ), sourceTimeUnit, targetTimeUnit ) );
			ivMedianDuration.setText( NameConverter.toDurationString( call.getMedianDuration( ), sourceTimeUnit, targetTimeUnit ) );
			ivTotalDuration.setText( NameConverter.toDurationString( call.getTotalDuration( ), sourceTimeUnit, targetTimeUnit ) );
			ivAvgDuration.setText( NameConverter.toDurationString( call.getMeanDuration( ), sourceTimeUnit, targetTimeUnit ) );
			ivCalls.setText( Integer.toString( call.getCalls( ) ) );
			ivTraceDepth.setText( Integer.toString( call.getStackDepth( ) ) );
			ivTraceSize.setText( Integer.toString( call.getStackSize( ) ) );
			ivFailed.setText( call.getFailedCause( ) != null ? call.getFailedCause( ) : "N/A" );
		}
		else {
			ivContainer.setText( "N/A" );
			ivComponent.setText( "N/A" );
			ivOperation.setText( "N/A" );
			ivMinDuration.setText( "N/A" );
			ivMaxDuration.setText( "N/A" );
			ivMedianDuration.setText( "N/A" );
			ivTotalDuration.setText( "N/A" );
			ivAvgDuration.setText( "N/A" );
			ivCalls.setText( "N/A" );
			ivTraceDepth.setText( "N/A" );
			ivTraceSize.setText( "N/A" );
			ivFailed.setText( "N/A" );
		}
	}

	@ErrorHandling
	public void selectCall( ) {
		final TreeItem<AggregatedOperationCall> selectedItem = ivTreetable.getSelectionModel( ).getSelectedItem( );
		if ( selectedItem != null ) {
			ivSelection.set( Optional.ofNullable( selectedItem.getValue( ) ) );
		}
	}

	@ErrorHandling
	public void useFilter( ) {
		final Predicate<AggregatedOperationCall> predicate1 = FilterUtility.useFilter( ivShowAllButton, ivShowJustSuccessful, ivShowJustFailedButton,
				ivShowJustFailureContainingButton, AggregatedOperationCall::isFailed, AggregatedOperationCall::containsFailure );
		final Predicate<AggregatedOperationCall> predicate2 = FilterUtility.useFilter( ivFilterContainer, AggregatedOperationCall::getContainer,
				PropertiesModel.getInstance( ).isSearchInEntireTrace( ) );
		final Predicate<AggregatedOperationCall> predicate3 = FilterUtility.useFilter( ivFilterComponent, AggregatedOperationCall::getComponent,
				PropertiesModel.getInstance( ).isSearchInEntireTrace( ) );
		final Predicate<AggregatedOperationCall> predicate4 = FilterUtility.useFilter( ivFilterOperation, AggregatedOperationCall::getOperation,
				PropertiesModel.getInstance( ).isSearchInEntireTrace( ) );
		final Predicate<AggregatedOperationCall> predicate5 = FilterUtility.useFilter( ivFilterException,
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
		ivTreetable.setRoot( root );
		ivTreetable.setShowRoot( false );

		traces.stream( ).map( trace -> trace.getRootOperationCall( ) ).filter( ivPredicate )
				.forEach( call -> rootChildren.add( new LazyAggregatedOperationCallTreeItem( call ) ) );

		ivCounter.textProperty( ).set( rootChildren.size( ) + " " + resources.getString( "AggregatedTracesView.lblCounter.text" ) );
	}

	@ErrorHandling
	public void saveAsFavorite( ) {
		MainController.instance( ).saveAsFavorite( saveFilterContent( ), AggregatedTracesViewController.class );
	}

	private FilterContent saveFilterContent( ) {
		final FilterContent filterContent = new FilterContent( );

		filterContent.setFilterComponent( ivFilterComponent.getText( ) );
		filterContent.setFilterContainer( ivFilterContainer.getText( ) );
		filterContent.setFilterException( ivFilterException.getText( ) );
		filterContent.setFilterOperation( ivFilterOperation.getText( ) );
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
		ivShowAllButton.setSelected( aFilterContent.isShowAllButton( ) );
		ivShowJustFailedButton.setSelected( aFilterContent.isShowJustFailedButton( ) );
		ivShowJustSuccessful.setSelected( aFilterContent.isShowJustSuccessful( ) );
		ivShowJustFailureContainingButton.setSelected( aFilterContent.isShowJustFailureContainingButton( ) );
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
