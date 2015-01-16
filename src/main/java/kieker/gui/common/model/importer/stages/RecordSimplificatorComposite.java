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

package kieker.gui.common.model.importer.stages;

import java.util.List;

import kieker.common.record.IMonitoringRecord;
import kieker.gui.common.domain.Record;
import teetime.framework.InputPort;
import teetime.framework.Stage;
import teetime.framework.TerminationStrategy;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;
import teetime.framework.signal.ISignal;
import teetime.framework.validation.InvalidPortConnection;
import teetime.stage.CollectorSink;

public final class RecordSimplificatorComposite extends Stage {

	private final RecordSimplificator simplificator;
	private final CollectorSink<Record> collector;

	public RecordSimplificatorComposite(final List<Record> records) {
		this.simplificator = new RecordSimplificator();
		this.collector = new CollectorSink<>(records);

		final IPipeFactory pipeFactory = PipeFactoryRegistry.INSTANCE.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false);
		pipeFactory.create(this.simplificator.getOutputPort(), this.collector.getInputPort());
	}

	@Override
	protected void executeWithPorts() {
		this.simplificator.executeWithPorts();
	}

	public InputPort<IMonitoringRecord> getInputPort() {
		return this.simplificator.getInputPort();
	}

	@Override
	public void validateOutputPorts(final List<InvalidPortConnection> invalidPortConnections) {
		// No code necessary
	}

	@Override
	protected void onSignal(final ISignal signal, final InputPort<?> inputPort) {
		this.simplificator.onSignal(signal, inputPort);
	}

	@Override
	protected TerminationStrategy getTerminationStrategy() {
		return this.simplificator.getTerminationStrategy();
	}

	@Override
	protected void terminate() {
		this.simplificator.terminate();
	}

	@Override
	protected boolean shouldBeTerminated() {
		return this.simplificator.shouldBeTerminated();
	}

	@Override
	protected InputPort<?>[] getInputPorts() {
		return this.simplificator.getInputPorts();
	}

	@Override
	protected boolean isStarted() {
		return this.collector.isStarted();
	}
}
