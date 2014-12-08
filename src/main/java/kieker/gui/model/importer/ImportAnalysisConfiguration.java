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

package kieker.gui.model.importer;

import java.io.File;
import java.util.List;
import java.util.Vector;

import kieker.common.record.IMonitoringRecord;
import kieker.common.record.flow.IFlowRecord;
import kieker.gui.model.AggregatedExecutionEntry;
import kieker.gui.model.ExecutionEntry;
import kieker.gui.model.RecordEntry;
import kieker.gui.model.importer.filter.Cloner;
import kieker.gui.model.importer.filter.RecordSimplificator;
import kieker.gui.model.importer.filter.TraceAggregator;
import kieker.gui.model.importer.filter.TraceReconstructor;
import teetime.framework.AnalysisConfiguration;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;
import teetime.stage.InstanceOfFilter;
import teetime.stage.className.ClassNameRegistryRepository;
import teetime.stage.io.filesystem.Dir2RecordsFilter;

/**
 * A configuration for the import and analysis of monitoring logs.
 *
 * @author Nils Christian Ehmke
 */
public final class ImportAnalysisConfiguration extends AnalysisConfiguration {

	private final List<RecordEntry> recordsList = new Vector<>(100000);
	private final List<ExecutionEntry> tracesList = new Vector<>(100000);
	private final List<AggregatedExecutionEntry> aggregatedTraces = new Vector<>(100000);

	public ImportAnalysisConfiguration(final File importDirectory) {
		// Create the stages
		final InitialElementProducer<File> producer = new InitialElementProducer<>(importDirectory);
		final Dir2RecordsFilter reader = new Dir2RecordsFilter(new ClassNameRegistryRepository());
		final InstanceOfFilter<IMonitoringRecord, IFlowRecord> typeFilter = new InstanceOfFilter<>(IFlowRecord.class);
		final Cloner<IFlowRecord> fstDistributor = new Cloner<>();
		final RecordSimplificator recordSimplificator = new RecordSimplificator();
		final CollectorSink<RecordEntry> recordCollector = new CollectorSink<>(this.recordsList);
		final TraceReconstructor traceReconstructor = new TraceReconstructor();
		final Cloner<ExecutionEntry> sndDistributor = new Cloner<>();
		final CollectorSink<ExecutionEntry> traceCollector = new CollectorSink<>(this.tracesList);
		final TraceAggregator traceAggregator = new TraceAggregator();
		final CollectorSink<AggregatedExecutionEntry> aggregatedTraceCollector = new CollectorSink<>(this.aggregatedTraces);

		// Connect the stages
		final IPipeFactory pipeFactory = AnalysisConfiguration.PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false);
		pipeFactory.create(producer.getOutputPort(), reader.getInputPort());
		pipeFactory.create(reader.getOutputPort(), typeFilter.getInputPort());
		pipeFactory.create(typeFilter.getOutputPort(), fstDistributor.getInputPort());
		pipeFactory.create(fstDistributor.getFirstOutputPort(), recordSimplificator.getInputPort());
		pipeFactory.create(recordSimplificator.getOutputPort(), recordCollector.getInputPort());
		pipeFactory.create(fstDistributor.getSecondOutputPort(), traceReconstructor.getInputPort());
		pipeFactory.create(traceReconstructor.getOutputPort(), sndDistributor.getInputPort());
		pipeFactory.create(sndDistributor.getFirstOutputPort(), traceCollector.getInputPort());
		pipeFactory.create(sndDistributor.getSecondOutputPort(), traceAggregator.getInputPort());
		pipeFactory.create(traceAggregator.getOutputPort(), aggregatedTraceCollector.getInputPort());

		// Make sure that the producer is executed by the analysis
		super.addThreadableStage(producer);
	}

	public List<RecordEntry> getRecordsList() {
		return this.recordsList;
	}

	public List<ExecutionEntry> getTracesList() {
		return this.tracesList;
	}

	public List<AggregatedExecutionEntry> getAggregatedTraces() {
		return this.aggregatedTraces;
	}

}
