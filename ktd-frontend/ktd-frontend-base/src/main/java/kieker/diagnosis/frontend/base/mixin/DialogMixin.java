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

package kieker.diagnosis.frontend.base.mixin;

import java.net.URL;

import javafx.scene.Scene;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;

public interface DialogMixin {

	default Stage getStage( ) {
		final DialogPane dialogPane = getDialogPane( );
		final Scene scene = dialogPane.getScene( );
		return ( Stage ) scene.getWindow( );
	}

	default void addDefaultStylesheet( ) {
		final URL cssURL = getClass( ).getResource( getClass( ).getSimpleName( ) + ".css" );
		final String cssExternalForm = cssURL.toExternalForm( );
		getDialogPane( ).getStylesheets( ).add( cssExternalForm );
	}

	DialogPane getDialogPane( );

}
