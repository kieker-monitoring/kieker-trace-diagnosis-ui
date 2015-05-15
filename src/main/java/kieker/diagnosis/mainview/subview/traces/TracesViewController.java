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

package kieker.diagnosis.mainview.subview.traces;

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
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.domain.Trace;
import kieker.diagnosis.mainview.subview.util.LazyOperationCallTreeItem;
import kieker.diagnosis.model.DataModel;

/**
 * The sub-controller responsible for the sub-view presenting the available traces.
 *
 * @author Nils Christian Ehmke
 */
public final class TracesViewController {

	private final DataModel dataModel = DataModel.getInstance();

	private final SimpleObjectProperty<Optional<OperationCall>> selection = new SimpleObjectProperty<>(Optional.empty());

	@FXML private TreeTableView<OperationCall> treetable;

	@FXML private TextField traceDepth;
	@FXML private TextField traceSize;
	@FXML private TextField timestamp;
	@FXML private TextField container;
	@FXML private TextField component;
	@FXML private TextField operation;
	@FXML private TextField duration;
	@FXML private TextField percent;
	@FXML private TextField traceID;
	@FXML private TextField failed;

	@FXML private TextField counter;

	@FXML private ResourceBundle resources;

	public void initialize() {
		this.reloadTreetable();

		final ObservableList<Trace> traces = this.dataModel.getTraces();

		traces.addListener((final Change<? extends Trace> c) -> this.reloadTreetable());

		this.traceDepth.textProperty().bind(this.createStringBindingForSelection(OperationCall::getStackDepth));
		this.traceSize.textProperty().bind(this.createStringBindingForSelection(OperationCall::getStackSize));
		this.timestamp.textProperty().bind(this.createStringBindingForSelection(OperationCall::getTimestamp));
		this.container.textProperty().bind(this.createStringBindingForSelection(OperationCall::getContainer));
		this.component.textProperty().bind(this.createStringBindingForSelection(OperationCall::getComponent));
		this.operation.textProperty().bind(this.createStringBindingForSelection(OperationCall::getOperation));
		this.duration.textProperty().bind(this.createStringBindingForSelection(OperationCall::getDuration));
		this.percent.textProperty().bind(this.createStringBindingForSelection(OperationCall::getPercent));
		this.traceID.textProperty().bind(this.createStringBindingForSelection(OperationCall::getTraceID));
		this.failed.textProperty().bind(this.createStringBindingForSelection(OperationCall::getFailedCause));

		this.counter.textProperty().bind(Bindings.createStringBinding(() -> traces.size() + " " + this.resources.getString("TracesView.lblCounter.text"), traces));
	}

	private StringBinding createStringBindingForSelection(final Function<OperationCall, Object> mapper) {
		return Bindings.createStringBinding(() -> this.selection.get().map(mapper).map(Object::toString).orElse("N/A"), this.selection);
	}

	public void selectCall(final MouseEvent event) {
		final TreeItem<OperationCall> selectedItem = this.treetable.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			this.selection.set(Optional.ofNullable(selectedItem.getValue()));
		}
	}

	private void reloadTreetable() {
		final List<Trace> traces = this.dataModel.getTraces();
		final TreeItem<OperationCall> root = new TreeItem<>();
		this.treetable.setRoot(root);
		this.treetable.setShowRoot(false);

		for (final Trace trace : traces) {
			root.getChildren().add(new LazyOperationCallTreeItem<OperationCall>(trace.getRootOperationCall()));
		}
	}
}
