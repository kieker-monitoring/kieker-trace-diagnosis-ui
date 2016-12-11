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

import kieker.common.record.IMonitoringRecord;
import teetime.stage.basic.AbstractTransformation;

/**
 * @author Nils Christian Ehmke
 */
public final class BeginEndOfMonitoringDetector extends AbstractTransformation<IMonitoringRecord, IMonitoringRecord> {

	private long ivBeginTimestamp = Long.MAX_VALUE;
	private long ivEndTimestamp = 0;

	@Override
	protected void execute(final IMonitoringRecord aRecord) {
		final long loggingTimestamp = aRecord.getLoggingTimestamp();

		if (loggingTimestamp < this.ivBeginTimestamp) {
			this.ivBeginTimestamp = loggingTimestamp;
		}
		if (loggingTimestamp > this.ivEndTimestamp) {
			this.ivEndTimestamp = loggingTimestamp;
		}

		super.getOutputPort().send(aRecord);
	}

	public long getBeginTimestamp() {
		return this.ivBeginTimestamp;
	}

	public long getEndTimestamp() {
		return this.ivEndTimestamp;
	}

}
