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

package kieker.gui.common.importer;

import java.io.File;
import java.util.List;
import java.util.Vector;

import kieker.common.record.IMonitoringRecord;
import kieker.common.record.flow.IFlowRecord;
import kieker.common.record.misc.KiekerMetadataRecord;
import kieker.gui.common.domain.AggregatedExecution;
import kieker.gui.common.domain.Execution;
import kieker.gui.common.domain.Record;
import kieker.gui.common.importer.stages.FailedAggregatedTraceFilter;
import kieker.gui.common.importer.stages.FailedTraceFilter;
import kieker.gui.common.importer.stages.FailureContainingAggregatedTraceFilter;
import kieker.gui.common.importer.stages.FailureContainingTraceFilter;
import kieker.gui.common.importer.stages.RecordSimplificator;
import kieker.gui.common.importer.stages.TraceAggregator;
import kieker.gui.common.importer.stages.TraceReconstructor;
import teetime.framework.AnalysisConfiguration;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;
import teetime.stage.MultipleInstanceOfFilter;
import teetime.stage.basic.distributor.CopyByReferenceStrategy;
import teetime.stage.basic.distributor.Distributor;
import teetime.stage.className.ClassNameRegistryRepository;
import teetime.stage.io.filesystem.Dir2RecordsFilter;

/**
 * A configuration for the import and analysis of monitoring logs.
 *
 * @author Nils Christian Ehmke
 */
public final class ImportAnalysisConfiguration extends AnalysisConfiguration {

	private final List<Record> recordsList = new Vector<>(1000);
	private final List<Execution> failedTracesList = new Vector<>(1000);
	private final List<Execution> failureContainingTracesList = new Vector<>(1000);
	private final List<Execution> tracesList = new Vector<>(1000);
	private final List<AggregatedExecution> aggregatedTraces = new Vector<>(1000);
	private final List<AggregatedExecution> failedAggregatedTracesList = new Vector<>(1000);
	private final List<AggregatedExecution> failureContainingAggregatedTracesList = new Vector<>(1000);
	private final List<KiekerMetadataRecord> metadataRecords = new Vector<>(1000);

