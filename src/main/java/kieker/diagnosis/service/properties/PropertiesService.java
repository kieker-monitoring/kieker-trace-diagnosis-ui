/***************************************************************************
 * Copyright 2015-2016 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.service.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import kieker.diagnosis.service.ServiceIfc;

/**
 * @author Nils Christian Ehmke
 */
public final class PropertiesService implements ServiceIfc {

	private static final Logger LOGGER = LogManager.getLogger( PropertiesService.class );

	private static final String KEY_TIMEUNIT = "timeunit";
	private static final String KEY_OPERATIONS = "operations";
	private static final String KEY_COMPONENTS = "components";
	private static final String KEY_ADDITIONAL_LOG_CHECKS = "additionalLogChecks";
	private static final String KEY_REGULAR_EXPRESSIONS = "regularExpressions";
	private static final String KEY_METHOD_CALL_AGGREGATION = "methodCallAggregationActive";
	private static final String KEY_THRESHOLD = "threshold";
	private static final String KEY_CASE_SENSITIVE = "caseSensitive";
	private static final String KEY_PERCENTAGE_CALCULATION = "percentageCalculation";
	private static final String KEY_TIMESTAMP_TYPE = "timestampType";
	private static final String KEY_CACHE_VIEWS = "cacheViews";
	private static final String KEY_SEARCH_IN_ENTIRE_TRACE = "searchInEntireTrace";

	private static final String KEY_GITLAB_URL = "GitLabURL";
	private static final String KEY_TRAC_URL = "TracURL";

	private TimeUnit ivTimeUnit;
	private ComponentNames ivComponentNames;
	private OperationNames ivOperationNames;
	private Threshold ivThreshold;
	private TimestampTypes ivTimestampType;
	private boolean ivAdditionalLogChecksActive;
	private boolean ivRegularExpressionsActive;
	private boolean ivMethodCallAggregationActive;
	private boolean ivCaseSensitivityActive;
	private boolean ivPercentageCalculationActive;
	private boolean ivCacheViews;
	private String ivGitLabURL;
	private String ivTracURL;
	private boolean ivSearchInEntireTrace;

	private long ivVersion = 0L;

	public PropertiesService( ) {
		loadSettings( );
	}

	private void loadSettings( ) {
		final Preferences preferences = Preferences.userNodeForPackage( PropertiesService.class );

		ivTimeUnit = TimeUnit.valueOf( preferences.get( PropertiesService.KEY_TIMEUNIT, TimeUnit.NANOSECONDS.name( ) ) );
		ivComponentNames = ComponentNames.valueOf( preferences.get( PropertiesService.KEY_COMPONENTS, ComponentNames.LONG.name( ) ) );
		ivOperationNames = OperationNames.valueOf( preferences.get( PropertiesService.KEY_OPERATIONS, OperationNames.SHORT.name( ) ) );
		ivAdditionalLogChecksActive = Boolean.valueOf( preferences.get( PropertiesService.KEY_ADDITIONAL_LOG_CHECKS, Boolean.FALSE.toString( ) ) );
		ivRegularExpressionsActive = Boolean.valueOf( preferences.get( PropertiesService.KEY_REGULAR_EXPRESSIONS, Boolean.FALSE.toString( ) ) );
		ivMethodCallAggregationActive = Boolean.valueOf( preferences.get( PropertiesService.KEY_METHOD_CALL_AGGREGATION, Boolean.FALSE.toString( ) ) );
		ivThreshold = Threshold.valueOf( preferences.get( PropertiesService.KEY_THRESHOLD, Threshold.THRESHOLD_1.name( ) ) );
		ivCaseSensitivityActive = Boolean.valueOf( preferences.get( PropertiesService.KEY_CASE_SENSITIVE, Boolean.FALSE.toString( ) ) );
		ivPercentageCalculationActive = Boolean.valueOf( preferences.get( PropertiesService.KEY_PERCENTAGE_CALCULATION, Boolean.FALSE.toString( ) ) );
		ivTimestampType = TimestampTypes.valueOf( preferences.get( PropertiesService.KEY_TIMESTAMP_TYPE, TimestampTypes.TIMESTAMP.name( ) ) );
		ivCacheViews = Boolean.valueOf( preferences.get( PropertiesService.KEY_CACHE_VIEWS, Boolean.FALSE.toString( ) ) );
		ivSearchInEntireTrace = Boolean.valueOf( preferences.get( PropertiesService.KEY_SEARCH_IN_ENTIRE_TRACE, Boolean.FALSE.toString( ) ) );

		final Properties properties = new Properties( );
		final ClassLoader classLoader = PropertiesService.class.getClassLoader( );
		try ( InputStream inputStream = classLoader.getResourceAsStream( "config.properties" ) ) {
			properties.load( inputStream );
			ivGitLabURL = properties.getProperty( PropertiesService.KEY_GITLAB_URL );
			ivTracURL = properties.getProperty( PropertiesService.KEY_TRAC_URL );
		} catch ( final IOException e ) {
			PropertiesService.LOGGER.error( e );
		}
	}

