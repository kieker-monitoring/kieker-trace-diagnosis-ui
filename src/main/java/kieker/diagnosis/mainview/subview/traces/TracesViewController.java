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
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.domain.Trace;
import kieker.diagnosis.mainview.subview.util.LazyOperationCallTreeItem;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;

/**
 * The sub-controller responsible for the sub-view presenting the available traces.
 *
 * @author Nils Christian Ehmke
 */
public final class TracesViewController {

	private final PropertiesModel propertiesModel = PropertiesModel.getInstance();
	private final DataModel dataModel = DataModel.getInstance();

	@FXML private TreeTableView<OperationCall> treetable;
	@FXML private TextField counter;

	@FXML private ResourceBundle resources;

	public void initialize() {
		this.reloadTreetable();

		final ObservableList<Trace> traces = this.dataModel.getTraces();
		final ObservableValue<Integer> maxTracesToShow = this.propertiesModel.getMaxTracesToShow();

		traces.addListener((final Change<? extends Trace> c) -> this.reloadTreetable());

		this.counter.textProperty().bind(Bindings.createStringBinding(() -> traces.size() + " " + String.format(this.resources.getString("TracesView.lblCounter.text"),
				maxTracesToShow.getValue()), traces, maxTracesToShow));
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
