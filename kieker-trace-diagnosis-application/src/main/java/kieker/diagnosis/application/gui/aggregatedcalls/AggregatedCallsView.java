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

package kieker.diagnosis.application.gui.aggregatedcalls;

import kieker.diagnosis.application.service.data.domain.AggregatedOperationCall;
import kieker.diagnosis.architecture.gui.AbstractView;
import kieker.diagnosis.architecture.gui.AutowiredElement;

import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import org.springframework.stereotype.Component;

/**
 * The view for the aggregated calls.
 *
 * @author Nils Christian Ehmke
 */
@Component
final class AggregatedCallsView extends AbstractView {

	@AutowiredElement
	private TableView<AggregatedOperationCall> ivTable;

	@AutowiredElement
	private RadioButton ivShowAllButton;
	@AutowiredElement
	private RadioButton ivShowJustFailedButton;
	@AutowiredElement
	private RadioButton ivShowJustSuccessful;

	@AutowiredElement
	private TextField ivFilterContainer;
	@AutowiredElement
	private TextField ivFilterComponent;
	@AutowiredElement
	private TextField ivFilterOperation;
	@AutowiredElement
	private TextField ivFilterException;

	@AutowiredElement
	private TextField ivMinimalDuration;
	@AutowiredElement
	private TextField ivMaximalDuration;
	@AutowiredElement
	private TextField ivMedianDuration;
	@AutowiredElement
	private TextField ivTotalDuration;
	@AutowiredElement
	private TextField ivMeanDuration;
	@AutowiredElement
	private TextField ivContainer;
	@AutowiredElement
	private TextField ivComponent;
	@AutowiredElement
	private TextField ivOperation;
	@AutowiredElement
	private TextField ivFailed;
	@AutowiredElement
	private TextField ivCalls;

	@AutowiredElement
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
