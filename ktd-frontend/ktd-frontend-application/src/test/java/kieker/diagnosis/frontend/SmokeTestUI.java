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

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.stage.Stage;
import kieker.diagnosis.frontend.application.KiekerTraceDiagnosis;

/**
 * This is a UI test which just makes sure that the application is at least startable.
 *
 * @author Nils Christian Ehmke
 */
public final class SmokeTestUI extends ApplicationTest {

	@Override
	public void start( final Stage stage ) throws Exception {
		final KiekerTraceDiagnosis kiekerTraceDiagnosis = new KiekerTraceDiagnosis( );
		kiekerTraceDiagnosis.start( stage );
	}

	@Test
	public void visitMethods( ) {
		clickOn( "#tabMethods" );
	}

	@Test
	public void visitAggregatedMethods( ) {
		clickOn( "#tabAggregatedMethods" );
	}

	@Test
	public void visitStatistics( ) {
		clickOn( "#tabStatistics" );
	}

	@Test
	public void visitTraces( ) {
		clickOn( "#tabTraces" );
	}

}
