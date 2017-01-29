/***************************************************************************
 * Copyright 2015-2016 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.guitest;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.stage.Stage;
import kieker.diagnosis.Main;
import kieker.diagnosis.guitest.mainview.MainView;
import kieker.diagnosis.guitest.mainview.dialog.AboutDialog;
import kieker.diagnosis.guitest.mainview.dialog.SettingsDialog;
import kieker.diagnosis.guitest.mainview.subview.CallsView;
import kieker.diagnosis.guitest.mainview.subview.TracesView;
import kieker.diagnosis.service.ServiceUtil;
import kieker.diagnosis.service.data.DataService;

public final class GUITest extends ApplicationTest {

	@Override
	public void start( final Stage stage ) throws Exception {
		final Main main = new Main( );
		main.start( stage );
	}

	@Test
	public void allButtonsShouldBeAvailable( ) {
		final MainView mainView = new MainView( this );
		mainView.getAggregatedTracesButton( ).click( );
		mainView.getAggregatedCallsButton( ).click( );
		mainView.getStatisticsButton( ).click( );
		mainView.getTracesButton( ).click( );
		mainView.getCallsButton( ).click( );
	}

	@Test
	public void aboutDialogShouldWork( ) {
		final MainView mainView = new MainView( this );
		mainView.getHelpButton( ).click( );
		mainView.getAboutButton( ).click( );

		final AboutDialog aboutDialog = new AboutDialog( this );
		assertTrue( aboutDialog.getDescriptionLabel( ).getText( ).contains( "Kieker Trace Diagnosis" ) );
		assertTrue( aboutDialog.getDescriptionLabel( ).getText( ).contains( "Copyright 2015-2016 Kieker Project (http://kieker-monitoring.net)" ) );
		aboutDialog.getOkayButton( ).click( );
	}

	@Test
	public void settingsDialogShouldWork( ) {
		final MainView mainView = new MainView( this );
		mainView.getFileButton( ).click( );
		mainView.getSettingsButton( ).click( );

		final SettingsDialog settingsDialog = new SettingsDialog( this );
		settingsDialog.getOkayButton( ).click( );
	}

	@Test
	public void importOfFirstExampleMonitoringLogShouldWork( ) {
		// Unfortunately TestFX cannot handle the native file dialog of JavaFX. Therefore we have to use direct access as a workaround.
		final DataService dataService = ServiceUtil.getService( DataService.class );
		dataService.loadMonitoringLogFromFS( new File( "example/execution monitoring log" ) );

		final MainView mainView = new MainView( this );
		mainView.getCallsButton( ).click( );

		final CallsView callsView = new CallsView( this );
		assertTrue( callsView.getCounterTextField( ).getText( ).startsWith( "6540 " ) );

		mainView.getTracesButton( ).click( );

		final TracesView tracesView = new TracesView( this );
		assertTrue( tracesView.getCounterTextField( ).getText( ).startsWith( "1635 " ) );
	}

	@Test
	public void importOfSecondExampleMonitoringLogShouldWork( ) {
		// Unfortunately TestFX cannot handle the native file dialog of JavaFX. Therefore we have to use direct access as a workaround.
		final DataService dataService = ServiceUtil.getService( DataService.class );
		dataService.loadMonitoringLogFromFS( new File( "example/event monitoring log" ) );

		final MainView mainView = new MainView( this );
		mainView.getCallsButton( ).click( );

		final CallsView callsView = new CallsView( this );
		assertTrue( callsView.getCounterTextField( ).getText( ).startsWith( "396 " ) );

		mainView.getTracesButton( ).click( );

		final TracesView tracesView = new TracesView( this );
		assertTrue( tracesView.getCounterTextField( ).getText( ).startsWith( "100 " ) );
	}

	@Test
	public void testFilterForCallView( ) throws InterruptedException {
		// Unfortunately TestFX cannot handle the native file dialog of JavaFX. Therefore we have to use direct access as a workaround.
		final DataService dataService = ServiceUtil.getService( DataService.class );
		dataService.loadMonitoringLogFromFS( new File( "example/event monitoring log" ) );

		final MainView mainView = new MainView( this );
		mainView.getCallsButton( ).click( );

		final CallsView callsView = new CallsView( this );
		callsView.getFilterContainerTextField( ).setText( "SE-Nils-Ehmke" );
		callsView.getFilterContainerTextField( ).pushEnter( );
		callsView.getFilterComponentTextField( ).setText( "kieker.examples.bookstore.Bookstore" );
		callsView.getFilterComponentTextField( ).pushEnter( );
		callsView.getFilterOperationTextField( ).setText( "public void kieker.examples.bookstore.Bookstore.searchBook()" );
		callsView.getFilterOperationTextField( ).pushEnter( );
		callsView.getFilterTraceIDTextField( ).setText( "4658150164341456896" );
		callsView.getFilterTraceIDTextField( ).pushEnter( );

		assertTrue( callsView.getCounterTextField( ).getText( ).startsWith( "1 " ) );
	}

	@Test
	public void testFilterForTracesView( ) throws InterruptedException {
		// Unfortunately TestFX cannot handle the native file dialog of JavaFX. Therefore we have to use direct access as a workaround.
		final DataService dataService = ServiceUtil.getService( DataService.class );
		dataService.loadMonitoringLogFromFS( new File( "example/event monitoring log" ) );

		final MainView mainView = new MainView( this );
		mainView.getTracesButton( ).click( );

		final TracesView tracesView = new TracesView( this );
		tracesView.getFilterContainerTextField( ).setText( "SE-Nils-Ehmke" );
		tracesView.getFilterContainerTextField( ).pushEnter( );
		tracesView.getFilterComponentTextField( ).setText( "kieker.examples.bookstore.Bookstore" );
		tracesView.getFilterComponentTextField( ).pushEnter( );
		tracesView.getFilterOperationTextField( ).setText( "public void kieker.examples.bookstore.Bookstore.searchBook()" );
		tracesView.getFilterOperationTextField( ).pushEnter( );
		tracesView.getFilterTraceIDTextField( ).setText( "4658150164341456896" );
		tracesView.getFilterTraceIDTextField( ).pushEnter( );

		assertTrue( tracesView.getCounterTextField( ).getText( ).startsWith( "1 " ) );
	}

}
