/***************************************************************************
 * Copyright 2015 Kieker Project (http://kieker-monitoring.net)
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

import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import kieker.diagnosis.controller.AbstractController;
import kieker.diagnosis.controller.MainController;
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.util.Context;
import kieker.diagnosis.util.ErrorHandling;
import kieker.diagnosis.util.FilterUtility;
import kieker.diagnosis.util.NameConverter;

/**
 * @author Nils Christian Ehmke
 */
public final class CallsViewController extends AbstractController {

	private final SimpleObjectProperty<Optional<OperationCall>> selection = new SimpleObjectProperty<>(Optional.empty());

	private FilteredList<OperationCall> filteredData;

	@FXML private TableView<OperationCall> table;

	@FXML private RadioButton showAllButton;
	@FXML private RadioButton showJustFailedButton;

	@FXML private TextField filterContainer;
	@FXML private TextField filterComponent;
	@FXML private TextField filterOperation;
	@FXML private TextField filterTraceID;

	@FXML private TextField container;
	@FXML private TextField component;
	@FXML private TextField operation;
	@FXML private TextField timestamp;
	@FXML private TextField duration;
	@FXML private TextField traceID;
	@FXML private TextField failed;

	@FXML private TextField counter;

	@FXML private ResourceBundle resources;

	public CallsViewController(final Context context) {
		super(context);
	}

	@ErrorHandling
	public void initialize() {
		final DataModel dataModel = DataModel.getInstance();

		this.filteredData = new FilteredList<>(dataModel.getOperationCalls());
		this.filteredData.addListener((ListChangeListener<OperationCall>) change -> this.selection.set(Optional.empty()));

		final SortedList<OperationCall> sortedData = new SortedList<>(this.filteredData);
		sortedData.comparatorProperty().bind(this.table.comparatorProperty());
		this.table.setItems(sortedData);

		this.selection.addListener(e -> this.updateDetailPanel());

		this.counter.textProperty().bind(Bindings.createStringBinding(() -> sortedData.size() + " " + this.resources.getString("CallsView.lbCounter.text"), sortedData));
	}

	private void updateDetailPanel() {
		if (this.selection.get().isPresent()) {
			final OperationCall call = this.selection.get().get();
			final TimeUnit sourceTimeUnit = DataModel.getInstance().getTimeUnit();
			final TimeUnit targetTimeUnit = PropertiesModel.getInstance().getTimeUnit();

			this.container.setText(call.getContainer());
			this.component.setText(call.getComponent());
			this.operation.setText(call.getOperation());
			this.timestamp.setText(Long.toString(call.getTimestamp()));
			this.duration.setText(NameConverter.toDurationString(call.getDuration(), sourceTimeUnit, targetTimeUnit));
			this.traceID.setText(Long.toString(call.getTraceID()));
			this.failed.setText(call.getFailedCause() != null ? call.getFailedCause() : "N/A");
		} else {
			this.container.setText("N/A");
			this.component.setText("N/A");
			this.operation.setText("N/A");
			this.timestamp.setText("N/A");
			this.duration.setText("N/A");
			this.traceID.setText("N/A");
			this.failed.setText("N/A");
		}
	}

	@ErrorHandling
	public void selectCall(final MouseEvent event) throws Exception {
		final int clicked = event.getClickCount();

		if (clicked == 1) {
			this.selection.set(Optional.ofNullable(this.table.getSelectionModel().getSelectedItem()));
		} else if (clicked == 2) {
			jumpToTrace();
		}
	}

	private void jumpToTrace() throws Exception {
		if (this.selection.get().isPresent()) {
			final OperationCall call = this.selection.get().get();
			MainController.instance().jumpToTrace(call);
		}
	}

	@ErrorHandling
	public void useFilter() {
		final Predicate<OperationCall> predicate1 = (showAllButton.isSelected()) ? FilterUtility.alwaysTrue() : OperationCall::isFailed;
		final Predicate<OperationCall> predicate2 = FilterUtility.useFilter(this.filterContainer, OperationCall::getContainer);
		final Predicate<OperationCall> predicate3 = FilterUtility.useFilter(this.filterComponent, OperationCall::getComponent);
		final Predicate<OperationCall> predicate4 = FilterUtility.useFilter(this.filterOperation, OperationCall::getOperation);
		final Predicate<OperationCall> predicate5 = FilterUtility.useFilter(this.filterTraceID, (call -> Long.toString(call.getTraceID())));

		final Predicate<OperationCall> predicate = predicate1.and(predicate2).and(predicate3).and(predicate4).and(predicate5);
		filteredData.setPredicate(predicate);
	}

}
