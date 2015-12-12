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

	private final MultipleInstanceOfFilter<IMonitoringRecord> ivTypeFilter;
	private final CollectorSink<Trace> ivTracesCollector;
	private final TraceStatisticsDecorator ivStatisticsDecorator;
	private final OutputPort<Trace> ivOutputPort;
	private final LegacyTraceReconstructor ivLegacyReconstructor;
	private final TraceReconstructor ivReconstructor;

	public TraceReconstructionComposite(final List<Trace> aTraces, final boolean aActivateAdditionalLogChecks) {
		final Distributor<Trace> distributor = new Distributor<>(new CopyByReferenceStrategy());
		final Merger<Trace> merger = new Merger<>();

		this.ivTypeFilter = new MultipleInstanceOfFilter<>();
		this.ivTracesCollector = new CollectorSink<>(aTraces);
		this.ivStatisticsDecorator = new TraceStatisticsDecorator();
		this.ivReconstructor = new TraceReconstructor(aActivateAdditionalLogChecks);
		this.ivLegacyReconstructor = new LegacyTraceReconstructor();

		this.ivOutputPort = this.ivStatisticsDecorator.getOutputPort();

		super.connectPorts(this.ivTypeFilter.getOutputPortForType(IFlowRecord.class), this.ivReconstructor.getInputPort());
		super.connectPorts(this.ivTypeFilter.getOutputPortForType(OperationExecutionRecord.class), this.ivLegacyReconstructor.getInputPort());
		super.connectPorts(this.ivReconstructor.getOutputPort(), merger.getNewInputPort());
		super.connectPorts(this.ivLegacyReconstructor.getOutputPort(), merger.getNewInputPort());
		super.connectPorts(merger.getOutputPort(), distributor.getInputPort());
		super.connectPorts(distributor.getNewOutputPort(), this.ivTracesCollector.getInputPort());
		super.connectPorts(distributor.getNewOutputPort(), this.ivStatisticsDecorator.getInputPort());
	}

	public int countIncompleteTraces() {
		return this.ivReconstructor.countIncompleteTraces() + this.ivLegacyReconstructor.countIncompleteTraces();
	}

	public int countDanglingRecords() {
		return this.ivReconstructor.countDanglingRecords() + this.ivLegacyReconstructor.countDanglingRecords();
	}

	public InputPort<IMonitoringRecord> getInputPort() {
		return this.ivTypeFilter.getInputPort();
	}

	public OutputPort<Trace> getOutputPort() {
		return this.ivOutputPort;
	}

}
