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

package kieker.diagnosis.frontend;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import com.google.inject.Guice;
import com.google.inject.Injector;

import javafx.scene.Scene;
import javafx.scene.control.Labeled;
import javafx.stage.Stage;
import kieker.diagnosis.backend.base.ServiceBaseModule;
import kieker.diagnosis.frontend.main.MainView;

/**
 * This is a UI test which checks that the monitoring settings dialog is working as expected.
 *
 * @author Nils Christian Ehmke
 */
public final class MonitoringTestUI extends ApplicationTest {

	@Override
	public void start( final Stage stage ) throws Exception {
		final Injector injector = Guice.createInjector( new ServiceBaseModule( ) );

		final MainView mainView = injector.getInstance( MainView.class );

		final Scene scene = new Scene( mainView );
		stage.setScene( scene );
		stage.show( );
	}

	@Test
	public void testMonitoringSettingsDialog( ) throws InterruptedException {
		clickOn( "#menuFile" ).clickOn( "#menuItemMonitoringSettings" );

		final Labeled statusLabelFst = lookup( "#monitoringDialogStatus" ).queryLabeled( );
		assertThat( statusLabelFst.getText( ), is( "Kein Monitoring gestartet" ) );

		clickOn( "#monitoringDialogActive" );
		clickOn( "#monitoringDialogOk" );

		clickOn( "#menuFile" ).clickOn( "#menuItemMonitoringSettings" );
		final Labeled statusLabelSnd = lookup( "#monitoringDialogStatus" ).queryLabeled( );
		assertThat( statusLabelSnd.getText( ), is( "Monitoring läuft" ) );

		clickOn( "#monitoringDialogActive" );
		clickOn( "#monitoringDialogOk" );
	}
}
