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

package kieker.diagnosis.service.filter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import com.google.inject.Singleton;

import kieker.diagnosis.architecture.service.ServiceBase;
import kieker.diagnosis.service.pattern.PatternService;

@Singleton
public class FilterService extends ServiceBase {

	public <T> Predicate<T> getLongPredicate( final Function<T, Long> aLongFunction, final Long aSearchLong ) {
		return t -> {
			final long actualLong = aLongFunction.apply( t );
			return aSearchLong == null || actualLong == aSearchLong.longValue( );
		};
	}

	public <T> Predicate<T> getStringPredicate( final Function<T, String> aStringFunction, final String aSearchString, final boolean aUseRegExpr ) {
		if ( aUseRegExpr && aSearchString != null ) {
			// If we use a regular expression, we have to compile the pattern
			final PatternService patternService = getService( PatternService.class );
			final Pattern pattern = patternService.compilePattern( aSearchString );

			// The returned function checks whether the actual string matches on the pattern
			return t -> {
				final String actualString = aStringFunction.apply( t );
				return actualString != null && pattern.matcher( actualString ).matches( );
			};
		} else {
			return t -> {
				final String actualString = aStringFunction.apply( t );
				return aSearchString == null || actualString != null && actualString.toLowerCase( ).contains( aSearchString.toLowerCase( ) );
			};
		}
	}

	/**
	 * This method conjuncts a given list of predicates. That means they are AND-linked. If the list is empty, an always true predicate is returned.
	 *
	 * @param aPredicates
	 *            The (possible empty) list of predicates.
	 *
	 * @return The conjuncted predicates.
	 */
	public <T> Predicate<T> conjunct( final Iterable<Predicate<T>> aPredicates ) {
		Predicate<T> predicate = t -> true;

		for ( final Predicate<T> subPredicate : aPredicates ) {
			predicate = predicate.and( subPredicate );
		}

		return predicate;
	}

	public <T> Predicate<T> getAfterTimePredicate( final Function<T, Long> aTimestampFunction, final LocalDate aLowerDate, final Calendar aLowerTime ) {
		if ( aLowerDate == null && aLowerTime == null ) {
			// Nothing to do. Just return an always-true-filter
			return t -> true;
		} else {
			return t -> {
				// Get the date and time from the timestamp
				final ZonedDateTime zonedDateTime = getZonedDateTime( aTimestampFunction, t );

				// Compare the date
				if ( aLowerDate != null ) {
					final LocalDate localDate = LocalDate.from( zonedDateTime );
					if ( localDate.isAfter( aLowerDate ) ) {
						// We don't have to check the time. It is another day.
						return true;
					}
					if ( localDate.isBefore( aLowerDate ) ) {
						return false;
					}
				}

				// Compare the time
				if ( aLowerTime != null ) {
					final int hour1 = zonedDateTime.get( ChronoField.HOUR_OF_DAY );
					final int minute1 = zonedDateTime.get( ChronoField.MINUTE_OF_HOUR );

					final int hour2 = aLowerTime.get( Calendar.HOUR_OF_DAY );
					final int minute2 = aLowerTime.get( Calendar.MINUTE );

					final boolean result;

					if ( hour2 < hour1 ) {
						result = true;
					} else if ( hour2 > hour1 ) {
						result = false;
					} else if ( minute2 < minute1 ) {
						result = true;
					} else if ( minute2 > minute1 ) {
						result = false;
					} else {
						result = true;
					}

					if ( !result ) {
						return result;
					}

				}

				return true;

			};
		}
	}

	public <T> Predicate<T> getBeforeTimePredicate( final Function<T, Long> aTimestampFunction, final LocalDate aUpperDate, final Calendar aUpperTime ) {
		if ( aUpperDate == null && aUpperTime == null ) {
			// Nothing to do. Just return an always-true-filter
			return t -> true;
		} else {
			return t -> {
				// Get the date and time from the timestamp
				final ZonedDateTime zonedDateTime = getZonedDateTime( aTimestampFunction, t );

				// Compare the date
				if ( aUpperDate != null ) {
					final LocalDate localDate = LocalDate.from( zonedDateTime );
					if ( localDate.isBefore( aUpperDate ) ) {
						// We don't have to check the time. It is another day.
						return true;
					}
					if ( localDate.isAfter( aUpperDate ) ) {
						return false;
					}
				}

				// Compare the time
				if ( aUpperTime != null ) {
					final int hour1 = zonedDateTime.get( ChronoField.HOUR_OF_DAY );
					final int minute1 = zonedDateTime.get( ChronoField.MINUTE_OF_HOUR );

					final int hour2 = aUpperTime.get( Calendar.HOUR_OF_DAY );
					final int minute2 = aUpperTime.get( Calendar.MINUTE );

					final boolean result;

					if ( hour2 > hour1 ) {
						result = true;
					} else if ( hour2 < hour1 ) {
						result = false;
					} else if ( minute2 > minute1 ) {
						result = true;
					} else if ( minute2 < minute1 ) {
						result = false;
					} else {
						result = true;
					}

					if ( !result ) {
						return result;
					}

				}

				return true;

			};
		}
	}

	private <T> ZonedDateTime getZonedDateTime( final Function<T, Long> aTimestampFunction, final T t ) {
		final long timestamp = aTimestampFunction.apply( t );
		final Instant instant = Instant.ofEpochMilli( timestamp );
		return ZonedDateTime.ofInstant( instant, ZoneId.systemDefault( ) );
	}

}
