/***************************************************************************
 * Copyright 2015-2017 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.guitest.tests;

import static org.junit.Assert.assertTrue;

import kieker.diagnosis.guitest.GuiTestConfiguration;
import kieker.diagnosis.guitest.views.AboutDialog;
import kieker.diagnosis.guitest.views.MainView;
import kieker.diagnosis.guitest.views.SettingsDialog;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith ( SpringRunner.class )
@ContextConfiguration ( classes = GuiTestConfiguration.class )
public class VisitDialogsTest {

	@Autowired
	private MainView mainView;

	@Autowired
	private AboutDialog aboutDialog;

	@Autowired
	private SettingsDialog settingsDialog;

	@Test
	public void visitAboutDialog( ) {
		mainView.getHelpButton( ).click( );
		mainView.getAboutButton( ).click( );

		assertTrue( aboutDialog.getDescriptionLabel( ).getText( ).contains( "Kieker Trace Diagnosis" ) );
		assertTrue( aboutDialog.getDescriptionLabel( ).getText( ).contains( "Copyright 2015-2017 Kieker Project (http://kieker-monitoring.net)" ) );

		aboutDialog.getOkayButton( ).click( );
	}

	@Test
	public void visitSettingsDialog( ) {
		mainView.getFileButton( ).click( );
		mainView.getSettingsButton( ).click( );

		settingsDialog.getCancelButton( ).click( );
	}

}
