/***************************************************************************
 * Copyright 2015 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Nils Christian Ehmke
 */
public final class PropertiesModel {

	private static final Logger LOGGER = LogManager.getLogger(PropertiesModel.class);
	private static final PropertiesModel INSTANCE = new PropertiesModel();

	private static final String KEY_TIMEUNIT = "timeunit";
	private static final String KEY_OPERATIONS = "operations";
	private static final String KEY_COMPONENTS = "components";
	private static final String KEY_GRAPHVIZ_PATH = "graphvizpath";
	private static final String KEY_ADDITIONAL_LOG_CHECKS = "additionalLogChecks";
	private static final String KEY_REGULAR_EXPRESSIONS = "regularExpressions";
	private static final String KEY_METHOD_CALL_AGGREGATION = "methodCallAggregationActive";
	private static final String KEY_THRESHOLD = "threshold";
	private static final String KEY_CASE_SENSITIVE = "caseSensitive";
	private static final String KEY_PERCENTAGE_CALCULATION = "percentageCalculation";
	private static final String KEY_TIMESTAMP_TYPE = "timestampType";

	private static final String KEY_GITLAB_URL = "GitLabURL";
	private static final String KEY_TRAC_URL = "TracURL";

	private String ivGraphvizPath;
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
	private String ivGitLabURL;
	private String ivTracURL;

	private long ivVersion = 0L;

	public PropertiesModel() {
		this.loadSettings();
	}

	public static PropertiesModel getInstance() {
		return PropertiesModel.INSTANCE;
	}

	private void loadSettings() {
		final Preferences preferences = Preferences.userNodeForPackage(PropertiesModel.class);

		this.ivGraphvizPath = preferences.get(PropertiesModel.KEY_GRAPHVIZ_PATH, ".");
		this.ivTimeUnit = TimeUnit.valueOf(preferences.get(PropertiesModel.KEY_TIMEUNIT, TimeUnit.NANOSECONDS.name()));
		this.ivComponentNames = ComponentNames.valueOf(preferences.get(PropertiesModel.KEY_COMPONENTS, ComponentNames.LONG.name()));
		this.ivOperationNames = OperationNames.valueOf(preferences.get(PropertiesModel.KEY_OPERATIONS, OperationNames.SHORT.name()));
		this.ivAdditionalLogChecksActive = Boolean.valueOf(preferences.get(PropertiesModel.KEY_ADDITIONAL_LOG_CHECKS, Boolean.FALSE.toString()));
		this.ivRegularExpressionsActive = Boolean.valueOf(preferences.get(PropertiesModel.KEY_REGULAR_EXPRESSIONS, Boolean.FALSE.toString()));
		this.ivMethodCallAggregationActive = Boolean.valueOf(preferences.get(PropertiesModel.KEY_METHOD_CALL_AGGREGATION, Boolean.FALSE.toString()));
		this.ivThreshold = Threshold.valueOf(preferences.get(PropertiesModel.KEY_THRESHOLD, Threshold.THRESHOLD_1.name()));
		this.ivCaseSensitivityActive = Boolean.valueOf(preferences.get(PropertiesModel.KEY_CASE_SENSITIVE, Boolean.FALSE.toString()));
		this.ivPercentageCalculationActive = Boolean.valueOf(preferences.get(PropertiesModel.KEY_PERCENTAGE_CALCULATION, Boolean.FALSE.toString()));
		this.ivTimestampType = TimestampTypes.valueOf(preferences.get(PropertiesModel.KEY_TIMESTAMP_TYPE, TimestampTypes.TIMESTAMP.name()));

		final Properties properties = new Properties();
		final ClassLoader classLoader = PropertiesModel.class.getClassLoader();
		try (InputStream inputStream = classLoader.getResourceAsStream("config.properties")) {
			properties.load(inputStream);
			this.ivGitLabURL = properties.getProperty(PropertiesModel.KEY_GITLAB_URL);
			this.ivTracURL = properties.getProperty(PropertiesModel.KEY_TRAC_URL);
		} catch (final IOException e) {
			PropertiesModel.LOGGER.error(e);
		}
	}

