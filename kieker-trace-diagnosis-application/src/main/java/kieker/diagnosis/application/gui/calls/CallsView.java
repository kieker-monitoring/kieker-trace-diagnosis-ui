package kieker.diagnosis.application.gui.calls;

import kieker.diagnosis.application.service.data.domain.OperationCall;
import kieker.diagnosis.architecture.gui.AbstractView;
import kieker.diagnosis.architecture.gui.AutowiredElement;

import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import org.springframework.stereotype.Component;

import jfxtras.scene.control.CalendarTimeTextField;

@Component
final class CallsView extends AbstractView {

	@AutowiredElement
	private TableView<OperationCall> ivTable;

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
	private TextField ivContainer;
	@AutowiredElement
	private TextField ivComponent;
	@AutowiredElement
	private TextField ivOperation;
	@AutowiredElement
	private TextField ivTimestamp;
	@AutowiredElement
	private TextField ivDuration;
	@AutowiredElement
	private TextField ivTraceID;
	@AutowiredElement
	private TextField ivFailed;

	@AutowiredElement
	private TextField ivCounter;

	TableView<OperationCall> getTable( ) {
		return ivTable;
	}

	RadioButton getShowAllButton( ) {
		return ivShowAllButton;
	}

	RadioButton getShowJustFailedButton( ) {
		return ivShowJustFailedButton;
	}

	RadioButton getShowJustSuccessful( ) {
		return ivShowJustSuccessful;
	}

	TextField getFilterContainer( ) {
		return ivFilterContainer;
	}

	TextField getFilterComponent( ) {
		return ivFilterComponent;
	}

	TextField getFilterOperation( ) {
		return ivFilterOperation;
	}

	TextField getFilterTraceID( ) {
		return ivFilterTraceID;
	}

	TextField getFilterException( ) {
		return ivFilterException;
	}

	DatePicker getFilterLowerDate( ) {
		return ivFilterLowerDate;
	}

	CalendarTimeTextField getFilterLowerTime( ) {
		return ivFilterLowerTime;
	}

	DatePicker getFilterUpperDate( ) {
		return ivFilterUpperDate;
	}

	CalendarTimeTextField getFilterUpperTime( ) {
		return ivFilterUpperTime;
	}

	TextField getContainer( ) {
		return ivContainer;
	}

	TextField getComponent( ) {
		return ivComponent;
	}

	TextField getOperation( ) {
		return ivOperation;
	}

	TextField getTimestamp( ) {
		return ivTimestamp;
	}

	TextField getDuration( ) {
		return ivDuration;
	}

	TextField getTraceID( ) {
		return ivTraceID;
	}

	TextField getFailed( ) {
		return ivFailed;
	}

	TextField getCounter( ) {
		return ivCounter;
	}

}
