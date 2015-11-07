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

package kieker.diagnosis.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;
import kieker.diagnosis.controller.about.AboutDialogViewController;
import kieker.diagnosis.controller.aggregatedcalls.AggregatedCallsViewController;
import kieker.diagnosis.controller.aggregatedtraces.AggregatedTracesViewController;
import kieker.diagnosis.controller.bugreporting.BugReportingDialogViewController;
import kieker.diagnosis.controller.calls.CallsViewController;
import kieker.diagnosis.controller.monitoringstatistics.MonitoringStatisticsViewController;
import kieker.diagnosis.controller.settings.SettingsDialogViewController;
import kieker.diagnosis.controller.traces.TracesViewController;
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.util.Context;
import kieker.diagnosis.util.ContextEntry;
import kieker.diagnosis.util.ContextKey;
import kieker.diagnosis.util.ErrorHandling;

/**
 * The main controller of this application. It is responsible for controlling the application's main window.
 *
 * @author Nils Christian Ehmke
 */
public final class MainController {

	private static final String KEY_LAST_IMPORT_PATH = "lastimportpath";

	private static final Logger LOGGER = LogManager.getLogger(MainController.class);

	private static MainController INSTANCE;

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

	private MainController() {
	}

	@ErrorHandling
	public void showTraces() throws Exception {
		this.toggleDisabledButton(this.traces);
		this.activeController = Optional.of(TracesViewController.class);
		this.loadPane(TracesViewController.class);
	}

	private void showTraces(final ContextEntry... contextEntries) throws Exception {
		this.toggleDisabledButton(this.traces);
		this.activeController = Optional.of(TracesViewController.class);
		this.loadPane(TracesViewController.class, contextEntries);
	}

	@ErrorHandling
	public void showAggregatedTraces() throws Exception {
		this.toggleDisabledButton(this.aggregatedtraces);
		this.activeController = Optional.of(AggregatedTracesViewController.class);
		this.loadPane(AggregatedTracesViewController.class);
	}

	@ErrorHandling
	public void showCalls() throws Exception {
		this.toggleDisabledButton(this.calls);
		this.activeController = Optional.of(CallsViewController.class);
		this.loadPane(CallsViewController.class);
	}

	@ErrorHandling
	public void showAggregatedCalls() throws Exception {
		this.toggleDisabledButton(this.aggregatedcalls);
		this.activeController = Optional.of(AggregatedCallsViewController.class);
		this.loadPane(AggregatedCallsViewController.class);
	}

	@ErrorHandling
	public void showStatistics() throws Exception {
		this.toggleDisabledButton(this.statistics);
		this.activeController = Optional.of(MonitoringStatisticsViewController.class);
		this.loadPane(MonitoringStatisticsViewController.class);
	}

	@ErrorHandling
	public void showImportDialog() {
		final Preferences preferences = Preferences.userNodeForPackage(MainController.class);
		final File initialDirectory = new File(preferences.get(MainController.KEY_LAST_IMPORT_PATH, "."));

		final DirectoryChooser directoryChooser = new DirectoryChooser();
		if (initialDirectory.exists()) {
			directoryChooser.setInitialDirectory(initialDirectory);
		}
		final File selectedDirectory = directoryChooser.showDialog((this.view.getScene().getWindow()));
		if (null != selectedDirectory) {
			this.view.setCursor(Cursor.WAIT);
			this.dataModel.loadMonitoringLogFromFS(selectedDirectory);
			this.view.setCursor(Cursor.DEFAULT);

			preferences.put(MainController.KEY_LAST_IMPORT_PATH, selectedDirectory.getAbsolutePath());
			try {
				preferences.flush();
			} catch (final BackingStoreException ex) {
				MainController.LOGGER.error(ex);
			}
		}
	}

	@ErrorHandling
	public void showSettings() throws Exception {
		final long propertiesVersionPre = PropertiesModel.getInstance().getVersion();
		this.loadDialogPane(SettingsDialogViewController.class);

		if (this.activeController.isPresent()) {
			final long propertiesVersionPost = PropertiesModel.getInstance().getVersion();
			if (propertiesVersionPre != propertiesVersionPost) {
				this.loadPane(this.activeController.get());
			}
		}
	}

