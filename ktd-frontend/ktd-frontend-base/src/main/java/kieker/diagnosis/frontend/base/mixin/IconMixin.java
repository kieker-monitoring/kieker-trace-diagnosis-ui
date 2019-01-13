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

import javafx.scene.Node;
import javafx.scene.control.Label;

public interface IconMixin {

	/**
	 * This method creates an icon which can for instance be used as a graphic for a node.
	 *
	 * @param aIcon
	 *            The type of the icon.
	 *
	 * @return A new icon.
	 */
	default Node createIcon( final Icon aIcon ) {
		final Label label = new Label( );

		label.setText( aIcon.getUnicode( ) );
		label.getStyleClass( ).add( "font-awesome-icon" );

		return label;
	}

	enum Icon {

		SEARCH( "\uf002" ), FOLDER_OPEN( "\uf07c" ), ZIP_ARCHIVE( "\uf1c6" ), TIMES( "\uf00d" ), COGS( "\uf085" ), QUESTION_CIRCLE( "\uf059" ), INFO_CIRCLE( "\uf05a" ), CHART( "\uf080" );

		private String unicode;

		Icon( final String unicode ) {
			this.unicode = unicode;
		}

		public String getUnicode( ) {
			return unicode;
		}

	}

}
