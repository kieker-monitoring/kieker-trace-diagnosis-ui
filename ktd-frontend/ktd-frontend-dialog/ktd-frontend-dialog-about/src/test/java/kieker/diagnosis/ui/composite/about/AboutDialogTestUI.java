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

package kieker.diagnosis.ui.composite.about;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.scene.Scene;
import javafx.scene.control.Labeled;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This is a UI test which checks that the about dialog is working as expected.
 *
 * @author Nils Christian Ehmke
 */
public final class AboutDialogTestUI extends ApplicationTest {

	@Override
	public void start( final Stage stage ) throws Exception {
		final Scene scene = new Scene( new VBox( ) );
		stage.setScene( scene );
		stage.show( );

		final AboutDialog aboutDialog = new AboutDialog( );
		aboutDialog.show( );
	}

	@Test
	public void testAboutDialog( ) {
		assertThat( listWindows( ), hasSize( 2 ) );

		final Labeled descriptionLabel = lookup( "#aboutDialogDescription" ).queryLabeled( );

		assertThat( descriptionLabel.getText( ), containsString( "Kieker Trace Diagnosis - " ) );
		assertThat( descriptionLabel.getText( ), containsString( "Copyright 2015-2018 Kieker Project (http://kieker-monitoring.net)" ) );
		assertThat( descriptionLabel.getText( ), containsString( "Dieses Werkzeug ist unter der Apache License 2.0 lizenziert." ) );

		clickOn( "#aboutDialogOk" );
		assertThat( listWindows( ), hasSize( 1 ) );
	}

}
