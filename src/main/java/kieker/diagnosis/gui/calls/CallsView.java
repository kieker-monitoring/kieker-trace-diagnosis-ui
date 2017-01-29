package kieker.diagnosis.gui.calls;

import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import jfxtras.scene.control.CalendarTimeTextField;
import kieker.diagnosis.gui.AbstractView;
import kieker.diagnosis.service.data.domain.OperationCall;

public class CallsView extends AbstractView {

	private TableView<OperationCall> ivTable;

	private RadioButton ivShowAllButton;
	private RadioButton ivShowJustFailedButton;
	private RadioButton ivShowJustSuccessful;

	private TextField ivFilterContainer;
	private TextField ivFilterComponent;
	private TextField ivFilterOperation;
	private TextField ivFilterTraceID;
	private TextField ivFilterException;

	private DatePicker ivFilterLowerDate;
	private CalendarTimeTextField ivFilterLowerTime;
	private DatePicker ivFilterUpperDate;
	private CalendarTimeTextField ivFilterUpperTime;

	private TextField ivContainer;
	private TextField ivComponent;
	private TextField ivOperation;
	private TextField ivTimestamp;
	private TextField ivDuration;
	private TextField ivTraceID;
	private TextField ivFailed;

	private TextField ivCounter;

	public TableView<OperationCall> getTable( ) {
		return ivTable;
	}

	public void setTable( final TableView<OperationCall> aTable ) {
		ivTable = aTable;
	}

	public RadioButton getShowAllButton( ) {
		return ivShowAllButton;
	}

	public void setShowAllButton( final RadioButton aShowAllButton ) {
		ivShowAllButton = aShowAllButton;
	}

	public RadioButton getShowJustFailedButton( ) {
		return ivShowJustFailedButton;
	}

	public void setShowJustFailedButton( final RadioButton aShowJustFailedButton ) {
		ivShowJustFailedButton = aShowJustFailedButton;
	}

	public RadioButton getShowJustSuccessful( ) {
		return ivShowJustSuccessful;
	}

	public void setShowJustSuccessful( final RadioButton aShowJustSuccessful ) {
		ivShowJustSuccessful = aShowJustSuccessful;
	}

	public TextField getFilterContainer( ) {
		return ivFilterContainer;
	}

	public void setFilterContainer( final TextField aFilterContainer ) {
		ivFilterContainer = aFilterContainer;
	}

	public TextField getFilterComponent( ) {
		return ivFilterComponent;
	}

	public void setFilterComponent( final TextField aFilterComponent ) {
		ivFilterComponent = aFilterComponent;
	}

	public TextField getFilterOperation( ) {
		return ivFilterOperation;
	}

	public void setFilterOperation( final TextField aFilterOperation ) {
		ivFilterOperation = aFilterOperation;
	}

	public TextField getFilterTraceID( ) {
		return ivFilterTraceID;
	}

	public void setFilterTraceID( final TextField aFilterTraceID ) {
		ivFilterTraceID = aFilterTraceID;
	}

	public TextField getFilterException( ) {
		return ivFilterException;
	}

	public void setFilterException( final TextField aFilterException ) {
		ivFilterException = aFilterException;
	}

	public DatePicker getFilterLowerDate( ) {
		return ivFilterLowerDate;
	}

	public void setFilterLowerDate( final DatePicker aFilterLowerDate ) {
		ivFilterLowerDate = aFilterLowerDate;
	}

	public CalendarTimeTextField getFilterLowerTime( ) {
		return ivFilterLowerTime;
	}

	public void setFilterLowerTime( final CalendarTimeTextField aFilterLowerTime ) {
		ivFilterLowerTime = aFilterLowerTime;
	}

	public DatePicker getFilterUpperDate( ) {
		return ivFilterUpperDate;
	}

	public void setFilterUpperDate( final DatePicker aFilterUpperDate ) {
		ivFilterUpperDate = aFilterUpperDate;
	}

	public CalendarTimeTextField getFilterUpperTime( ) {
		return ivFilterUpperTime;
	}

	public void setFilterUpperTime( final CalendarTimeTextField aFilterUpperTime ) {
		ivFilterUpperTime = aFilterUpperTime;
	}

	public TextField getContainer( ) {
		return ivContainer;
	}

	public void setContainer( final TextField aContainer ) {
		ivContainer = aContainer;
	}

	public TextField getComponent( ) {
		return ivComponent;
	}

	public void setComponent( final TextField aComponent ) {
		ivComponent = aComponent;
	}

	public TextField getOperation( ) {
		return ivOperation;
	}

	public void setOperation( final TextField aOperation ) {
		ivOperation = aOperation;
	}

	public TextField getTimestamp( ) {
		return ivTimestamp;
	}

	public void setTimestamp( final TextField aTimestamp ) {
		ivTimestamp = aTimestamp;
	}

	public TextField getDuration( ) {
		return ivDuration;
	}

	public void setDuration( final TextField aDuration ) {
		ivDuration = aDuration;
	}

	public TextField getTraceID( ) {
		return ivTraceID;
	}

	public void setTraceID( final TextField aTraceID ) {
		ivTraceID = aTraceID;
	}

	public TextField getFailed( ) {
		return ivFailed;
	}

	public void setFailed( final TextField aFailed ) {
		ivFailed = aFailed;
	}

	public TextField getCounter( ) {
		return ivCounter;
	}

	public void setCounter( final TextField aCounter ) {
		ivCounter = aCounter;
	}

}
