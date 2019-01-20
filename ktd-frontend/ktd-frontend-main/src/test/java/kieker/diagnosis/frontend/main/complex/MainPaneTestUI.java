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

package kieker.diagnosis.frontend.main.complex;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;
import org.junitpioneer.jupiter.TempDirectory.TempDir;
import org.testfx.framework.junit5.ApplicationTest;

import com.google.inject.Guice;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Labeled;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;
import kieker.diagnosis.frontend.base.FrontendBaseModule;

/**
 * This is a UI test which checks that the main view is working as expected.
 *
 * @author Nils Christian Ehmke
 */
public final class MainPaneTestUI extends ApplicationTest {

	private MainPane mainPane;

	@Override
	public void start( final Stage stage ) throws Exception {
		Guice.createInjector( new FrontendBaseModule( ) );

		mainPane = new MainPane( );

		final Scene scene = new Scene( mainPane );
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
	public void testMonitoringSettingsDialog( ) throws InterruptedException {
		clickOn( "#menuFile" ).clickOn( "#menuItemMonitoringSettings" );

		final Labeled statusLabelFst = lookup( "#monitoringDialogStatus" ).queryLabeled( );
		assertThat( statusLabelFst.getText( ) ).isEqualTo( "Kein Monitoring gestartet" );

		clickOn( "#monitoringDialogActive" );
		clickOn( "#monitoringDialogOk" );

		clickOn( "#menuFile" ).clickOn( "#menuItemMonitoringSettings" );
		final Labeled statusLabelSnd = lookup( "#monitoringDialogStatus" ).queryLabeled( );
		assertThat( statusLabelSnd.getText( ) ).isEqualTo( "Monitoring läuft" );

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
		clickOn( "#favoriteFilterDialogOk" );

		clickOn( "#tabTracesSaveAsFavorite" );
		clickOn( ".dialog-pane .text-field" ).write( "Favorite 2" );
		clickOn( "#favoriteFilterDialogCancel" );

		clickOn( "#tabTracesSaveAsFavorite" );
		clickOn( ".dialog-pane .text-field" ).write( "Favorite 3" );
		clickOn( "#favoriteFilterDialogOk" );

		clickOn( "#tabTracesFilterHost" ).eraseText( 5 );

		clickOn( "#menuFavorites" ).clickOn( "Favorite 1" );
		assertThat( lookup( "#tabTracesFilterHost" ).queryTextInputControl( ).getText( ) ).isEqualTo( "host1" );
	}

	@Test
	@ExtendWith ( TempDirectory.class )
	public void testImportLog( @TempDir final Path tempDir ) throws IOException {
		loadBinaryDataIntoTemporaryFolder( tempDir );
		importTemporaryFolder( tempDir );

		clickOn( "#tabTraces" );
		final TreeTableView<?> treeTableView = lookup( "#tabTracesTreeTable" ).query( );
		assertThat( treeTableView.getRoot( ).getChildren( ) ).hasSize( 2 );

		clickOn( "#tabMethods" );
		final TableView<Object> methodsTableView = lookup( "#tabMethodsTable" ).queryTableView( );
		assertThat( methodsTableView.getItems( ) ).hasSize( 3 );

		clickOn( "#tabAggregatedMethods" );
		final TableView<Object> aggregatedMethodsTableView = lookup( "#tabAggregatedMethodsTable" ).queryTableView( );
		assertThat( aggregatedMethodsTableView.getItems( ) ).hasSize( 3 );
	}

	private void loadBinaryDataIntoTemporaryFolder( final Path tempDir ) throws IOException {
		final InputStream binaryDataStream = getClass( ).getResourceAsStream( "/kieker-log-binary/kieker.bin" );
		final InputStream mappingFileStream = getClass( ).getResourceAsStream( "/kieker-log-binary/kieker.map" );

		Files.copy( binaryDataStream, tempDir.resolve( "kieker.bin" ) );
		Files.copy( mappingFileStream, tempDir.resolve( "kieker.map" ) );
	}

	private void importTemporaryFolder( final Path tempDir ) {
		// We cannot use the GUI at this point. TestFX cannot handle native dialogs and
		// neither can the headless test environment.
		// In this case we have to import the logs via code.
		mainPane.performImportLog( tempDir.toFile( ) );
	}

	@Test
	@ExtendWith ( TempDirectory.class )
	public void testJumpToMethods( @TempDir final Path tempDir ) throws IOException {
		loadBinaryDataIntoTemporaryFolder( tempDir );
		importTemporaryFolder( tempDir );

		clickOn( "#tabAggregatedMethods" );
		clickOn( lookup( "#tabAggregatedMethodsTable" ).lookup( ".table-row-cell" ).nth( 0 ).queryAs( Node.class ) );
		clickOn( "#tabAggregatedMethodsJumpToMethods" );

		final TabPane tabPane = lookup( "#mainTabPane" ).queryAs( TabPane.class );
		assertThat( tabPane.getSelectionModel( ).getSelectedItem( ).getText( ) ).isEqualTo( "Methodenaufrufe" );
	}

	@Test
	@ExtendWith ( TempDirectory.class )
	public void testJumpToTrace( @TempDir final Path tempDir ) throws IOException {
		loadBinaryDataIntoTemporaryFolder( tempDir );
		importTemporaryFolder( tempDir );

		clickOn( "#tabMethods" );
		clickOn( lookup( "#tabMethodsTable" ).lookup( ".table-row-cell" ).nth( 0 ).queryAs( Node.class ) );
		clickOn( "#tabMethodsJumpToTrace" );

		final TabPane tabPane = lookup( "#mainTabPane" ).queryAs( TabPane.class );
		assertThat( tabPane.getSelectionModel( ).getSelectedItem( ).getText( ) ).isEqualTo( "Traces" );
	}

}
