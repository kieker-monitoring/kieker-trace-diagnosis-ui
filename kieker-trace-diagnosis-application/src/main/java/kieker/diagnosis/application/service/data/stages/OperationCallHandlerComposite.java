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

import kieker.diagnosis.application.service.data.domain.AggregatedOperationCall;
import kieker.diagnosis.application.service.data.domain.OperationCall;
import kieker.diagnosis.application.service.data.domain.Trace;

import java.util.List;

import teetime.framework.AbstractCompositeStage;
import teetime.framework.InputPort;
import teetime.stage.CollectorSink;
import teetime.stage.basic.distributor.Distributor;
import teetime.stage.basic.distributor.strategy.CopyByReferenceStrategy;

/**
 * @author Nils Christian Ehmke
 */
public final class OperationCallHandlerComposite extends AbstractCompositeStage {

	private final InputPort<Trace> ivInputPort;

	public OperationCallHandlerComposite( final List<OperationCall> aOperationCalls, final List<AggregatedOperationCall> aAggOperationCalls ) {
		final OperationCallExtractor operationCallExtractor = new OperationCallExtractor( );
		final CollectorSink<OperationCall> callCollector = new CollectorSink<>( aOperationCalls );
		final Distributor<OperationCall> distributor = new Distributor<>( new CopyByReferenceStrategy( ) );
		final OperationCallAggregator callAggregator = new OperationCallAggregator( );
		final CollectorSink<AggregatedOperationCall> aggCallCollector = new CollectorSink<>( aAggOperationCalls );

		ivInputPort = operationCallExtractor.getInputPort( );

		connectPorts( operationCallExtractor.getOutputPort( ), distributor.getInputPort( ) );
		connectPorts( distributor.getNewOutputPort( ), callCollector.getInputPort( ) );
		connectPorts( distributor.getNewOutputPort( ), callAggregator.getInputPort( ) );
		connectPorts( callAggregator.getOutputPort( ), aggCallCollector.getInputPort( ) );
	}

	public InputPort<Trace> getInputPort( ) {
		return ivInputPort;
	}

}
