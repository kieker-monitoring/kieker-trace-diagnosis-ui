/***************************************************************************
 * Copyright 2015-2018 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.ui.dialogs.about;

import java.util.ResourceBundle;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import kieker.diagnosis.ui.mixin.DialogMixin;
import kieker.diagnosis.ui.mixin.ImageMixin;

/**
 * The about dialog shows some information (like the license) about the tool.
 *
 * @author Nils Christian Ehmke
 */
public final class AboutDialog extends Alert implements DialogMixin, ImageMixin {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( AboutDialog.class.getCanonicalName( ) );

	public AboutDialog( ) {
		super( AlertType.NONE );

		configureDialog( );
		addComponents( );
		addButtons( );
	}

	private void configureDialog( ) {
		setTitle( RESOURCE_BUNDLE.getString( "title" ) );
		getStage( ).getIcons( ).add( createIcon( ) );
		addDefaultStylesheet( );
	}

	private Image createIcon( ) {
		final String iconPath = RESOURCE_BUNDLE.getString( "icon" );
		return loadImage( iconPath );
	}

	private void addComponents( ) {
		final Label label = new Label( );
		label.setText( RESOURCE_BUNDLE.getString( "description" ) );
		getDialogPane( ).setContent( label );
	}

	private void addButtons( ) {
		getButtonTypes( ).add( ButtonType.OK );
	}

}
