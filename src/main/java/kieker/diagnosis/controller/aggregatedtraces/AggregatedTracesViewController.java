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
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseEvent;
import kieker.diagnosis.components.LazyAggregatedOperationCallTreeItem;
import kieker.diagnosis.domain.AggregatedOperationCall;
import kieker.diagnosis.domain.AggregatedTrace;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.util.ErrorHandling;
import kieker.diagnosis.util.FilterUtility;
import kieker.diagnosis.util.NameConverter;

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
	public void showAllTraces() {
		this.fstPredicate = call -> true;
		this.reloadTreetable();
	}
	
	@ErrorHandling
	public void showJustFailedTraces() {
		this.fstPredicate = AggregatedOperationCall::isFailed;
		this.reloadTreetable();
	}

	@ErrorHandling
	public void showJustFailureContainingTraces() {
		this.fstPredicate = AggregatedOperationCall::containsFailure;
		this.reloadTreetable();
	}

	@ErrorHandling
	public void useContainerFilter() {
		final Predicate<AggregatedOperationCall> predicate = FilterUtility.useFilter(this.filterContainer, AggregatedOperationCall::getContainer);
		this.sndPredicate = predicate;
		this.reloadTreetable();
	}

	@ErrorHandling
	public void useComponentFilter() {
		final Predicate<AggregatedOperationCall> predicate = FilterUtility.useFilter(this.filterComponent, AggregatedOperationCall::getComponent);
		this.thdPredicate = predicate;
		this.reloadTreetable();
	}

	@ErrorHandling
	public void useOperationFilter() {
		final Predicate<AggregatedOperationCall> predicate = FilterUtility.useFilter(this.filterOperation, AggregatedOperationCall::getOperation);
		this.fthPredicate = predicate;
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
				rootChildren.add(new LazyAggregatedOperationCallTreeItem(trace.getRootOperationCall()));
			}
		}
		
		this.counter.textProperty().set(rootChildren.size() + " " + this.resources.getString("AggregatedTracesView.lblCounter.text"));
	}
}
