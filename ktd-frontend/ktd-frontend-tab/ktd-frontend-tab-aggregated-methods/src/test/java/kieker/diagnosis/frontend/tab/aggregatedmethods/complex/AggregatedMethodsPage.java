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

package kieker.diagnosis.frontend.tab.aggregatedmethods.complex;

import org.testfx.api.FxRobot;

import kieker.diagnosis.frontend.tab.aggregatedmethods.composite.AggregatedMethodDetailsPage;
import kieker.diagnosis.frontend.tab.aggregatedmethods.composite.AggregatedMethodFilterPage;
import kieker.diagnosis.frontend.tab.aggregatedmethods.composite.AggregatedMethodStatusBarPage;
import kieker.diagnosis.frontend.test.Table;
import lombok.Getter;

@Getter
public final class AggregatedMethodsPage {

	private final AggregatedMethodFilterPage filter;
	private final AggregatedMethodDetailsPage detail;
	private final Table table;
	private final AggregatedMethodStatusBarPage statusBar;
	private final DialogPage dialog;

	public AggregatedMethodsPage( final FxRobot fxRobot ) {
		filter = new AggregatedMethodFilterPage( fxRobot );
		detail = new AggregatedMethodDetailsPage( fxRobot );
		table = new Table( fxRobot, "#tabAggregatedMethodsTable" );
		statusBar = new AggregatedMethodStatusBarPage( fxRobot );
		dialog = new DialogPage( fxRobot );
	}
}
