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

package kieker.diagnosis.controller.calls;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Calendar;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import jfxtras.scene.control.CalendarTimeTextField;
import kieker.diagnosis.controller.AbstractController;
import kieker.diagnosis.controller.MainController;
import kieker.diagnosis.domain.AggregatedOperationCall;
import kieker.diagnosis.domain.OperationCall;
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
public final class CallsViewController extends AbstractController {

	private final SimpleObjectProperty<Optional<OperationCall>> ivSelection = new SimpleObjectProperty<>(Optional.empty());

	private FilteredList<OperationCall> ivFilteredData;

	@FXML private TableView<OperationCall> ivTable;

	@FXML private RadioButton ivShowAllButton;
	@FXML private RadioButton ivShowJustFailedButton;
	@FXML private RadioButton ivShowJustSuccessful;

	@FXML private TextField ivFilterContainer;
	@FXML private TextField ivFilterComponent;
	@FXML private TextField ivFilterOperation;
	@FXML private TextField ivFilterTraceID;
	@FXML private TextField ivFilterException;

	@FXML private DatePicker ivFilterLowerDate;
	@FXML private CalendarTimeTextField ivFilterLowerTime;
	@FXML private DatePicker ivFilterUpperDate;
	@FXML private CalendarTimeTextField ivFilterUpperTime;

	@FXML private TextField ivContainer;
	@FXML private TextField ivComponent;
	@FXML private TextField ivOperation;
	@FXML private TextField ivTimestamp;
	@FXML private TextField ivDuration;
	@FXML private TextField ivTraceID;
	@FXML private TextField ivFailed;

	@FXML private TextField ivCounter;

	@FXML private ResourceBundle resources;

	public CallsViewController(final Context aContext) {
		super(aContext);
	}

	@ErrorHandling
	public void initialize() {
		final DataModel dataModel = DataModel.getInstance();

		this.ivFilteredData = new FilteredList<>(dataModel.getOperationCalls());
		this.ivFilteredData.addListener((ListChangeListener<OperationCall>) change -> this.ivSelection.set(Optional.empty()));

		final SortedList<OperationCall> sortedData = new SortedList<>(this.ivFilteredData);
		sortedData.comparatorProperty().bind(this.ivTable.comparatorProperty());
		this.ivTable.setItems(sortedData);

		final Object filterContent = getContext().get(ContextKey.FILTER_CONTENT);
		if (filterContent instanceof FilterContent) {
			loadFilterContent((FilterContent) filterContent);
			useFilter();
		}
		
		this.ivSelection.addListener(e -> this.updateDetailPanel());

		this.ivCounter.textProperty().bind(Bindings.createStringBinding(() -> sortedData.size() + " " + this.resources.getString("CallsView.lbCounter.text"), sortedData));

		final Object call = super.getContext().get(ContextKey.AGGREGATED_OPERATION_CALL);
		if (call instanceof AggregatedOperationCall) {
			this.jumpToCalls((AggregatedOperationCall) call);
		}

	}
	
	@Override
	@ErrorHandling
	protected void reinitialize() {
		final Object call = super.getContext().get(ContextKey.AGGREGATED_OPERATION_CALL);
		if (call instanceof AggregatedOperationCall) {
			this.jumpToCalls((AggregatedOperationCall) call);
		}
		
		final Object filterContent = getContext().get(ContextKey.FILTER_CONTENT);
		if (filterContent instanceof FilterContent) {
			loadFilterContent((FilterContent) filterContent);
			useFilter();
		}
	}
	
	private void jumpToCalls(final AggregatedOperationCall aCall) {
		// Clear all filters (as the view might be cached)
		this.ivFilterLowerDate.setValue(null);
		this.ivFilterLowerTime.setCalendar(null);
		this.ivFilterUpperDate.setValue(null);
		this.ivFilterUpperTime.setCalendar(null);
		this.ivFilterTraceID.setText(null);
		this.ivFilterException.setText(null);
		this.ivShowAllButton.setSelected(true);
		
		// Now use the values from the given aggregated call for the filters
		this.ivFilterContainer.setText(aCall.getContainer());
		this.ivFilterComponent.setText(aCall.getComponent());
		this.ivFilterOperation.setText(aCall.getOperation());

		if (aCall.getFailedCause() != null) {
			this.ivFilterException.setText(aCall.getFailedCause());
		} else {
			this.ivShowJustSuccessful.setSelected(true);
		}

		this.useFilter();
	}

