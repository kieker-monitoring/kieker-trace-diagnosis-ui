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

package kieker.gui.mainview;

import java.util.Observable;

import org.eclipse.swt.graphics.Cursor;

/**
 * The model of the main view.
 *
 * @author Nils Christian Ehmke
 */
public final class Model extends Observable {

	private Cursor cursor;
	private String currentActiveSubViewKey;

	public Cursor getCursor() {
		return this.cursor;
	}

	public void setCursor(final Cursor cursor) {
		this.cursor = cursor;

		this.setChanged();
		this.notifyObservers();
	}

	public String getCurrentActiveSubViewKey() {
		return this.currentActiveSubViewKey;
	}

	public void setCurrentActiveSubView(final String currentActiveSubViewKey) {
		this.currentActiveSubViewKey = currentActiveSubViewKey;

		this.setChanged();
		this.notifyObservers();
	}

}
