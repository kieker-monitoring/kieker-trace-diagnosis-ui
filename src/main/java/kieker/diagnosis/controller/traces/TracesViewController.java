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

package kieker.diagnosis.controller.traces;

import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseEvent;
import jfxtras.scene.control.CalendarTimeTextField;
import kieker.diagnosis.components.treetable.LazyOperationCallTreeItem;
import kieker.diagnosis.controller.AbstractController;
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.domain.Trace;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.util.Context;
import kieker.diagnosis.util.ContextKey;
import kieker.diagnosis.util.ErrorHandling;
import kieker.diagnosis.util.FilterUtility;
import kieker.diagnosis.util.NameConverter;

/**
 * The sub-controller responsible for the sub-view presenting the available traces.
 *
 * @author Nils Christian Ehmke
 */
public final class TracesViewController extends AbstractController {

	private final DataModel ivDataModel = DataModel.getInstance();

	private final SimpleObjectProperty<Optional<OperationCall>> ivSelection = new SimpleObjectProperty<>(Optional.empty());

	@FXML private TreeTableView<OperationCall> ivTreetable;

	@FXML private RadioButton ivShowAllButton;
	@FXML private RadioButton ivShowJustFailedButton;
	@FXML private RadioButton ivShowJustFailureContainingButton;
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

	@FXML private TextField ivTraceDepth;
	@FXML private TextField ivTraceSize;
	@FXML private TextField ivTimestamp;
	@FXML private TextField ivContainer;
	@FXML private TextField ivComponent;
	@FXML private TextField ivOperation;
	@FXML private TextField duration;
	@FXML private TextField percent;
	@FXML private TextField traceID;
	@FXML private TextField failed;

	@FXML private TextField ivCounter;

	@FXML private ResourceBundle ivResources;

	private Predicate<OperationCall> ivPredicate = FilterUtility.alwaysTrue();

	public TracesViewController(final Context aContext) {
		super(aContext);
	}

	@ErrorHandling
	public void initialize() {
		this.reloadTreetable();

		final ObservableList<Trace> traces = this.ivDataModel.getTraces();
		traces.addListener((final Change<? extends Trace> c) -> this.reloadTreetable());

		this.ivSelection.addListener(e -> this.updateDetailPanel());

		final Object call = super.getContext().get(ContextKey.OPERATION_CALL);
		if (call instanceof OperationCall) {
			this.jumpToCall((OperationCall) call);
		}

	}

	private void jumpToCall(final OperationCall aCall) {
		final TreeItem<OperationCall> root = this.ivTreetable.getRoot();

		final Optional<TreeItem<OperationCall>> traceRoot = this.findTraceRoot(root, aCall);
		if (traceRoot.isPresent()) {
			final TreeItem<OperationCall> treeItem = this.findCall(traceRoot.get(), aCall);
			if (treeItem != null) {
				this.ivTreetable.getSelectionModel().select(treeItem);
				this.ivSelection.set(Optional.ofNullable(treeItem.getValue()));
			}
		}
	}

	private Optional<TreeItem<OperationCall>> findTraceRoot(final TreeItem<OperationCall> aRoot, final OperationCall aCall) {
		return aRoot.getChildren().stream().filter(t -> t.getValue().getTraceID() == aCall.getTraceID()).findFirst();
	}

	private TreeItem<OperationCall> findCall(final TreeItem<OperationCall> aRoot, final OperationCall aCall) {
		if (aRoot.getValue() == aCall) {
			aRoot.setExpanded(true);
			return aRoot;
		}

		for (final TreeItem<OperationCall> child : aRoot.getChildren()) {
			final TreeItem<OperationCall> item = this.findCall(child, aCall);
			if (item != null) {
				aRoot.setExpanded(true);
				return item;
			}
		}

		return null;
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
			this.duration.setText(NameConverter.toDurationString(call.getDuration(), sourceTimeUnit, targetTimeUnit));
			this.traceID.setText(Long.toString(call.getTraceID()));
			this.ivTraceDepth.setText(Integer.toString(call.getStackDepth()));
			this.ivTraceSize.setText(Integer.toString(call.getStackSize()));
			this.percent.setText(call.getPercent() + " %");
			this.failed.setText(call.getFailedCause() != null ? call.getFailedCause() : "N/A");
		} else {
			this.ivContainer.setText("N/A");
			this.ivComponent.setText("N/A");
			this.ivOperation.setText("N/A");
			this.ivTimestamp.setText("N/A");
			this.duration.setText("N/A");
			this.traceID.setText("N/A");
			this.percent.setText("N/A");
			this.failed.setText("N/A");
		}
	}

	@ErrorHandling
	public void selectCall(final MouseEvent aEvent) {
		final TreeItem<OperationCall> selectedItem = this.ivTreetable.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			this.ivSelection.set(Optional.ofNullable(selectedItem.getValue()));
		}
	}

	@ErrorHandling
	public void useFilter() {
		final Predicate<OperationCall> predicate1 = FilterUtility.useFilter(this.ivShowAllButton, this.ivShowJustSuccessful, this.ivShowJustFailedButton,
				this.ivShowJustFailureContainingButton, OperationCall::isFailed, OperationCall::containsFailure);
		final Predicate<OperationCall> predicate2 = FilterUtility.useFilter(this.ivFilterContainer, OperationCall::getContainer);
		final Predicate<OperationCall> predicate3 = FilterUtility.useFilter(this.ivFilterComponent, OperationCall::getComponent);
		final Predicate<OperationCall> predicate4 = FilterUtility.useFilter(this.ivFilterOperation, OperationCall::getOperation);
		final Predicate<OperationCall> predicate5 = FilterUtility.useFilter(this.ivFilterTraceID, (call -> Long.toString(call.getTraceID())));
		final Predicate<OperationCall> predicate6 = FilterUtility.useFilter(this.ivFilterLowerDate, OperationCall::getTimestamp, true);
		final Predicate<OperationCall> predicate7 = FilterUtility.useFilter(this.ivFilterUpperDate, OperationCall::getTimestamp, false);
		final Predicate<OperationCall> predicate8 = FilterUtility.useFilter(this.ivFilterLowerTime, OperationCall::getTimestamp, true);
		final Predicate<OperationCall> predicate9 = FilterUtility.useFilter(this.ivFilterUpperTime, OperationCall::getTimestamp, false);
		final Predicate<OperationCall> predicate10 = FilterUtility.useFilter(this.ivFilterException, (call -> call.isFailed() ? call.getFailedCause() : ""));

		this.ivPredicate = predicate1.and(predicate2).and(predicate3).and(predicate4).and(predicate5).and(predicate6).and(predicate7).and(predicate8).and(predicate9).and(predicate10);
		this.reloadTreetable();
	}

	private void reloadTreetable() {
		this.ivSelection.set(Optional.empty());

		final List<Trace> traces = this.ivDataModel.getTraces();
		final TreeItem<OperationCall> root = new TreeItem<>();
		final ObservableList<TreeItem<OperationCall>> rootChildren = root.getChildren();
		this.ivTreetable.setRoot(root);
		this.ivTreetable.setShowRoot(false);

		traces.stream().map(trace -> trace.getRootOperationCall()).filter(this.ivPredicate).forEach(call -> rootChildren.add(new LazyOperationCallTreeItem(call)));

		this.ivCounter.textProperty().set(rootChildren.size() + " " + this.ivResources.getString("TracesView.lblCounter.text"));
	}
}
