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

	private String graphvizPath;
	private TimeUnit timeUnit;
	private ComponentNames componentNames;
	private OperationNames operationNames;
	private Threshold threshold;
	private TimestampTypes timestampType;
	private boolean additionalLogChecksActive;
	private boolean regularExpressionsActive;
	private boolean methodCallAggregationActive;
	private boolean caseSensitivityActive;
	private boolean percentageCalculationActive;
	private String gitLabURL;
	private String tracURL;

	private long version = 0L;

	public PropertiesModel() {
		this.loadSettings();
	}

	public static PropertiesModel getInstance() {
		return PropertiesModel.INSTANCE;
	}

	private void loadSettings() {
		final Preferences preferences = Preferences.userNodeForPackage(PropertiesModel.class);

		this.graphvizPath = preferences.get(PropertiesModel.KEY_GRAPHVIZ_PATH, ".");
		this.timeUnit = TimeUnit.valueOf(preferences.get(PropertiesModel.KEY_TIMEUNIT, TimeUnit.NANOSECONDS.name()));
		this.componentNames = ComponentNames.valueOf(preferences.get(PropertiesModel.KEY_COMPONENTS, ComponentNames.LONG.name()));
		this.operationNames = OperationNames.valueOf(preferences.get(PropertiesModel.KEY_OPERATIONS, OperationNames.SHORT.name()));
		this.additionalLogChecksActive = Boolean.valueOf(preferences.get(PropertiesModel.KEY_ADDITIONAL_LOG_CHECKS, Boolean.FALSE.toString()));
		this.regularExpressionsActive = Boolean.valueOf(preferences.get(PropertiesModel.KEY_REGULAR_EXPRESSIONS, Boolean.FALSE.toString()));
		this.methodCallAggregationActive = Boolean.valueOf(preferences.get(PropertiesModel.KEY_METHOD_CALL_AGGREGATION, Boolean.FALSE.toString()));
		this.threshold = Threshold.valueOf(preferences.get(PropertiesModel.KEY_THRESHOLD, Threshold.THRESHOLD_1.name()));
		this.caseSensitivityActive = Boolean.valueOf(preferences.get(PropertiesModel.KEY_CASE_SENSITIVE, Boolean.FALSE.toString()));
		this.percentageCalculationActive = Boolean.valueOf(preferences.get(PropertiesModel.KEY_PERCENTAGE_CALCULATION, Boolean.FALSE.toString()));
		this.timestampType = TimestampTypes.valueOf(preferences.get(PropertiesModel.KEY_TIMESTAMP_TYPE, TimestampTypes.TIMESTAMP.name()));

		final Properties properties = new Properties();
		final ClassLoader classLoader = PropertiesModel.class.getClassLoader();
		try (InputStream inputStream = classLoader.getResourceAsStream("config.properties")) {
			properties.load(inputStream);
			this.gitLabURL = properties.getProperty(PropertiesModel.KEY_GITLAB_URL);
			this.tracURL = properties.getProperty(PropertiesModel.KEY_TRAC_URL);
		} catch (final IOException e) {
			PropertiesModel.LOGGER.error(e);
		}
	}

	private void saveSettings() {
		final Preferences preferences = Preferences.userNodeForPackage(PropertiesModel.class);

		preferences.put(PropertiesModel.KEY_GRAPHVIZ_PATH, this.graphvizPath);
		preferences.put(PropertiesModel.KEY_TIMEUNIT, this.timeUnit.name());
		preferences.put(PropertiesModel.KEY_COMPONENTS, this.componentNames.name());
		preferences.put(PropertiesModel.KEY_OPERATIONS, this.operationNames.name());
		preferences.put(PropertiesModel.KEY_ADDITIONAL_LOG_CHECKS, Boolean.toString(this.additionalLogChecksActive));
		preferences.put(PropertiesModel.KEY_REGULAR_EXPRESSIONS, Boolean.toString(this.regularExpressionsActive));
		preferences.put(PropertiesModel.KEY_METHOD_CALL_AGGREGATION, Boolean.toString(this.methodCallAggregationActive));
		preferences.put(PropertiesModel.KEY_THRESHOLD, this.threshold.name());
		preferences.put(PropertiesModel.KEY_CASE_SENSITIVE, Boolean.toString(this.caseSensitivityActive));
		preferences.put(PropertiesModel.KEY_PERCENTAGE_CALCULATION, Boolean.toString(this.percentageCalculationActive));
		preferences.put(PropertiesModel.KEY_TIMESTAMP_TYPE, this.timestampType.name());

		try {
			preferences.flush();
		} catch (final BackingStoreException e) {
			PropertiesModel.LOGGER.error(e);
		}

		this.version++;
	}

	public String getGraphvizPath() {
		return this.graphvizPath;
	}

	public void setGraphvizPath(final String graphvizPath) {
		this.graphvizPath = graphvizPath;
		this.saveSettings();
	}

	public TimeUnit getTimeUnit() {
		return this.timeUnit;
	}

	public void setTimeUnit(final TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
		this.saveSettings();
	}

	public ComponentNames getComponentNames() {
		return this.componentNames;
	}

	public void setComponentNames(final ComponentNames componentNames) {
		this.componentNames = componentNames;
		this.saveSettings();
	}

	public OperationNames getOperationNames() {
		return this.operationNames;
	}

	public void setOperationNames(final OperationNames operationNames) {
		this.operationNames = operationNames;
		this.saveSettings();
	}

	public boolean isAdditionalLogChecksActive() {
		return this.additionalLogChecksActive;
	}

	public void setAdditionalLogChecksActive(final boolean active) {
		this.additionalLogChecksActive = active;
		this.saveSettings();
	}

	public boolean isRegularExpressionsActive() {
		return this.regularExpressionsActive;
	}

	public void setRegularExpressionsActive(final boolean active) {
		this.regularExpressionsActive = active;
		this.saveSettings();
	}

	public String getGitLabURL() {
		return this.gitLabURL;
	}

	public String getTracURL() {
		return this.tracURL;
	}

	public boolean isMethodCallAggregationActive() {
		return this.methodCallAggregationActive;
	}

	public void setMethodCallAggregationActive(final boolean active) {
		this.methodCallAggregationActive = active;
		this.saveSettings();
	}

	public Threshold getThreshold() {
		return this.threshold;
	}

	public void setThreshold(final Threshold threshold) {
		this.threshold = threshold;
		this.saveSettings();
	}

	public boolean isCaseSensitivityActive() {
		return this.caseSensitivityActive;
	}

	public void setCaseSensitivityActive(final boolean active) {
		this.caseSensitivityActive = active;
		this.saveSettings();
	}

	public long getVersion() {
		return this.version;
	}

	public boolean isPercentageCalculationActive() {
		return this.percentageCalculationActive;
	}

	public void setPercentageCalculationActive(final boolean active) {
		this.percentageCalculationActive = active;
		this.saveSettings();
	}

	public TimestampTypes getTimestampType() {
		return this.timestampType;
	}

	public void setTimestampType(final TimestampTypes timestampType) {
		this.timestampType = timestampType;
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

		private final float percent;

		private Threshold(final float percent) {
			this.percent = percent;
		}

		public float getPercent() {
			return this.percent;
		}

	}

}
