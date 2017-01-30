package kieker.diagnosis.gui.traces;

import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableView;
import jfxtras.scene.control.CalendarTimeTextField;
import kieker.diagnosis.gui.AbstractView;
import kieker.diagnosis.gui.InjectComponent;
import kieker.diagnosis.service.data.domain.OperationCall;

public class TracesView extends AbstractView {

	@InjectComponent
	private TreeTableView<OperationCall> ivTreetable;

	@InjectComponent
	private RadioButton ivShowAllButton;
	@InjectComponent
	private RadioButton ivShowJustFailedButton;
	@InjectComponent
	private RadioButton ivShowJustFailureContainingButton;
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
	private TextField ivTraceDepth;
	@InjectComponent
	private TextField ivTraceSize;
	@InjectComponent
	private TextField ivTimestamp;
	@InjectComponent
	private TextField ivContainer;
	@InjectComponent
	private TextField ivComponent;
	@InjectComponent
	private TextField ivOperation;
	@InjectComponent
	private TextField ivDuration;
	@InjectComponent
	private TextField ivPercent;
	@InjectComponent
	private TextField ivTraceID;
	@InjectComponent
	private TextField ivFailed;

	@InjectComponent
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
