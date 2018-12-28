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

package kieker.diagnosis.ui;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.stage.Stage;
import kieker.diagnosis.frontend.application.KiekerTraceDiagnosis;

/**
 * This is a UI test which checks that the manual dialog is working as expected.
 *
 * @author Nils Christian Ehmke
 */
public final class ManualTestUI extends ApplicationTest {

	@Override
	public void start( final Stage stage ) throws Exception {
		final KiekerTraceDiagnosis kiekerTraceDiagnosis = new KiekerTraceDiagnosis( );
		kiekerTraceDiagnosis.start( stage );
	}

	@Test
	public void testManualDialog( ) {
		clickOn( "#menuHelp" ).clickOn( "#menuItemManual" );
		clickOn( "#manualDialogOk" );
	}

}
