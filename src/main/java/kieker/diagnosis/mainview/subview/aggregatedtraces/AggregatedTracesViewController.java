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
import java.util.function.Predicate;

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
import kieker.diagnosis.mainview.subview.util.NameConverter;
import kieker.diagnosis.model.DataModel;

/**
 * @author Nils Christian Ehmke
 */
public final class AggregatedTracesViewController {

	private final SimpleObjectProperty<Optional<AggregatedOperationCall>> selection = new SimpleObjectProperty<>(Optional.empty());

	@FXML private TreeTableView<AggregatedOperationCall> treetable;
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

	private Predicate<AggregatedOperationCall> fstPredicate = call -> true;
	private Predicate<AggregatedOperationCall> sndPredicate = call -> true;
	private Predicate<AggregatedOperationCall> thdPredicate = call -> true;
	private Predicate<AggregatedOperationCall> fthPredicate = call -> true;

	public void initialize() {
		this.reloadTreetable();

		final DataModel dataModel = DataModel.getInstance();
		dataModel.getAggregatedTraces().addListener((final Change<? extends AggregatedTrace> c) -> this.reloadTreetable());

		this.medianDuration.textProperty().bind(this.createDurationStringBindingForSelection(AggregatedOperationCall::getMedianDuration));
		this.totalDuration.textProperty().bind(this.createDurationStringBindingForSelection(AggregatedOperationCall::getTotalDuration));
		this.minDuration.textProperty().bind(this.createDurationStringBindingForSelection(AggregatedOperationCall::getMinDuration));
		this.avgDuration.textProperty().bind(this.createDurationStringBindingForSelection(AggregatedOperationCall::getMeanDuration));
		this.maxDuration.textProperty().bind(this.createDurationStringBindingForSelection(AggregatedOperationCall::getMaxDuration));
		this.traceDepth.textProperty().bind(this.createStringBindingForSelection(AggregatedOperationCall::getStackDepth));
		this.traceSize.textProperty().bind(this.createStringBindingForSelection(AggregatedOperationCall::getStackSize));
		this.container.textProperty().bind(this.createStringBindingForSelection(AggregatedOperationCall::getContainer));
		this.component.textProperty().bind(this.createStringBindingForSelection(AggregatedOperationCall::getComponent));
		this.operation.textProperty().bind(this.createStringBindingForSelection(AggregatedOperationCall::getOperation));
		this.failed.textProperty().bind(this.createStringBindingForSelection(AggregatedOperationCall::getFailedCause));
		this.calls.textProperty().bind(this.createStringBindingForSelection(AggregatedOperationCall::getCalls));
	}

	private StringBinding createStringBindingForSelection(final Function<AggregatedOperationCall, Object> mapper) {
		return Bindings.createStringBinding(() -> this.selection.get().map(mapper).map(Object::toString).orElse("N/A"), this.selection);
	}

	private StringBinding createDurationStringBindingForSelection(final Function<AggregatedOperationCall, Object> mapper) {
		return Bindings.createStringBinding(
				() -> this.selection.get().map(mapper).map(x -> x.toString() + " " + NameConverter.toShortTimeUnit(DataModel.getInstance().getTimeUnit())).orElse("N/A"),
				this.selection);
	}

	public void selectCall(final MouseEvent event) {
		final TreeItem<AggregatedOperationCall> selectedItem = this.treetable.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			this.selection.set(Optional.ofNullable(selectedItem.getValue()));
		}
	}

	public void showAllTraces() {
		this.fstPredicate = call -> true;
		this.reloadTreetable();
	}

	public void showJustFailedTraces() {
		this.fstPredicate = AggregatedOperationCall::isFailed;
		this.reloadTreetable();
	}

	public void showJustFailureContainingTraces() {
		this.fstPredicate = AggregatedOperationCall::containsFailure;
		this.reloadTreetable();
	}

	public void useContainerFilter() {
		final String text = this.filterContainer.getText();

		if ((text == null) || text.isEmpty()) {
			this.sndPredicate = call -> true;
		} else {
			this.sndPredicate = call -> call.getContainer().toLowerCase().contains(text.toLowerCase());
		}

		this.reloadTreetable();
	}

	public void useComponentFilter() {
		final String text = this.filterComponent.getText();

		if ((text == null) || text.isEmpty()) {
			this.thdPredicate = call -> true;
		} else {
			this.thdPredicate = call -> call.getComponent().toLowerCase().contains(text.toLowerCase());
		}

		this.reloadTreetable();
	}

	public void useOperationFilter() {
		final String text = this.filterOperation.getText();

		if ((text == null) || text.isEmpty()) {
			this.fthPredicate = call -> true;
		} else {
			this.fthPredicate = call -> call.getOperation().toLowerCase().contains(text.toLowerCase());
		}

		this.reloadTreetable();
	}

	private void reloadTreetable() {
		this.selection.set(Optional.empty());

		final DataModel dataModel = DataModel.getInstance();
		final List<AggregatedTrace> traces = dataModel.getAggregatedTraces();
		final TreeItem<AggregatedOperationCall> root = new TreeItem<>();
		final ObservableList<TreeItem<AggregatedOperationCall>> rootChildren = root.getChildren();
		this.treetable.setRoot(root);
		this.treetable.setShowRoot(false);

		for (final AggregatedTrace trace : traces) {
			if (this.fstPredicate.test(trace.getRootOperationCall()) && this.sndPredicate.test(trace.getRootOperationCall())
					&& this.thdPredicate.test(trace.getRootOperationCall()) && this.fthPredicate.test(trace.getRootOperationCall())) {
				rootChildren.add(new LazyOperationCallTreeItem<AggregatedOperationCall>(trace.getRootOperationCall()));
			}
		}
		
		this.counter.textProperty().set(rootChildren.size() + " " + this.resources.getString("AggregatedTracesView.lblCounter.text"));
	}
}
