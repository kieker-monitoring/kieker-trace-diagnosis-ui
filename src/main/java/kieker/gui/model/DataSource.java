/***************************************************************************
 * Copyright 2014 Kieker Project (http://kieker-monitoring.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/

package kieker.gui.model;

import java.io.File;
import java.util.Collections;
import java.util.List;

import kieker.gui.model.importer.ImportAnalysisConfiguration;
import teetime.framework.Analysis;

/**
 * A container for data used within this application.
 *
 * @author Nils Christian Ehmke
 */
public final class DataSource {

	private List<RecordEntry> records = Collections.emptyList();
	private List<ExecutionEntry> traces = Collections.emptyList();
	private List<AggregatedExecutionEntry> aggregatedTraces;

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
	}

	public List<RecordEntry> getRecords() {
		return this.records;
	}

	public List<ExecutionEntry> getTraces() {
		return this.traces;
	}

	public List<AggregatedExecutionEntry> getAggregatedTrace() {
		return this.aggregatedTraces;
	}

}
