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
import javafx.scene.input.MouseEvent;
import kieker.diagnosis.components.treetable.LazyAggregatedOperationCallTreeItem;
import kieker.diagnosis.controller.AbstractController;
import kieker.diagnosis.domain.AggregatedOperationCall;
import kieker.diagnosis.domain.AggregatedTrace;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.util.Context;
import kieker.diagnosis.util.ErrorHandling;
import kieker.diagnosis.util.FilterUtility;
import kieker.diagnosis.util.NameConverter;

/**
 * @author Nils Christian Ehmke
 */
public final class AggregatedTracesViewController extends AbstractController {

	private final SimpleObjectProperty<Optional<AggregatedOperationCall>> selection = new SimpleObjectProperty<>(Optional.empty());

	@FXML private TreeTableView<AggregatedOperationCall> treetable;
	
	@FXML private RadioButton showAllButton;
	@FXML private RadioButton showJustFailedButton;
	@FXML private RadioButton showJustFailureContainingButton;
	
	@FXML private TextField filterContainer;
	@FXML private TextField filterComponent;
	@FXML private TextField filterOperation;

	@FXML private TextField medianDuration;
	@FXML private TextField totalDuration;
	@FXML private TextField minDuration;
	@FXML private TextField avgDuration;
	@FXML private TextField maxDuration;
	@FXML private TextField traceDepth;
	@FXML private TextField traceSize;
	@FXML private TextField container;
	@FXML private TextField component;
	@FXML private TextField operation;
	@FXML private TextField failed;
	@FXML private TextField calls;

	@FXML private TextField counter;

	@FXML private ResourceBundle resources;

	private Predicate<AggregatedOperationCall> predicate = FilterUtility.alwaysTrue();
	
	public AggregatedTracesViewController(final Context context) {
		super(context);
	}
	
	@ErrorHandling
	public void initialize() {
		this.reloadTreetable();

		final DataModel dataModel = DataModel.getInstance();
		dataModel.getAggregatedTraces().addListener((final Change<? extends AggregatedTrace> c) -> this.reloadTreetable());

		this.selection.addListener(e -> this.updateDetailPanel());
	}

	private void updateDetailPanel() {
		if (this.selection.get().isPresent()) {
			final AggregatedOperationCall call = this.selection.get().get();
			final TimeUnit sourceTimeUnit = DataModel.getInstance().getTimeUnit();
			final TimeUnit targetTimeUnit = PropertiesModel.getInstance().getTimeUnit();

			this.container.setText(call.getContainer());
			this.component.setText(call.getComponent());
			this.operation.setText(call.getOperation());
			this.minDuration.setText(NameConverter.toDurationString(call.getMinDuration(), sourceTimeUnit, targetTimeUnit));
			this.maxDuration.setText(NameConverter.toDurationString(call.getMaxDuration(), sourceTimeUnit, targetTimeUnit));
			this.medianDuration.setText(NameConverter.toDurationString(call.getMedianDuration(), sourceTimeUnit, targetTimeUnit));
			this.totalDuration.setText(NameConverter.toDurationString(call.getTotalDuration(), sourceTimeUnit, targetTimeUnit));
			this.avgDuration.setText(NameConverter.toDurationString(call.getMeanDuration(), sourceTimeUnit, targetTimeUnit));
			this.calls.setText(Integer.toString(call.getCalls()));
			this.traceDepth.setText(Integer.toString(call.getStackDepth()));
			this.traceSize.setText(Integer.toString(call.getStackSize()));
			this.failed.setText(call.getFailedCause() != null ? call.getFailedCause() : "N/A");
		} else {
			this.container.setText("N/A");
			this.component.setText("N/A");
			this.operation.setText("N/A");
			this.minDuration.setText("N/A");
			this.maxDuration.setText("N/A");
			this.medianDuration.setText("N/A");
			this.totalDuration.setText("N/A");
			this.avgDuration.setText("N/A");
			this.calls.setText("N/A");
			this.traceDepth.setText("N/A");
			this.traceSize.setText("N/A");
			this.failed.setText("N/A");
		}
	}

	@ErrorHandling
	public void selectCall(final MouseEvent event) {
		final TreeItem<AggregatedOperationCall> selectedItem = this.treetable.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			this.selection.set(Optional.ofNullable(selectedItem.getValue()));
		}
	}

	@ErrorHandling
	public void useFilter() {
		final Predicate<AggregatedOperationCall> predicate1 = (showJustFailedButton.isSelected()) ? AggregatedOperationCall::isFailed : FilterUtility.alwaysTrue();
		final Predicate<AggregatedOperationCall> predicate2 = (showJustFailureContainingButton.isSelected()) ?  AggregatedOperationCall::containsFailure : FilterUtility.alwaysTrue();
		final Predicate<AggregatedOperationCall> predicate3 = FilterUtility.useFilter(this.filterContainer, AggregatedOperationCall::getContainer);
		final Predicate<AggregatedOperationCall> predicate4 = FilterUtility.useFilter(this.filterComponent, AggregatedOperationCall::getComponent);
		final Predicate<AggregatedOperationCall> predicate5 = FilterUtility.useFilter(this.filterOperation, AggregatedOperationCall::getOperation);

		predicate = predicate1.and(predicate2).and(predicate3).and(predicate4).and(predicate5);
		reloadTreetable();
	}

	private void reloadTreetable() {
		this.selection.set(Optional.empty());

		final DataModel dataModel = DataModel.getInstance();
		final List<AggregatedTrace> traces = dataModel.getAggregatedTraces();
		final TreeItem<AggregatedOperationCall> root = new TreeItem<>();
		final ObservableList<TreeItem<AggregatedOperationCall>> rootChildren = root.getChildren();
		this.treetable.setRoot(root);
		this.treetable.setShowRoot(false);

		traces.stream().map(trace -> trace.getRootOperationCall()).filter(predicate).forEach(call -> rootChildren.add(new LazyAggregatedOperationCallTreeItem(call)));
		
		this.counter.textProperty().set(rootChildren.size() + " " + this.resources.getString("AggregatedTracesView.lblCounter.text"));
	}
}
