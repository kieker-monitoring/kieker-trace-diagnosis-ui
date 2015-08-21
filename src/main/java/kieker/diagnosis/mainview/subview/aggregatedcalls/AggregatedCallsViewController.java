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

package kieker.diagnosis.mainview.subview.aggregatedcalls;

import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import kieker.diagnosis.domain.AggregatedOperationCall;
import kieker.diagnosis.mainview.subview.util.NameConverter;
import kieker.diagnosis.model.DataModel;

/**
 * @author Nils Christian Ehmke
 */
public final class AggregatedCallsViewController {

	private FilteredList<AggregatedOperationCall> fstFilteredData;
	private FilteredList<AggregatedOperationCall> sndFilteredData;

	private final SimpleObjectProperty<Optional<AggregatedOperationCall>> selection = new SimpleObjectProperty<>(Optional.empty());

	@FXML private TableView<AggregatedOperationCall> table;
	@FXML private TextField regexpfilter;

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

	public void initialize() {
		final DataModel dataModel = DataModel.getInstance();

		this.fstFilteredData = new FilteredList<>(dataModel.getAggregatedOperationCalls());
		this.sndFilteredData = new FilteredList<AggregatedOperationCall>(this.fstFilteredData);

		this.sndFilteredData.addListener((ListChangeListener<AggregatedOperationCall>) change -> this.selection.set(Optional.empty()));

		final SortedList<AggregatedOperationCall> sortedData = new SortedList<>(this.sndFilteredData);
		sortedData.comparatorProperty().bind(this.table.comparatorProperty());
		this.table.setItems(sortedData);

		this.minimalDuration.textProperty().bind(this.createDurationStringBindingForSelection(AggregatedOperationCall::getMinDuration));
		this.maximalDuration.textProperty().bind(this.createDurationStringBindingForSelection(AggregatedOperationCall::getMaxDuration));
		this.medianDuration.textProperty().bind(this.createDurationStringBindingForSelection(AggregatedOperationCall::getMedianDuration));
		this.totalDuration.textProperty().bind(this.createDurationStringBindingForSelection(AggregatedOperationCall::getTotalDuration));
		this.meanDuration.textProperty().bind(this.createDurationStringBindingForSelection(AggregatedOperationCall::getMeanDuration));
		this.container.textProperty().bind(this.createStringBindingForSelection(AggregatedOperationCall::getContainer));
		this.component.textProperty().bind(this.createStringBindingForSelection(AggregatedOperationCall::getComponent));
		this.operation.textProperty().bind(this.createStringBindingForSelection(AggregatedOperationCall::getOperation));
		this.failed.textProperty().bind(this.createStringBindingForSelection(AggregatedOperationCall::getFailedCause));
		this.calls.textProperty().bind(this.createStringBindingForSelection(AggregatedOperationCall::getCalls));

		this.counter.textProperty().bind(Bindings.createStringBinding(() -> sortedData.size() + " " + this.resources.getString("AggregatedCallsView.lblCounter.text"), sortedData));
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
		this.selection.set(Optional.ofNullable(this.table.getSelectionModel().getSelectedItem()));
	}

	public void showAllMethods() {
		this.fstFilteredData.setPredicate(null);
	}

	public void showJustFailedMethods() {
		this.fstFilteredData.setPredicate(call -> call.isFailed());
	}

	public void useRegExp() {
		final String regExpr = this.regexpfilter.getText();

		if ((regExpr == null) || regExpr.isEmpty() || !this.isRegex(regExpr)) {
			this.sndFilteredData.setPredicate(null);
		} else {
			this.sndFilteredData.setPredicate(call -> call.getOperation().matches(regExpr));
		}
	}

	private boolean isRegex(final String str) {
		try {
			Pattern.compile(str);
			return true;
		} catch (final PatternSyntaxException e) {
			return false;
		}
	}

}
