/***************************************************************************
 * Copyright 2015 Kieker Project (http://kieker-monitoring.net)
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

import javafx.stage.Stage;
import kieker.diagnosis.Main;
import kieker.diagnosis.guitest.mainview.MainView;
import kieker.diagnosis.guitest.mainview.dialog.AboutDialog;
import kieker.diagnosis.guitest.mainview.dialog.SettingsDialog;
import kieker.diagnosis.guitest.mainview.subview.CallsView;
import kieker.diagnosis.guitest.mainview.subview.TracesView;
import kieker.diagnosis.model.DataModel;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

public final class GUITest extends ApplicationTest {

	@Override
	public void start(final Stage stage) throws Exception {
		final Main main = new Main();
		main.start(stage);
	}

	@Test
	public void allButtonsShouldBeAvailable() {
		final MainView mainView = new MainView(this);
		mainView.getAggregatedTracesButton().click();
		mainView.getAggregatedCallsButton().click();
		mainView.getStatisticsButton().click();
		mainView.getTracesButton().click();
		mainView.getCallsButton().click();
	}

	@Test
	public void aboutDialogShouldWork() {
		final MainView mainView = new MainView(this);
		mainView.getHelpButton().click();
		mainView.getAboutButton().click();

		final AboutDialog aboutDialog = new AboutDialog(this);
		assertTrue(aboutDialog.getDescriptionLabel().getText().contains("Kieker Trace Diagnosis"));
		assertTrue(aboutDialog.getDescriptionLabel().getText().contains("Copyright 2015 Kieker Project (http://kieker-monitoring.net)"));
		aboutDialog.getOkayButton().click();
	}

	@Test
	public void settingsDialogShouldWork() {
		final MainView mainView = new MainView(this);
		mainView.getFileButton().click();
		mainView.getSettingsButton().click();

		final SettingsDialog settingsDialog = new SettingsDialog(this);
		settingsDialog.getOkayButton().click();
	}

	@Test
	public void importOfFirstExampleMonitoringLogShouldWork() {
		// Unfortunately TestFX cannot handle the native file dialog of JavaFX. Therefore we have to use direct access as a workaround.
		DataModel.getInstance().loadMonitoringLogFromFS(new File("example/execution monitoring log"));

		final MainView mainView = new MainView(this);
		mainView.getCallsButton().click();

		final CallsView callsView = new CallsView(this);
		assertTrue(callsView.getCounterTextField().getText().contains("6540"));

		mainView.getTracesButton().click();

		final TracesView tracesView = new TracesView(this);
		assertTrue(tracesView.getCounterTextField().getText().contains("1635"));
	}

	@Test
	public void importOfSecondExampleMonitoringLogShouldWork() {
		// Unfortunately TestFX cannot handle the native file dialog of JavaFX. Therefore we have to use direct access as a workaround.
		DataModel.getInstance().loadMonitoringLogFromFS(new File("example/event monitoring log"));

		final MainView mainView = new MainView(this);
		mainView.getCallsButton().click();

		final CallsView callsView = new CallsView(this);
		assertTrue(callsView.getCounterTextField().getText().contains("396"));

		mainView.getTracesButton().click();

		final TracesView tracesView = new TracesView(this);
		assertTrue(tracesView.getCounterTextField().getText().contains("100"));
	}

}
