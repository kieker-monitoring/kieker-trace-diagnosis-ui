/***************************************************************************
 * Copyright 2015-2016 Kieker Project (http://kieker-monitoring.net)
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

import kieker.diagnosis.domain.AggregatedOperationCall;
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.domain.Trace;
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
	private final OperationCallExtractor ivOperationCallExtractor;
	private final CollectorSink<OperationCall> ivCallCollector;
	private final CollectorSink<AggregatedOperationCall> ivAggCallCollector;

	public OperationCallHandlerComposite(final List<OperationCall> aOperationCalls, final List<AggregatedOperationCall> aAggOperationCalls) {
		this.ivOperationCallExtractor = new OperationCallExtractor();
		this.ivCallCollector = new CollectorSink<>(aOperationCalls);
		final Distributor<OperationCall> distributor = new Distributor<>(new CopyByReferenceStrategy());
		final OperationCallAggregator callAggregator = new OperationCallAggregator();
		this.ivAggCallCollector = new CollectorSink<>(aAggOperationCalls);

		this.ivInputPort = this.ivOperationCallExtractor.getInputPort();

		super.connectPorts(this.ivOperationCallExtractor.getOutputPort(), distributor.getInputPort());
		super.connectPorts(distributor.getNewOutputPort(), this.ivCallCollector.getInputPort());
		super.connectPorts(distributor.getNewOutputPort(), callAggregator.getInputPort());
		super.connectPorts(callAggregator.getOutputPort(), this.ivAggCallCollector.getInputPort());
	}

	public InputPort<Trace> getInputPort() {
		return this.ivInputPort;
	}

}
