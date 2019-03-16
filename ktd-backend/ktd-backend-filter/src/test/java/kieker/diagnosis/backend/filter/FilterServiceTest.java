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

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.function.Predicate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kieker.diagnosis.backend.pattern.PatternService;

/**
 * This is a unit test the {@link FilterService}.
 *
 * @author Nils Christian Ehmke
 */
@DisplayName ( "Unit-Test for FilterService" )
public final class FilterServiceTest {

	private final FilterService filterService = new FilterService( new PatternService( ) );

	@Test
	@DisplayName ( "Test the creation of a long predicate" )
	public void testCreateLongPredicate( ) {
		final Predicate<Long> predicate = filterService.createLongPredicate( e -> e, 42L );
		assertThat( predicate.test( 42L ) ).isTrue( );
		assertThat( predicate.test( 15L ) ).isFalse( );
	}

	@Test
	@DisplayName ( "Test the creation of a long predicate with null values" )
	public void testCreateLongPredicateWithNull( ) {
		final Predicate<Long> predicate = filterService.createLongPredicate( null, null );
		assertThat( predicate.test( null ) ).isTrue( );
	}

	@Test
	@DisplayName ( "Test the creation of a string predicate" )
	public void testCreateStringPredicate( ) {
		final Predicate<String> predicate = filterService.createStringPredicate( e -> e, "A", false );
		assertThat( predicate.test( "A" ) ).isTrue( );
		assertThat( predicate.test( "a" ) ).isTrue( );
		assertThat( predicate.test( "B" ) ).isFalse( );
		assertThat( predicate.test( null ) ).isFalse( );
	}

	@Test
	@DisplayName ( "Test the creation of a string predicate with regular expressions" )
	public void testCreateStringPredicateWithRegExpr( ) {
		final Predicate<String> predicate = filterService.createStringPredicate( e -> e, ".*A.*", true );
		assertThat( predicate.test( "A" ) ).isTrue( );
		assertThat( predicate.test( "BAB" ) ).isTrue( );
		assertThat( predicate.test( "B" ) ).isFalse( );
		assertThat( predicate.test( null ) ).isFalse( );
	}

	@Test
	@DisplayName ( "Test the creation of a string predicate with null values" )
	public void testCreateStringPredicateWithNull( ) {
		final Predicate<String> predicate = filterService.createStringPredicate( null, null, false );
		assertThat( predicate.test( null ) ).isTrue( );
	}

	@Test
	@DisplayName ( "Test the creation of a string predicate with regular expressions and null values" )
	public void testCreateStringPredicateWithNullAndRegExpr( ) {
		final Predicate<String> predicate = filterService.createStringPredicate( e -> e, ".*", true );
		assertThat( predicate.test( null ) ).isFalse( );
	}

	@Test
	@DisplayName ( "Test the conjunction of predicates" )
	public void testConjunct( ) {
		final Predicate<Integer> predicateA = value -> value % 2 == 0;
		final Predicate<Integer> predicateB = value -> value % 3 == 0;
		final Predicate<Integer> conjunction = filterService.conjunct( Arrays.asList( predicateA, predicateB ) );

		assertThat( conjunction.test( 12 ) ).isTrue( );
		assertThat( conjunction.test( 30 ) ).isTrue( );
		assertThat( conjunction.test( 9 ) ).isFalse( );
		assertThat( conjunction.test( 4 ) ).isFalse( );
	}

