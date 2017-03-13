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

package kieker.diagnosis.application.gui.traces;

import kieker.diagnosis.application.service.data.domain.OperationCall;
import kieker.diagnosis.architecture.gui.AbstractView;
import kieker.diagnosis.architecture.gui.AutowiredElement;

import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableView;

import org.springframework.stereotype.Component;

import jfxtras.scene.control.CalendarTimeTextField;

/**
 * @author Nils Christian Ehmke
 */
@Component
final class TracesView extends AbstractView {

	@AutowiredElement
	private TreeTableView<OperationCall> ivTreetable;

	@AutowiredElement
	private RadioButton ivShowAllButton;
	@AutowiredElement
	private RadioButton ivShowJustFailedButton;
	@AutowiredElement
	private RadioButton ivShowJustFailureContainingButton;
	@AutowiredElement
	private RadioButton ivShowJustSuccessful;

	@AutowiredElement
	private TextField ivFilterContainer;
	@AutowiredElement
	private TextField ivFilterComponent;
	@AutowiredElement
	private TextField ivFilterOperation;
	@AutowiredElement
	private TextField ivFilterTraceID;
	@AutowiredElement
	private TextField ivFilterException;

	@AutowiredElement
	private DatePicker ivFilterLowerDate;
	@AutowiredElement
	private CalendarTimeTextField ivFilterLowerTime;
	@AutowiredElement
	private DatePicker ivFilterUpperDate;
	@AutowiredElement
	private CalendarTimeTextField ivFilterUpperTime;

	@AutowiredElement
	private TextField ivTraceDepth;
	@AutowiredElement
	private TextField ivTraceSize;
	@AutowiredElement
	private TextField ivTimestamp;
	@AutowiredElement
	private TextField ivContainer;
	@AutowiredElement
	private TextField ivComponent;
	@AutowiredElement
	private TextField ivOperation;
	@AutowiredElement
	private TextField ivDuration;
	@AutowiredElement
	private TextField ivPercent;
	@AutowiredElement
	private TextField ivTraceID;
	@AutowiredElement
	private TextField ivFailed;

	@AutowiredElement
	private TextField ivCounter;

	public TreeTableView<OperationCall> getTreetable( ) {
		return ivTreetable;
	}

	public RadioButton getShowAllButton( ) {
		return ivShowAllButton;
	}

	public RadioButton getShowJustFailedButton( ) {
		return ivShowJustFailedButton;
	}

	public RadioButton getShowJustFailureContainingButton( ) {
		return ivShowJustFailureContainingButton;
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

	public TextField getFilterTraceID( ) {
		return ivFilterTraceID;
	}

	public TextField getFilterException( ) {
		return ivFilterException;
	}

	public DatePicker getFilterLowerDate( ) {
		return ivFilterLowerDate;
	}

	public CalendarTimeTextField getFilterLowerTime( ) {
		return ivFilterLowerTime;
	}

	public DatePicker getFilterUpperDate( ) {
		return ivFilterUpperDate;
	}

	public CalendarTimeTextField getFilterUpperTime( ) {
		return ivFilterUpperTime;
	}

	public TextField getTraceDepth( ) {
		return ivTraceDepth;
	}

	public TextField getTraceSize( ) {
		return ivTraceSize;
	}

	public TextField getTimestamp( ) {
		return ivTimestamp;
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

	public TextField getDuration( ) {
		return ivDuration;
	}

	public TextField getPercent( ) {
		return ivPercent;
	}

	public TextField getTraceID( ) {
		return ivTraceID;
	}

	public TextField getFailed( ) {
		return ivFailed;
	}

	public TextField getCounter( ) {
		return ivCounter;
	}

}
