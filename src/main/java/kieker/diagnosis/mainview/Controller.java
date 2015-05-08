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

package kieker.diagnosis.mainview;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.mainview.dialog.settings.SettingsDialogViewController;
import kieker.diagnosis.mainview.subview.aggregatedcalls.AggregatedCallsViewController;
import kieker.diagnosis.mainview.subview.aggregatedtraces.AggregatedTracesViewController;
import kieker.diagnosis.mainview.subview.calls.CallsViewController;
import kieker.diagnosis.mainview.subview.monitoringstatistics.MonitoringStatisticsViewController;
import kieker.diagnosis.mainview.subview.traces.TracesViewController;
import kieker.diagnosis.model.DataModel;

/**
 * The main controller of this application. It is responsible for controlling the application's main window.
 *
 * @author Nils Christian Ehmke
 */
public final class Controller {

	private static final String KEY_LAST_IMPORT_PATH = "lastimportpath";

	private static final Logger LOGGER = Logger.getAnonymousLogger();

	private final DataModel dataModel = DataModel.getInstance();

	@FXML private Node view;
	@FXML private Pane content;

	@FXML private Button traces;
	@FXML private Button aggregatedtraces;
	@FXML private Button calls;
	@FXML private Button aggregatedcalls;
	@FXML private Button statistics;

	private Optional<Button> disabledButton = Optional.empty();

	public void showTraces() throws IOException {
		this.toggleDisabledButton(this.traces);
		this.loadPane(TracesViewController.class);
	}

	public void showAggregatedTraces() throws IOException {
		this.toggleDisabledButton(this.aggregatedtraces);
		this.loadPane(AggregatedTracesViewController.class);
	}

	public void showCalls() throws IOException {
		this.toggleDisabledButton(this.calls);
		this.loadPane(CallsViewController.class);
	}

	public void showAggregatedCalls() throws IOException {
		this.toggleDisabledButton(this.aggregatedcalls);
		this.loadPane(AggregatedCallsViewController.class);
	}

	public void showStatistics() throws IOException {
		this.toggleDisabledButton(this.statistics);
		this.loadPane(MonitoringStatisticsViewController.class);
	}

	public void showImportDialog() {
		final Preferences preferences = Preferences.userNodeForPackage(Controller.class);
		final File initialDirectory = new File(preferences.get(Controller.KEY_LAST_IMPORT_PATH, "."));

		final DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(initialDirectory);
		final File selectedDirectory = directoryChooser.showDialog((this.view.getScene().getWindow()));
		if (null != selectedDirectory) {
			this.view.setCursor(Cursor.WAIT);

			// this.view.getProgressMonitorDialog().open();
			this.dataModel.loadMonitoringLogFromFS(selectedDirectory);
			// this.view.getProgressMonitorDialog().close();

			this.view.setCursor(Cursor.DEFAULT);

			preferences.put(Controller.KEY_LAST_IMPORT_PATH, selectedDirectory.getAbsolutePath());
			try {
				preferences.flush();
			} catch (final BackingStoreException ex) {
				Controller.LOGGER.warning(ex.getLocalizedMessage());
			}
		}
	}

	public void showSettings() throws IOException {
		this.loadDialogPane(SettingsDialogViewController.class, "Settings");
	}

	public void close() {
		((Stage) this.view.getScene().getWindow()).close();
	}

	private void toggleDisabledButton(final Button disabledButton) {
		this.disabledButton.ifPresent(b -> b.setDisable(false));
		this.disabledButton = Optional.of(disabledButton);
		disabledButton.setDisable(true);
	}

	private void loadPane(final Class<?> controllerClass) throws IOException {
		final String baseName = controllerClass.getCanonicalName().replace("Controller", "");
		final String viewFXMLName = baseName.replace(".", "/") + ".fxml";
		final String cssName = baseName.replace(".", "/") + ".css";
		final String bundleBaseName = baseName.toLowerCase();

		final URL viewResource = Controller.class.getClassLoader().getResource(viewFXMLName);
		final ResourceBundle resourceBundle = ResourceBundle.getBundle(bundleBaseName, Locale.getDefault());
		final Node node = (Node) FXMLLoader.load(viewResource, resourceBundle);
		final URL cssResource = Controller.class.getClassLoader().getResource(cssName);

		this.content.getChildren().clear();
		this.content.getStylesheets().clear();

		this.content.getStylesheets().add(cssResource.toExternalForm());
		this.content.getChildren().setAll(node);
	}

	private void loadDialogPane(final Class<?> controllerClass, final String title) throws IOException {
		final String baseName = controllerClass.getCanonicalName().replace("Controller", "");
		final String viewFXMLName = baseName.replace(".", "/") + ".fxml";
		final String cssName = baseName.replace(".", "/") + ".css";
		final String bundleBaseName = baseName.toLowerCase();

		final URL viewResource = Controller.class.getClassLoader().getResource(viewFXMLName);
		final ResourceBundle resourceBundle = ResourceBundle.getBundle(bundleBaseName, Locale.getDefault());
		final Parent node = (Parent) FXMLLoader.load(viewResource, resourceBundle);
		final URL cssResource = Controller.class.getClassLoader().getResource(cssName);

		final Scene scene = new Scene(node);
		scene.getStylesheets().add(cssResource.toExternalForm());

		final Stage dialogStage = new Stage();
		dialogStage.setTitle(title);
		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.initOwner((this.view.getScene().getWindow()));
		dialogStage.setScene(scene);
		dialogStage.showAndWait();
	}

	public void jumpToCorrespondingTrace(final OperationCall call) {
		// this.view.getTree().select(this.view.getTrtmTraces());
		// this.model.setActiveSubView(this.subViewMapper.resolve(SubView.TRACES_SUB_VIEW));
		// this.tracesViewController.jumpToCorrespondingTrace(call);
	}

}
