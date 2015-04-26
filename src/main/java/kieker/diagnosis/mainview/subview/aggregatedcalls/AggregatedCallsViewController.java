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

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import kieker.diagnosis.domain.AggregatedOperationCall;
import kieker.diagnosis.model.DataModel;

/**
 * @author Nils Christian Ehmke
 */
public final class AggregatedCallsViewController {

	private final DataModel dataModel = DataModel.getInstance();

	private FilteredList<AggregatedOperationCall> fstFilteredData;
	private FilteredList<AggregatedOperationCall> sndFilteredData;

	@FXML private TableView<AggregatedOperationCall> table;
	@FXML private TextField regexpfilter;

	public void initialize() {
		this.fstFilteredData = new FilteredList<>(this.dataModel.getAggregatedOperationCalls());
		this.sndFilteredData = new FilteredList<AggregatedOperationCall>(this.fstFilteredData);
		final SortedList<AggregatedOperationCall> sortedData = new SortedList<>(this.sndFilteredData);

		sortedData.comparatorProperty().bind(this.table.comparatorProperty());

		this.table.setItems(sortedData);
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
		}

		this.sndFilteredData.setPredicate(call -> call.getOperation().matches(regExpr));
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
