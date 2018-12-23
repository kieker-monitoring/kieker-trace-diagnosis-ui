/***************************************************************************
 * Copyright 2015-2018 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.ui;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testfx.framework.junit.ApplicationTest;

import com.google.inject.Injector;

import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;
import kieker.diagnosis.KiekerTraceDiagnosis;
import kieker.diagnosis.ui.complex.main.MainController;

/**
 * This is a UI test which imports both ascii and binary monitoring logs and makes sure that the results are correct.
 *
 * @author Nils Christian Ehmke
 */
public final class ImportLogsTestUI extends ApplicationTest {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder( );
	private MainController mainController;

	@Override
	public void start( final Stage stage ) throws Exception {
		final KiekerTraceDiagnosis kiekerTraceDiagnosis = new KiekerTraceDiagnosis( );
		kiekerTraceDiagnosis.start( stage );
		
		final Injector injector = kiekerTraceDiagnosis.getInjector( );
		mainController = injector.getInstance( MainController.class );
	}

	@Test
	public void testBinary( ) throws IOException, URISyntaxException {
		loadBinaryDataIntoTemporaryFolder( );
		importTemporaryFolder( );

		checkTraces( );
		checkMethods( );
		checkAggregatedMethods( );
	}

	private void loadBinaryDataIntoTemporaryFolder( ) throws IOException {
		final InputStream binaryDataStream = getClass( ).getResourceAsStream( "/kieker-log-binary/kieker.bin" );
		final InputStream mappingFileStream = getClass( ).getResourceAsStream( "/kieker-log-binary/kieker.map" );

		final Path temporaryPath = temporaryFolder.getRoot( ).toPath( );
		Files.copy( binaryDataStream, temporaryPath.resolve( "kieker.bin" ) );
		Files.copy( mappingFileStream, temporaryPath.resolve( "kieker.map" ) );
	}

	private void importTemporaryFolder( ) {
		// We cannot use the GUI at this point. TestFX cannot handle native dialogs and neither can the headless test environment.
		// In this case we have to import the logs via code.
		mainController.performImportLog(temporaryFolder.getRoot());
	}

	private void checkTraces( ) {
		clickOn( "#tabTraces" );

		final TreeTableView<?> treeTableView = lookup( "#tabTracesTreeTable" ).query( );
		assertThat( treeTableView.getRoot( ).getChildren( ).size( ), is( 2 ) );
		
		clickOn( "#tabTracesFilterHost" ).write( "host1" );
		clickOn( "#tabTracesSearch" );
		assertThat( treeTableView.getRoot( ).getChildren( ).size( ), is( 1 ) );
	}

	private void checkMethods( ) {
		clickOn( "#tabMethods" );

		final TableView<Object> tableView = lookup( "#tabMethodsTable" ).queryTableView( );
		assertThat( tableView.getItems( ).size( ), is( 3 ) );
		
		clickOn( "#tabMethodsFilterHost" ).write( "host1" );
		clickOn( "#tabMethodsSearch" );
		assertThat( tableView.getItems( ).size( ), is( 2 ) );
	}

	private void checkAggregatedMethods( ) {
		clickOn( "#tabAggregatedMethods" );

		final TableView<Object> tableView = lookup( "#tabAggregatedMethodsTable" ).queryTableView( );
		assertThat( tableView.getItems( ).size( ), is( 3 ) );
		
		clickOn( "#tabAggregatedMethodsFilterHost" ).write( "host1" );
		clickOn( "#tabAggregatedMethodsSearch" );
		assertThat( tableView.getItems( ).size( ), is( 2 ) );
	}

	@Test
	public void testAscii( ) throws IOException {
		loadAsciiDataIntoTemporaryFolder( );
		importTemporaryFolder( );

		checkTraces( );
		checkMethods( );
		checkAggregatedMethods( );
	}

	private void loadAsciiDataIntoTemporaryFolder( ) throws IOException {
		final InputStream binaryDataStream = getClass( ).getResourceAsStream( "/kieker-log-ascii/kieker.dat" );
		final InputStream mappingFileStream = getClass( ).getResourceAsStream( "/kieker-log-ascii/kieker.map" );

		final Path temporaryPath = temporaryFolder.getRoot( ).toPath( );
		Files.copy( binaryDataStream, temporaryPath.resolve( "kieker.dat" ) );
		Files.copy( mappingFileStream, temporaryPath.resolve( "kieker.map" ) );
	}

}