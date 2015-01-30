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

package kieker.gui.common.model.importer;

import java.io.File;
import java.util.List;
import java.util.Vector;

import kieker.common.record.IMonitoringRecord;
import kieker.common.record.flow.IFlowRecord;
import kieker.common.record.misc.KiekerMetadataRecord;
import kieker.gui.common.domain.AggregatedExecution;
import kieker.gui.common.domain.Execution;
import kieker.gui.common.model.importer.stages.ReadingComposite;
import kieker.gui.common.model.importer.stages.TraceAggregationComposite;
import kieker.gui.common.model.importer.stages.TraceReconstructionComposite;
import teetime.framework.AnalysisConfiguration;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;
import teetime.stage.CollectorSink;
import teetime.stage.MultipleInstanceOfFilter;

/**
 * A configuration for the import and analysis of monitoring logs.
 *
 * @author Nils Christian Ehmke
 */
public final class ImportAnalysisConfiguration extends AnalysisConfiguration {

	private final List<Execution> traces = new Vector<>(1000);
	private final List<Execution> failedTraces = new Vector<>(1000);
	private final List<Execution> failureContainingTraces = new Vector<>(1000);

	private final List<AggregatedExecution> aggregatedTraces = new Vector<>(1000);
	private final List<AggregatedExecution> failedAggregatedTraces = new Vector<>(1000);
	private final List<AggregatedExecution> failureContainingAggregatedTraces = new Vector<>(1000);

	private final List<KiekerMetadataRecord> metadataRecords = new Vector<>(1000);

	public ImportAnalysisConfiguration(final File importDirectory) {
		// Create the stages
		final ReadingComposite reader = new ReadingComposite(importDirectory);
		final MultipleInstanceOfFilter<IMonitoringRecord> typeFilter = new MultipleInstanceOfFilter<>();
		final TraceReconstructionComposite traceReconstruction = new TraceReconstructionComposite(this.traces, this.failedTraces, this.failureContainingTraces);
		final TraceAggregationComposite traceAggregation = new TraceAggregationComposite(this.aggregatedTraces, this.failedAggregatedTraces,
				this.failureContainingAggregatedTraces);

		final CollectorSink<KiekerMetadataRecord> metadataCollector = new CollectorSink<>(this.metadataRecords);

		// Connect the stages
		final IPipeFactory pipeFactory = AnalysisConfiguration.PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false);
		pipeFactory.create(reader.getOutputPort(), typeFilter.getInputPort());
		pipeFactory.create(typeFilter.getOutputPortForType(IFlowRecord.class), traceReconstruction.getInputPort());
		pipeFactory.create(traceReconstruction.getOutputPort(), traceAggregation.getInputPort());
		pipeFactory.create(typeFilter.getOutputPortForType(KiekerMetadataRecord.class), metadataCollector.getInputPort());

		// Make sure that the producer is executed by the analysis
		super.addThreadableStage(reader);
	}

	public List<Execution> getTracesList() {
		return this.traces;
	}

	public List<Execution> getFailedTracesList() {
		return this.failedTraces;
	}

	public List<Execution> getFailureContainingTracesList() {
		return this.failureContainingTraces;
	}

	public List<AggregatedExecution> getFailedAggregatedTracesList() {
		return this.failedAggregatedTraces;
	}

	public List<AggregatedExecution> getFailureContainingAggregatedTracesList() {
		return this.failureContainingAggregatedTraces;
	}

	public List<AggregatedExecution> getAggregatedTraces() {
		return this.aggregatedTraces;
	}

	public List<KiekerMetadataRecord> getMetadataRecords() {
		return this.metadataRecords;
	}

}
