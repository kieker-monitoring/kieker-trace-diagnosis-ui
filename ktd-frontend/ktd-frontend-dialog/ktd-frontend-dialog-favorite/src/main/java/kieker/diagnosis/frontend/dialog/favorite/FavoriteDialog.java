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

package kieker.diagnosis.frontend.dialog.favorite;

import java.util.ResourceBundle;

import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import kieker.diagnosis.frontend.base.mixin.DialogMixin;
import kieker.diagnosis.frontend.base.mixin.ImageMixin;

public final class FavoriteDialog extends TextInputDialog implements DialogMixin, ImageMixin {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( FavoriteDialog.class.getName( ) );

	public FavoriteDialog( ) {
		configureDialog( );
		configureButtons( );
	}

	private void configureDialog( ) {
		setTitle( RESOURCE_BUNDLE.getString( "newFilterFavorite" ) );
		setHeaderText( RESOURCE_BUNDLE.getString( "newFilterFavoriteName" ) );

		getStage( ).getIcons( ).add( createIcon( ) );
		addDefaultStylesheet( );
	}

	private Image createIcon( ) {
		return loadImage( "/kieker-logo.png" );
	}

	private void configureButtons( ) {
		getDialogPane( ).lookupButton( ButtonType.OK ).setId( "favoriteFilterDialogOk" );
		getDialogPane( ).lookupButton( ButtonType.CANCEL ).setId( "favoriteFilterDialogCancel" );
	}

}