	public ImportAnalysisConfiguration(final File importDirectory) {
		// Create the stages
		final InitialElementProducer<File> producer = new InitialElementProducer<>(importDirectory);
		final Dir2RecordsFilter reader = new Dir2RecordsFilter(new ClassNameRegistryRepository());
		final MultipleInstanceOfFilter<IMonitoringRecord> typeFilter = new MultipleInstanceOfFilter<>();
		final Distributor<IFlowRecord> fstDistributor = new Distributor<>();
		final RecordSimplificator recordSimplificator = new RecordSimplificator();
		final CollectorSink<Record> recordCollector = new CollectorSink<>(this.recordsList);
		final TraceReconstructor traceReconstructor = new TraceReconstructor();
		final Distributor<Execution> sndDistributor = new Distributor<>();
		final CollectorSink<Execution> traceCollector = new CollectorSink<>(this.tracesList);
		final FailedTraceFilter failedTraceFilter = new FailedTraceFilter();
		final CollectorSink<Execution> failedTraceCollector = new CollectorSink<>(this.failedTracesList);
		final FailureContainingTraceFilter failureContainingTraceFilter = new FailureContainingTraceFilter();
		final CollectorSink<Execution> failureContainingTraceCollector = new CollectorSink<>(this.failureContainingTracesList);
		final TraceAggregator traceAggregator = new TraceAggregator();
		final CollectorSink<AggregatedExecution> aggregatedTraceCollector = new CollectorSink<>(this.aggregatedTraces);
		final CollectorSink<KiekerMetadataRecord> metadataCollector = new CollectorSink<>(this.metadataRecords);
		final FailedAggregatedTraceFilter failedAggregatedTraceFilter = new FailedAggregatedTraceFilter();
		final Distributor<AggregatedExecution> thrdDistributor = new Distributor<>();
		final CollectorSink<AggregatedExecution> failedAggregatedTraceCollector = new CollectorSink<>(this.failedAggregatedTracesList);
		final FailureContainingAggregatedTraceFilter failureContainingAggregatedTraceFilter = new FailureContainingAggregatedTraceFilter();
		final CollectorSink<AggregatedExecution> failureContainingAggregatedTraceCollector = new CollectorSink<>(this.failureContainingAggregatedTracesList);

		// Configure the stages
		fstDistributor.setStrategy(new CopyByReferenceStrategy<IFlowRecord>());
		sndDistributor.setStrategy(new CopyByReferenceStrategy<Execution>());
		thrdDistributor.setStrategy(new CopyByReferenceStrategy<AggregatedExecution>());

		// Connect the stages
		final IPipeFactory pipeFactory = AnalysisConfiguration.PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false);
		pipeFactory.create(producer.getOutputPort(), reader.getInputPort());
		pipeFactory.create(reader.getOutputPort(), typeFilter.getInputPort());
		pipeFactory.create(typeFilter.getOutputPortForType(IFlowRecord.class), fstDistributor.getInputPort());
		pipeFactory.create(fstDistributor.getNewOutputPort(), recordSimplificator.getInputPort());
		pipeFactory.create(recordSimplificator.getOutputPort(), recordCollector.getInputPort());
		pipeFactory.create(fstDistributor.getNewOutputPort(), traceReconstructor.getInputPort());
		pipeFactory.create(traceReconstructor.getOutputPort(), sndDistributor.getInputPort());
		pipeFactory.create(sndDistributor.getNewOutputPort(), traceAggregator.getInputPort());
		pipeFactory.create(sndDistributor.getNewOutputPort(), traceCollector.getInputPort());
		pipeFactory.create(sndDistributor.getNewOutputPort(), failedTraceFilter.getInputPort());
		pipeFactory.create(failedTraceFilter.getOutputPort(), failedTraceCollector.getInputPort());
		pipeFactory.create(sndDistributor.getNewOutputPort(), failureContainingTraceFilter.getInputPort());
		pipeFactory.create(failureContainingTraceFilter.getOutputPort(), failureContainingTraceCollector.getInputPort());
		pipeFactory.create(traceAggregator.getOutputPort(), thrdDistributor.getInputPort());
		pipeFactory.create(thrdDistributor.getNewOutputPort(), aggregatedTraceCollector.getInputPort());
		pipeFactory.create(thrdDistributor.getNewOutputPort(), failedAggregatedTraceFilter.getInputPort());
		pipeFactory.create(thrdDistributor.getNewOutputPort(), failureContainingAggregatedTraceFilter.getInputPort());
		pipeFactory.create(failedAggregatedTraceFilter.getOutputPort(), failedAggregatedTraceCollector.getInputPort());
		pipeFactory.create(failureContainingAggregatedTraceFilter.getOutputPort(), failureContainingAggregatedTraceCollector.getInputPort());
		pipeFactory.create(typeFilter.getOutputPortForType(KiekerMetadataRecord.class), metadataCollector.getInputPort());

		// Make sure that the producer is executed by the analysis
		super.addThreadableStage(producer);
	}

	public List<Record> getRecordsList() {
		return this.recordsList;
	}

	public List<Execution> getTracesList() {
		return this.tracesList;
	}

	public List<Execution> getFailedTracesList() {
		return this.failedTracesList;
	}

	public List<Execution> getFailureContainingTracesList() {
		return this.failureContainingTracesList;
	}

	public List<AggregatedExecution> getFailedAggregatedTracesList() {
		return this.failedAggregatedTracesList;
	}

	public List<AggregatedExecution> getFailureContainingAggregatedTracesList() {
		return this.failureContainingAggregatedTracesList;
	}

	public List<AggregatedExecution> getAggregatedTraces() {
		return this.aggregatedTraces;
	}

	public List<KiekerMetadataRecord> getMetadataRecords() {
		return this.metadataRecords;
	}

}