	@Test
	@DisplayName ( "Test the creation of an after-time predicate" )
	public void testCreateAfterTimePredicate( ) {
		final Predicate<Long> predicate = filterService.createAfterTimePredicate( e -> e, LocalDate.of( 2000, 05, 01 ), LocalTime.of( 15, 20 ) );

		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 15, 20 ) ) ).isTrue( );
		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 15, 25 ) ) ).isTrue( );
		assertThat( predicate.test( createTimestamp( 2001, 05, 01, 10, 20 ) ) ).isTrue( );
		assertThat( predicate.test( createTimestamp( 1999, 05, 01, 15, 20 ) ) ).isFalse( );
		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 14, 20 ) ) ).isFalse( );
	}

	@Test
	@DisplayName ( "Test the creation of an after-time predicate without date" )
	public void testCreateAfterTimePredicateDateNull( ) {
		final Predicate<Long> predicate = filterService.createAfterTimePredicate( e -> e, null, LocalTime.of( 15, 20 ) );

		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 15, 20 ) ) ).isTrue( );
		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 15, 25 ) ) ).isTrue( );
		assertThat( predicate.test( createTimestamp( 2001, 05, 01, 10, 20 ) ) ).isFalse( );
		assertThat( predicate.test( createTimestamp( 1999, 05, 01, 15, 20 ) ) ).isTrue( );
		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 14, 20 ) ) ).isFalse( );
	}

	@Test
	@DisplayName ( "Test the creation of an after-time predicate without time" )
	public void testCreateAfterTimePredicateTimeNull( ) {
		final Predicate<Long> predicate = filterService.createAfterTimePredicate( e -> e, LocalDate.of( 2000, 05, 01 ), null );

		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 15, 20 ) ) ).isTrue( );
		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 15, 25 ) ) ).isTrue( );
		assertThat( predicate.test( createTimestamp( 2001, 05, 01, 10, 20 ) ) ).isTrue( );
		assertThat( predicate.test( createTimestamp( 1999, 05, 01, 15, 20 ) ) ).isFalse( );
		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 14, 20 ) ) ).isTrue( );
	}

	@Test
	@DisplayName ( "Test the creation of an after-time predicate without date and time" )
	public void testCreateAfterTimePredicateDateAndTimeNull( ) {
		final Predicate<Long> predicate = filterService.createAfterTimePredicate( e -> e, null, null );

		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 15, 20 ) ) ).isTrue( );
		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 15, 25 ) ) ).isTrue( );
		assertThat( predicate.test( createTimestamp( 2001, 05, 01, 10, 20 ) ) ).isTrue( );
		assertThat( predicate.test( createTimestamp( 1999, 05, 01, 15, 20 ) ) ).isTrue( );
		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 14, 20 ) ) ).isTrue( );
	}

	@Test
	@DisplayName ( "Test the creation of a before-time predicate" )
	public void testCreateBeforeTimePredicate( ) {
		final Predicate<Long> predicate = filterService.createBeforeTimePredicate( e -> e, LocalDate.of( 2000, 05, 01 ), LocalTime.of( 15, 20 ) );

		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 15, 20 ) ) ).isTrue( );
		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 15, 25 ) ) ).isFalse( );
		assertThat( predicate.test( createTimestamp( 2001, 05, 01, 10, 20 ) ) ).isFalse( );
		assertThat( predicate.test( createTimestamp( 1999, 05, 01, 15, 20 ) ) ).isTrue( );
		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 14, 20 ) ) ).isTrue( );
	}

	@Test
	@DisplayName ( "Test the creation of a before-time predicate without date" )
	public void testCreateBeforeTimePredicateDateNull( ) {
		final Predicate<Long> predicate = filterService.createBeforeTimePredicate( e -> e, null, LocalTime.of( 15, 20 ) );

		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 15, 20 ) ) ).isTrue( );
		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 15, 25 ) ) ).isFalse( );
		assertThat( predicate.test( createTimestamp( 2001, 05, 01, 10, 20 ) ) ).isTrue( );
		assertThat( predicate.test( createTimestamp( 1999, 05, 01, 15, 20 ) ) ).isTrue( );
		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 14, 20 ) ) ).isTrue( );
	}

	@Test
	@DisplayName ( "Test the creation of a before-time predicate without time" )
	public void testCreateBeforeTimePredicateTimeNull( ) {
		final Predicate<Long> predicate = filterService.createBeforeTimePredicate( e -> e, LocalDate.of( 2000, 05, 01 ), null );

		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 15, 20 ) ) ).isTrue( );
		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 15, 25 ) ) ).isTrue( );
		assertThat( predicate.test( createTimestamp( 2001, 05, 01, 10, 20 ) ) ).isFalse( );
		assertThat( predicate.test( createTimestamp( 1999, 05, 01, 15, 20 ) ) ).isTrue( );
		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 14, 20 ) ) ).isTrue( );
	}

	@Test
	@DisplayName ( "Test the creation of a before-time predicate without date and time" )
	public void testCreateBeforeTimePredicateDateAndTimeNull( ) {
		final Predicate<Long> predicate = filterService.createBeforeTimePredicate( e -> e, null, null );

		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 15, 20 ) ) ).isTrue( );
		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 15, 25 ) ) ).isTrue( );
		assertThat( predicate.test( createTimestamp( 2001, 05, 01, 10, 20 ) ) ).isTrue( );
		assertThat( predicate.test( createTimestamp( 1999, 05, 01, 15, 20 ) ) ).isTrue( );
		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 14, 20 ) ) ).isTrue( );
	}

	private long createTimestamp( final int aYear, final int aMonth, final int aDay, final int aHour, final int aMinute ) {
		final Calendar calendar = Calendar.getInstance( );
		calendar.set( aYear, aMonth - 1, aDay, aHour, aMinute, 0 );
		return calendar.getTimeInMillis( );
	}

}