	private void updateDetailPanel() {
		if (this.ivSelection.get().isPresent()) {
			final OperationCall call = this.ivSelection.get().get();
			final TimeUnit sourceTimeUnit = DataModel.getInstance().getTimeUnit();
			final TimeUnit targetTimeUnit = PropertiesModel.getInstance().getTimeUnit();

			this.ivContainer.setText(call.getContainer());
			this.ivComponent.setText(call.getComponent());
			this.ivOperation.setText(call.getOperation());
			this.ivTimestamp.setText(NameConverter.toTimestampString(call.getTimestamp(), sourceTimeUnit) + " (" + call.getTimestamp() + ")");
			this.ivDuration.setText(NameConverter.toDurationString(call.getDuration(), sourceTimeUnit, targetTimeUnit));
			this.ivTraceID.setText(Long.toString(call.getTraceID()));
			this.ivFailed.setText(call.getFailedCause() != null ? call.getFailedCause() : "N/A");
		} else {
			this.ivContainer.setText("N/A");
			this.ivComponent.setText("N/A");
			this.ivOperation.setText("N/A");
			this.ivTimestamp.setText("N/A");
			this.ivDuration.setText("N/A");
			this.ivTraceID.setText("N/A");
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
			this.jumpToTrace();
		}
	}

	private void jumpToTrace() throws Exception {
		if (this.ivSelection.get().isPresent()) {
			final OperationCall call = this.ivSelection.get().get();
			MainController.instance().jumpToTrace(call);
		}
	}

	@ErrorHandling
	public void useFilter() {
		final Predicate<OperationCall> predicate1 = FilterUtility.useFilter(this.ivShowAllButton, this.ivShowJustSuccessful, this.ivShowJustFailedButton, OperationCall::isFailed);
		final Predicate<OperationCall> predicate2 = FilterUtility.useFilter(this.ivFilterContainer, OperationCall::getContainer);
		final Predicate<OperationCall> predicate3 = FilterUtility.useFilter(this.ivFilterComponent, OperationCall::getComponent);
		final Predicate<OperationCall> predicate4 = FilterUtility.useFilter(this.ivFilterOperation, OperationCall::getOperation);
		final Predicate<OperationCall> predicate5 = FilterUtility.useFilter(this.ivFilterTraceID, (call -> Long.toString(call.getTraceID())));
		final Predicate<OperationCall> predicate6 = FilterUtility.useFilter(this.ivFilterLowerDate, OperationCall::getTimestamp, true);
		final Predicate<OperationCall> predicate7 = FilterUtility.useFilter(this.ivFilterUpperDate, OperationCall::getTimestamp, false);
		final Predicate<OperationCall> predicate8 = FilterUtility.useFilter(this.ivFilterLowerTime, OperationCall::getTimestamp, true);
		final Predicate<OperationCall> predicate9 = FilterUtility.useFilter(this.ivFilterUpperTime, OperationCall::getTimestamp, false);
		final Predicate<OperationCall> predicate10 = FilterUtility.useFilter(this.ivFilterException, (call -> call.isFailed() ? call.getFailedCause() : ""));

		final Predicate<OperationCall> predicate = predicate1.and(predicate2).and(predicate3).and(predicate4).and(predicate5).and(predicate6).and(predicate7).and(predicate8)
				.and(predicate9).and(predicate10);
		this.ivFilteredData.setPredicate(predicate);
	}
	
	@ErrorHandling
	public void exportToCSV() throws IOException {
		MainController.instance().exportToCSV(new CallsCSVDataCollector());
	}
	
	@ErrorHandling
	public void saveAsFavorite() {
		MainController.instance().saveAsFavorite(saveFilterContent(), CallsViewController.class);
	}
	
