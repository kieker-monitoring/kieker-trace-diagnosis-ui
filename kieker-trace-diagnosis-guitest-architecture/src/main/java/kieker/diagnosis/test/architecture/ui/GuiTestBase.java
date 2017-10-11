package kieker.diagnosis.test.architecture.ui;

import java.util.concurrent.TimeoutException;

import org.junit.BeforeClass;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.toolkit.ApplicationFixture;

import javafx.stage.Stage;
import kieker.diagnosis.KiekerTraceDiagnosis;

public abstract class GuiTestBase {

	private static final FxRobot cvRobot = new FxRobot( );

	@BeforeClass
	public static void setUp( ) {
		try {
			FxToolkit.registerPrimaryStage( );
			FxToolkit.setupApplication( new KiekerTraceDiagnosisApplicationFixture( ) );
		} catch ( final TimeoutException ex ) {
			throw new IllegalStateException( ex );
		}
	}

	public static FxRobot getRobot( ) {
		return cvRobot;
	}

	private static final class KiekerTraceDiagnosisApplicationFixture implements ApplicationFixture {

		@Override
		public void stop( ) throws Exception {
		}

		@Override
		public void start( final Stage stage ) throws Exception {
			final KiekerTraceDiagnosis kiekerTraceDiagnosis = new KiekerTraceDiagnosis( );
			kiekerTraceDiagnosis.start( stage );
		}

		@Override
		public void init( ) throws Exception {
		}
	}

}
