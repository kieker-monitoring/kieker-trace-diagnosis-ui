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

package kieker.diagnosis.application.service.data.stages;

/**
 * @author Nils Christian Ehmke
 */
final class Statistics {

	private final long ivTotalDuration;
	private final long ivMeanDuration;
	private final long ivMedianDuration;
	private final long ivMinDuration;
	private final long ivMaxDuration;

	public Statistics( final long aTotalDuration, final long aMeanDuration, final long aMedianDuration, final long aMinDuration, final long aMaxDuration ) {
		ivTotalDuration = aTotalDuration;
		ivMeanDuration = aMeanDuration;
		ivMedianDuration = aMedianDuration;
		ivMinDuration = aMinDuration;
		ivMaxDuration = aMaxDuration;
	}

	public long getTotalDuration( ) {
		return ivTotalDuration;
	}

	public long getMeanDuration( ) {
		return ivMeanDuration;
	}

	public long getMedianDuration( ) {
		return ivMedianDuration;
	}

	public long getMinDuration( ) {
		return ivMinDuration;
	}

	public long getMaxDuration( ) {
		return ivMaxDuration;
	}

}
