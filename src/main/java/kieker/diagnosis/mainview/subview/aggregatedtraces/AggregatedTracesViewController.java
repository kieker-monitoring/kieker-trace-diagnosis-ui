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
import java.util.function.Function;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
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

	private final SimpleObjectProperty<Optional<AggregatedOperationCall>> selection = new SimpleObjectProperty<>(Optional.empty());

	@FXML private TreeTableView<AggregatedOperationCall> treetable;

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

	public void initialize() {
		this.reloadTreetable();

		final DataModel dataModel = DataModel.getInstance();
		final ObservableList<AggregatedTrace> traces = dataModel.getAggregatedTraces();
		dataModel.getAggregatedTraces().addListener((final Change<? extends AggregatedTrace> c) -> this.reloadTreetable());

		this.medianDuration.textProperty().bind(this.createStringBindingForSelection(AggregatedOperationCall::getMedianDuration));
		this.totalDuration.textProperty().bind(this.createStringBindingForSelection(AggregatedOperationCall::getTotalDuration));
		this.minDuration.textProperty().bind(this.createStringBindingForSelection(AggregatedOperationCall::getMinDuration));
		this.avgDuration.textProperty().bind(this.createStringBindingForSelection(AggregatedOperationCall::getMeanDuration));
		this.maxDuration.textProperty().bind(this.createStringBindingForSelection(AggregatedOperationCall::getMaxDuration));
		this.traceDepth.textProperty().bind(this.createStringBindingForSelection(AggregatedOperationCall::getStackDepth));
		this.traceSize.textProperty().bind(this.createStringBindingForSelection(AggregatedOperationCall::getStackSize));
		this.container.textProperty().bind(this.createStringBindingForSelection(AggregatedOperationCall::getContainer));
		this.component.textProperty().bind(this.createStringBindingForSelection(AggregatedOperationCall::getComponent));
		this.operation.textProperty().bind(this.createStringBindingForSelection(AggregatedOperationCall::getOperation));
		this.failed.textProperty().bind(this.createStringBindingForSelection(AggregatedOperationCall::getFailedCause));
		this.calls.textProperty().bind(this.createStringBindingForSelection(AggregatedOperationCall::getCalls));

		this.counter.textProperty().bind(Bindings.createStringBinding(() -> traces.size() + " " + this.resources.getString("AggregatedTracesView.lblCounter.text"), traces));
	}

	private StringBinding createStringBindingForSelection(final Function<AggregatedOperationCall, Object> mapper) {
		return Bindings.createStringBinding(() -> this.selection.get().map(mapper).map(Object::toString).orElse("N/A"), this.selection);
	}

	public void selectCall(final MouseEvent event) {
		this.selection.set(Optional.of(this.treetable.getSelectionModel().getSelectedItem().getValue()));
	}

	private void reloadTreetable() {
		final DataModel dataModel = DataModel.getInstance();
		final List<AggregatedTrace> traces = dataModel.getAggregatedTraces();
		final TreeItem<AggregatedOperationCall> root = new TreeItem<>();
		this.treetable.setRoot(root);
		this.treetable.setShowRoot(false);

		for (final AggregatedTrace trace : traces) {
			root.getChildren().add(new LazyOperationCallTreeItem<AggregatedOperationCall>(trace.getRootOperationCall()));
		}
	}
}
