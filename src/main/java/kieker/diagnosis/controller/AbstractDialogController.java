/***************************************************************************
 * Copyright 2015 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import kieker.diagnosis.util.Context;

public abstract class AbstractDialogController extends AbstractController {

	@FXML private Node view;

	public AbstractDialogController(final Context context) {
		super(context);
	}

	public final void closeDialog() {
		final Scene scene = this.getView().getScene();
		final Window window = scene.getWindow();
		if (window instanceof Stage) {
			((Stage) window).close();
		}
	}

	protected final Node getView() {
		return this.view;
	}

}
