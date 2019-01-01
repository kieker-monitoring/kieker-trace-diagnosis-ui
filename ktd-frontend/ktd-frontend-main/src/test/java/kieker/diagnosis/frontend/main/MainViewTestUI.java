package kieker.diagnosis.frontend.main;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testfx.framework.junit.ApplicationTest;

import com.google.inject.Guice;
import com.google.inject.Injector;

import javafx.scene.Scene;
import javafx.scene.control.Labeled;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;
import kieker.diagnosis.backend.base.ServiceBaseModule;
import kieker.diagnosis.frontend.base.FrontendBaseModule;

/**
 * This is a UI test which checks that the main view is working as expected.
 *
 * @author Nils Christian Ehmke
 */
public final class MainViewTestUI extends ApplicationTest {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder( );

	private MainController mainController;

	@Override
	public void start( final Stage stage ) throws Exception {
		final Injector injector = Guice.createInjector( new ServiceBaseModule( ), new FrontendBaseModule( ) );

		mainController = injector.getInstance( MainController.class );
		mainController.initialize( );

		final MainView mainView = injector.getInstance( MainView.class );
		mainView.setParameter( null );

		final Scene scene = new Scene( mainView );
		stage.setScene( scene );
		stage.show( );
	}

	@Test
	public void testAboutDialog( ) {
		clickOn( "#menuHelp" ).clickOn( "#menuItemAbout" );
		clickOn( "#aboutDialogOk" );
	}

	@Test
	public void testSettingsDialog( ) {
		clickOn( "#menuFile" ).clickOn( "#menuItemSettings" );
		clickOn( "#settingsDialogOk" );
	}

	@Test
	public void testManualDialog( ) {
		clickOn( "#menuHelp" ).clickOn( "#menuItemManual" );
		clickOn( "#manualDialogOk" );
	}

	@Test
	public void testMonitoringSettingsDialog( ) throws InterruptedException {
		clickOn( "#menuFile" ).clickOn( "#menuItemMonitoringSettings" );

		final Labeled statusLabelFst = lookup( "#monitoringDialogStatus" ).queryLabeled( );
		assertThat( statusLabelFst.getText( ), is( "Kein Monitoring gestartet" ) );

		clickOn( "#monitoringDialogActive" );
		clickOn( "#monitoringDialogOk" );

		clickOn( "#menuFile" ).clickOn( "#menuItemMonitoringSettings" );
		final Labeled statusLabelSnd = lookup( "#monitoringDialogStatus" ).queryLabeled( );
		assertThat( statusLabelSnd.getText( ), is( "Monitoring läuft" ) );

		clickOn( "#monitoringDialogActive" );
		clickOn( "#monitoringDialogOk" );
	}

	@Test
	public void testCloseDialog( ) {
		clickOn( "#menuFile" ).clickOn( "#menuFileClose" );
		clickOn( "#mainCloseDialogYes" );
	}

	@Test
	public void testSaveAsFavorite( ) {
		clickOn( "#tabTracesFilterHost" ).write( "host1" );
		clickOn( "#tabTracesSaveAsFavorite" );

		clickOn( ".dialog-pane .text-field" ).write( "Favorite 1" );
		clickOn( ".dialog-pane .button" );

		clickOn( "#tabTracesFilterHost" ).eraseText( 5 );
		clickOn( "#menuFavorites" ).clickOn( "Favorite 1" );

		assertThat( lookup( "#tabTracesFilterHost" ).queryTextInputControl( ).getText( ), is( "host1" ) );
	}

	@Test
	public void testImportLog( ) throws IOException {
		loadBinaryDataIntoTemporaryFolder( );
		importTemporaryFolder( );

		clickOn( "#tabTraces" );
		final TreeTableView<?> treeTableView = lookup( "#tabTracesTreeTable" ).query( );
		assertThat( treeTableView.getRoot( ).getChildren( ).size( ), is( 2 ) );

		clickOn( "#tabMethods" );
		final TableView<Object> methodsTableView = lookup( "#tabMethodsTable" ).queryTableView( );
		assertThat( methodsTableView.getItems( ).size( ), is( 3 ) );

		clickOn( "#tabAggregatedMethods" );
		final TableView<Object> aggregatedMethodsTableView = lookup( "#tabAggregatedMethodsTable" ).queryTableView( );
		assertThat( aggregatedMethodsTableView.getItems( ).size( ), is( 3 ) );
	}

	private void loadBinaryDataIntoTemporaryFolder( ) throws IOException {
		final InputStream binaryDataStream = getClass( ).getResourceAsStream( "/kieker-log-binary/kieker.bin" );
		final InputStream mappingFileStream = getClass( ).getResourceAsStream( "/kieker-log-binary/kieker.map" );

		final Path temporaryPath = temporaryFolder.getRoot( ).toPath( );
		Files.copy( binaryDataStream, temporaryPath.resolve( "kieker.bin" ) );
		Files.copy( mappingFileStream, temporaryPath.resolve( "kieker.map" ) );
	}

	private void importTemporaryFolder( ) {
		// We cannot use the GUI at this point. TestFX cannot handle native dialogs and
		// neither can the headless test environment.
		// In this case we have to import the logs via code.
		mainController.performImportLog( temporaryFolder.getRoot( ) );
	}

}
