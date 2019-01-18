/***************************************************************************
 * Copyright 2015-2019 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.frontend.tab.methods.complex;

import org.testfx.api.FxRobot;

import kieker.diagnosis.frontend.tab.methods.composite.MethodDetailsPage;
import kieker.diagnosis.frontend.tab.methods.composite.MethodStatusBarPage;
import kieker.diagnosis.frontend.tab.methods.composite.MethodsFilterPage;
import kieker.diagnosis.frontend.test.Table;
import lombok.Getter;

@Getter
public final class MethodsPage {

	private final MethodsFilterPage filter;
	private final MethodDetailsPage detail;
	private final Table table;
	private final MethodStatusBarPage statusBar;
	private final DialogPage dialog;

	public MethodsPage( final FxRobot fxRobot ) {
		filter = new MethodsFilterPage( fxRobot );
		detail = new MethodDetailsPage( fxRobot );
		table = new Table( fxRobot, "#tabMethodsTable" );
		statusBar = new MethodStatusBarPage( fxRobot );
		dialog = new DialogPage( fxRobot );
	}
}
