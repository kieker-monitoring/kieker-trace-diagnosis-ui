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

package kieker.diagnosis.frontend.dialog.alert;

import javafx.scene.control.ButtonType;
import javafx.stage.Window;
import kieker.diagnosis.frontend.base.mixin.DialogMixin;
import kieker.diagnosis.frontend.base.mixin.ImageMixin;

/**
 * This is a simple alert which already uses the icons and stylesheets from the Kieker Trace Diagnosis tool.
 *
 * @author Nils Christian Ehmke
 */
public final class Alert extends javafx.scene.control.Alert implements DialogMixin, ImageMixin {

	public Alert( final AlertType alertType, final Window owner ) {
		super( alertType );

		configureDialog( owner );
		configureButtons( );
	}

	private void configureDialog( final Window owner ) {
		initOwner( owner );
		addDefaultStylesheet( );
	}

	private void configureButtons( ) {
		getDialogPane( ).lookupButton( ButtonType.OK ).setId( "infoDialogOk" );
	}

}
