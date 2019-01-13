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

import java.util.concurrent.TimeUnit;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import kieker.diagnosis.backend.base.service.Service;
import kieker.diagnosis.backend.properties.PropertiesService;
import kieker.diagnosis.backend.settings.properties.ClassAppearanceProperty;
import kieker.diagnosis.backend.settings.properties.MaxNumberOfMethodCallsProperty;
import kieker.diagnosis.backend.settings.properties.MethodAppearanceProperty;
import kieker.diagnosis.backend.settings.properties.MethodCallAggregationProperty;
import kieker.diagnosis.backend.settings.properties.MethodCallThresholdProperty;
import kieker.diagnosis.backend.settings.properties.ShowUnmonitoredTimeProperty;
import kieker.diagnosis.backend.settings.properties.TimeUnitProperty;
import kieker.diagnosis.backend.settings.properties.TimestampProperty;

/**
 * This service is responsible for handling the application settings.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class SettingsService implements Service {

	@Inject
	private PropertiesService propertiesService;

	/**
	 * This method loads the current settings of the application.
	 *
	 * @return The current settings.
	 */
	public Settings loadSettings( ) {
		return Settings.builder( )
				.timestampAppearance( propertiesService.loadApplicationProperty( TimestampProperty.class ) )
				.timeUnit( propertiesService.loadApplicationProperty( TimeUnitProperty.class ) )
				.classAppearance( propertiesService.loadApplicationProperty( ClassAppearanceProperty.class ) )
				.methodAppearance( propertiesService.loadApplicationProperty( MethodAppearanceProperty.class ) )
				.showUnmonitoredTimeProperty( propertiesService.loadApplicationProperty( ShowUnmonitoredTimeProperty.class ) )
				.methodCallAggregation( propertiesService.loadApplicationProperty( MethodCallAggregationProperty.class ) )
				.methodCallThreshold( propertiesService.loadApplicationProperty( MethodCallThresholdProperty.class ) )
				.maxNumberOfMethodCalls( propertiesService.loadApplicationProperty( MaxNumberOfMethodCallsProperty.class ) )
				.build( );
	}

	/**
	 * This method saves the given application settings.
	 *
	 * @param aSettings
	 *            The new settings.
	 */
	public void saveSettings( final Settings aSettings ) {
		propertiesService.saveApplicationProperty( TimestampProperty.class, aSettings.getTimestampAppearance( ) );
		propertiesService.saveApplicationProperty( TimeUnitProperty.class, aSettings.getTimeUnit( ) );
		propertiesService.saveApplicationProperty( ClassAppearanceProperty.class, aSettings.getClassAppearance( ) );
		propertiesService.saveApplicationProperty( MethodAppearanceProperty.class, aSettings.getMethodAppearance( ) );
		propertiesService.saveApplicationProperty( ShowUnmonitoredTimeProperty.class, aSettings.isShowUnmonitoredTimeProperty( ) );
		propertiesService.saveApplicationProperty( MethodCallAggregationProperty.class, aSettings.getMethodCallAggregation( ) );
		propertiesService.saveApplicationProperty( MethodCallThresholdProperty.class, aSettings.getMethodCallThreshold( ) );
		propertiesService.saveApplicationProperty( MaxNumberOfMethodCallsProperty.class, aSettings.getMaxNumberOfMethodCalls( ) );
	}

	/**
	 * This method returns a suitable suffix for durations depending on the current settings. The suffix is of the form
	 * {@code [ms]} for milliseconds for example.
	 *
	 * @return The current duration suffix.
	 */
	public String getCurrentDurationSuffix( ) {
		final TimeUnit timeUnit = propertiesService.loadApplicationProperty( TimeUnitProperty.class );

		final String suffix;

		switch ( timeUnit ) {
			case DAYS:
				suffix = "[d]";
			break;
			case HOURS:
				suffix = "[h]";
			break;
			case MICROSECONDS:
				suffix = "[µs]";
			break;
			case MILLISECONDS:
				suffix = "[ms]";
			break;
			case MINUTES:
				suffix = "[m]";
			break;
			case NANOSECONDS:
				suffix = "[ns]";
			break;
			case SECONDS:
				suffix = "[s]";
			break;
			default:
				suffix = null;
			break;

		}

		return suffix;
	}

}
