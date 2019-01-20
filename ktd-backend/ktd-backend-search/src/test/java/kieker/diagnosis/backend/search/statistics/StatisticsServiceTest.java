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

package kieker.diagnosis.backend.search.statistics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import kieker.diagnosis.backend.base.service.ServiceMockModule;
import kieker.diagnosis.backend.data.MethodCall;
import kieker.diagnosis.backend.data.reader.Repository;

/**
 * Test class for the {@link StatisticsService}.
 *
 * @author Nils Christian Ehmke
 */
public final class StatisticsServiceTest {

	private StatisticsService statisticsService;
	private Repository repository;

	@BeforeEach
	public void setUp( ) {
		repository = mock( Repository.class );
		final Injector injector = Guice.createInjector( new ServiceMockModule<>( Repository.class, repository ) );

		statisticsService = injector.getInstance( StatisticsService.class );
	}

	@Test
	public void testNoData( ) {
		final Optional<Statistics> statistics = statisticsService.getStatistics( );
		assertThat( statistics.isPresent( ) ).isFalse( );
	}

	@Test
	public void testExistingData( ) {
		when( repository.isDataAvailable( ) ).thenReturn( true );
		when( repository.getDanglingRecords( ) ).thenReturn( 42 );
		when( repository.getIgnoredRecords( ) ).thenReturn( 15 );
		when( repository.getIncompleteTraces( ) ).thenReturn( 10 );
		when( repository.getProcessedBytes( ) ).thenReturn( 50L );
		when( repository.getProcessDuration( ) ).thenReturn( 25L );
		when( repository.getDirectory( ) ).thenReturn( "/tmp/" );

		final MethodCall methodCallStart = createMethodCall( 2018, 12, 1, 20, 00 );
		final MethodCall methodCallEnd = createMethodCall( 2018, 12, 2, 22, 00 );

		when( repository.getMethods( ) ).thenReturn( Arrays.asList( methodCallStart, methodCallEnd ) );
		when( repository.getTraceRoots( ) ).thenReturn( Arrays.asList( methodCallStart ) );

		final Statistics statistics = statisticsService.getStatistics( ).get( );
		assertThat( statistics.getDanglingRecords( ) ).isEqualTo( 42 );
		assertThat( statistics.getIgnoredRecords( ) ).isEqualTo( 15 );
		assertThat( statistics.getIncompleteTraces( ) ).isEqualTo( 10 );
		assertThat( statistics.getProcessedBytes( ) ).isEqualTo( 50L );
		assertThat( statistics.getIncompleteTraces( ) ).isEqualTo( 10 );
		assertThat( statistics.getProcessDuration( ) ).isEqualTo( 25L );
		assertThat( statistics.getProcessSpeed( ) ).isEqualTo( 2L );

		assertThat( statistics.getDirectory( ) ).isEqualTo( "/tmp/" );

		assertThat( statistics.getAggregatedMethods( ) ).isEqualTo( 0 );
		assertThat( statistics.getMethods( ) ).isEqualTo( 2 );
		assertThat( statistics.getTraces( ) ).isEqualTo( 1 );

		assertThat( statistics.getBeginnOfMonitoring( ) ).isEqualTo( "01.12.2018, 20:00:00" );
		assertThat( statistics.getEndOfMonitoring( ) ).isEqualTo( "02.12.2018, 22:00:00" );
	}

	@Test
	public void testProcessDurationIsZero( ) {
		when( repository.isDataAvailable( ) ).thenReturn( true );
		when( repository.getProcessedBytes( ) ).thenReturn( 50L );
		when( repository.getProcessDuration( ) ).thenReturn( 0L );

		final Statistics statistics = statisticsService.getStatistics( ).get( );
		assertThat( statistics.getProcessDuration( ) ).isEqualTo( 0L );
		assertThat( statistics.getProcessSpeed( ) ).isEqualTo( 0L );
	}

	private MethodCall createMethodCall( final int year, final int month, final int day, final int hour, final int minute ) {
		final Calendar calendar = Calendar.getInstance( );
		calendar.set( year, month - 1, day, hour, minute, 0 );

		final MethodCall methodCall = new MethodCall( );
		methodCall.setTimestamp( calendar.getTimeInMillis( ) );

		return methodCall;
	}

}