	private void saveSettings() {
		final Preferences preferences = Preferences.userNodeForPackage(PropertiesModel.class);

		preferences.put(PropertiesModel.KEY_GRAPHVIZ_PATH, this.ivGraphvizPath);
		preferences.put(PropertiesModel.KEY_TIMEUNIT, this.ivTimeUnit.name());
		preferences.put(PropertiesModel.KEY_COMPONENTS, this.ivComponentNames.name());
		preferences.put(PropertiesModel.KEY_OPERATIONS, this.ivOperationNames.name());
		preferences.put(PropertiesModel.KEY_ADDITIONAL_LOG_CHECKS, Boolean.toString(this.ivAdditionalLogChecksActive));
		preferences.put(PropertiesModel.KEY_REGULAR_EXPRESSIONS, Boolean.toString(this.ivRegularExpressionsActive));
		preferences.put(PropertiesModel.KEY_METHOD_CALL_AGGREGATION, Boolean.toString(this.ivMethodCallAggregationActive));
		preferences.put(PropertiesModel.KEY_THRESHOLD, this.ivThreshold.name());
		preferences.put(PropertiesModel.KEY_CASE_SENSITIVE, Boolean.toString(this.ivCaseSensitivityActive));
		preferences.put(PropertiesModel.KEY_PERCENTAGE_CALCULATION, Boolean.toString(this.ivPercentageCalculationActive));
		preferences.put(PropertiesModel.KEY_TIMESTAMP_TYPE, this.ivTimestampType.name());

		try {
			preferences.flush();
		} catch (final BackingStoreException e) {
			PropertiesModel.LOGGER.error(e);
		}

		this.ivVersion++;
	}

	public String getGraphvizPath() {
		return this.ivGraphvizPath;
	}

	public void setGraphvizPath(final String aGraphvizPath) {
		this.ivGraphvizPath = aGraphvizPath;
		this.saveSettings();
	}

	public TimeUnit getTimeUnit() {
		return this.ivTimeUnit;
	}

	public void setTimeUnit(final TimeUnit aTimeUnit) {
		this.ivTimeUnit = aTimeUnit;
		this.saveSettings();
	}

	public ComponentNames getComponentNames() {
		return this.ivComponentNames;
	}

	public void setComponentNames(final ComponentNames aComponentNames) {
		this.ivComponentNames = aComponentNames;
		this.saveSettings();
	}

	public OperationNames getOperationNames() {
		return this.ivOperationNames;
	}

	public void setOperationNames(final OperationNames aOperationNames) {
		this.ivOperationNames = aOperationNames;
		this.saveSettings();
	}

	public boolean isAdditionalLogChecksActive() {
		return this.ivAdditionalLogChecksActive;
	}

	public void setAdditionalLogChecksActive(final boolean aActive) {
		this.ivAdditionalLogChecksActive = aActive;
		this.saveSettings();
	}

	public boolean isRegularExpressionsActive() {
		return this.ivRegularExpressionsActive;
	}

	public void setRegularExpressionsActive(final boolean aActive) {
		this.ivRegularExpressionsActive = aActive;
		this.saveSettings();
	}

	public String getGitLabURL() {
		return this.ivGitLabURL;
	}

	public String getTracURL() {
		return this.ivTracURL;
	}

	public boolean isMethodCallAggregationActive() {
		return this.ivMethodCallAggregationActive;
	}

	public void setMethodCallAggregationActive(final boolean aActive) {
		this.ivMethodCallAggregationActive = aActive;
		this.saveSettings();
	}

	public Threshold getThreshold() {
		return this.ivThreshold;
	}

	public void setThreshold(final Threshold aThreshold) {
		this.ivThreshold = aThreshold;
		this.saveSettings();
	}

	public boolean isCaseSensitivityActive() {
		return this.ivCaseSensitivityActive;
	}

	public void setCaseSensitivityActive(final boolean aActive) {
		this.ivCaseSensitivityActive = aActive;
		this.saveSettings();
	}

	public long getVersion() {
		return this.ivVersion;
	}

	public boolean isPercentageCalculationActive() {
		return this.ivPercentageCalculationActive;
	}

	public void setPercentageCalculationActive(final boolean aActive) {
		this.ivPercentageCalculationActive = aActive;
		this.saveSettings();
	}

	public TimestampTypes getTimestampType() {
		return this.ivTimestampType;
	}

	public void setTimestampType(final TimestampTypes aTimestampType) {
		this.ivTimestampType = aTimestampType;
		this.saveSettings();
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

		THRESHOLD_0_5(0.5f), THRESHOLD_1(1f), THRESHOLD_10(10f), THRESHOLD_20(20f), THRESHOLD_30(30f), THRESHOLD_40(40f), THRESHOLD_50(50f);

		private final float ivPercent;

		private Threshold(final float aPercent) {
			this.ivPercent = aPercent;
		}

		public float getPercent() {
			return this.ivPercent;
		}

	}

}
