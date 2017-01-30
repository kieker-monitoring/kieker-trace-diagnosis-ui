package kieker.diagnosis.gui.aggregatedcalls;

import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import kieker.diagnosis.gui.AbstractView;
import kieker.diagnosis.gui.InjectComponent;
import kieker.diagnosis.service.data.domain.AggregatedOperationCall;

public class AggregatedCallsView extends AbstractView {

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
