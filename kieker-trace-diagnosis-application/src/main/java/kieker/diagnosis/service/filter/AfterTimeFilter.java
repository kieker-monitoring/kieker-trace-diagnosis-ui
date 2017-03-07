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

package kieker.diagnosis.service.filter;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author Nils Christian Ehmke
 */
final class AfterTimeFilter<T> extends AbstractTimeFilter<T> {

	public AfterTimeFilter( final Calendar aCalendar, final Function<T, Long> aFunction, final TimeUnit aDestinationTimeUnit ) {
		super( aCalendar, aFunction, aDestinationTimeUnit );
	}

	@Override
	protected boolean doFilter( final int aHour1, final int aHour2, final int aMinute1, final int aMinute2 ) {
		if ( aHour2 > aHour1 ) {
			return true;
		}
		if ( aHour2 < aHour1 ) {
			return false;
		}
		if ( aMinute2 > aMinute1 ) {
			return true;
		}
		if ( aMinute2 < aMinute1 ) {
			return false;
		}
		return true;
	}
}
