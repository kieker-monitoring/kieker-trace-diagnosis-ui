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

package kieker.diagnosis.frontend.dialog.settings;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import kieker.diagnosis.backend.settings.ClassAppearance;
import kieker.diagnosis.backend.settings.MethodAppearance;
import kieker.diagnosis.backend.settings.MethodCallAggregation;
import kieker.diagnosis.backend.settings.Settings;
import kieker.diagnosis.backend.settings.TimestampAppearance;

/**
 * This is a UI test which checks that the settings dialog is working as expected.
 *
 * @author Nils Christian Ehmke
 */
public final class SettingsDialogTestUI extends ApplicationTest {

	private SettingsDialog settingsDialog;

	@Override
	public void start( final Stage stage ) throws Exception {
		final Scene scene = new Scene( new VBox( ) );
		stage.setScene( scene );
		stage.show( );

		settingsDialog = new SettingsDialog( );
		settingsDialog.show( );

		final Settings settings = Settings
				.builder( )
				.classAppearance( ClassAppearance.SHORT )
				.maxNumberOfMethodCalls( 100 )
				.methodAppearance( MethodAppearance.LONG )
				.methodCallAggregation( MethodCallAggregation.BY_THRESHOLD )
				.methodCallThreshold( 42.0f )
				.showUnmonitoredTimeProperty( true )
				.timestampAppearance( TimestampAppearance.SHORT_TIME )
				.timeUnit( TimeUnit.MICROSECONDS )
				.build( );
		settingsDialog.setValue( settings );
	}

	@Test
	public void testSettingsDialog( ) {
		assertThat( listWindows( ) ).hasSize( 2 );

		enterNullValues( );
		enterInvalidValues( );
		enterValidValues( );

		assertThat( listWindows( ) ).hasSize( 1 );
	}

	private void enterNullValues( ) {
		clickOn( "#settingsDialogMethodCallThreshold" ).eraseText( 5 );
		clickOn( "#settingsDialogOk" );
		clickOn( "#settingsDialogValidationOk" );
	}

	private void enterInvalidValues( ) {
		clickOn( "#settingsDialogMethodCallThreshold" ).write( "-42.0" );
		clickOn( "#settingsDialogOk" );
		clickOn( "#settingsDialogValidationOk" );

		clickOn( "#settingsDialogMethodCallThreshold" ).eraseText( 5 ).write( "101.0" );
		clickOn( "#settingsDialogOk" );
		clickOn( "#settingsDialogValidationOk" );
	}

	private void enterValidValues( ) {
		clickOn( "#settingsDialogMethodCallThreshold" ).eraseText( 5 ).write( "50" );
		clickOn( "#settingsDialogOk" );
	}

}
