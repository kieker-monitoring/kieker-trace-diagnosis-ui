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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import kieker.diagnosis.backend.base.ServiceBaseModule;

/**
 * This is a unit test the {@link FilterService}.
 *
 * @author Nils Christian Ehmke
 */
public final class FilterServiceTest {

	private FilterService filterService;

	@Before
	public void setUp( ) {
		final Injector injector = Guice.createInjector( new ServiceBaseModule( ) );
		filterService = injector.getInstance( FilterService.class );
	}

	@Test
	public void testCreateLongPredicate( ) {
		final Predicate<Long> predicate = filterService.createLongPredicate( e -> e, 42L );
		assertThat( predicate.test( 42L ), is( true ) );
		assertThat( predicate.test( 15L ), is( false ) );
	}

	@Test
	public void testCreateLongPredicateWithNull( ) {
		final Predicate<Long> predicate = filterService.createLongPredicate( null, null );
		assertThat( predicate.test( null ), is( true ) );
	}

	@Test
	public void testCreateStringPredicate( ) {
		final Predicate<String> predicate = filterService.createStringPredicate( e -> e, "A", false );
		assertThat( predicate.test( "A" ), is( true ) );
		assertThat( predicate.test( "a" ), is( true ) );
		assertThat( predicate.test( "B" ), is( false ) );
		assertThat( predicate.test( null ), is( false ) );
	}

	@Test
	public void testCreateStringPredicateWithRegExpr( ) {
		final Predicate<String> predicate = filterService.createStringPredicate( e -> e, ".*A.*", true );
		assertThat( predicate.test( "A" ), is( true ) );
		assertThat( predicate.test( "BAB" ), is( true ) );
		assertThat( predicate.test( "B" ), is( false ) );
		assertThat( predicate.test( null ), is( false ) );
	}

	@Test
	public void testCreateStringPredicateWithNull( ) {
		final Predicate<String> predicate = filterService.createStringPredicate(null, null, false );
		assertThat( predicate.test( null ), is( true ) );
	}

	@Test
	public void testCreateStringPredicateWithNullAndRegExpr( ) {
		final Predicate<String> predicate = filterService.createStringPredicate( e -> e, ".*", true );
		assertThat( predicate.test( null ), is( false ) );
	}

	@Test
	public void testConjunct( ) {
		final Predicate<Integer> predicateA = value -> value % 2 == 0;
		final Predicate<Integer> predicateB = value -> value % 3 == 0;
		final Predicate<Integer> conjunction = filterService.conjunct( Arrays.asList( predicateA, predicateB ) );

		assertThat( conjunction.test( 12 ), is( true ) );
		assertThat( conjunction.test( 30 ), is( true ) );
		assertThat( conjunction.test( 9 ), is( false ) );
		assertThat( conjunction.test( 4 ), is( false ) );
	}

