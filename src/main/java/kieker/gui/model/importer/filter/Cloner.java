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

package kieker.gui.model.importer.filter;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

/**
 * Distributes incoming elements to the two output ports.
 *
 * @author Nils Christian Ehmke
 */
public final class Cloner<T> extends AbstractConsumerStage<T> {

	private final OutputPort<T> firstOutputPort = super.createOutputPort();
	private final OutputPort<T> secondOutputPort = super.createOutputPort();

	@Override
	protected void execute(final T element) {
		this.firstOutputPort.send(element);
		this.secondOutputPort.send(element);
	}

	public OutputPort<T> getFirstOutputPort() {
		return this.firstOutputPort;
	}

	public OutputPort<T> getSecondOutputPort() {
		return this.secondOutputPort;
	}
}
