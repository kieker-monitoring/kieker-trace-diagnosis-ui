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
		assertTrue( aboutDialog.getDescriptionLabel( ).getText( ).contains( "Copyright 2015-2016 Kieker Project (http://kieker-monitoring.net)" ) );

		aboutDialog.getOkayButton( ).click( );
	}

	@Test
	public void visitSettingsDialog( ) {
		mainView.getFileButton( ).click( );
		mainView.getSettingsButton( ).click( );

		settingsDialog.getCancelButton( ).click( );
	}

}
