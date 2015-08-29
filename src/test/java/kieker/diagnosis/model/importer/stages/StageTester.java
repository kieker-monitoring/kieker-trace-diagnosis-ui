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

package kieker.diagnosis.model.importer.stages;

import java.util.ArrayList;
import java.util.List;

import teetime.framework.Configuration;
import teetime.framework.Execution;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;

public final class StageTester {

	public static <I> InputHolder<I> testStageBySending(final Iterable<I> input) {
		return new InputHolder<I>(input);
	}

	public static final class InputHolder<I> {

		private final Iterable<I> input;

		public InputHolder(final Iterable<I> input) {
			this.input = input;
		}

		public InputPortHolder<I> to(final InputPort<I> inputPort) {
			return new InputPortHolder<I>(this.input, inputPort);
		}

	}

	public static final class InputPortHolder<I> {

		private final Iterable<I> input;
		private final InputPort<I> inputPort;

		public InputPortHolder(final Iterable<I> input, final InputPort<I> inputPort) {
			this.input = input;
			this.inputPort = inputPort;
		}

		public <O> List<O> andReceivingFrom(final OutputPort<O> outputPort) {
			final TestConfiguration<I, O> configuration = new TestConfiguration<I, O>(this.input, this.inputPort, outputPort);
			final Execution<TestConfiguration<I, O>> analysis = new Execution<>(configuration);
			analysis.executeBlocking();

			return configuration.getOutput();
		}
	}

	private static class TestConfiguration<I, O> extends Configuration {

		private final List<O> collectorList = new ArrayList<>();

		public TestConfiguration(final Iterable<I> input, final InputPort<I> inputPort, final OutputPort<O> outputPort) {
			final InitialElementProducer<I> producer = new InitialElementProducer<>(input);
			final CollectorSink<O> collector = new CollectorSink<>(this.collectorList);

			super.connectPorts(producer.getOutputPort(), inputPort);
			super.connectPorts(outputPort, collector.getInputPort());
		}

		public List<O> getOutput() {
			return this.collectorList;
		}

	}

}
