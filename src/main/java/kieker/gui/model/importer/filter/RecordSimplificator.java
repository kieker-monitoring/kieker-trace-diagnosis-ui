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

import kieker.common.record.IMonitoringRecord;
import kieker.gui.model.domain.RecordEntry;
import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

/**
 * Converts incoming instances of {@link IMonitoringRecord} into simplified representations ({@link RecordEntry}) used within this application.
 *
 * @author Nils Christian Ehmke
 */
public final class RecordSimplificator extends AbstractConsumerStage<IMonitoringRecord> {

	private final OutputPort<RecordEntry> outputPort = super.createOutputPort();

	@Override
	protected void execute(final IMonitoringRecord input) {
		final long timestamp = input.getLoggingTimestamp();
		final String type = input.getClass().getCanonicalName();
		final String representation = input.toString();

		final RecordEntry simplifiedRecord = new RecordEntry(timestamp, type, representation);
		this.outputPort.send(simplifiedRecord);
	}

	public OutputPort<RecordEntry> getOutputPort() {
		return this.outputPort;
	}

}
