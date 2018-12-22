package kieker.diagnosis.ui;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.stage.Stage;
import kieker.diagnosis.KiekerTraceDiagnosis;

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
