package kieker.gui.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

import kieker.gui.model.domain.AggregatedExecutionEntry;
import kieker.gui.model.domain.ExecutionEntry;
import kieker.gui.model.domain.RecordEntry;
import kieker.gui.model.importer.ImportAnalysisConfiguration;
import teetime.framework.Analysis;

/**
 * A container for data used within this application.
 * 
 * @author Nils Christian Ehmke
 */
public final class DataModel extends Observable {

	private List<RecordEntry> records = Collections.emptyList();
	private List<ExecutionEntry> traces = Collections.emptyList();
	private List<AggregatedExecutionEntry> aggregatedTraces;

	public DataModel() {}

	public void loadMonitoringLogFromFS(final String directory) {
		// Load and analyze the monitoring logs from the given directory
		final File importDirectory = new File(directory);
		final ImportAnalysisConfiguration analysisConfiguration = new ImportAnalysisConfiguration(importDirectory);
		final Analysis analysis = new Analysis(analysisConfiguration);
		analysis.init();
		analysis.start();

		// Store the results from the analysis
		this.records = analysisConfiguration.getRecordsList();
		this.traces = analysisConfiguration.getTracesList();
		this.aggregatedTraces = analysisConfiguration.getAggregatedTraces();

		this.setChanged();
		this.notifyObservers();
	}

	public List<RecordEntry> getRecordsCopy() {
		return new ArrayList<>(this.records);
	}

	public List<ExecutionEntry> getTracesCopy() {
		return new ArrayList<>(this.traces);
	}

	public List<AggregatedExecutionEntry> getAggregatedTracesCopy() {
		return new ArrayList<>(this.aggregatedTraces);
	}

}
