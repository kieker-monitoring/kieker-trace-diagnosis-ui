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

package kieker.diagnosis.frontend.tab.traces.composite;

import org.testfx.api.FxRobot;

import kieker.diagnosis.frontend.test.Button;
import kieker.diagnosis.frontend.test.CheckBox;
import kieker.diagnosis.frontend.test.ComboBox;
import kieker.diagnosis.frontend.test.Link;
import kieker.diagnosis.frontend.test.TextField;
import lombok.Getter;

@Getter
public final class TracesFilterPage {

	private final TextField host;
	private final TextField clazz;
	private final TextField method;
	private final TextField exception;
	private final TextField traceId;
	private final ComboBox searchType;
	private final CheckBox useRegularExpression;
	private final Button search;
	private final Link saveAsFavorite;

	public TracesFilterPage( final FxRobot fxRobot ) {
		host = new TextField( fxRobot, "#tabTracesFilterHost" );
		clazz = new TextField( fxRobot, "#tabTracesFilterClass" );
		method = new TextField( fxRobot, "#tabTracesFilterMethod" );
		exception = new TextField( fxRobot, "#tabTracesFilterException" );
		traceId = new TextField( fxRobot, "#tabTracesFilterTraceId" );
		searchType = new ComboBox( fxRobot, "#tabTracesFilterSearchType" );
		useRegularExpression = new CheckBox( fxRobot, "#tabTracesFilterUseRegExpr" );
		search = new Button( fxRobot, "#tabTracesSearch" );
		saveAsFavorite = new Link( fxRobot, "#tabTracesSaveAsFavorite" );
	}
}
