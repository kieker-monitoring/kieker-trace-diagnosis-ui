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

package kieker.diagnosis.common.model.importer.stages;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

/**
 * This is a abstract base for {@code TeeTime} stages with exactly one input and one output port.
 *
 * @author Nils Christian Ehmke
 *
 * @param <I>
 *            The type of the input port.
 * @param <O>
 *            The type of the output port.
 */
public abstract class AbstractStage<I, O> extends AbstractConsumerStage<I> {

	private final OutputPort<O> outputPort = super.createOutputPort();

	protected final void send(final O element) {
		this.outputPort.send(element);
	}

	public final OutputPort<O> getOutputPort() {
		return this.outputPort;
	}

}
