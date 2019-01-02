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
package kieker.diagnosis.frontend.dialog.progress;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.scene.Scene;
import javafx.scene.control.Labeled;
import javafx.stage.Stage;

/**
 * This is a UI test which checks that the progress dialog is working as expected.
 *
 * @author Nils Christian Ehmke
 */
public final class ProgressDialogTest extends ApplicationTest {

	@Override
	public void start( final Stage stage ) throws Exception {
		final ProgressDialog progressDialog = new ProgressDialog( );

		final Scene scene = new Scene( progressDialog );
		stage.setScene( scene );
		stage.show( );

		progressDialog.setProgress( 50.0 );
		progressDialog.setMessage( "test-message" );
	}

	@Test
	public void testMonitoringDialog( ) {
		final Labeled messageLabel = lookup( "#progressDialogMessage" ).queryLabeled( );
		assertThat( messageLabel.getText( ), is( "test-message" ) );
	}

}
