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
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import kieker.diagnosis.domain.AggregatedOperationCall;
import kieker.diagnosis.model.DataModel;

/**
 * @author Nils Christian Ehmke
 */
public final class AggregatedCallsViewController {

	private final DataModel dataModel = DataModel.getInstance();

	private FilteredList<AggregatedOperationCall> fstFilteredData;
	private FilteredList<AggregatedOperationCall> sndFilteredData;

	private final SimpleObjectProperty<Optional<AggregatedOperationCall>> selection = new SimpleObjectProperty<>(Optional.empty());

	@FXML private TableView<AggregatedOperationCall> table;
	@FXML private TextField regexpfilter;

	@FXML private TextField container;
	@FXML private TextField component;
	@FXML private TextField operation;
	@FXML private TextField failed;
	@FXML private TextField calls;
	@FXML private TextField minimalDuration;
	@FXML private TextField meanDuration;
	@FXML private TextField medianDuration;
	@FXML private TextField maximalDuration;
	@FXML private TextField totalDuration;

	public void initialize() {
		this.fstFilteredData = new FilteredList<>(this.dataModel.getAggregatedOperationCalls());
		this.sndFilteredData = new FilteredList<AggregatedOperationCall>(this.fstFilteredData);

		final SortedList<AggregatedOperationCall> sortedData = new SortedList<>(this.sndFilteredData);

		sortedData.comparatorProperty().bind(this.table.comparatorProperty());

		this.table.setItems(sortedData);

		this.container.textProperty().bind(Bindings.createStringBinding(() -> this.selection.get().map(call -> call.getContainer()).orElse("N/A"), this.selection));
		this.component.textProperty().bind(Bindings.createStringBinding(() -> this.selection.get().map(call -> call.getComponent()).orElse("N/A"), this.selection));
		this.operation.textProperty().bind(Bindings.createStringBinding(() -> this.selection.get().map(call -> call.getOperation()).orElse("N/A"), this.selection));
		this.failed.textProperty().bind(Bindings.createStringBinding(() -> this.selection.get().map(call -> call.getFailedCause()).orElse("N/A"), this.selection));
		this.calls.textProperty().bind(Bindings.createStringBinding(() -> this.selection.get().map(call -> Integer.toString(call.getCalls())).orElse("N/A"), this.selection));

		this.minimalDuration.textProperty().bind(
				Bindings.createStringBinding(() -> this.selection.get().map(call -> Long.toString(call.getMinDuration())).orElse("N/A"), this.selection));
		this.meanDuration.textProperty().bind(
				Bindings.createStringBinding(() -> this.selection.get().map(call -> Long.toString(call.getMeanDuration())).orElse("N/A"), this.selection));
		this.medianDuration.textProperty().bind(
				Bindings.createStringBinding(() -> this.selection.get().map(call -> Long.toString(call.getMedianDuration())).orElse("N/A"), this.selection));
		this.maximalDuration.textProperty().bind(
				Bindings.createStringBinding(() -> this.selection.get().map(call -> Long.toString(call.getMaxDuration())).orElse("N/A"), this.selection));
		this.totalDuration.textProperty().bind(
				Bindings.createStringBinding(() -> this.selection.get().map(call -> Long.toString(call.getTotalDuration())).orElse("N/A"), this.selection));
	}

	public void selectCall(final MouseEvent event) {
		this.selection.set(Optional.of(this.table.getSelectionModel().getSelectedItem()));
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
