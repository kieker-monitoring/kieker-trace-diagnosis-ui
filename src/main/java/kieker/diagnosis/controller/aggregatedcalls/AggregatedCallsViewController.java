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

package kieker.diagnosis.controller.aggregatedcalls;

import java.io.IOException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import kieker.diagnosis.controller.AbstractController;
import kieker.diagnosis.controller.MainController;
import kieker.diagnosis.domain.AggregatedOperationCall;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.util.CSVData;
import kieker.diagnosis.util.CSVDataCollector;
import kieker.diagnosis.util.Context;
import kieker.diagnosis.util.ContextKey;
import kieker.diagnosis.util.ErrorHandling;
import kieker.diagnosis.util.FilterUtility;
import kieker.diagnosis.util.NameConverter;

/**
 * @author Nils Christian Ehmke
 */
public final class AggregatedCallsViewController extends AbstractController {

	private FilteredList<AggregatedOperationCall> ivFilteredData;

	private final SimpleObjectProperty<Optional<AggregatedOperationCall>> ivSelection = new SimpleObjectProperty<>(Optional.empty());

	@FXML private TableView<AggregatedOperationCall> ivTable;

	@FXML private RadioButton ivShowAllButton;
	@FXML private RadioButton ivShowJustFailedButton;
	@FXML private RadioButton ivShowJustSuccessful;

	@FXML private TextField ivFilterContainer;
	@FXML private TextField ivFilterComponent;
	@FXML private TextField ivFilterOperation;
	@FXML private TextField ivFilterException;

	@FXML private TextField ivMinimalDuration;
	@FXML private TextField ivMaximalDuration;
	@FXML private TextField ivMedianDuration;
	@FXML private TextField ivTotalDuration;
	@FXML private TextField ivMeanDuration;
	@FXML private TextField ivContainer;
	@FXML private TextField ivComponent;
	@FXML private TextField ivOperation;
	@FXML private TextField ivFailed;
	@FXML private TextField ivCalls;

	@FXML private TextField ivCounter;

	@FXML private ResourceBundle resources;

	public AggregatedCallsViewController(final Context aContext) {
		super(aContext);
	}

	@ErrorHandling
	public void initialize() {
		final DataModel dataModel = DataModel.getInstance();

		this.ivFilteredData = new FilteredList<>(dataModel.getAggregatedOperationCalls());
		this.ivFilteredData.addListener((ListChangeListener<AggregatedOperationCall>) change -> this.ivSelection.set(Optional.empty()));

		final SortedList<AggregatedOperationCall> sortedData = new SortedList<>(this.ivFilteredData);
		sortedData.comparatorProperty().bind(this.ivTable.comparatorProperty());
		this.ivTable.setItems(sortedData);

		final Object filterContent = getContext().get(ContextKey.FILTER_CONTENT);
		if (filterContent instanceof FilterContent) {
			loadFilterContent((FilterContent) filterContent);
			useFilter();
		}
		
		this.ivSelection.addListener(e -> this.updateDetailPanel());

		this.ivCounter.textProperty().bind(Bindings.createStringBinding(() -> sortedData.size() + " " + this.resources.getString("AggregatedCallsView.lblCounter.text"), sortedData));
	}
	
	@Override
	protected void reinitialize() {
		final Object filterContent = getContext().get(ContextKey.FILTER_CONTENT);
		if (filterContent instanceof FilterContent) {
			loadFilterContent((FilterContent) filterContent);
			useFilter();
		}
	}
	
	private void updateDetailPanel() {
		if (this.ivSelection.get().isPresent()) {
			final AggregatedOperationCall call = this.ivSelection.get().get();
			final TimeUnit sourceTimeUnit = DataModel.getInstance().getTimeUnit();
			final TimeUnit targetTimeUnit = PropertiesModel.getInstance().getTimeUnit();

			this.ivContainer.setText(call.getContainer());
			this.ivComponent.setText(call.getComponent());
			this.ivOperation.setText(call.getOperation());
			this.ivMinimalDuration.setText(NameConverter.toDurationString(call.getMinDuration(), sourceTimeUnit, targetTimeUnit));
			this.ivMaximalDuration.setText(NameConverter.toDurationString(call.getMaxDuration(), sourceTimeUnit, targetTimeUnit));
			this.ivMedianDuration.setText(NameConverter.toDurationString(call.getMedianDuration(), sourceTimeUnit, targetTimeUnit));
			this.ivTotalDuration.setText(NameConverter.toDurationString(call.getTotalDuration(), sourceTimeUnit, targetTimeUnit));
			this.ivMeanDuration.setText(NameConverter.toDurationString(call.getMeanDuration(), sourceTimeUnit, targetTimeUnit));
			this.ivCalls.setText(Integer.toString(call.getCalls()));
			this.ivFailed.setText(call.getFailedCause() != null ? call.getFailedCause() : "N/A");
		} else {
			this.ivContainer.setText("N/A");
			this.ivComponent.setText("N/A");
			this.ivOperation.setText("N/A");
			this.ivMinimalDuration.setText("N/A");
			this.ivMaximalDuration.setText("N/A");
			this.ivMedianDuration.setText("N/A");
			this.ivTotalDuration.setText("N/A");
			this.ivMeanDuration.setText("N/A");
			this.ivCalls.setText("N/A");
			this.ivFailed.setText("N/A");
		}
	}

	@ErrorHandling
	public void selectCall(final InputEvent aEvent) throws Exception {
		final int clicked;
		if (aEvent instanceof MouseEvent) {
			clicked = ((MouseEvent) aEvent).getClickCount();
		} else {
			clicked = 1;
		}

		if (clicked == 1) {
			this.ivSelection.set(Optional.ofNullable(this.ivTable.getSelectionModel().getSelectedItem()));
		} else if (clicked == 2) {
			this.jumpToCalls();
		}
	}

