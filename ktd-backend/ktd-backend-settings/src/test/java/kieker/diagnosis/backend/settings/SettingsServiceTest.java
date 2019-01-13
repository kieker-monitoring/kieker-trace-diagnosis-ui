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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import kieker.diagnosis.backend.base.ServiceBaseModule;
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

	@Before
	public void setUp( ) {
		final Injector injector = Guice.createInjector( new ServiceBaseModule( ) );
		settingsService = injector.getInstance( SettingsService.class );
		propertiesService = injector.getInstance( PropertiesService.class );
		currentSettings = settingsService.loadSettings( );
		currentTimeUnit = propertiesService.loadApplicationProperty( TimeUnitProperty.class );
	}

	@After
	public void tearDown( ) {
		settingsService.saveSettings( currentSettings );
		propertiesService.saveApplicationProperty( TimeUnitProperty.class, currentTimeUnit );
	}

	@Test
	public void testLoadSettings( ) {
		final Settings settings = settingsService.loadSettings( );

		assertThat( settings, is( notNullValue( ) ) );
		assertThat( settings.getClassAppearance( ), is( notNullValue( ) ) );
		assertThat( settings.getMaxNumberOfMethodCalls( ), is( notNullValue( ) ) );
		assertThat( settings.getMethodAppearance( ), is( notNullValue( ) ) );
		assertThat( settings.getMethodCallAggregation( ), is( notNullValue( ) ) );
		assertThat( settings.getMethodCallThreshold( ), is( notNullValue( ) ) );
		assertThat( settings.getTimestampAppearance( ), is( notNullValue( ) ) );
		assertThat( settings.getTimeUnit( ), is( notNullValue( ) ) );
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
		assertThat( loadedSettings, is( notNullValue( ) ) );
		assertThat( loadedSettings.getClassAppearance( ), is( ClassAppearance.SHORT ) );
		assertThat( loadedSettings.getMaxNumberOfMethodCalls( ), is( 42 ) );
		assertThat( loadedSettings.getMethodAppearance( ), is( MethodAppearance.LONG ) );
		assertThat( loadedSettings.getMethodCallAggregation( ), is( MethodCallAggregation.BY_TRACE_DEPTH ) );
		assertThat( loadedSettings.isShowUnmonitoredTimeProperty( ), is( true ) );
		assertThat( loadedSettings.getMethodCallThreshold( ), is( 42.5f ) );
		assertThat( loadedSettings.getTimestampAppearance( ), is( TimestampAppearance.LONG_TIME ) );
		assertThat( loadedSettings.getTimeUnit( ), is( TimeUnit.HOURS ) );
	}

	@Test
	public void testGetCurrentDurationSuffix( ) {
		propertiesService.saveApplicationProperty( TimeUnitProperty.class, TimeUnit.DAYS );
		assertThat( settingsService.getCurrentDurationSuffix( ), is( "[d]" ) );

		propertiesService.saveApplicationProperty( TimeUnitProperty.class, TimeUnit.HOURS );
		assertThat( settingsService.getCurrentDurationSuffix( ), is( "[h]" ) );

		propertiesService.saveApplicationProperty( TimeUnitProperty.class, TimeUnit.MICROSECONDS );
		assertThat( settingsService.getCurrentDurationSuffix( ), is( "[µs]" ) );

		propertiesService.saveApplicationProperty( TimeUnitProperty.class, TimeUnit.MILLISECONDS );
		assertThat( settingsService.getCurrentDurationSuffix( ), is( "[ms]" ) );

		propertiesService.saveApplicationProperty( TimeUnitProperty.class, TimeUnit.MINUTES );
		assertThat( settingsService.getCurrentDurationSuffix( ), is( "[m]" ) );

		propertiesService.saveApplicationProperty( TimeUnitProperty.class, TimeUnit.NANOSECONDS );
		assertThat( settingsService.getCurrentDurationSuffix( ), is( "[ns]" ) );

		propertiesService.saveApplicationProperty( TimeUnitProperty.class, TimeUnit.SECONDS );
		assertThat( settingsService.getCurrentDurationSuffix( ), is( "[s]" ) );
	}

}
