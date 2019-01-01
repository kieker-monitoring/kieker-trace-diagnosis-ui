package kieker.diagnosis.frontend.application;

import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

public final class KiekerTraceDiagnosisTestUI extends ApplicationTest {

	@Override
	public void start( final Stage stage ) throws Exception {
		final KiekerTraceDiagnosis kiekerTraceDiagnosis = new KiekerTraceDiagnosis( );
		kiekerTraceDiagnosis.start( stage );
	}

	@Test
	public void closeApplication( ) {
		closeCurrentWindowViaJavaFx( );
		clickOn( "#mainCloseDialogYes" );
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
