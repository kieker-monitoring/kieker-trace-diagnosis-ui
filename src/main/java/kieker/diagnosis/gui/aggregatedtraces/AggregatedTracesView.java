package kieker.diagnosis.gui.aggregatedtraces;

import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableView;
import kieker.diagnosis.gui.AbstractView;
import kieker.diagnosis.gui.InjectComponent;
import kieker.diagnosis.service.data.domain.AggregatedOperationCall;

public class AggregatedTracesView extends AbstractView {

	@InjectComponent
	private TreeTableView<AggregatedOperationCall> ivTreetable;

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
	private TextField ivFilterException;

	@InjectComponent
	private TextField ivMedianDuration;
	@InjectComponent
	private TextField ivTotalDuration;
	@InjectComponent
	private TextField ivMinDuration;
	@InjectComponent
	private TextField ivAvgDuration;
	@InjectComponent
	private TextField ivMaxDuration;
	@InjectComponent
	private TextField ivTraceDepth;
	@InjectComponent
	private TextField ivTraceSize;
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

	public TreeTableView<AggregatedOperationCall> getTreetable( ) {
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

	public TextField getFilterException( ) {
		return ivFilterException;
	}

	public TextField getMedianDuration( ) {
		return ivMedianDuration;
	}

	public TextField getTotalDuration( ) {
		return ivTotalDuration;
	}

	public TextField getMinDuration( ) {
		return ivMinDuration;
	}

	public TextField getAvgDuration( ) {
		return ivAvgDuration;
	}

	public TextField getMaxDuration( ) {
		return ivMaxDuration;
	}

	public TextField getTraceDepth( ) {
		return ivTraceDepth;
	}

	public TextField getTraceSize( ) {
		return ivTraceSize;
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
