/***************************************************************************
 * Copyright 2015-2018 Kieker Project (http://kieker-monitoring.net)
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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import kieker.diagnosis.backend.base.ServiceBaseModule;
import kieker.diagnosis.backend.data.MethodCall;
import kieker.diagnosis.backend.data.MonitoringLogService;
import kieker.diagnosis.backend.search.ServiceMockModule;
import kieker.diagnosis.backend.search.statistics.Statistics;
import kieker.diagnosis.backend.search.statistics.StatisticsService;

/**
 * Test class for the {@link StatisticsService}.
 *
 * @author Nils Christian Ehmke
 */
public final class StatisticsServiceTest {

	private StatisticsService statisticsService;
	private MonitoringLogService dataService;

	@Before
	public void setUp( ) {
		dataService = mock( MonitoringLogService.class );

		final Injector injector = Guice.createInjector( new ServiceBaseModule( ), new ServiceMockModule( MonitoringLogService.class, dataService ) );
		statisticsService = injector.getInstance( StatisticsService.class );
	}

	@Test
	public void testNoData( ) {
		final Optional<Statistics> statistics = statisticsService.getStatistics( );
		assertThat( statistics.isPresent( ), is( false ) );
	}

	@Test
	public void testExistingData( ) {
		when( dataService.isDataAvailable( ) ).thenReturn( true );
		when( dataService.getDanglingRecords( ) ).thenReturn( 42 );
		when( dataService.getIgnoredRecords( ) ).thenReturn( 15 );
		when( dataService.getIncompleteTraces( ) ).thenReturn( 10 );
		when( dataService.getProcessedBytes( ) ).thenReturn( 50L );
		when( dataService.getProcessDuration( ) ).thenReturn( 25L );
		when( dataService.getDirectory( ) ).thenReturn( "/tmp/" );

		final MethodCall methodCallStart = createMethodCall( 2018, 12, 1, 20, 00 );
		final MethodCall methodCallEnd = createMethodCall( 2018, 12, 2, 22, 00 );

		when( dataService.getMethods( ) ).thenReturn( Arrays.asList( methodCallStart, methodCallEnd ) );
		when( dataService.getTraceRoots( ) ).thenReturn( Arrays.asList( methodCallStart ) );

		final Statistics statistics = statisticsService.getStatistics( ).get( );
		assertThat( statistics.getDanglingRecords( ), is( 42 ) );
		assertThat( statistics.getIgnoredRecords( ), is( 15 ) );
		assertThat( statistics.getIncompleteTraces( ), is( 10 ) );
		assertThat( statistics.getProcessedBytes( ), is( 50L ) );
		assertThat( statistics.getIncompleteTraces( ), is( 10 ) );
		assertThat( statistics.getProcessDuration( ), is( 25L ) );
		assertThat( statistics.getProcessSpeed( ), is( 2L ) );

		assertThat( statistics.getDirectory( ), is( "/tmp/" ) );

		assertThat( statistics.getAggregatedMethods( ), is( 0 ) );
		assertThat( statistics.getMethods( ), is( 2 ) );
		assertThat( statistics.getTraces( ), is( 1 ) );

		assertThat( statistics.getBeginnOfMonitoring( ), is( "01.12.2018, 20:00:00" ) );
		assertThat( statistics.getEndOfMonitoring( ), is( "02.12.2018, 22:00:00" ) );
	}

	@Test
	public void testProcessDurationIsZero( ) {
		when( dataService.isDataAvailable( ) ).thenReturn( true );
		when( dataService.getProcessedBytes( ) ).thenReturn( 50L );
		when( dataService.getProcessDuration( ) ).thenReturn( 0L );

		final Statistics statistics = statisticsService.getStatistics( ).get( );
		assertThat( statistics.getProcessDuration( ), is( 0L ) );
		assertThat( statistics.getProcessSpeed( ), is( 0L ) );
	}

	private MethodCall createMethodCall( final int aYear, final int aMonth, final int aDay, final int aHour, final int aMinute ) {
		final Calendar calendar = Calendar.getInstance( );
		calendar.set( aYear, aMonth - 1, aDay, aHour, aMinute, 0 );

		final MethodCall methodCall = new MethodCall( );
		methodCall.setTimestamp( calendar.getTimeInMillis( ) );

		return methodCall;
	}

}