	private FilterContent saveFilterContent() {
		final FilterContent filterContent = new FilterContent();
		
		filterContent.setFilterComponent(ivFilterComponent.getText());
		filterContent.setFilterContainer(ivFilterContainer.getText());
		filterContent.setFilterException(ivFilterException.getText());
		filterContent.setFilterOperation(ivFilterOperation.getText());
		filterContent.setFilterLowerDate(ivFilterLowerDate.getValue());
		filterContent.setFilterLowerTime(ivFilterLowerTime.getCalendar());
		filterContent.setFilterTraceID(ivFilterTraceID.getText());
		filterContent.setFilterUpperDate(ivFilterUpperDate.getValue());
		filterContent.setFilterUpperTime(ivFilterUpperTime.getCalendar());
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
		ivFilterTraceID.setText(aFilterContent.getFilterTraceID());
		ivFilterLowerDate.setValue(aFilterContent.getFilterLowerDate());
		ivFilterUpperDate.setValue(aFilterContent.getFilterUpperDate());
		ivFilterLowerTime.setCalendar(aFilterContent.getFilterLowerTime());
		ivFilterUpperTime.setCalendar(aFilterContent.getFilterUpperTime());
		ivShowAllButton.setSelected(aFilterContent.isShowAllButton());
		ivShowJustFailedButton.setSelected(aFilterContent.isShowJustFailedButton());
		ivShowJustSuccessful.setSelected(aFilterContent.isShowJustSuccessful());
	}
	
	private final class CallsCSVDataCollector implements CSVDataCollector {

		@Override
		public CSVData collectData() {
			final ObservableList<OperationCall> items = ivTable.getItems();
			final ObservableList<TableColumn<OperationCall,?>> columns = ivTable.getVisibleLeafColumns();
			
			final String[][] rows = new String[items.size()][columns.size()];
			final String[] header = new String[columns.size()];
			
			
			for (int i = 0; i < columns.size(); i++) {
				final TableColumn<OperationCall, ?> column = columns.get(i);
				
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
		
		public String getFilterComponent() {
			return ivFilterComponent;
		}
		
		public void setFilterComponent(String filterComponent) {
			this.ivFilterComponent = filterComponent;
		}
		
		public String getFilterContainer() {
			return ivFilterContainer;
		}
		
		public void setFilterContainer(String filterContainer) {
			this.ivFilterContainer = filterContainer;
		}
		
		public String getFilterException() {
			return ivFilterException;
		}
		
		public void setFilterException(String filterException) {
			this.ivFilterException = filterException;
		}
		
		public String getFilterOperation() {
			return ivFilterOperation;
		}
		
		public void setFilterOperation(String filterOperation) {
			this.ivFilterOperation = filterOperation;
		}
		
		public String getFilterTraceID() {
			return ivFilterTraceID;
		}
		
		public void setFilterTraceID(String filterTraceID) {
			this.ivFilterTraceID = filterTraceID;
		}
		
		public LocalDate getFilterLowerDate() {
			return ivFilterLowerDate;
		}
		
		public void setFilterLowerDate(LocalDate filterLowerDate) {
			this.ivFilterLowerDate = filterLowerDate;
		}
		
		public LocalDate getFilterUpperDate() {
			return ivFilterUpperDate;
		}
		
		public void setFilterUpperDate(LocalDate filterUpperDate) {
			this.ivFilterUpperDate = filterUpperDate;
		}
		
		public Calendar getFilterLowerTime() {
			return ivFilterLowerTime;
		}
		
		public void setFilterLowerTime(Calendar filterLowerTime) {
			this.ivFilterLowerTime = filterLowerTime;
		}
		
		public Calendar getFilterUpperTime() {
			return ivFilterUpperTime;
		}
		
		public void setFilterUpperTime(Calendar filterUpperTime) {
			this.ivFilterUpperTime = filterUpperTime;
		}
		
		public boolean isShowAllButton() {
			return ivShowAllButton;
		}
		
		public void setShowAllButton(boolean showAllButton) {
			this.ivShowAllButton = showAllButton;
		}
		
		public boolean isShowJustFailedButton() {
			return ivShowJustFailedButton;
		}
		
		public void setShowJustFailedButton(boolean showJustFailedButton) {
			this.ivShowJustFailedButton = showJustFailedButton;
		}
		
		public boolean isShowJustSuccessful() {
			return ivShowJustSuccessful;
		}
		
		public void setShowJustSuccessful(boolean showJustSuccessful) {
			this.ivShowJustSuccessful = showJustSuccessful;
		}
		
	}
	
}
