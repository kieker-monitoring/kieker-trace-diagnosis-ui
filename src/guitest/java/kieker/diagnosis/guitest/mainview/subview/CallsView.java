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

package kieker.diagnosis.guitest.mainview.subview;

import org.testfx.framework.junit.ApplicationTest;

import kieker.diagnosis.guitest.components.TextField;

public final class CallsView {

	private final ApplicationTest applicationTest;

	public CallsView(final ApplicationTest applicationTest) {
		this.applicationTest = applicationTest;
	}

	public TextField getCounterTextField() {
		return new TextField(this.applicationTest, "#ivCounter");
	}

	public TextField getFilterContainerTextField() {
		return new TextField(this.applicationTest, "#ivFilterContainer");
	}

	public TextField getFilterComponentTextField() {
		return new TextField(this.applicationTest, "#ivFilterComponent");
	}

	public TextField getFilterOperationTextField() {
		return new TextField(this.applicationTest, "#ivFilterOperation");
	}

	public TextField getFilterTraceIDTextField() {
		return new TextField(this.applicationTest, "#ivFilterTraceID");
	}

}
