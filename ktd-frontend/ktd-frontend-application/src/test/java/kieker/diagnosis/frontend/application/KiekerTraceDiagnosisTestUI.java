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

package kieker.diagnosis.frontend.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

public final class KiekerTraceDiagnosisTestUI extends ApplicationTest {

	private CloseDialogPage closeDialogPage;

	@Override
	public void start( final Stage stage ) throws Exception {
		final KiekerTraceDiagnosis kiekerTraceDiagnosis = new KiekerTraceDiagnosis( );
		kiekerTraceDiagnosis.start( stage );

		closeDialogPage = new CloseDialogPage( this );
	}

	@Test
	public void closeApplication( ) {
		assertThat( listWindows( ) ).hasSize( 1 );

		closeCurrentWindowViaJavaFx( );
		closeDialogPage.getCancel( ).click( );
		assertThat( listWindows( ) ).hasSize( 1 );

		closeCurrentWindowViaJavaFx( );
		closeDialogPage.getYes( ).click( );
		assertThat( listWindows( ) ).isEmpty( );
	}

	private void closeCurrentWindowViaJavaFx( ) {
		// There is currently no way to close the current window platform-independent with TestFX. We therefore have to
		// use the JavaFX API.
		final Window currentWindow = window( 0 );
		final WindowEvent windowEvent = mock( WindowEvent.class );

		WaitForAsyncUtils.asyncFx( ( ) -> currentWindow.getOnCloseRequest( ).handle( windowEvent ) );
		WaitForAsyncUtils.waitForFxEvents( );
	}

}
