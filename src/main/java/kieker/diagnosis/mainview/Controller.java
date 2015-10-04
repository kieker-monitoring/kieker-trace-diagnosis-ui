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
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import kieker.diagnosis.mainview.dialog.about.AboutDialogViewController;
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

	private static final Logger LOGGER = LogManager.getLogger(Controller.class);

	private final DataModel dataModel = DataModel.getInstance();

	@FXML private Node view;
	@FXML private Pane content;

	@FXML private Button traces;
	@FXML private Button aggregatedtraces;
	@FXML private Button calls;
	@FXML private Button aggregatedcalls;
	@FXML private Button statistics;

	private Optional<Button> disabledButton = Optional.empty();
	private Optional<Class<?>> activeController = Optional.empty();

	public void showTraces() throws IOException {
		this.toggleDisabledButton(this.traces);
		this.activeController = Optional.of(TracesViewController.class);
		this.loadPane(TracesViewController.class);
	}

	public void showAggregatedTraces() throws IOException {
		this.toggleDisabledButton(this.aggregatedtraces);
		this.activeController = Optional.of(AggregatedTracesViewController.class);
		this.loadPane(AggregatedTracesViewController.class);
	}

	public void showCalls() throws IOException {
		this.toggleDisabledButton(this.calls);
		this.activeController = Optional.of(CallsViewController.class);
		this.loadPane(CallsViewController.class);
	}

	public void showAggregatedCalls() throws IOException {
		this.toggleDisabledButton(this.aggregatedcalls);
		this.activeController = Optional.of(AggregatedCallsViewController.class);
		this.loadPane(AggregatedCallsViewController.class);
	}

	public void showStatistics() throws IOException {
		this.toggleDisabledButton(this.statistics);
		this.activeController = Optional.of(MonitoringStatisticsViewController.class);
		this.loadPane(MonitoringStatisticsViewController.class);
	}

	public void showImportDialog() {
		final Preferences preferences = Preferences.userNodeForPackage(Controller.class);
		final File initialDirectory = new File(preferences.get(Controller.KEY_LAST_IMPORT_PATH, "."));

		final DirectoryChooser directoryChooser = new DirectoryChooser();
		if (initialDirectory.exists()) {
			directoryChooser.setInitialDirectory(initialDirectory);
		}
		final File selectedDirectory = directoryChooser.showDialog((this.view.getScene().getWindow()));
		if (null != selectedDirectory) {
			this.view.setCursor(Cursor.WAIT);
			this.dataModel.loadMonitoringLogFromFS(selectedDirectory);
			this.view.setCursor(Cursor.DEFAULT);

			preferences.put(Controller.KEY_LAST_IMPORT_PATH, selectedDirectory.getAbsolutePath());
			try {
				preferences.flush();
			} catch (final BackingStoreException ex) {
				Controller.LOGGER.error(ex);
			}
		}
	}

	public void showSettings() throws IOException {
		this.loadDialogPane(SettingsDialogViewController.class);

		if (this.activeController.isPresent()) {
			this.loadPane(this.activeController.get());
		}
	}

	public void showAbout() throws IOException {
		this.loadDialogPane(AboutDialogViewController.class);
	}

	public void close() {
		final Window window = this.view.getScene().getWindow();
		if (window instanceof Stage) {
			((Stage) window).close();
		}
	}

	private void toggleDisabledButton(final Button disabledButton) {
		this.disabledButton.ifPresent(b -> b.setDisable(false));
		this.disabledButton = Optional.of(disabledButton);
		disabledButton.setDisable(true);
	}

	private void loadPane(final Class<?> controllerClass) throws IOException {
		final PaneData paneData = Controller.loadPaneData(controllerClass);

		this.content.getChildren().clear();
		this.content.getStylesheets().clear();

		this.content.getStylesheets().add(paneData.getStylesheetURL());
		this.content.getChildren().setAll(paneData.getNode());
	}

	private void loadDialogPane(final Class<?> controllerClass) throws IOException {
		final PaneData paneData = Controller.loadPaneData(controllerClass);

		final Scene scene = new Scene((Parent) paneData.getNode());
		scene.getStylesheets().add(paneData.stylesheetURL);

		final Stage dialogStage = new Stage();
		dialogStage.getIcons().add(new Image("kieker-logo.png"));
		dialogStage.setTitle(paneData.getTitle());
		dialogStage.setResizable(false);
		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.initOwner((this.view.getScene().getWindow()));
		dialogStage.setScene(scene);
		dialogStage.showAndWait();
	}

	public static void loadMainPane(final Stage stage) throws IOException {
		final URL resource = Controller.class.getClassLoader().getResource("views/kieker/diagnosis/mainview/View.fxml");
		final Pane pane = (Pane) FXMLLoader.load(resource, ResourceBundle.getBundle("locale.kieker.diagnosis.mainview.view", Locale.getDefault()));
		final Scene root = new Scene(pane);
		stage.setScene(root);

		stage.getIcons().add(new Image("kieker-logo.png"));
		stage.setTitle("Kieker Trace Diagnosis - 1.1-SNAPSHOT");
		stage.setMaximized(true);
		stage.show();
	}

	private static PaneData loadPaneData(final Class<?> controllerClass) throws IOException {
		final String baseName = controllerClass.getCanonicalName().replace("Controller", "");
		final String viewFXMLName = "views/" + baseName.replace(".", "/") + ".fxml";
		final String cssName = "views/" + baseName.replace(".", "/") + ".css";
		final String bundleBaseName = "locale." + baseName.toLowerCase(Locale.ROOT);

		final URL viewResource = Controller.class.getClassLoader().getResource(viewFXMLName);
		final ResourceBundle resourceBundle = ResourceBundle.getBundle(bundleBaseName, Locale.getDefault());
		final Node node = (Node) FXMLLoader.load(viewResource, resourceBundle);
		final URL cssResource = Controller.class.getClassLoader().getResource(cssName);
		final String title = (resourceBundle.containsKey("title") ? resourceBundle.getString("title") : "");

		final PaneData paneData = new PaneData(node, title, cssResource.toExternalForm());
		return paneData;
	}

	private static class PaneData {

		private final Node node;
		private final String title;
		private final String stylesheetURL;

		public PaneData(final Node node, final String title, final String stylesheetURL) {
			this.node = node;
			this.title = title;
			this.stylesheetURL = stylesheetURL;
		}

		public Node getNode() {
			return this.node;
		}

		public String getTitle() {
			return this.title;
		}

		public String getStylesheetURL() {
			return this.stylesheetURL;
		}

	}

}
