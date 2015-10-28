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

package kieker.diagnosis.controller.aggregatedcalls;

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
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import kieker.diagnosis.domain.AggregatedOperationCall;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.util.ErrorHandling;
import kieker.diagnosis.util.FilterUtility;
import kieker.diagnosis.util.NameConverter;

/**
 * @author Nils Christian Ehmke
 */
public final class AggregatedCallsViewController {

	private FilteredList<AggregatedOperationCall> fstFilteredData;
	private FilteredList<AggregatedOperationCall> sndFilteredData;
	private FilteredList<AggregatedOperationCall> thdFilteredData;
	private FilteredList<AggregatedOperationCall> fthFilteredData;

	private final SimpleObjectProperty<Optional<AggregatedOperationCall>> selection = new SimpleObjectProperty<>(Optional.empty());

	@FXML private TableView<AggregatedOperationCall> table;
	@FXML private TextField filterContainer;
	@FXML private TextField filterComponent;
	@FXML private TextField filterOperation;

	@FXML private TextField minimalDuration;
	@FXML private TextField maximalDuration;
	@FXML private TextField medianDuration;
	@FXML private TextField totalDuration;
	@FXML private TextField meanDuration;
	@FXML private TextField container;
	@FXML private TextField component;
	@FXML private TextField operation;
	@FXML private TextField failed;
	@FXML private TextField calls;

	@FXML private TextField counter;

	@FXML private ResourceBundle resources;

	@ErrorHandling
	public void initialize() {
		final DataModel dataModel = DataModel.getInstance();

		this.fstFilteredData = new FilteredList<>(dataModel.getAggregatedOperationCalls());
		this.sndFilteredData = new FilteredList<>(this.fstFilteredData);
		this.thdFilteredData = new FilteredList<>(this.sndFilteredData);
		this.fthFilteredData = new FilteredList<>(this.thdFilteredData);

		this.fthFilteredData.addListener((ListChangeListener<AggregatedOperationCall>) change -> this.selection.set(Optional.empty()));

		final SortedList<AggregatedOperationCall> sortedData = new SortedList<>(this.fthFilteredData);
		sortedData.comparatorProperty().bind(this.table.comparatorProperty());
		this.table.setItems(sortedData);

		this.selection.addListener(e -> this.updateDetailPanel());

		this.counter.textProperty().bind(Bindings.createStringBinding(() -> sortedData.size() + " " + this.resources.getString("AggregatedCallsView.lblCounter.text"), sortedData));
	}

	private void updateDetailPanel() {
		if (this.selection.get().isPresent()) {
			final AggregatedOperationCall call = this.selection.get().get();
			final TimeUnit sourceTimeUnit = DataModel.getInstance().getTimeUnit();
			final TimeUnit targetTimeUnit = PropertiesModel.getInstance().getTimeUnit();

			this.container.setText(call.getContainer());
			this.component.setText(call.getComponent());
			this.operation.setText(call.getOperation());
			this.minimalDuration.setText(NameConverter.toDurationString(call.getMinDuration(), sourceTimeUnit, targetTimeUnit));
			this.maximalDuration.setText(NameConverter.toDurationString(call.getMaxDuration(), sourceTimeUnit, targetTimeUnit));
			this.medianDuration.setText(NameConverter.toDurationString(call.getMedianDuration(), sourceTimeUnit, targetTimeUnit));
			this.totalDuration.setText(NameConverter.toDurationString(call.getTotalDuration(), sourceTimeUnit, targetTimeUnit));
			this.meanDuration.setText(NameConverter.toDurationString(call.getMeanDuration(), sourceTimeUnit, targetTimeUnit));
			this.calls.setText(Integer.toString(call.getCalls()));
			this.failed.setText(call.getFailedCause() != null ? call.getFailedCause() : "N/A");
		} else {
			this.container.setText("N/A");
			this.component.setText("N/A");
			this.operation.setText("N/A");
			this.minimalDuration.setText("N/A");
			this.maximalDuration.setText("N/A");
			this.medianDuration.setText("N/A");
			this.totalDuration.setText("N/A");
			this.meanDuration.setText("N/A");
			this.calls.setText("N/A");
			this.failed.setText("N/A");
		}
	}

	@ErrorHandling
	public void selectCall(final MouseEvent event) {
		this.selection.set(Optional.ofNullable(this.table.getSelectionModel().getSelectedItem()));
	}

	@ErrorHandling
	public void showAllMethods() {
		this.fstFilteredData.setPredicate(null);
	}

	@ErrorHandling
	public void showJustFailedMethods() {
		this.fstFilteredData.setPredicate(call -> call.isFailed());
	}

	@ErrorHandling
	public void useContainerFilter() {
		final Predicate<AggregatedOperationCall> predicate = FilterUtility.useFilter(this.filterContainer, AggregatedOperationCall::getContainer);
		this.sndFilteredData.setPredicate(predicate);
	}

	@ErrorHandling
	public void useComponentFilter() {
		final Predicate<AggregatedOperationCall> predicate = FilterUtility.useFilter(this.filterComponent, AggregatedOperationCall::getComponent);
		this.thdFilteredData.setPredicate(predicate);
	}

	@ErrorHandling
	public void useOperationFilter() {
		final Predicate<AggregatedOperationCall> predicate = FilterUtility.useFilter(this.filterOperation, AggregatedOperationCall::getOperation);
		this.fthFilteredData.setPredicate(predicate);
	}

}
