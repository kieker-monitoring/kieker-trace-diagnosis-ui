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

package kieker.diagnosis.service.data.stages;

import java.util.Collections;
import java.util.List;

/**
 * @author Nils Christian Ehmke
 */
public final class StatisticsUtility {

	private StatisticsUtility( ) {
	}

	public static Statistics calculateStatistics( final List<Long> aDurations ) {
		Collections.sort( aDurations );

		long totalDuration = 0;
		for ( final Long duration : aDurations ) {
			totalDuration += duration;
		}

		final long minDuration = aDurations.get( 0 );
		final long maxDuration = aDurations.get( aDurations.size( ) - 1 );
		final long meanDuration = totalDuration / aDurations.size( );
		final long medianDuration = aDurations.get( aDurations.size( ) / 2 );

		return new Statistics( totalDuration, meanDuration, medianDuration, minDuration, maxDuration );
	}

}
