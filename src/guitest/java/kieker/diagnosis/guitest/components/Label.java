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

package kieker.diagnosis.guitest.components;

import javafx.scene.Node;

import org.testfx.framework.junit.ApplicationTest;

public final class Label {

	private final String id;
	private final ApplicationTest applicationTest;

	public Label(final ApplicationTest applicationTest, final String id) {
		this.applicationTest = applicationTest;
		this.id = id;
	}

	public String getText() {
		final Node node = this.applicationTest.lookup(this.id).queryFirst();
		return ((javafx.scene.control.Label) node).getText();
	}
}
