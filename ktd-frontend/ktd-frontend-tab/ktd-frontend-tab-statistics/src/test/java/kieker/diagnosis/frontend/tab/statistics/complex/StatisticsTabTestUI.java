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

package kieker.diagnosis.frontend.tab.statistics.complex;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit.ApplicationTest;

import com.google.inject.Guice;

import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import kieker.diagnosis.backend.base.service.ServiceMockModule;
import kieker.diagnosis.backend.search.statistics.Statistics;
import kieker.diagnosis.backend.search.statistics.StatisticsService;
import kieker.diagnosis.frontend.base.FrontendBaseModule;

/**
 * This is a UI test which checks that the statistics tab is working as expected.
 *
 * @author Nils Christian Ehmke
 */
public final class StatisticsTabTestUI extends ApplicationTest {

	private StatisticsService statisticsService;
	private StatisticsTab statisticsTab;
	private StatisticsPage statisticsPage;

	@Override
	public void start( final Stage stage ) throws Exception {
		statisticsService = mock( StatisticsService.class );
		Guice.createInjector( new FrontendBaseModule( ), new ServiceMockModule<>( StatisticsService.class, statisticsService ) );

		statisticsTab = new StatisticsTab( );
		final TabPane tabPane = new TabPane( statisticsTab );
		final Scene scene = new Scene( tabPane );
		stage.setScene( scene );
		stage.show( );

		statisticsPage = new StatisticsPage( this );
	}

	@Test
	public void testWithoutData( ) {
		loadData( createFirstStatistics( ) );

		assertThat( statisticsPage.getProcessedBytes( ).getText( ) ).isEqualTo( "<Keine Daten verfügbar> " );
		assertThat( statisticsPage.getProcessDuration( ).getText( ) ).isEqualTo( "<Keine Daten verfügbar> " );
		assertThat( statisticsPage.getProcessSpeed( ).getText( ) ).isEqualTo( "<Keine Daten verfügbar> " );
	}

	private void loadData( final Optional<Statistics> statistics ) {
		when( statisticsService.getStatistics( ) ).thenReturn( statistics );
		statisticsTab.prepareRefresh( );
		statisticsTab.performRefresh( );
	}

	private Optional<Statistics> createFirstStatistics( ) {
		return Optional.empty( );
	}

	@Test
	public void testWithFirstUnits( ) {
		loadData( createSecondStatistics( ) );

		assertThat( statisticsPage.getProcessedBytes( ).getText( ) ).isEqualTo( "250 [B]" );
		assertThat( statisticsPage.getProcessDuration( ).getText( ) ).isEqualTo( "10 [ms]" );
		assertThat( statisticsPage.getProcessSpeed( ).getText( ) ).isEqualTo( "1000 [B/s]" );
	}

	private Optional<Statistics> createSecondStatistics( ) {
		return Optional.of( Statistics
				.builder( )
				.processedBytes( 250L )
				.processDuration( 10L )
				.processSpeed( 1L )
				.build( ) );
	}

	@Test
	public void testWithSecondsUnits( ) {
		loadData( createThirdStatistics( ) );

		assertThat( statisticsPage.getProcessedBytes( ).getText( ) ).isEqualTo( "244 [KB]" );
		assertThat( statisticsPage.getProcessDuration( ).getText( ) ).isEqualTo( "10 [s]" );
		assertThat( statisticsPage.getProcessSpeed( ).getText( ) ).isEqualTo( "9 [KB/s]" );
	}

	private Optional<Statistics> createThirdStatistics( ) {
		return Optional.of( Statistics
				.builder( )
				.processedBytes( 250000L )
				.processDuration( 10000L )
				.processSpeed( 10L )
				.build( ) );
	}

	@Test
	public void testWithThirdUnits( ) {
		loadData( createFourthStatistics( ) );

		assertThat( statisticsPage.getProcessedBytes( ).getText( ) ).isEqualTo( "238 [MB]" );
		assertThat( statisticsPage.getProcessDuration( ).getText( ) ).isEqualTo( "10 [m]" );
		assertThat( statisticsPage.getProcessSpeed( ).getText( ) ).isEqualTo( "9 [MB/s]" );
	}

	private Optional<Statistics> createFourthStatistics( ) {
		return Optional.of( Statistics
				.builder( )
				.processedBytes( 250000000L )
				.processDuration( 600000L )
				.processSpeed( 9999L )
				.build( ) );
	}

}
