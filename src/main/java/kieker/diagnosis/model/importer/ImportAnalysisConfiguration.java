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

package kieker.diagnosis.model.importer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import kieker.common.record.IMonitoringRecord;
import kieker.common.record.misc.KiekerMetadataRecord;
import kieker.diagnosis.domain.AggregatedOperationCall;
import kieker.diagnosis.domain.AggregatedTrace;
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.domain.Trace;
import kieker.diagnosis.model.importer.stages.BeginEndOfMonitoringDetector;
import kieker.diagnosis.model.importer.stages.OperationCallHandlerComposite;
import kieker.diagnosis.model.importer.stages.ReadingComposite;
import kieker.diagnosis.model.importer.stages.TraceAggregationComposite;
import kieker.diagnosis.model.importer.stages.TraceReconstructionComposite;
import teetime.framework.AnalysisConfiguration;
import teetime.stage.CollectorSink;
import teetime.stage.MultipleInstanceOfFilter;
import teetime.stage.basic.distributor.CopyByReferenceStrategy;
import teetime.stage.basic.distributor.Distributor;

/**
 * A {@code TeeTime} configuration for the import and analysis of monitoring logs.
 * 
 * @author Nils Christian Ehmke
 */
public final class ImportAnalysisConfiguration extends AnalysisConfiguration {

	private final List<Trace> traces = new ArrayList<>(1000);
	private final List<Trace> failedTraces = new ArrayList<>(1000);
	private final List<Trace> failureContainingTraces = new ArrayList<>(1000);

	private final List<OperationCall> operationCalls = new ArrayList<>(1000);
	private final List<OperationCall> failedOperationCalls = new ArrayList<>(1000);

	private final List<AggregatedOperationCall> aggregatedOperationCalls = new ArrayList<>(1000);
	private final List<AggregatedOperationCall> aggregatedFailedOperationCalls = new ArrayList<>(1000);

	private final List<AggregatedTrace> aggregatedTraces = new ArrayList<>(1000);
	private final List<AggregatedTrace> failedAggregatedTraces = new ArrayList<>(1000);
	private final List<AggregatedTrace> failureContainingAggregatedTraces = new ArrayList<>(1000);

	private final List<KiekerMetadataRecord> metadataRecords = new ArrayList<>(1000);
	private final TraceReconstructionComposite reconstruction;
	private final BeginEndOfMonitoringDetector beginEndOfMonitoringDetector;

	public ImportAnalysisConfiguration(final File importDirectory) {
		// Create the stages
		final ReadingComposite reader = new ReadingComposite(importDirectory);
		final MultipleInstanceOfFilter<IMonitoringRecord> typeFilter = new MultipleInstanceOfFilter<>();
		final Distributor<Trace> distributor = new Distributor<>(new CopyByReferenceStrategy());
		final TraceAggregationComposite aggregation = new TraceAggregationComposite(this.aggregatedTraces, this.failedAggregatedTraces, this.failureContainingAggregatedTraces);
		final CollectorSink<KiekerMetadataRecord> metadataCollector = new CollectorSink<>(this.metadataRecords);
		final OperationCallHandlerComposite operationCallHandler = new OperationCallHandlerComposite(this.operationCalls, this.failedOperationCalls, this.aggregatedOperationCalls,
				this.aggregatedFailedOperationCalls);

		this.beginEndOfMonitoringDetector = new BeginEndOfMonitoringDetector();
		this.reconstruction = new TraceReconstructionComposite(this.traces, this.failedTraces, this.failureContainingTraces);

		// Connect the stages
		AnalysisConfiguration.connectIntraThreads(reader.getOutputPort(), typeFilter.getInputPort());
		AnalysisConfiguration.connectIntraThreads(typeFilter.getOutputPortForType(IMonitoringRecord.class), this.beginEndOfMonitoringDetector.getInputPort());
		AnalysisConfiguration.connectIntraThreads(this.beginEndOfMonitoringDetector.getOutputPort(), this.reconstruction.getInputPort());
		AnalysisConfiguration.connectIntraThreads(this.reconstruction.getOutputPort(), distributor.getInputPort());
		AnalysisConfiguration.connectIntraThreads(distributor.getNewOutputPort(), operationCallHandler.getInputPort());
		AnalysisConfiguration.connectIntraThreads(distributor.getNewOutputPort(), aggregation.getInputPort());
		AnalysisConfiguration.connectIntraThreads(typeFilter.getOutputPortForType(KiekerMetadataRecord.class), metadataCollector.getInputPort());

		// Make sure that the producer is executed by the analysis
		super.addThreadableStage(reader);
	}

	public long getBeginTimestamp() {
		return this.beginEndOfMonitoringDetector.getBeginTimestamp();
	}

	public long getEndTimestamp() {
		return this.beginEndOfMonitoringDetector.getEndTimestamp();
	}

	public int countIncompleteTraces() {
		return this.reconstruction.countIncompleteTraces();
	}

	public List<Trace> getTracesList() {
		return this.traces;
	}

	public List<Trace> getFailedTracesList() {
		return this.failedTraces;
	}

	public List<Trace> getFailureContainingTracesList() {
		return this.failureContainingTraces;
	}

	public List<AggregatedTrace> getFailedAggregatedTracesList() {
		return this.failedAggregatedTraces;
	}

	public List<AggregatedTrace> getFailureContainingAggregatedTracesList() {
		return this.failureContainingAggregatedTraces;
	}

	public List<AggregatedTrace> getAggregatedTraces() {
		return this.aggregatedTraces;
	}

	public List<KiekerMetadataRecord> getMetadataRecords() {
		return this.metadataRecords;
	}

	public List<OperationCall> getOperationCalls() {
		return this.operationCalls;
	}

	public List<OperationCall> getFailedOperationCalls() {
		return this.failedOperationCalls;
	}

	public List<AggregatedOperationCall> getAggregatedOperationCalls() {
		return this.aggregatedOperationCalls;
	}

	public List<AggregatedOperationCall> getAggregatedFailedOperationCalls() {
		return this.aggregatedFailedOperationCalls;
	}

}
