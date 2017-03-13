/***************************************************************************
 * Copyright 2015-2016 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.architecture.gui;

import java.util.Optional;

import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * @author Nils Christian Ehmke
 */
public interface GuiLoader {

	public <C extends AbstractController<?>> void loadAsMainView( Class<C> aControllerClass, Stage aPrimaryStage, Optional<?> aParameter );

	public <C extends AbstractController<?>> void loadAsMainView( Class<C> aControllerClass, Stage aPrimaryStage );

	public <C extends AbstractController<?>> void loadAsDialog( Class<C> aControllerClass, final Window aOwner, Optional<?> aParameter );

	public <C extends AbstractController<?>> void loadAsDialog( Class<C> aControllerClass, final Window aOwner );

	public <C extends AbstractController<?>> void loadInPane( Class<C> aControllerClass, AnchorPane aPane, Optional<?> aParameter );

	public <C extends AbstractController<?>> void loadInPane( Class<C> aControllerClass, AnchorPane aPane );

}