	@Test
	public void testCreateAfterTimePredicate( ) {
		final Predicate<Long> predicate = filterService.createAfterTimePredicate( e -> e, LocalDate.of( 2000, 05, 01 ), LocalTime.of( 15, 20 ) );

		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 15, 20 ) ), is( true ) );
		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 15, 25 ) ), is( true ) );
		assertThat( predicate.test( createTimestamp( 2001, 05, 01, 10, 20 ) ), is( true ) );
		assertThat( predicate.test( createTimestamp( 1999, 05, 01, 15, 20 ) ), is( false ) );
		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 14, 20 ) ), is( false ) );
	}

	@Test
	public void testCreateAfterTimePredicateDateNull( ) {
		final Predicate<Long> predicate = filterService.createAfterTimePredicate( e -> e, null, LocalTime.of( 15, 20 ) );

		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 15, 20 ) ), is( true ) );
		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 15, 25 ) ), is( true ) );
		assertThat( predicate.test( createTimestamp( 2001, 05, 01, 10, 20 ) ), is( false ) );
		assertThat( predicate.test( createTimestamp( 1999, 05, 01, 15, 20 ) ), is( true ) );
		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 14, 20 ) ), is( false ) );
	}

	@Test
	public void testCreateAfterTimePredicateTimeNull( ) {
		final Predicate<Long> predicate = filterService.createAfterTimePredicate( e -> e, LocalDate.of( 2000, 05, 01 ), null );

		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 15, 20 ) ), is( true ) );
		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 15, 25 ) ), is( true ) );
		assertThat( predicate.test( createTimestamp( 2001, 05, 01, 10, 20 ) ), is( true ) );
		assertThat( predicate.test( createTimestamp( 1999, 05, 01, 15, 20 ) ), is( false ) );
		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 14, 20 ) ), is( true ) );
	}

	@Test
	public void testCreateAfterTimePredicateDateAndTimeNull( ) {
		final Predicate<Long> predicate = filterService.createAfterTimePredicate( e -> e, null, null );

		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 15, 20 ) ), is( true ) );
		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 15, 25 ) ), is( true ) );
		assertThat( predicate.test( createTimestamp( 2001, 05, 01, 10, 20 ) ), is( true ) );
		assertThat( predicate.test( createTimestamp( 1999, 05, 01, 15, 20 ) ), is( true ) );
		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 14, 20 ) ), is( true ) );
	}

	@Test
	public void testCreateBeforeTimePredicate( ) {
		final Predicate<Long> predicate = filterService.createBeforeTimePredicate( e -> e, LocalDate.of( 2000, 05, 01 ), LocalTime.of( 15, 20 ) );

		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 15, 20 ) ), is( true ) );
		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 15, 25 ) ), is( false ) );
		assertThat( predicate.test( createTimestamp( 2001, 05, 01, 10, 20 ) ), is( false ) );
		assertThat( predicate.test( createTimestamp( 1999, 05, 01, 15, 20 ) ), is( true ) );
		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 14, 20 ) ), is( true ) );
	}

	@Test
	public void testCreateBeforeTimePredicateDateNull( ) {
		final Predicate<Long> predicate = filterService.createBeforeTimePredicate( e -> e, null, LocalTime.of( 15, 20 ) );

		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 15, 20 ) ), is( true ) );
		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 15, 25 ) ), is( false ) );
		assertThat( predicate.test( createTimestamp( 2001, 05, 01, 10, 20 ) ), is( true ) );
		assertThat( predicate.test( createTimestamp( 1999, 05, 01, 15, 20 ) ), is( true ) );
		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 14, 20 ) ), is( true ) );
	}

	@Test
	public void testCreateBeforeTimePredicateTimeNull( ) {
		final Predicate<Long> predicate = filterService.createBeforeTimePredicate( e -> e, LocalDate.of( 2000, 05, 01 ), null );

		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 15, 20 ) ), is( true ) );
		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 15, 25 ) ), is( true ) );
		assertThat( predicate.test( createTimestamp( 2001, 05, 01, 10, 20 ) ), is( false ) );
		assertThat( predicate.test( createTimestamp( 1999, 05, 01, 15, 20 ) ), is( true ) );
		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 14, 20 ) ), is( true ) );
	}

	@Test
	public void testCreateBeforeTimePredicateDateAndTimeNull( ) {
		final Predicate<Long> predicate = filterService.createBeforeTimePredicate( e -> e, null, null );

		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 15, 20 ) ), is( true ) );
		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 15, 25 ) ), is( true ) );
		assertThat( predicate.test( createTimestamp( 2001, 05, 01, 10, 20 ) ), is( true ) );
		assertThat( predicate.test( createTimestamp( 1999, 05, 01, 15, 20 ) ), is( true ) );
		assertThat( predicate.test( createTimestamp( 2000, 05, 01, 14, 20 ) ), is( true ) );
	}

	private long createTimestamp( final int aYear, final int aMonth, final int aDay, final int aHour, final int aMinute ) {
		final Calendar calendar = Calendar.getInstance( );
		calendar.set( aYear, aMonth - 1, aDay, aHour, aMinute, 0 );
		return calendar.getTimeInMillis( );
	}

}
