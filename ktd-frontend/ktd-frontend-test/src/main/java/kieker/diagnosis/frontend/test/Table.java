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

package kieker.diagnosis.frontend.test;

import org.testfx.api.FxRobot;

import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.control.TableView;

public final class Table {

	private final FxRobot fxRobot;
	private final String locator;

	public Table( final FxRobot fxRobot, final String locator ) {
		this.fxRobot = fxRobot;
		this.locator = locator;
	}

	public void clickOnNthHeader( final int nth ) {
		final Node headerNode = fxRobot.lookup( locator ).lookup( ".column-header" ).nth( nth ).queryAs( Node.class );
		fxRobot.clickOn( headerNode );
	}

	public String getNthTextInFirstRow( final int nth ) {
		final Labeled labeled = fxRobot.lookup( locator ).lookup( ".table-cell" ).nth( nth ).queryLabeled( );
		return labeled.getText( );
	}

	public void clickOnNthElementInFirstRow( final int nth ) {
		final Node node = fxRobot.lookup( locator ).lookup( ".table-cell" ).nth( nth ).query( );
		fxRobot.clickOn( node );
	}

	public void clickOnNthTableRow( final int nth ) {
		final Node node = fxRobot.lookup( locator ).lookup( ".table-row-cell" ).nth( nth ).query( );
		fxRobot.clickOn( node );
	}

	public int countItems( ) {
		final TableView<Object> tableView = fxRobot.lookup( locator ).queryTableView( );
		return tableView.getItems( ).size( );
	}

}
