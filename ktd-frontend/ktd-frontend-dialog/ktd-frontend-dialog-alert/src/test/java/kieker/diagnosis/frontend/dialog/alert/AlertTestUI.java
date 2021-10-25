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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Labeled;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * This is a UI test which checks that the alert is working as expected.
 *
 * @author Nils Christian Ehmke
 */
public final class AlertTestUI extends ApplicationTest {

	@Override
	public void start( final Stage stage ) throws Exception {
		final Scene scene = new Scene( new VBox( ) );
		stage.setScene( scene );
		stage.show( );

		final Alert alert = new Alert( AlertType.WARNING, stage );
		alert.setContentText( "Ungültige Dateneingabe." );
		alert.show( );
	}

	@Test
	public void testInfoDialog( ) {
		assertThat( listWindows( ) ).hasSize( 2 );

		final Text headerText = lookup( ".dialog-pane .text" ).queryText( );
		assertThat( headerText.getText( ) ).isEqualTo( "Warnung" );

		final Labeled content = lookup( ".dialog-pane .content" ).queryLabeled( );
		assertThat( content.getText( ) ).isEqualTo( "Ungültige Dateneingabe." );

		clickOn( "#infoDialogOk" );
		assertThat( listWindows( ) ).hasSize( 1 );
	}

}
