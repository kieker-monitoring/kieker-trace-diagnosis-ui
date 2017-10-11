package kieker.diagnosis.test.ui;

import org.junit.Test;

import kieker.diagnosis.test.architecture.ui.GuiTestBase;
import kieker.diagnosis.test.ui.main.MainView;
import kieker.diagnosis.test.ui.main.OkCancelDialog;

public class ApplicationCloseTest extends GuiTestBase {

	@Test
	public void closeApplication( ) {
		// First we cancel the dialog
		final MainView mainView = new MainView( getRobot( ) );
		mainView.getFileMenu( ).click( );
		mainView.getFileMenu( ).getCloseMenuItem( ).click( );

		final OkCancelDialog dialog = new OkCancelDialog( getRobot( ) );
		dialog.getCancelButton( ).click( );

		// Now we close the application
		mainView.getFileMenu( ).click( );
		mainView.getFileMenu( ).getCloseMenuItem( ).click( );
		dialog.getOkButton( ).click( );
	}

}
