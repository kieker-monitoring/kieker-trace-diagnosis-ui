package kieker.diagnosis.gui.monitoringstatistics;

import javafx.scene.control.TextField;
import kieker.diagnosis.gui.AbstractView;
import kieker.diagnosis.gui.InjectComponent;

public class MonitoringStatisticsView extends AbstractView {

	@InjectComponent
	private TextField ivMonitoringlog;
	@InjectComponent
	private TextField ivMonitoringsize;
	@InjectComponent
	private TextField ivAnalysistime;
	@InjectComponent
	private TextField ivBeginofmonitoring;
	@InjectComponent
	private TextField ivEndofmonitoring;
	@InjectComponent
	private TextField ivNumberofcalls;
	@InjectComponent
	private TextField ivNumberoffailedcalls;
	@InjectComponent
	private TextField ivNumberofaggcalls;
	@InjectComponent
	private TextField ivNumberoffailedaggcalls;
	@InjectComponent
	private TextField ivNumberoftraces;
	@InjectComponent
	private TextField ivNumberoffailedtraces;
	@InjectComponent
	private TextField ivNumberoffailuretraces;
	@InjectComponent
	private TextField ivNumberofaggtraces;
	@InjectComponent
	private TextField ivNumberofaggfailedtraces;
	@InjectComponent
	private TextField ivNumberofaggfailuretraces;
	@InjectComponent
	private TextField ivIncompletetraces;
	@InjectComponent
	private TextField ivDanglingrecords;
	@InjectComponent
	private TextField ivIgnoredRecords;

	public TextField getMonitoringlog( ) {
		return ivMonitoringlog;
	}

	public TextField getMonitoringsize( ) {
		return ivMonitoringsize;
	}

	public TextField getAnalysistime( ) {
		return ivAnalysistime;
	}

	public TextField getBeginofmonitoring( ) {
		return ivBeginofmonitoring;
	}

	public TextField getEndofmonitoring( ) {
		return ivEndofmonitoring;
	}

	public TextField getNumberofcalls( ) {
		return ivNumberofcalls;
	}

	public TextField getNumberoffailedcalls( ) {
		return ivNumberoffailedcalls;
	}

	public TextField getNumberofaggcalls( ) {
		return ivNumberofaggcalls;
	}

	public TextField getNumberoffailedaggcalls( ) {
		return ivNumberoffailedaggcalls;
	}

	public TextField getNumberoftraces( ) {
		return ivNumberoftraces;
	}

	public TextField getNumberoffailedtraces( ) {
		return ivNumberoffailedtraces;
	}

	public TextField getNumberoffailuretraces( ) {
		return ivNumberoffailuretraces;
	}

	public TextField getNumberofaggtraces( ) {
		return ivNumberofaggtraces;
	}

	public TextField getNumberofaggfailedtraces( ) {
		return ivNumberofaggfailedtraces;
	}

	public TextField getNumberofaggfailuretraces( ) {
		return ivNumberofaggfailuretraces;
	}

	public TextField getIncompletetraces( ) {
		return ivIncompletetraces;
	}

	public TextField getDanglingrecords( ) {
		return ivDanglingrecords;
	}

	public TextField getIgnoredRecords( ) {
		return ivIgnoredRecords;
	}

}
