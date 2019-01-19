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

package kieker.diagnosis.backend.settings;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import kieker.diagnosis.backend.properties.PropertiesService;
import kieker.diagnosis.backend.settings.properties.TimeUnitProperty;

/**
 * Test class for the {@link SettingsService}.
 *
 * @author Nils Christian Ehmke
 */
public final class SettingsServiceTest {

	private SettingsService settingsService;
	private PropertiesService propertiesService;

	private Settings currentSettings;
	private TimeUnit currentTimeUnit;

	@BeforeEach
	public void setUp( ) {
		final Injector injector = Guice.createInjector( );
		settingsService = injector.getInstance( SettingsService.class );
		propertiesService = injector.getInstance( PropertiesService.class );
		currentSettings = settingsService.loadSettings( );
		currentTimeUnit = propertiesService.loadApplicationProperty( TimeUnitProperty.class );
	}

	@AfterEach
	public void tearDown( ) {
		settingsService.saveSettings( currentSettings );
		propertiesService.saveApplicationProperty( TimeUnitProperty.class, currentTimeUnit );
	}

	@Test
	public void testLoadSettings( ) {
		final Settings settings = settingsService.loadSettings( );

		assertThat( settings ).isNotNull( );
		assertThat( settings.getClassAppearance( ) ).isNotNull( );
		assertThat( settings.getMaxNumberOfMethodCalls( ) ).isNotNull( );
		assertThat( settings.getMethodAppearance( ) ).isNotNull( );
		assertThat( settings.getMethodCallAggregation( ) ).isNotNull( );
		assertThat( settings.getMethodCallThreshold( ) ).isNotNull( );
		assertThat( settings.getTimestampAppearance( ) ).isNotNull( );
		assertThat( settings.getTimeUnit( ) ).isNotNull( );
	}

	@Test
	public void testSaveSettings( ) {
		final Settings settings = Settings.builder( )
				.classAppearance( ClassAppearance.SHORT )
				.maxNumberOfMethodCalls( 42 )
				.methodAppearance( MethodAppearance.LONG )
				.methodCallAggregation( MethodCallAggregation.BY_TRACE_DEPTH )
				.methodCallThreshold( 42.5f )
				.showUnmonitoredTimeProperty( true )
				.timestampAppearance( TimestampAppearance.LONG_TIME )
				.timeUnit( TimeUnit.HOURS )
				.build( );
		settingsService.saveSettings( settings );

		final Settings loadedSettings = settingsService.loadSettings( );
		assertThat( loadedSettings ).isNotNull( );
		assertThat( loadedSettings.getClassAppearance( ) ).isEqualTo( ClassAppearance.SHORT );
		assertThat( loadedSettings.getMaxNumberOfMethodCalls( ) ).isEqualTo( 42 );
		assertThat( loadedSettings.getMethodAppearance( ) ).isEqualTo( MethodAppearance.LONG );
		assertThat( loadedSettings.getMethodCallAggregation( ) ).isEqualTo( MethodCallAggregation.BY_TRACE_DEPTH );
		assertThat( loadedSettings.isShowUnmonitoredTimeProperty( ) ).isEqualTo( true );
		assertThat( loadedSettings.getMethodCallThreshold( ) ).isEqualTo( 42.5f );
		assertThat( loadedSettings.getTimestampAppearance( ) ).isEqualTo( TimestampAppearance.LONG_TIME );
		assertThat( loadedSettings.getTimeUnit( ) ).isEqualTo( TimeUnit.HOURS );
	}

	@Test
	public void testGetCurrentDurationSuffix( ) {
		propertiesService.saveApplicationProperty( TimeUnitProperty.class, TimeUnit.DAYS );
		assertThat( settingsService.getCurrentDurationSuffix( ) ).isEqualTo( "[d]" );

		propertiesService.saveApplicationProperty( TimeUnitProperty.class, TimeUnit.HOURS );
		assertThat( settingsService.getCurrentDurationSuffix( ) ).isEqualTo( "[h]" );

		propertiesService.saveApplicationProperty( TimeUnitProperty.class, TimeUnit.MICROSECONDS );
		assertThat( settingsService.getCurrentDurationSuffix( ) ).isEqualTo( "[µs]" );

		propertiesService.saveApplicationProperty( TimeUnitProperty.class, TimeUnit.MILLISECONDS );
		assertThat( settingsService.getCurrentDurationSuffix( ) ).isEqualTo( "[ms]" );

		propertiesService.saveApplicationProperty( TimeUnitProperty.class, TimeUnit.MINUTES );
		assertThat( settingsService.getCurrentDurationSuffix( ) ).isEqualTo( "[m]" );

		propertiesService.saveApplicationProperty( TimeUnitProperty.class, TimeUnit.NANOSECONDS );
		assertThat( settingsService.getCurrentDurationSuffix( ) ).isEqualTo( "[ns]" );

		propertiesService.saveApplicationProperty( TimeUnitProperty.class, TimeUnit.SECONDS );
		assertThat( settingsService.getCurrentDurationSuffix( ) ).isEqualTo( "[s]" );
	}

}
