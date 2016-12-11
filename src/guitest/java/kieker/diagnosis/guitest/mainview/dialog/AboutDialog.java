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

package kieker.diagnosis.guitest.mainview.dialog;

import org.testfx.framework.junit.ApplicationTest;

import kieker.diagnosis.guitest.components.Button;
import kieker.diagnosis.guitest.components.Label;

public final class AboutDialog {

	private final ApplicationTest applicationTest;

	public AboutDialog(final ApplicationTest applicationTest) {
		this.applicationTest = applicationTest;
	}

	public Button getOkayButton() {
		return new Button(this.applicationTest, "#ivOkay");
	}

	public Label getDescriptionLabel() {
		return new Label(this.applicationTest, "#ivDescription");
	}

}
