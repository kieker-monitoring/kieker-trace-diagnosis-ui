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

import com.google.inject.Singleton;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import jfxtras.scene.layout.VBox;
import kieker.diagnosis.architecture.ui.DialogViewBase;

/**
 * The view of the about dialog.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class AboutDialogView extends DialogViewBase<AboutDialogController, Void> {

	public AboutDialogView( ) {
		super( Modality.WINDOW_MODAL, StageStyle.DECORATED, true );

		setSpacing( 10 );

		{
			final Label label = new Label( );
			label.setText( getLocalizedString( "description" ) );
			VBox.setMargin( label, new Insets( 10, 10, 0, 10 ) );

			getChildren( ).add( label );
		}

		{
			final Separator separator = new Separator( );

			getChildren( ).add( separator );
		}

		{
			final ButtonBar buttonBar = new ButtonBar( );
			VBox.setMargin( buttonBar, new Insets( 10 ) );

			{
				final Button button = new Button( );
				button.setText( getLocalizedString( "ok" ) );
				button.setDefaultButton( true );
				button.setCancelButton( true );
				button.setOnAction( e -> getController( ).performClose( ) );

				buttonBar.getButtons( ).add( button );
			}

			getChildren( ).add( buttonBar );
		}
	}

	@Override
	public void setParameter( final Object aParameter ) {

	}

}
