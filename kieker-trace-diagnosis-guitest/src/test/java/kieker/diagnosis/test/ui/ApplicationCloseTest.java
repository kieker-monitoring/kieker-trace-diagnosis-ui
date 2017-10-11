package kieker.diagnosis.test.ui;

import java.util.concurrent.TimeoutException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.toolkit.ApplicationFixture;

import javafx.stage.Stage;
import kieker.diagnosis.KiekerTraceDiagnosis;
import kieker.diagnosis.test.ui.main.MainView;

public class ApplicationCloseTest {

	@BeforeClass
	public static void setUp( ) {
		try {
			FxToolkit.registerPrimaryStage( );
			FxToolkit.setupApplication( new ApplicationFixture( ) {

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
			} );
		} catch ( final TimeoutException ex ) {
			throw new IllegalStateException( ex );
		}
	}

	@Test
	public void closeApplication( ) {
		final MainView mainView = new MainView( new FxRobot( ) );
		mainView.getFileMenu( ).click( );
		mainView.getFileMenu( ).getCloseMenuItem( ).click( );
	}

}
