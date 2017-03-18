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

package kieker.diagnosis.application.gui.bugreporting;

import kieker.diagnosis.architecture.gui.AbstractView;
import kieker.diagnosis.architecture.gui.AutowiredElement;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Window;

import org.springframework.stereotype.Component;

/**
 * The view for the bug reporting dialog.
 *
 * @author Nils Christian Ehmke
 */
@Component
final class BugReportingDialogView extends AbstractView {

	@AutowiredElement
	private Node ivView;

	public Node getView( ) {
		return ivView;
	}

	public Window getStage( ) {
		final Scene scene = ivView.getScene( );
		return scene.getWindow( );
	}

}
