/***************************************************************************
 * Copyright 2015-2017 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.application.service.filter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Nils Christian Ehmke
 *
 * @param <T>
 *            The type of the object on which the element is applied to get the timestamp.
 */
abstract class AbstractTimeFilter<T> implements Predicate<T> {

	private final Calendar ivCalendar;
	private final Function<T, Long> ivFunction;
	private final TimeUnit ivSourceTimeUnit;

	public AbstractTimeFilter( final Calendar aCalendar, final Function<T, Long> aFunction, final TimeUnit aSourceTimeUnit ) {
		ivCalendar = aCalendar;
		ivFunction = aFunction;
		ivSourceTimeUnit = aSourceTimeUnit;
	}

	@Override // NOPMD ( This is a false positive for a JUnit test method)
	public boolean test( final T aElement ) {
		final long timestamp = ivFunction.apply( aElement );
		final long timestampInMS = TimeUnit.MILLISECONDS.convert( timestamp, ivSourceTimeUnit );
		final Instant instant = Instant.ofEpochMilli( timestampInMS );
		final ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant( instant, ZoneId.systemDefault( ) );

		final int hour1 = zonedDateTime.get( ChronoField.HOUR_OF_DAY );
		final int minute1 = zonedDateTime.get( ChronoField.MINUTE_OF_HOUR );

		final int hour2 = ivCalendar.get( Calendar.HOUR_OF_DAY );
		final int minute2 = ivCalendar.get( Calendar.MINUTE );

		return doFilter( hour1, hour2, minute1, minute2 );
	}

	protected abstract boolean doFilter( int aHour1, int aHour2, int aMinute1, int aMinute2 );

}