	@ErrorHandling
	public void showAbout() throws Exception {
		this.loadDialogPane(AboutDialogViewController.class);
	}

	@ErrorHandling
	public void showBugReporting() throws Exception {
		this.loadDialogPane(BugReportingDialogViewController.class);
	}

	@ErrorHandling
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

	private void loadPane(final Class<?> controllerClass, final ContextEntry... arguments) throws Exception {
		final PaneData paneData = MainController.loadPaneData(controllerClass, arguments);

		this.content.getChildren().clear();
		this.content.getStylesheets().clear();

		this.content.getStylesheets().add(paneData.getStylesheetURL());
		this.content.getChildren().setAll(paneData.getNode());
	}

	private void loadDialogPane(final Class<?> controllerClass) throws Exception {
		final PaneData paneData = MainController.loadPaneData(controllerClass);

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

	public static void loadMainPane(final Stage stage) throws Exception {
		try {
			INSTANCE = new MainController();
			final URL resource = MainController.class.getClassLoader().getResource("views/kieker/diagnosis/view/View.fxml");
			final FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setResources(ResourceBundle.getBundle("locale.kieker.diagnosis.view.view", Locale.getDefault()));
			fxmlLoader.setLocation(resource);
			fxmlLoader.setController(INSTANCE);
			final Pane pane = (Pane) fxmlLoader.load();

			final Scene root = new Scene(pane);
			stage.setScene(root);

			stage.getIcons().add(new Image("kieker-logo.png"));
			stage.setTitle("Kieker Trace Diagnosis - 1.1-SNAPSHOT");
			stage.setMaximized(true);

			MainController.showSplashScreen(root);

			stage.show();
		} catch (final IOException ex) {
			MainController.LOGGER.error(ex);
			throw ex;
		}
	}

	private static void showSplashScreen(final Scene root) {
		final ImageView imageView = new ImageView("splashscreen.png");
		final Pane parent = new Pane(imageView);
		final Scene scene = new Scene(parent);

		final Stage stage = new Stage();
		stage.setResizable(false);
		stage.initStyle(StageStyle.UNDECORATED);
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(root.getWindow());
		stage.setScene(scene);

		final FadeTransition transition = new FadeTransition(Duration.millis(3000), stage.getScene().getRoot());
		transition.setFromValue(1.0);
		transition.setToValue(0.0);
		final EventHandler<ActionEvent> handler = t -> stage.hide();
		transition.setOnFinished(handler);
		transition.play();

		stage.showAndWait();
	}

	private static PaneData loadPaneData(final Class<?> controllerClass, final ContextEntry... arguments) throws Exception {
		final long tin = System.currentTimeMillis();
		
		final String baseName = controllerClass.getCanonicalName().replace("Controller", "").replace(".controller.", ".view.");
		final String viewFXMLName = "views/" + baseName.replace(".", "/") + ".fxml";
		final String cssName = "views/" + baseName.replace(".", "/") + ".css";
		final String bundleBaseName = "locale." + baseName.toLowerCase(Locale.ROOT);

		final Constructor<?> constructor = controllerClass.getConstructor(Context.class);
		final Context context = new Context(arguments);
		final Object controller = constructor.newInstance(context);

		final URL viewResource = MainController.class.getClassLoader().getResource(viewFXMLName);
		final ResourceBundle resourceBundle = ResourceBundle.getBundle(bundleBaseName, Locale.getDefault());
		final FXMLLoader loader = new FXMLLoader();
		loader.setController(controller);
		loader.setLocation(viewResource);
		loader.setResources(resourceBundle);
		final Node node = (Node) loader.load();
		final URL cssResource = MainController.class.getClassLoader().getResource(cssName);
		final String title = (resourceBundle.containsKey("title") ? resourceBundle.getString("title") : "");

		final PaneData paneData = new PaneData(node, title, cssResource.toExternalForm());
		
		final long tout = System.currentTimeMillis();
		LOGGER.info("View for '" + controllerClass.getCanonicalName() + "' loaded in " + (tout - tin) + "ms");
		
		return paneData;
	}

	public static MainController instance() {
		return MainController.INSTANCE;
	}

	public void jumpToTrace(final OperationCall call) throws Exception {
		showTraces(new ContextEntry(ContextKey.OPERATION_CALL, call));
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
