/***************************************************************************
 * Copyright 2014 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.common.model;

import java.util.Observable;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

public final class PropertiesModel extends Observable {

	private boolean commit = true;

	private String graphvizPath;
	private GraphvizGenerator graphvizGenerator;
	private TimeUnit timeunit;
	private ComponentNames componentNames;
	private OperationNames operationNames;

	public PropertiesModel() {
		this.loadSettings();
	}

	private void loadSettings() {
		final Preferences preferences = Preferences.userNodeForPackage(PropertiesModel.class);

		this.graphvizPath = preferences.get("graphvizpath", ".");
		this.graphvizGenerator = GraphvizGenerator.valueOf(preferences.get("graphvizgenerator", GraphvizGenerator.DOT.name()));
		this.timeunit = TimeUnit.valueOf(preferences.get("timeunit", TimeUnit.NANOSECONDS.name()));
		this.componentNames = ComponentNames.valueOf(preferences.get("operations", ComponentNames.LONG.name()));
		this.operationNames = OperationNames.valueOf(preferences.get("components", OperationNames.SHORT.name()));
	}

	private void saveSettings() {
		final Preferences preferences = Preferences.userNodeForPackage(PropertiesModel.class);

		preferences.put("graphvizpath", this.graphvizPath);
		preferences.put("graphvizgenerator", this.graphvizGenerator.name());
		preferences.put("timeunit", this.timeunit.name());
		preferences.put("operations", this.componentNames.name());
		preferences.put("components", this.operationNames.name());
	}

	public String getGraphvizPath() {
		return this.graphvizPath;
	}

	public void setGraphvizPath(final String graphvizPath) {
		this.graphvizPath = graphvizPath;

		this.notifyObserversAndSaveSettings();
	}

	public GraphvizGenerator getGraphvizGenerator() {
		return this.graphvizGenerator;
	}

	public void setGraphvizGenerator(final GraphvizGenerator graphvizGenerator) {
		this.graphvizGenerator = graphvizGenerator;

		this.notifyObserversAndSaveSettings();
	}

	public TimeUnit getTimeunit() {
		return this.timeunit;
	}

	public void setTimeunit(final TimeUnit timeunit) {
		this.timeunit = timeunit;

		this.notifyObserversAndSaveSettings();
	}

	public ComponentNames getComponentNames() {
		return this.componentNames;
	}

	public void setComponentNames(final ComponentNames componentNames) {
		this.componentNames = componentNames;

		this.notifyObserversAndSaveSettings();
	}

	public OperationNames getOperationNames() {
		return this.operationNames;
	}

	public void setOperationNames(final OperationNames operationNames) {
		this.operationNames = operationNames;

		this.notifyObserversAndSaveSettings();
	}

	public void startModification() {
		this.commit = false;
	}

	public void commitModification() {
		this.commit = true;

		this.notifyObserversAndSaveSettings();
	}

	private void notifyObserversAndSaveSettings() {
		if (this.commit) {
			this.setChanged();
			this.notifyObservers();
			this.saveSettings();
		}
	}

	public enum GraphvizGenerator {
		DOT, NEATO
	}

	public enum ComponentNames {
		SHORT, LONG
	}

	public enum OperationNames {
		SHORT, LONG
	}

}
