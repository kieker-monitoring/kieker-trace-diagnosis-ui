package kieker.diagnosis.service.settings;

import java.util.concurrent.TimeUnit;

import com.google.inject.Singleton;

import kieker.diagnosis.architecture.service.ServiceBase;
import kieker.diagnosis.architecture.service.properties.PropertiesService;
import kieker.diagnosis.service.settings.properties.ClassAppearanceProperty;
import kieker.diagnosis.service.settings.properties.MaxNumberOfMethodCallsProperty;
import kieker.diagnosis.service.settings.properties.MethodAppearanceProperty;
import kieker.diagnosis.service.settings.properties.MethodCallAggregationProperty;
import kieker.diagnosis.service.settings.properties.MethodCallThresholdProperty;
import kieker.diagnosis.service.settings.properties.ShowUnmonitoredTimeProperty;
import kieker.diagnosis.service.settings.properties.TimeUnitProperty;
import kieker.diagnosis.service.settings.properties.TimestampProperty;

/**
 * This service is responsible for handling the application settings.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class SettingsService extends ServiceBase {

	/**
	 * This method loads the current settings of the application.
	 *
	 * @return The current settings.
	 */
	public Settings loadSettings( ) {
		final Settings settings = new Settings( );

		final PropertiesService propertiesService = getService( PropertiesService.class );
		settings.setTimestampAppearance( propertiesService.loadApplicationProperty( TimestampProperty.class ) );
		settings.setTimeUnit( propertiesService.loadApplicationProperty( TimeUnitProperty.class ) );
		settings.setClassAppearance( propertiesService.loadApplicationProperty( ClassAppearanceProperty.class ) );
		settings.setMethodAppearance( propertiesService.loadApplicationProperty( MethodAppearanceProperty.class ) );
		settings.setShowUnmonitoredTimeProperty( propertiesService.loadApplicationProperty( ShowUnmonitoredTimeProperty.class ) );
		settings.setMethodCallAggregation( propertiesService.loadApplicationProperty( MethodCallAggregationProperty.class ) );
		settings.setMethodCallThreshold( propertiesService.loadApplicationProperty( MethodCallThresholdProperty.class ) );
		settings.setMaxNumberOfMethodCalls( propertiesService.loadApplicationProperty( MaxNumberOfMethodCallsProperty.class ) );

		return settings;
	}

	/**
	 * This method saves the given application settings.
	 *
	 * @param aSettings
	 *            The new settings.
	 */
	public void saveSettings( final Settings aSettings ) {
		final PropertiesService propertiesService = getService( PropertiesService.class );
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
	 * This method returns a suitable suffix for durations depending on the current settings. The suffix is of the form {@code [ms]} for milliseconds for
	 * example.
	 *
	 * @return The current duration suffix.
	 */
	public String getCurrentDurationSuffix( ) {
		final PropertiesService propertiesService = getService( PropertiesService.class );
		final TimeUnit timeUnit = propertiesService.loadApplicationProperty( TimeUnitProperty.class );

		String suffix;

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
