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

package kieker.diagnosis.mainview.subview.aggregatedtraces;

import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseEvent;
import kieker.diagnosis.domain.AggregatedOperationCall;
import kieker.diagnosis.domain.AggregatedTrace;
import kieker.diagnosis.mainview.subview.util.LazyOperationCallTreeItem;
import kieker.diagnosis.model.DataModel;

/**
 * @author Nils Christian Ehmke
 */
public final class AggregatedTracesViewController {

	private final DataModel dataModel = DataModel.getInstance();

	private final SimpleObjectProperty<Optional<AggregatedOperationCall>> selection = new SimpleObjectProperty<>(Optional.empty());

	@FXML private TreeTableView<AggregatedOperationCall> treetable;
	@FXML private TextField counter;

	@FXML private TextField container;
	@FXML private TextField component;
	@FXML private TextField operation;
	@FXML private TextField failed;
	@FXML private TextField calls;
	@FXML private TextField traceDepth;
	@FXML private TextField traceSize;
	@FXML private TextField minDuration;
	@FXML private TextField avgDuration;
	@FXML private TextField medianDuration;
	@FXML private TextField maxDuration;
	@FXML private TextField totalDuration;

	@FXML private ResourceBundle resources;

	public void initialize() {
		this.reloadTreetable();

		final ObservableList<AggregatedTrace> traces = this.dataModel.getAggregatedTraces();

		this.dataModel.getAggregatedTraces().addListener((final Change<? extends AggregatedTrace> c) -> this.reloadTreetable());

		this.container.textProperty().bind(Bindings.createStringBinding(() -> this.selection.get().map(call -> call.getContainer()).orElse("N/A"), this.selection));
		this.component.textProperty().bind(Bindings.createStringBinding(() -> this.selection.get().map(call -> call.getComponent()).orElse("N/A"), this.selection));
		this.operation.textProperty().bind(Bindings.createStringBinding(() -> this.selection.get().map(call -> call.getOperation()).orElse("N/A"), this.selection));
		this.failed.textProperty().bind(Bindings.createStringBinding(() -> this.selection.get().map(call -> call.getFailedCause()).orElse("N/A"), this.selection));
		this.calls.textProperty().bind(Bindings.createStringBinding(() -> this.selection.get().map(call -> Integer.toString(call.getCalls())).orElse("N/A"), this.selection));
		this.traceDepth.textProperty().bind(
				Bindings.createStringBinding(() -> this.selection.get().map(call -> Integer.toString(call.getStackDepth())).orElse("N/A"), this.selection));
		this.traceSize.textProperty().bind(
				Bindings.createStringBinding(() -> this.selection.get().map(call -> Integer.toString(call.getStackSize())).orElse("N/A"), this.selection));
		this.minDuration.textProperty().bind(
				Bindings.createStringBinding(() -> this.selection.get().map(call -> Long.toString(call.getMinDuration())).orElse("N/A"), this.selection));
		this.avgDuration.textProperty().bind(
				Bindings.createStringBinding(() -> this.selection.get().map(call -> Long.toString(call.getMeanDuration())).orElse("N/A"), this.selection));
		this.medianDuration.textProperty().bind(
				Bindings.createStringBinding(() -> this.selection.get().map(call -> Long.toString(call.getMedianDuration())).orElse("N/A"), this.selection));
		this.maxDuration.textProperty().bind(
				Bindings.createStringBinding(() -> this.selection.get().map(call -> Long.toString(call.getMaxDuration())).orElse("N/A"), this.selection));
		this.totalDuration.textProperty().bind(
				Bindings.createStringBinding(() -> this.selection.get().map(call -> Long.toString(call.getTotalDuration())).orElse("N/A"), this.selection));

		this.counter.textProperty().bind(Bindings.createStringBinding(() -> traces.size() + " " + this.resources.getString("AggregatedTracesView.lblCounter.text"), traces));
	}

	public void selectCall(final MouseEvent event) {
		this.selection.set(Optional.of(this.treetable.getSelectionModel().getSelectedItem().getValue()));
	}

	private void reloadTreetable() {
		final List<AggregatedTrace> traces = this.dataModel.getAggregatedTraces();
		final TreeItem<AggregatedOperationCall> root = new TreeItem<>();
		this.treetable.setRoot(root);
		this.treetable.setShowRoot(false);

		for (final AggregatedTrace trace : traces) {
			root.getChildren().add(new LazyOperationCallTreeItem<AggregatedOperationCall>(trace.getRootOperationCall()));
		}
	}
}
