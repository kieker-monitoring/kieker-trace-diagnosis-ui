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

import kieker.diagnosis.guitest.components.TextField;

import org.testfx.framework.junit.ApplicationTest;

public final class CallsView {

	private final ApplicationTest applicationTest;

	public CallsView(final ApplicationTest applicationTest) {
		this.applicationTest = applicationTest;
	}

	public TextField getCounterTextField() {
		return new TextField(this.applicationTest, "#counter");
	}

	public TextField getFilterContainerTextField() {
		return new TextField(this.applicationTest, "#filterContainer");
	}

	public TextField getFilterComponentTextField() {
		return new TextField(this.applicationTest, "#filterComponent");
	}

	public TextField getFilterOperationTextField() {
		return new TextField(this.applicationTest, "#filterOperation");
	}

	public TextField getFilterTraceIDTextField() {
		return new TextField(this.applicationTest, "#filterTraceID");
	}

}
