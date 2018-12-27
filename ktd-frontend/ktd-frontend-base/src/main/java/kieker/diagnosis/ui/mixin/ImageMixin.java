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

package kieker.diagnosis.ui.mixin;

import java.net.URL;

import javafx.scene.image.Image;

public interface ImageMixin {

	default Image loadImage( final String imagePath ) {
		final URL imageUrl = getClass( ).getResource( imagePath );
		final String imageExternalForm = imageUrl.toExternalForm( );
		return new Image( imageExternalForm );
	}

}
