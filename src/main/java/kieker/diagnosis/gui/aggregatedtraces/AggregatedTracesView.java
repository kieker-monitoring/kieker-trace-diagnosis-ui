package kieker.diagnosis.gui.aggregatedtraces;

import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableView;
import kieker.diagnosis.domain.AggregatedOperationCall;
import kieker.diagnosis.gui.AbstractView;

public class AggregatedTracesView extends AbstractView {

	private TreeTableView<AggregatedOperationCall> ivTreetable;

	private RadioButton ivShowAllButton;
	private RadioButton ivShowJustFailedButton;
	private RadioButton ivShowJustFailureContainingButton;
	private RadioButton ivShowJustSuccessful;

	private TextField ivFilterContainer;
	private TextField ivFilterComponent;
	private TextField ivFilterOperation;
	private TextField ivFilterException;

	private TextField ivMedianDuration;
	private TextField ivTotalDuration;
	private TextField ivMinDuration;
	private TextField ivAvgDuration;
	private TextField ivMaxDuration;
	private TextField ivTraceDepth;
	private TextField ivTraceSize;
	private TextField ivContainer;
	private TextField ivComponent;
	private TextField ivOperation;
	private TextField ivFailed;
	private TextField ivCalls;

	private TextField ivCounter;

	public RadioButton getShowAllButton( ) {
		return ivShowAllButton;
	}

	public void setShowAllButton( final RadioButton aShowAllButton ) {
		ivShowAllButton = aShowAllButton;
	}

	public TreeTableView<AggregatedOperationCall> getTreetable( ) {
		return ivTreetable;
	}

	public void setTreetable( final TreeTableView<AggregatedOperationCall> aTreetable ) {
		ivTreetable = aTreetable;
	}

	public RadioButton getShowJustFailedButton( ) {
		return ivShowJustFailedButton;
	}

	public void setShowJustFailedButton( final RadioButton aShowJustFailedButton ) {
		ivShowJustFailedButton = aShowJustFailedButton;
	}

	public RadioButton getShowJustFailureContainingButton( ) {
		return ivShowJustFailureContainingButton;
	}

	public void setShowJustFailureContainingButton( final RadioButton aShowJustFailureContainingButton ) {
		ivShowJustFailureContainingButton = aShowJustFailureContainingButton;
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

	public TextField getMinDuration( ) {
		return ivMinDuration;
	}

	public void setMinDuration( final TextField aMinDuration ) {
		ivMinDuration = aMinDuration;
	}

	public TextField getAvgDuration( ) {
		return ivAvgDuration;
	}

	public void setAvgDuration( final TextField aAvgDuration ) {
		ivAvgDuration = aAvgDuration;
	}

	public TextField getMaxDuration( ) {
		return ivMaxDuration;
	}

	public void setMaxDuration( final TextField aMaxDuration ) {
		ivMaxDuration = aMaxDuration;
	}

	public TextField getTraceDepth( ) {
		return ivTraceDepth;
	}

	public void setTraceDepth( final TextField aTraceDepth ) {
		ivTraceDepth = aTraceDepth;
	}

	public TextField getTraceSize( ) {
		return ivTraceSize;
	}

	public void setTraceSize( final TextField aTraceSize ) {
		ivTraceSize = aTraceSize;
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
