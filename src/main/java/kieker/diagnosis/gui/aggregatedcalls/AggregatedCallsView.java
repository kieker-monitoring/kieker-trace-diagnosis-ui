package kieker.diagnosis.gui.aggregatedcalls;

import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import kieker.diagnosis.gui.AbstractView;
import kieker.diagnosis.service.data.domain.AggregatedOperationCall;

public class AggregatedCallsView extends AbstractView {

	private TableView<AggregatedOperationCall> ivTable;

	private RadioButton ivShowAllButton;
	private RadioButton ivShowJustFailedButton;
	private RadioButton ivShowJustSuccessful;

	private TextField ivFilterContainer;
	private TextField ivFilterComponent;
	private TextField ivFilterOperation;
	private TextField ivFilterException;

	private TextField ivMinimalDuration;
	private TextField ivMaximalDuration;
	private TextField ivMedianDuration;
	private TextField ivTotalDuration;
	private TextField ivMeanDuration;
	private TextField ivContainer;
	private TextField ivComponent;
	private TextField ivOperation;
	private TextField ivFailed;
	private TextField ivCalls;

	private TextField ivCounter;

	public TableView<AggregatedOperationCall> getTable( ) {
		return ivTable;
	}

	public void setTable( final TableView<AggregatedOperationCall> aTable ) {
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

	public TextField getFilterException( ) {
		return ivFilterException;
	}

	public void setFilterException( final TextField aFilterException ) {
		ivFilterException = aFilterException;
	}

	public TextField getMinimalDuration( ) {
		return ivMinimalDuration;
	}

	public void setMinimalDuration( final TextField aMinimalDuration ) {
		ivMinimalDuration = aMinimalDuration;
	}

	public TextField getMaximalDuration( ) {
		return ivMaximalDuration;
	}

	public void setMaximalDuration( final TextField aMaximalDuration ) {
		ivMaximalDuration = aMaximalDuration;
	}

	public TextField getMedianDuration( ) {
		return ivMedianDuration;
	}

	public void setMedianDuration( final TextField aMedianDuration ) {
		ivMedianDuration = aMedianDuration;
	}

	public TextField getTotalDuration( ) {
		return ivTotalDuration;
	}

	public void setTotalDuration( final TextField aTotalDuration ) {
		ivTotalDuration = aTotalDuration;
	}

	public TextField getMeanDuration( ) {
		return ivMeanDuration;
	}

	public void setMeanDuration( final TextField aMeanDuration ) {
		ivMeanDuration = aMeanDuration;
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

	public TextField getFailed( ) {
		return ivFailed;
	}

	public void setFailed( final TextField aFailed ) {
		ivFailed = aFailed;
	}

	public TextField getCalls( ) {
		return ivCalls;
	}

	public void setCalls( final TextField aCalls ) {
		ivCalls = aCalls;
	}

	public TextField getCounter( ) {
		return ivCounter;
	}

	public void setCounter( final TextField aCounter ) {
		ivCounter = aCounter;
	}

}
