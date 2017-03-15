/***************************************************************************
 * Copyright 2015-2017 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.application.service.data.stages;

import kieker.diagnosis.application.service.data.domain.Trace;

import java.util.List;

import kieker.common.record.IMonitoringRecord;
import kieker.common.record.controlflow.OperationExecutionRecord;
import kieker.common.record.flow.IFlowRecord;

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
	private final OutputPort<Trace> ivOutputPort;
	private final LegacyTraceReconstructor ivLegacyReconstructor;
	private final TraceReconstructor ivReconstructor;

	public TraceReconstructionComposite( final List<Trace> aTraces, final boolean aActivateAdditionalLogChecks,
			final boolean aPercentCalculationsRefersToTopMost ) {
		final Distributor<Trace> distributor = new Distributor<>( new CopyByReferenceStrategy( ) );
		final Merger<Trace> merger = new Merger<>( );

		ivTypeFilter = new MultipleInstanceOfFilter<>( );
		final CollectorSink<Trace> tracesCollector = new CollectorSink<>( aTraces );
		final TraceStatisticsDecorator statisticsDecorator = new TraceStatisticsDecorator( aPercentCalculationsRefersToTopMost );
		ivReconstructor = new TraceReconstructor( aActivateAdditionalLogChecks );
		ivLegacyReconstructor = new LegacyTraceReconstructor( );

		ivOutputPort = statisticsDecorator.getOutputPort( );

		connectPorts( ivTypeFilter.getOutputPortForType( IFlowRecord.class ), ivReconstructor.getInputPort( ) );
		connectPorts( ivTypeFilter.getOutputPortForType( OperationExecutionRecord.class ), ivLegacyReconstructor.getInputPort( ) );
		connectPorts( ivReconstructor.getOutputPort( ), merger.getNewInputPort( ) );
		connectPorts( ivLegacyReconstructor.getOutputPort( ), merger.getNewInputPort( ) );
		connectPorts( merger.getOutputPort( ), distributor.getInputPort( ) );
		connectPorts( distributor.getNewOutputPort( ), tracesCollector.getInputPort( ) );
		connectPorts( distributor.getNewOutputPort( ), statisticsDecorator.getInputPort( ) );
	}

	public int countIncompleteTraces( ) {
		return ivReconstructor.countIncompleteTraces( ) + ivLegacyReconstructor.countIncompleteTraces( );
	}

	public int countDanglingRecords( ) {
		return ivReconstructor.countDanglingRecords( ) + ivLegacyReconstructor.countDanglingRecords( );
	}

	public InputPort<IMonitoringRecord> getInputPort( ) {
		return ivTypeFilter.getInputPort( );
	}

	public OutputPort<Trace> getOutputPort( ) {
		return ivOutputPort;
	}

}
