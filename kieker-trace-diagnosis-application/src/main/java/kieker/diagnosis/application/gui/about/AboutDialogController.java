/***************************************************************************
 * Copyright 2015-2017 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.application.gui.about;

import kieker.diagnosis.architecture.gui.AbstractController;

import java.util.Optional;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import org.springframework.stereotype.Component;

/**
 * The controller for the about dialog. The about dialog shows some information about the application, like the version number and the copyright information.
 *
 * @author Nils Christian Ehmke
 */
@Component
public class AboutDialogController extends AbstractController<AboutDialogView> {

	@Override
	protected void doInitialize( final boolean aFirstInitialization, final Optional<?> aParameter ) {
		// Nothing to initialize
	}

	@Override
	public void doRefresh( ) {
		// Nothing to refresh
	}

	/**
	 * The action which is performed when the user clicks the {@code ok} button.
	 */
	public void performCloseDialog( ) {
		getView( ).getStage( ).hide( );
	}

	/**
	 * The action which is performed when the user presses a key.
	 */
	public void performOnKeyPressed( final KeyEvent aKeyEvent ) {
		if ( aKeyEvent.getCode( ) == KeyCode.ESCAPE ) {
			performCloseDialog( );
		}
	}

}
