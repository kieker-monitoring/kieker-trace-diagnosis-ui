/***************************************************************************
 * Copyright 2015 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.czi;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import kieker.common.record.IMonitoringRecord;
import kieker.diagnosis.domain.DatabaseOperationCall;
import kieker.diagnosis.model.importer.stages.ReadingComposite;
import teetime.framework.AnalysisConfiguration;
import teetime.stage.CollectorSink;
import teetime.stage.MultipleInstanceOfFilter;

/**
 * A {@code TeeTime} configuration for the import and analysis of database
 * related monitoring logs.
 * 
 * @author Christian Zirkelbach
 */
public class DatabaseImportAnalysisConfiguration extends AnalysisConfiguration {

	private List<DatabaseOperationCall> databaseOperationCalls = new LinkedList<DatabaseOperationCall>();
	private final CollectorSink<DatabaseOperationCall> callCollector;

	public DatabaseImportAnalysisConfiguration(final File importDirectory) {

		this.callCollector = new CollectorSink<>(databaseOperationCalls);

		// Create the stages
		final ReadingComposite reader = new ReadingComposite(importDirectory);
		final MultipleInstanceOfFilter<IMonitoringRecord> typeFilter = new MultipleInstanceOfFilter<>();
		final DatabaseRecordTransformator transformator = new DatabaseRecordTransformator();

		// Connect the stages
		AnalysisConfiguration.connectIntraThreads(reader.getOutputPort(),
				typeFilter.getInputPort());

		AnalysisConfiguration.connectIntraThreads(
				typeFilter.getOutputPortForType(IMonitoringRecord.class),
				transformator.getInputPort());

		AnalysisConfiguration.connectIntraThreads(
				transformator.getOutputPort(),
				this.callCollector.getInputPort());
		
		// Make sure that the producer is executed by the analysis
		super.addThreadableStage(reader);
	}

	public List<DatabaseOperationCall> getDatabaseOperationCalls() {
		return this.databaseOperationCalls;
	}
}