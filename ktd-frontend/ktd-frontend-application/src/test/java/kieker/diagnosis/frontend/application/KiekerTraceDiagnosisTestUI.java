package kieker.diagnosis.frontend.application;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.stage.Stage;

public final class KiekerTraceDiagnosisTestUI extends ApplicationTest {

	@Override
	public void start( final Stage stage ) throws Exception {
		final KiekerTraceDiagnosis kiekerTraceDiagnosis = new KiekerTraceDiagnosis( );
		kiekerTraceDiagnosis.start( stage );
	}

	@Test
	public void closeApplication( ) {
		clickOn( "#menuFile" ).clickOn( "#menuFileClose" );
		clickOn( "#mainCloseDialogYes" );
	}

}
