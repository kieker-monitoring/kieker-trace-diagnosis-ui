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

package kieker.diagnosis.util.stages;

import java.util.List;

import kieker.common.record.IMonitoringRecord;
import kieker.common.record.controlflow.OperationExecutionRecord;
import kieker.common.record.flow.IFlowRecord;
import kieker.diagnosis.domain.Trace;
import teetime.framework.AbstractCompositeStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.stage.CollectorSink;
import teetime.stage.MultipleInstanceOfFilter;
import teetime.stage.basic.distributor.Distributor;
import teetime.stage.basic.distributor.strategy.CopyByReferenceStrategy;
import teetime.stage.basic.merger.Merger;

/**
 * This class is a composite {@code TeeTime} stage, which reconstruct traces based on the incoming records, adds statistical data and stores the traces.
 *
 * @author Nils Christian Ehmke
 */
public final class TraceReconstructionComposite extends AbstractCompositeStage {

	private final MultipleInstanceOfFilter<IMonitoringRecord> typeFilter;
	private final CollectorSink<Trace> tracesCollector;
	private final TraceStatisticsDecorator statisticsDecorator;
	private final OutputPort<Trace> outputPort;
	private final LegacyTraceReconstructor legacyReconstructor;
	private final TraceReconstructor reconstructor;

	public TraceReconstructionComposite(final List<Trace> traces, final boolean activateAdditionalLogChecks) {
		final Distributor<Trace> distributor = new Distributor<>(new CopyByReferenceStrategy());
		final Merger<Trace> merger = new Merger<>();

		this.typeFilter = new MultipleInstanceOfFilter<>();
		this.tracesCollector = new CollectorSink<>(traces);
		this.statisticsDecorator = new TraceStatisticsDecorator();
		this.reconstructor = new TraceReconstructor(activateAdditionalLogChecks);
		this.legacyReconstructor = new LegacyTraceReconstructor();

		this.outputPort = this.statisticsDecorator.getOutputPort();

		super.connectPorts(this.typeFilter.getOutputPortForType(IFlowRecord.class), this.reconstructor.getInputPort());
		super.connectPorts(this.typeFilter.getOutputPortForType(OperationExecutionRecord.class), this.legacyReconstructor.getInputPort());
		super.connectPorts(this.reconstructor.getOutputPort(), merger.getNewInputPort());
		super.connectPorts(this.legacyReconstructor.getOutputPort(), merger.getNewInputPort());
		super.connectPorts(merger.getOutputPort(), distributor.getInputPort());
		super.connectPorts(distributor.getNewOutputPort(), this.tracesCollector.getInputPort());
		super.connectPorts(distributor.getNewOutputPort(), this.statisticsDecorator.getInputPort());
	}

	public int countIncompleteTraces() {
		return this.reconstructor.countIncompleteTraces() + this.legacyReconstructor.countIncompleteTraces();
	}

	public int countDanglingRecords() {
		return this.reconstructor.countDanglingRecords() + this.legacyReconstructor.countDanglingRecords();
	}

	public InputPort<IMonitoringRecord> getInputPort() {
		return this.typeFilter.getInputPort();
	}

	public OutputPort<Trace> getOutputPort() {
		return this.outputPort;
	}

}
