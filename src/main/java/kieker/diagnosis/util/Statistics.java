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

package kieker.diagnosis.util;

/**
 * @author Nils Christian Ehmke
 */
public final class Statistics {

	private final long totalDuration;
	private final long meanDuration;
	private final long medianDuration;
	private final long minDuration;
	private final long maxDuration;

	public Statistics(final long totalDuration, final long meanDuration, final long medianDuration, final long minDuration, final long maxDuration) {
		this.totalDuration = totalDuration;
		this.meanDuration = meanDuration;
		this.medianDuration = medianDuration;
		this.minDuration = minDuration;
		this.maxDuration = maxDuration;
	}

	public long getTotalDuration() {
		return this.totalDuration;
	}

	public long getMeanDuration() {
		return this.meanDuration;
	}

	public long getMedianDuration() {
		return this.medianDuration;
	}

	public long getMinDuration() {
		return this.minDuration;
	}

	public long getMaxDuration() {
		return this.maxDuration;
	}

}