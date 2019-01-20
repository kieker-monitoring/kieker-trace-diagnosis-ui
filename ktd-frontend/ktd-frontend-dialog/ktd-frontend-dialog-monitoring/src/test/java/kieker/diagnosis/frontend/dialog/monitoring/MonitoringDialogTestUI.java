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

package kieker.diagnosis.frontend.dialog.monitoring;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.scene.Scene;
import javafx.scene.control.Labeled;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import kieker.diagnosis.backend.monitoring.MonitoringConfiguration;
import kieker.diagnosis.backend.monitoring.MonitoringService;
import kieker.diagnosis.backend.monitoring.Status;

/**
 * This is a UI test which checks that the monitoring dialog is working as expected.
 *
 * @author Nils Christian Ehmke
 */
public final class MonitoringDialogTestUI extends ApplicationTest {

	private MonitoringDialog monitoringDialog;

	@Override
	public void start( final Stage stage ) throws Exception {
		final Scene scene = new Scene( new VBox( ) );
		stage.setScene( scene );
		stage.show( );

		monitoringDialog = new MonitoringDialog( );
		monitoringDialog.show( );

		final MonitoringService monitoringService = new MonitoringService( );
		final MonitoringConfiguration monitoringConfiguration = monitoringService.getCurrentConfiguration( );
		monitoringDialog.setValue( monitoringConfiguration );
	}

	@Test
	public void testMonitoringDialog( ) {
		assertThat( listWindows( ) ).hasSize( 2 );

		changeMonitoringState( );
		enterInvalidValues( );
		enterNullValues( );
		enterValidValues( );

		assertThat( listWindows( ) ).hasSize( 1 );
	}

	private void changeMonitoringState( ) {
		interact( ( ) -> monitoringDialog.setStatus( Status.NO_MONITORING ) );
		final Labeled statusLabel = lookup( "#monitoringDialogStatus" ).queryLabeled( );
		assertThat( statusLabel.getText( ) ).isEqualTo( "Kein Monitoring gestartet" );

		interact( ( ) -> monitoringDialog.setStatus( Status.RUNNING ) );
		assertThat( statusLabel.getText( ) ).isEqualTo( "Monitoring läuft" );

		interact( ( ) -> monitoringDialog.setStatus( Status.TERMINATED ) );
		assertThat( statusLabel.getText( ) ).isEqualTo( "Monitoring terminiert" );
	}

	private void enterInvalidValues( ) {
		clickOn( "#monitoringDialogMaxEntriesPerFile" ).eraseText( 7 ).write( "-42" );
		clickOn( "#monitoringDialogQueueSize" ).eraseText( 6 ).write( "-42" );
		clickOn( "#monitoringDialogBufferSize" ).eraseText( 5 ).write( "-42" );
		clickOn( "#monitoringDialogOk" );
		clickOn( "#monitoringDialogValidationOk" );
	}

	private void enterNullValues( ) {
		clickOn( "#monitoringDialogMaxEntriesPerFile" ).eraseText( 3 );
		clickOn( "#monitoringDialogQueueSize" ).eraseText( 3 );
		clickOn( "#monitoringDialogBufferSize" ).eraseText( 3 );
		clickOn( "#monitoringDialogOk" );
		clickOn( "#monitoringDialogValidationOk" );
	}

	private void enterValidValues( ) {
		clickOn( "#monitoringDialogMaxEntriesPerFile" ).write( "100000" );
		clickOn( "#monitoringDialogQueueSize" ).write( "100000" );
		clickOn( "#monitoringDialogBufferSize" ).write( "16384" );
		clickOn( "#monitoringDialogOk" );
	}

}
