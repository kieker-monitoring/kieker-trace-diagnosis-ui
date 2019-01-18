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

package kieker.diagnosis.frontend.tab.traces.complex;

import org.testfx.api.FxRobot;

import kieker.diagnosis.frontend.tab.traces.composite.TracesDetailsPage;
import kieker.diagnosis.frontend.tab.traces.composite.TracesFilterPage;
import kieker.diagnosis.frontend.test.TreeTable;
import lombok.Getter;

@Getter
public final class TracesPage {

	private final TracesFilterPage filter;
	private final TracesDetailsPage detail;
	private final TreeTable table;
	private final DialogPage dialog;

	public TracesPage( final FxRobot fxRobot ) {
		filter = new TracesFilterPage( fxRobot );
		detail = new TracesDetailsPage( fxRobot );
		table = new TreeTable( fxRobot, "#tabTracesTreeTable" );
		dialog = new DialogPage( fxRobot );
	}
}
