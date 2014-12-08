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

package kieker.gui.model;

import java.util.Observable;

/**
 * An observable singleton container for properties used within this application.
 *
 * @author Nils Christian Ehmke
 */
public final class Properties extends Observable {

	private static final Properties INSTANCE = new Properties();
	private boolean shortComponentNames = false;
	private boolean shortOperationParameters = true;

	private Properties() {}

	public static Properties getInstance() {
		return Properties.INSTANCE;
	}

	public boolean isShortComponentNames() {
		return this.shortComponentNames;
	}

	public void setShortComponentNames(final boolean shortComponentNames) {
		this.shortComponentNames = shortComponentNames;

		this.setChanged();
		this.notifyObservers();
	}

	public boolean isShortOperationParameters() {
		return this.shortOperationParameters;
	}

	public void setShortOperationParameters(final boolean shortOperationParameters) {
		this.shortOperationParameters = shortOperationParameters;

		this.setChanged();
		this.notifyObservers();
	}

}