	private void jumpToCalls() throws Exception {
		if (this.ivSelection.get().isPresent()) {
			final AggregatedOperationCall call = this.ivSelection.get().get();
			MainController.instance().jumpToCalls(call);
		}
	}

	@ErrorHandling
	public void useFilter() {
		final Predicate<AggregatedOperationCall> predicate1 = FilterUtility.useFilter(this.ivShowAllButton, this.ivShowJustSuccessful, this.ivShowJustFailedButton,
				AggregatedOperationCall::isFailed);
		final Predicate<AggregatedOperationCall> predicate2 = FilterUtility.useFilter(this.ivFilterContainer, AggregatedOperationCall::getContainer);
		final Predicate<AggregatedOperationCall> predicate3 = FilterUtility.useFilter(this.ivFilterComponent, AggregatedOperationCall::getComponent);
		final Predicate<AggregatedOperationCall> predicate4 = FilterUtility.useFilter(this.ivFilterOperation, AggregatedOperationCall::getOperation);
		final Predicate<AggregatedOperationCall> predicate5 = FilterUtility.useFilter(this.ivFilterException, (call -> call.isFailed() ? call.getFailedCause() : ""));

		final Predicate<AggregatedOperationCall> predicate = predicate1.and(predicate2).and(predicate3).and(predicate4).and(predicate5);
		this.ivFilteredData.setPredicate(predicate);
	}
	
	@ErrorHandling
	public void exportToCSV() throws IOException {
		MainController.instance().exportToCSV(new AggregatedCallsCSVDataCollector());
	}
	
	@ErrorHandling
	public void saveAsFavorite() {
		MainController.instance().saveAsFavorite(saveFilterContent(), AggregatedCallsViewController.class);
	}
	
	private FilterContent saveFilterContent() {
		final FilterContent filterContent = new FilterContent();
		
		filterContent.setFilterComponent(ivFilterComponent.getText());
		filterContent.setFilterContainer(ivFilterContainer.getText());
		filterContent.setFilterException(ivFilterException.getText());
		filterContent.setFilterOperation(ivFilterOperation.getText());
		filterContent.setShowAllButton(ivShowAllButton.isSelected());
		filterContent.setShowJustFailedButton(ivShowJustFailedButton.isSelected());
		filterContent.setShowJustSuccessful(ivShowJustSuccessful.isSelected());
		
		return filterContent;
	}
	
	private void loadFilterContent(final FilterContent aFilterContent) {
		ivFilterComponent.setText(aFilterContent.getFilterComponent());
		ivFilterContainer.setText(aFilterContent.getFilterContainer());
		ivFilterException.setText(aFilterContent.getFilterException());
		ivFilterOperation.setText(aFilterContent.getFilterOperation());
		ivShowAllButton.setSelected(aFilterContent.isShowAllButton());
		ivShowJustFailedButton.setSelected(aFilterContent.isShowJustFailedButton());
		ivShowJustSuccessful.setSelected(aFilterContent.isShowJustSuccessful());
	}
	
	private class FilterContent {
		
		private boolean ivShowAllButton;
		private boolean ivShowJustSuccessful;
		private boolean ivShowJustFailedButton;

		private String ivFilterContainer;
		private String ivFilterComponent;
		private String ivFilterOperation;
		private String ivFilterException;
		
		public boolean isShowAllButton() {
			return ivShowAllButton;
		}
		
		public void setShowAllButton(boolean showAllButton) {
			this.ivShowAllButton = showAllButton;
		}
		
		public boolean isShowJustSuccessful() {
			return ivShowJustSuccessful;
		}
		
		public void setShowJustSuccessful(boolean showJustSuccessful) {
			this.ivShowJustSuccessful = showJustSuccessful;
		}
		
		public boolean isShowJustFailedButton() {
			return ivShowJustFailedButton;
		}
		
		public void setShowJustFailedButton(boolean showJustFailedButton) {
			this.ivShowJustFailedButton = showJustFailedButton;
		}
	
		public String getFilterContainer() {
			return ivFilterContainer;
		}
		
		public void setFilterContainer(String filterContainer) {
			this.ivFilterContainer = filterContainer;
		}
		
		public String getFilterComponent() {
			return ivFilterComponent;
		}
		
		public void setFilterComponent(String filterComponent) {
			this.ivFilterComponent = filterComponent;
		}
		
		public String getFilterOperation() {
			return ivFilterOperation;
		}
		
		public void setFilterOperation(String filterOperation) {
			this.ivFilterOperation = filterOperation;
		}
		
		public String getFilterException() {
			return ivFilterException;
		}
		
		public void setFilterException(String filterException) {
			this.ivFilterException = filterException;
		}
		
	}

	private final class AggregatedCallsCSVDataCollector implements CSVDataCollector {

		@Override
		public CSVData collectData() {
			final ObservableList<AggregatedOperationCall> items = ivTable.getItems();
			final ObservableList<TableColumn<AggregatedOperationCall,?>> columns = ivTable.getVisibleLeafColumns();
			
			final String[][] rows = new String[items.size()][columns.size()];
			final String[] header = new String[columns.size()];
			
			
			for (int i = 0; i < columns.size(); i++) {
				final TableColumn<AggregatedOperationCall, ?> column = columns.get(i);
				
				header[i] = column.getText();
				
				for (int j = 0; j < items.size(); j++) {
					final Object cellData = column.getCellData(j);
					rows[j][i] = cellData != null ? cellData.toString() : null;
				}
			}
		
			final CSVData result = new CSVData();	
			result.setHeader(header);
			result.setRows(rows);
			return result; 
		}

	}
	
}
