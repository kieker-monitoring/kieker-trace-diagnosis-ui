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

import java.util.Observable;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

/**
 * @author Nils Christian Ehmke
 */
public final class PropertiesModel extends Observable {

	private static final String KEY_TIMEUNIT = "timeunit";
	private static final String KEY_OPERATIONS = "operations";
	private static final String KEY_COMPONENTS = "components";
	private static final String KEY_GRAPHVIZ_PATH = "graphvizpath";

	private String graphvizPath;
	private TimeUnit timeUnit;
	private ComponentNames componentNames;
	private OperationNames operationNames;

	public PropertiesModel() {
		this.loadSettings();
	}

	private void loadSettings() {
		final Preferences preferences = Preferences.userNodeForPackage(PropertiesModel.class);

		this.graphvizPath = preferences.get(PropertiesModel.KEY_GRAPHVIZ_PATH, ".");
		this.timeUnit = TimeUnit.valueOf(preferences.get(PropertiesModel.KEY_TIMEUNIT, TimeUnit.NANOSECONDS.name()));
		this.componentNames = ComponentNames.valueOf(preferences.get(PropertiesModel.KEY_COMPONENTS, ComponentNames.LONG.name()));
		this.operationNames = OperationNames.valueOf(preferences.get(PropertiesModel.KEY_OPERATIONS, OperationNames.SHORT.name()));
	}

	public String getGraphvizPath() {
		return this.graphvizPath;
	}

	public void setGraphvizPath(final String graphvizPath) {
		this.graphvizPath = graphvizPath;
	}

	public TimeUnit getTimeUnit() {
		return this.timeUnit;
	}

	public void setTimeUnit(final TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}

	public ComponentNames getComponentNames() {
		return this.componentNames;
	}

	public void setComponentNames(final ComponentNames componentNames) {
		this.componentNames = componentNames;
	}

	public OperationNames getOperationNames() {
		return this.operationNames;
	}

	public void setOperationNames(final OperationNames operationNames) {
		this.operationNames = operationNames;
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

	public static PropertiesModel getInstance() {
		// To be removed
		return new PropertiesModel();
	}

}
