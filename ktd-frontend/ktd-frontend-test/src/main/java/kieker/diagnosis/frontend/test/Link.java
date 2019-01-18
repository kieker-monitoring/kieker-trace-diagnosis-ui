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

package kieker.diagnosis.frontend.test;

import org.testfx.api.FxRobot;
import org.testfx.service.query.NodeQuery;

import javafx.scene.control.Hyperlink;

public final class Link {

	private final FxRobot fxRobot;
	private final String locator;

	public Link( final FxRobot fxRobot, final String locator ) {
		this.fxRobot = fxRobot;
		this.locator = locator;
	}

	public void click( ) {
		final NodeQuery query = fxRobot.lookup( locator );
		fxRobot.clickOn( query.queryAs( Hyperlink.class ) );
	}

}
