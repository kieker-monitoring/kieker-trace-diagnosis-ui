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

package kieker.diagnosis.gui.calls;

import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import jfxtras.scene.control.CalendarTimeTextField;
import kieker.diagnosis.gui.AbstractView;
import kieker.diagnosis.gui.InjectComponent;
import kieker.diagnosis.service.data.domain.OperationCall;

public class CallsView extends AbstractView {

	@InjectComponent
	private TableView<OperationCall> ivTable;

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
	private TextField ivFilterTraceID;
	@InjectComponent
	private TextField ivFilterException;

	@InjectComponent
	private DatePicker ivFilterLowerDate;
	@InjectComponent
	private CalendarTimeTextField ivFilterLowerTime;
	@InjectComponent
	private DatePicker ivFilterUpperDate;
	@InjectComponent
	private CalendarTimeTextField ivFilterUpperTime;

	@InjectComponent
	private TextField ivContainer;
	@InjectComponent
	private TextField ivComponent;
	@InjectComponent
	private TextField ivOperation;
	@InjectComponent
	private TextField ivTimestamp;
	@InjectComponent
	private TextField ivDuration;
	@InjectComponent
	private TextField ivTraceID;
	@InjectComponent
	private TextField ivFailed;

	@InjectComponent
	private TextField ivCounter;

	public TableView<OperationCall> getTable( ) {
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

	public TextField getContainer( ) {
		return ivContainer;
	}

	public TextField getComponent( ) {
		return ivComponent;
	}

	public TextField getOperation( ) {
		return ivOperation;
	}

	public TextField getTimestamp( ) {
		return ivTimestamp;
	}

	public TextField getDuration( ) {
		return ivDuration;
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
