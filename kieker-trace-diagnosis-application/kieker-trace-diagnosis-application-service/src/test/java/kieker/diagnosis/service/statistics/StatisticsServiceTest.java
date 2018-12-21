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

package kieker.diagnosis.service.statistics;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import kieker.diagnosis.service.KiekerTraceDiagnosisServiceModule;
import kieker.diagnosis.service.data.MethodCall;
import kieker.diagnosis.service.data.MonitoringLogService;

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
		final Injector injector = Guice.createInjector( new KiekerTraceDiagnosisServiceModule( ) );
		statisticsService = injector.getInstance( StatisticsService.class );
		dataService = injector.getInstance( MonitoringLogService.class );
	}

	@Test
	public void testNoData( ) {
		final Optional<Statistics> statistics = statisticsService.getStatistics( );
		assertThat( statistics.isPresent( ), is( false ) );
	}

	@Test
	public void testExistingData( ) {
		loadTestDataIntoDataService( );

		final Statistics statistics = statisticsService.getStatistics( ).get( );
		assertThat( statistics.getDanglingRecords( ), is( 42 ) );
		assertThat( statistics.getIgnoredRecords( ), is( 15 ) );
		assertThat( statistics.getIncompleteTraces( ), is( 10 ) );
		assertThat( statistics.getProcessedBytes( ), is( 50L ) );
		assertThat( statistics.getIncompleteTraces( ), is( 10 ) );

		assertThat( statistics.getAggregatedMethods( ), is( 0 ) );
		assertThat( statistics.getMethods( ), is( 2 ) );
		assertThat( statistics.getTraces( ), is( 1 ) );

		assertThat( statistics.getBeginnOfMonitoring( ), is( "01.12.2018, 20:00:00" ) );
		assertThat( statistics.getEndOfMonitoring( ), is( "02.12.2018, 22:00:00" ) );
	}

	private void loadTestDataIntoDataService( ) {
		dataService.setDataAvailable( true );
		dataService.setDanglingRecords( 42 );
		dataService.setIgnoredRecords( 15 );
		dataService.setIncompleteTraces( 10 );
		dataService.setProcessedBytes( 50L );

		final MethodCall methodCallStart = createMethodCall( 2018, 12, 1, 20, 00 );
		final MethodCall methodCallEnd = createMethodCall( 2018, 12, 2, 22, 00 );
		dataService.addMethods( Arrays.asList( methodCallStart, methodCallEnd ) );
		dataService.addTraceRoot( methodCallStart );
	}

	private MethodCall createMethodCall( final int aYear, final int aMonth, final int aDay, final int aHour, final int aMinute ) {
		final Calendar calendar = Calendar.getInstance( );
		calendar.set( aYear, aMonth - 1, aDay, aHour, aMinute, 0 );

		final MethodCall methodCall = new MethodCall( );
		methodCall.setTimestamp( calendar.getTimeInMillis( ) );

		return methodCall;
	}

}
