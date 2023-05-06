/***************************************************************************
 * Copyright 2015-2023 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.frontend.tab.methods.composite;

import org.testfx.api.FxRobot;

import kieker.diagnosis.frontend.test.Link;
import kieker.diagnosis.frontend.test.TextField;
import lombok.Getter;

@Getter
public final class MethodDetailsPage {

	private final TextField host;
	private final TextField exception;
	private final Link jumpToTrace;

	public MethodDetailsPage( final FxRobot fxRobot ) {
		host = new TextField( fxRobot, "#tabMethodsDetailHost" );
		exception = new TextField( fxRobot, "#tabMethodsDetailException" );
		jumpToTrace = new Link( fxRobot, "#tabMethodsJumpToTrace" );
	}

}
