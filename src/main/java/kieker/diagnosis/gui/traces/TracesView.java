package kieker.diagnosis.gui.traces;

import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableView;
import jfxtras.scene.control.CalendarTimeTextField;
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.gui.AbstractView;

public class TracesView extends AbstractView {

	private TreeTableView<OperationCall> ivTreetable;

	private RadioButton ivShowAllButton;
	private RadioButton ivShowJustFailedButton;
	private RadioButton ivShowJustFailureContainingButton;
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

	private TextField ivTraceDepth;
	private TextField ivTraceSize;
	private TextField ivTimestamp;
	private TextField ivContainer;
	private TextField ivComponent;
	private TextField ivOperation;
	private TextField ivDuration;
	private TextField ivPercent;
	private TextField ivTraceID;
	private TextField ivFailed;

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
