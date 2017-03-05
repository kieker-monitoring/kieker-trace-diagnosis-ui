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

package kieker.diagnosis.gui.aggregatedcalls;

import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import kieker.diagnosis.gui.AbstractView;
import kieker.diagnosis.gui.InjectComponent;
import kieker.diagnosis.service.data.domain.AggregatedOperationCall;

/**
 * The view for the aggregated calls.
 *
 * @author Nils Christian Ehmke
 */
public final class AggregatedCallsView extends AbstractView {

	@InjectComponent
	private TableView<AggregatedOperationCall> ivTable;

	@InjectComponent
	private RadioButton ivShowAllButton;
	@InjectComponent
	private RadioButton ivShowJustFailedButton;
	@InjectComponent
	private RadioButton ivShowJustSuccessful;

	@InjectComponent
	private TextField ivFilterContainer;
	@InjectComponent
	private TextField ivFilterComponent;
	@InjectComponent
	private TextField ivFilterOperation;
	@InjectComponent
	private TextField ivFilterException;

	@InjectComponent
	private TextField ivMinimalDuration;
	@InjectComponent
	private TextField ivMaximalDuration;
	@InjectComponent
	private TextField ivMedianDuration;
	@InjectComponent
	private TextField ivTotalDuration;
	@InjectComponent
	private TextField ivMeanDuration;
	@InjectComponent
	private TextField ivContainer;
	@InjectComponent
	private TextField ivComponent;
	@InjectComponent
	private TextField ivOperation;
	@InjectComponent
	private TextField ivFailed;
	@InjectComponent
	private TextField ivCalls;

	@InjectComponent
	private TextField ivCounter;

	public TableView<AggregatedOperationCall> getTable( ) {
		return ivTable;
	}

	public RadioButton getShowAllButton( ) {
		return ivShowAllButton;
	}

	public RadioButton getShowJustFailedButton( ) {
		return ivShowJustFailedButton;
	}

	public RadioButton getShowJustSuccessful( ) {
		return ivShowJustSuccessful;
	}

	public TextField getFilterContainer( ) {
		return ivFilterContainer;
	}

	public TextField getFilterComponent( ) {
		return ivFilterComponent;
	}

	public TextField getFilterOperation( ) {
		return ivFilterOperation;
	}

	public TextField getFilterException( ) {
		return ivFilterException;
	}

	public TextField getMinimalDuration( ) {
		return ivMinimalDuration;
	}

	public TextField getMaximalDuration( ) {
		return ivMaximalDuration;
	}

	public TextField getMedianDuration( ) {
		return ivMedianDuration;
	}

	public TextField getTotalDuration( ) {
		return ivTotalDuration;
	}

	public TextField getMeanDuration( ) {
		return ivMeanDuration;
	}

	public TextField getContainer( ) {
		return ivContainer;
	}

	public TextField getComponent( ) {
		return ivComponent;
	}

	public TextField getOperation( ) {
		return ivOperation;
	}

	public TextField getFailed( ) {
		return ivFailed;
	}

	public TextField getCalls( ) {
		return ivCalls;
	}

	public TextField getCounter( ) {
		return ivCounter;
	}

}
