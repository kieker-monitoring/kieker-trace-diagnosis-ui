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

package kieker.diagnosis.frontend.tab.aggregatedmethods.composite;

import org.testfx.api.FxRobot;

import kieker.diagnosis.frontend.test.Button;
import kieker.diagnosis.frontend.test.CheckBox;
import kieker.diagnosis.frontend.test.ComboBox;
import kieker.diagnosis.frontend.test.Link;
import kieker.diagnosis.frontend.test.TextField;
import lombok.Getter;

@Getter
public final class AggregatedMethodFilterPage {

	private final TextField host;
	private final TextField clazz;
	private final TextField method;
	private final TextField exception;
	private final ComboBox searchType;
	private final CheckBox useRegularExpression;
	private final Button search;
	private final Link saveAsFavorite;

	public AggregatedMethodFilterPage( final FxRobot fxRobot ) {
		host = new TextField( fxRobot, "#tabAggregatedMethodsFilterHost" );
		clazz = new TextField( fxRobot, "#tabAggregatedMethodsFilterClass" );
		method = new TextField( fxRobot, "#tabAggregatedMethodsFilterMethod" );
		exception = new TextField( fxRobot, "#tabAggregatedMethodsFilterException" );
		searchType = new ComboBox( fxRobot, "#tabAggregatedMethodsFilterSearchType" );
		useRegularExpression = new CheckBox( fxRobot, "#tabAggregatedMethodsFilterUseRegExpr" );
		search = new Button( fxRobot, "#tabAggregatedMethodsSearch" );
		saveAsFavorite = new Link( fxRobot, "#tabAggregatedMethodsFilteSaveAsFavorite" );
	}
}
