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

package kieker.diagnosis;

import javafx.application.Application;
import javafx.stage.Stage;
import kieker.diagnosis.mainview.Controller;

/**
 * Contains the main method of this application.
 *
 * @author Nils Christian Ehmke
 */
public final class Main extends Application {

	/**
	 * The main method of this application. It initializes the JavaFX context and uses the main controller to start everything.
	 *
	 * @param args
	 *            The command line arguments. They have currently no effect.
	 */
	public static void main(final String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(final Stage stage) throws Exception {
		Controller.loadMainPane(stage);
	}

}
