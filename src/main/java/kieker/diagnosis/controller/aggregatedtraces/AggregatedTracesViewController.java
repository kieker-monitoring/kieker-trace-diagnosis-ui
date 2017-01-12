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

	private final SimpleObjectProperty<Optional<AggregatedOperationCall>> ivSelection = new SimpleObjectProperty<>(Optional.empty());

	@FXML private TreeTableView<AggregatedOperationCall> ivTreetable;

	@FXML private RadioButton ivShowAllButton;
	@FXML private RadioButton ivShowJustFailedButton;
	@FXML private RadioButton ivShowJustFailureContainingButton;
	@FXML private RadioButton ivShowJustSuccessful;

	@FXML private TextField ivFilterContainer;
	@FXML private TextField ivFilterComponent;
	@FXML private TextField ivFilterOperation;
	@FXML private TextField ivFilterException;

	@FXML private TextField ivMedianDuration;
	@FXML private TextField ivTotalDuration;
	@FXML private TextField ivMinDuration;
	@FXML private TextField ivAvgDuration;
	@FXML private TextField ivMaxDuration;
	@FXML private TextField ivTraceDepth;
	@FXML private TextField ivTraceSize;
	@FXML private TextField ivContainer;
	@FXML private TextField ivComponent;
	@FXML private TextField ivOperation;
	@FXML private TextField ivFailed;
	@FXML private TextField ivCalls;

	@FXML private TextField ivCounter;

	@FXML private ResourceBundle resources;

	private Predicate<AggregatedOperationCall> ivPredicate = FilterUtility.alwaysTrue();

	public AggregatedTracesViewController(final Context aContext) {
		super(aContext);
	}

	@ErrorHandling
	public void initialize() {
		this.reloadTreetable();

		final DataModel dataModel = DataModel.getInstance();
		dataModel.getAggregatedTraces().addListener((final Change<? extends AggregatedTrace> c) -> this.reloadTreetable());

		this.ivSelection.addListener(e -> this.updateDetailPanel());
	}
	
	@Override
	protected void reinitialize() {
	}
	
	private void updateDetailPanel() {
		if (this.ivSelection.get().isPresent()) {
			final AggregatedOperationCall call = this.ivSelection.get().get();
			final TimeUnit sourceTimeUnit = DataModel.getInstance().getTimeUnit();
			final TimeUnit targetTimeUnit = PropertiesModel.getInstance().getTimeUnit();

			this.ivContainer.setText(call.getContainer());
			this.ivComponent.setText(call.getComponent());
			this.ivOperation.setText(call.getOperation());
			this.ivMinDuration.setText(NameConverter.toDurationString(call.getMinDuration(), sourceTimeUnit, targetTimeUnit));
			this.ivMaxDuration.setText(NameConverter.toDurationString(call.getMaxDuration(), sourceTimeUnit, targetTimeUnit));
			this.ivMedianDuration.setText(NameConverter.toDurationString(call.getMedianDuration(), sourceTimeUnit, targetTimeUnit));
			this.ivTotalDuration.setText(NameConverter.toDurationString(call.getTotalDuration(), sourceTimeUnit, targetTimeUnit));
			this.ivAvgDuration.setText(NameConverter.toDurationString(call.getMeanDuration(), sourceTimeUnit, targetTimeUnit));
			this.ivCalls.setText(Integer.toString(call.getCalls()));
			this.ivTraceDepth.setText(Integer.toString(call.getStackDepth()));
			this.ivTraceSize.setText(Integer.toString(call.getStackSize()));
			this.ivFailed.setText(call.getFailedCause() != null ? call.getFailedCause() : "N/A");
		} else {
			this.ivContainer.setText("N/A");
			this.ivComponent.setText("N/A");
			this.ivOperation.setText("N/A");
			this.ivMinDuration.setText("N/A");
			this.ivMaxDuration.setText("N/A");
			this.ivMedianDuration.setText("N/A");
			this.ivTotalDuration.setText("N/A");
			this.ivAvgDuration.setText("N/A");
			this.ivCalls.setText("N/A");
			this.ivTraceDepth.setText("N/A");
			this.ivTraceSize.setText("N/A");
			this.ivFailed.setText("N/A");
		}
	}

	@ErrorHandling
	public void selectCall() {
		final TreeItem<AggregatedOperationCall> selectedItem = this.ivTreetable.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			this.ivSelection.set(Optional.ofNullable(selectedItem.getValue()));
		}
	}

	@ErrorHandling
	public void useFilter() {
		final Predicate<AggregatedOperationCall> predicate1 = FilterUtility.useFilter(this.ivShowAllButton, this.ivShowJustSuccessful, this.ivShowJustFailedButton,
				this.ivShowJustFailureContainingButton, AggregatedOperationCall::isFailed, AggregatedOperationCall::containsFailure);
		final Predicate<AggregatedOperationCall> predicate2 = FilterUtility.useFilter(this.ivFilterContainer, AggregatedOperationCall::getContainer, PropertiesModel.getInstance().isSearchInEntireTrace());
		final Predicate<AggregatedOperationCall> predicate3 = FilterUtility.useFilter(this.ivFilterComponent, AggregatedOperationCall::getComponent, PropertiesModel.getInstance().isSearchInEntireTrace());
		final Predicate<AggregatedOperationCall> predicate4 = FilterUtility.useFilter(this.ivFilterOperation, AggregatedOperationCall::getOperation, PropertiesModel.getInstance().isSearchInEntireTrace());
		final Predicate<AggregatedOperationCall> predicate5 = FilterUtility.useFilter(this.ivFilterException, (call -> call.isFailed() ? call.getFailedCause() : ""), PropertiesModel.getInstance().isSearchInEntireTrace());

		this.ivPredicate = predicate1.and(predicate2).and(predicate3).and(predicate4).and(predicate5);
		this.reloadTreetable();
	}

	private void reloadTreetable() {
		this.ivSelection.set(Optional.empty());

		final DataModel dataModel = DataModel.getInstance();
		final List<AggregatedTrace> traces = dataModel.getAggregatedTraces();
		final TreeItem<AggregatedOperationCall> root = new TreeItem<>();
		final ObservableList<TreeItem<AggregatedOperationCall>> rootChildren = root.getChildren();
		this.ivTreetable.setRoot(root);
		this.ivTreetable.setShowRoot(false);

		traces.stream().map(trace -> trace.getRootOperationCall()).filter(this.ivPredicate).forEach(call -> rootChildren.add(new LazyAggregatedOperationCallTreeItem(call)));

		this.ivCounter.textProperty().set(rootChildren.size() + " " + this.resources.getString("AggregatedTracesView.lblCounter.text"));
	}
}