	private void saveSettings( ) {
		final Preferences preferences = Preferences.userNodeForPackage( PropertiesService.class );

		preferences.put( PropertiesService.KEY_TIMEUNIT, ivTimeUnit.name( ) );
		preferences.put( PropertiesService.KEY_COMPONENTS, ivComponentNames.name( ) );
		preferences.put( PropertiesService.KEY_OPERATIONS, ivOperationNames.name( ) );
		preferences.put( PropertiesService.KEY_ADDITIONAL_LOG_CHECKS, Boolean.toString( ivAdditionalLogChecksActive ) );
		preferences.put( PropertiesService.KEY_REGULAR_EXPRESSIONS, Boolean.toString( ivRegularExpressionsActive ) );
		preferences.put( PropertiesService.KEY_METHOD_CALL_AGGREGATION, Boolean.toString( ivMethodCallAggregationActive ) );
		preferences.put( PropertiesService.KEY_THRESHOLD, ivThreshold.name( ) );
		preferences.put( PropertiesService.KEY_CASE_SENSITIVE, Boolean.toString( ivCaseSensitivityActive ) );
		preferences.put( PropertiesService.KEY_PERCENTAGE_CALCULATION, Boolean.toString( ivPercentageCalculationActive ) );
		preferences.put( PropertiesService.KEY_TIMESTAMP_TYPE, ivTimestampType.name( ) );
		preferences.put( PropertiesService.KEY_CACHE_VIEWS, Boolean.toString( ivCacheViews ) );
		preferences.put( PropertiesService.KEY_SEARCH_IN_ENTIRE_TRACE, Boolean.toString( ivSearchInEntireTrace ) );

		try {
			preferences.flush( );
		} catch ( final BackingStoreException e ) {
			PropertiesService.LOGGER.error( e );
		}

		ivVersion++;
	}

	public TimeUnit getTimeUnit( ) {
		return ivTimeUnit;
	}

	public void setTimeUnit( final TimeUnit aTimeUnit ) {
		ivTimeUnit = aTimeUnit;
		saveSettings( );
	}

	public ComponentNames getComponentNames( ) {
		return ivComponentNames;
	}

	public void setComponentNames( final ComponentNames aComponentNames ) {
		ivComponentNames = aComponentNames;
		saveSettings( );
	}

	public OperationNames getOperationNames( ) {
		return ivOperationNames;
	}

	public void setOperationNames( final OperationNames aOperationNames ) {
		ivOperationNames = aOperationNames;
		saveSettings( );
	}

	public boolean isAdditionalLogChecksActive( ) {
		return ivAdditionalLogChecksActive;
	}

	public void setAdditionalLogChecksActive( final boolean aActive ) {
		ivAdditionalLogChecksActive = aActive;
		saveSettings( );
	}

	public boolean isRegularExpressionsActive( ) {
		return ivRegularExpressionsActive;
	}

	public void setRegularExpressionsActive( final boolean aActive ) {
		ivRegularExpressionsActive = aActive;
		saveSettings( );
	}

	public String getGitLabURL( ) {
		return ivGitLabURL;
	}

	public String getTracURL( ) {
		return ivTracURL;
	}

	public boolean isMethodCallAggregationActive( ) {
		return ivMethodCallAggregationActive;
	}

	public void setMethodCallAggregationActive( final boolean aActive ) {
		ivMethodCallAggregationActive = aActive;
		saveSettings( );
	}

	public Threshold getThreshold( ) {
		return ivThreshold;
	}

	public void setThreshold( final Threshold aThreshold ) {
		ivThreshold = aThreshold;
		saveSettings( );
	}

	public boolean isCaseSensitivityActive( ) {
		return ivCaseSensitivityActive;
	}

	public void setCaseSensitivityActive( final boolean aActive ) {
		ivCaseSensitivityActive = aActive;
		saveSettings( );
	}

	public long getVersion( ) {
		return ivVersion;
	}

	public boolean isPercentageCalculationActive( ) {
		return ivPercentageCalculationActive;
	}

	public void setPercentageCalculationActive( final boolean aActive ) {
		ivPercentageCalculationActive = aActive;
		saveSettings( );
	}

	public TimestampTypes getTimestampType( ) {
		return ivTimestampType;
	}

	public void setTimestampType( final TimestampTypes aTimestampType ) {
		ivTimestampType = aTimestampType;
		saveSettings( );
	}

	public boolean isCacheViews( ) {
		return ivCacheViews;
	}

	public void setCacheViews( final boolean ivCacheViews ) {
		this.ivCacheViews = ivCacheViews;
		saveSettings( );
	}

	public boolean isSearchInEntireTrace( ) {
		return ivSearchInEntireTrace;
	}

	public void setSearchInEntireTrace( final boolean ivSearchInEntireTrace ) {
		this.ivSearchInEntireTrace = ivSearchInEntireTrace;
		saveSettings( );
	}

	/**
	 * @author Nils Christian Ehmke
	 */
	public enum ComponentNames {
		SHORT, LONG
	}

	/**
	 * @author Nils Christian Ehmke
	 */
	public enum OperationNames {
		SHORT, LONG
	}

	public enum TimestampTypes {
		TIMESTAMP, DATE_AND_TIME, DATE, LONG_TIME, SHORT_TIME
	}

	public enum Threshold {

		THRESHOLD_0_5( 0.5f ), THRESHOLD_1( 1f ), THRESHOLD_10( 10f ), THRESHOLD_20( 20f ), THRESHOLD_30( 30f ), THRESHOLD_40( 40f ), THRESHOLD_50( 50f );

		private final float ivPercent;

		private Threshold( final float aPercent ) {
			ivPercent = aPercent;
		}

		public float getPercent( ) {
			return ivPercent;
		}

	}

}
