/***************************************************************************
 * Copyright 2015-2019 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.backend.filter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import kieker.diagnosis.backend.base.service.Service;
import kieker.diagnosis.backend.pattern.PatternService;

/**
 * This is the service responsible for handling various filters and predicates. It helps to assemble predicates in a
 * uniform way.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class FilterService implements Service {

	@Inject
	private PatternService patternService;

	/**
	 * Creates a predicate which applies the given function and checks whether the result is equal to the given long. If
	 * the value is {@code null}, an always-true predicate will be returned.
	 *
	 * @param getter
	 *            The function to apply.
	 * @param value
	 *            The value which should be checked for equality.
	 *
	 * @param <T>
	 *            The original type.
	 *
	 * @return A new predicate.
	 */
	public <T> Predicate<T> createLongPredicate( final Function<T, Long> getter, final Long value ) {
		if ( value == null ) {
			return t -> true;
		}

		return t -> getter.apply( t ) == value.longValue( );
	}

	/**
	 * Creates a predicate which applies the given function and checks whether the result is equal to the given value.
	 * If the value is {@code null}, an always-true predicate will be returned.
	 *
	 * @param getter
	 *            The function to apply.
	 * @param value
	 *            The value which should be checked for equality.
	 * @param useRegExpr
	 *            Whether the value is actually a pattern.
	 *
	 * @param <T>
	 *            The original type.
	 *
	 * @return A new predicate.
	 */
	public <T> Predicate<T> createStringPredicate( final Function<T, String> getter, final String value, final boolean useRegExpr ) {
		if ( value == null ) {
			return t -> true;
		}

		if ( useRegExpr ) {
			// If we use a regular expression, we have to compile the pattern
			final Pattern pattern = patternService.compilePattern( value );

			// The returned function checks whether the actual string matches on the pattern
			return t -> {
				final String actualString = getter.apply( t );
				return actualString != null && pattern.matcher( actualString ).matches( );
			};
		}

		return t -> {
			final String actualString = getter.apply( t );
			return actualString != null && actualString.toLowerCase( ).contains( value.toLowerCase( ) );
		};
	}

	/**
	 * This method conjuncts a given list of predicates. That means they are AND-linked. If the list is empty, an always
	 * true predicate is returned.
	 *
	 * @param predicates
	 *            The (possible empty) list of predicates.
	 * 
	 * @param <T>
	 *            The original type.
	 * 
	 * @return The conjuncted predicates.
	 */
	public <T> Predicate<T> conjunct( final List<Predicate<T>> predicates ) {
		return predicates.stream( ).reduce( t -> true, Predicate::and );
	}

	public <T> Predicate<T> createAfterTimePredicate( final Function<T, Long> timestampFunction, final LocalDate lowerDate, final LocalTime lowerTime ) {
		if ( lowerDate == null && lowerTime == null ) {
			return t -> true;
		}

		return t -> {
			// Get the date and time from the timestamp
			final LocalDateTime localDateTime = getDateTime( timestampFunction, t );

			// Compare the date
			if ( lowerDate != null ) {
				final LocalDate localDate = localDateTime.toLocalDate( );

				if ( localDate.isAfter( lowerDate ) ) {
					return true;
				}
				if ( localDate.isBefore( lowerDate ) ) {
					return false;
				}
			}

			// Compare the time
			if ( lowerTime != null ) {
				final LocalTime localTime = localDateTime.toLocalTime( );

				if ( localTime.isBefore( lowerTime ) ) {
					return false;
				}

				return true;
			}

			return true;

		};
	}

	public <T> Predicate<T> createBeforeTimePredicate( final Function<T, Long> timestampFunction, final LocalDate upperDate, final LocalTime upperTime ) {
		if ( upperDate == null && upperTime == null ) {
			return t -> true;
		} else {
			return t -> {
				// Get the date and time from the timestamp
				final LocalDateTime localDateTime = getDateTime( timestampFunction, t );

				// Compare the date
				if ( upperDate != null ) {
					final LocalDate localDate = localDateTime.toLocalDate( );

					if ( localDate.isBefore( upperDate ) ) {
						return true;
					}
					if ( localDate.isAfter( upperDate ) ) {
						return false;
					}
				}

				// Compare the time
				if ( upperTime != null ) {
					final LocalTime localTime = localDateTime.toLocalTime( );

					if ( localTime.isAfter( upperTime ) ) {
						return false;
					}

					return true;
				}

				return true;

			};
		}
	}

	private <T> LocalDateTime getDateTime( final Function<T, Long> timestampFunction, final T value ) {
		final long timestamp = timestampFunction.apply( value );
		final Instant instant = Instant.ofEpochMilli( timestamp );
		return LocalDateTime.ofInstant( instant, ZoneId.systemDefault( ) ).truncatedTo( ChronoUnit.SECONDS );
	}

}
