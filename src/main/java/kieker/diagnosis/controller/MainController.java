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
import javafx.stage.FileChooser;
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
import kieker.diagnosis.domain.AggregatedOperationCall;
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.util.CSVData;
import kieker.diagnosis.util.CSVDataCollector;
import kieker.diagnosis.util.CSVExporter;
import kieker.diagnosis.util.Context;
import kieker.diagnosis.util.ContextEntry;
import kieker.diagnosis.util.ContextKey;
import kieker.diagnosis.util.ErrorHandling;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The main controller of this application. It is responsible for controlling
 * the application's main window.
 * 
 * @author Nils Christian Ehmke
 */
public final class MainController {

	private static final String KIEKER_LOGO_PNG = "kieker-logo.png";

	private static final String KEY_LAST_IMPORT_PATH = "lastimportpath";
	private static final String KEY_LAST_EXPORT_PATH = "lastexportpath";

	private static final Logger LOGGER = LogManager.getLogger(MainController.class);

	private static MainController cvInstance;

	private final DataModel ivDataModel = DataModel.getInstance();

	@FXML
	private Node ivView;
	@FXML
	private Pane ivContent;

	@FXML
	private Button ivTraces;
	@FXML
	private Button ivAggregatedtraces;
	@FXML
	private Button ivCalls;
	@FXML
	private Button ivAggregatedcalls;
	@FXML
	private Button ivStatistics;

	private Optional<Button> ivDisabledButton = Optional.empty();
	private Optional<Class<?>> ivActiveController = Optional.empty();

	private MainController() {
	}

	@ErrorHandling
	public void showTraces() throws Exception {
		this.showTraces(new ContextEntry[0]);
	}

	private void showTraces(final ContextEntry... aContextEntries) throws Exception {
		this.toggleDisabledButton(this.ivTraces);
		this.ivActiveController = Optional.of(TracesViewController.class);
		this.loadPane(TracesViewController.class, aContextEntries);
	}

	@ErrorHandling
	public void showAggregatedTraces() throws Exception {
		this.toggleDisabledButton(this.ivAggregatedtraces);
		this.ivActiveController = Optional.of(AggregatedTracesViewController.class);
		this.loadPane(AggregatedTracesViewController.class);
	}

	@ErrorHandling
	public void showCalls() throws Exception {
		this.showCalls(new ContextEntry[0]);
	}

	@ErrorHandling
	public void showCalls(final ContextEntry... aContextEntries) throws Exception {
		this.toggleDisabledButton(this.ivCalls);
		this.ivActiveController = Optional.of(CallsViewController.class);
		this.loadPane(CallsViewController.class, aContextEntries);
	}

	@ErrorHandling
	public void showAggregatedCalls() throws Exception {
		this.toggleDisabledButton(this.ivAggregatedcalls);
		this.ivActiveController = Optional.of(AggregatedCallsViewController.class);
		this.loadPane(AggregatedCallsViewController.class);
	}

	@ErrorHandling
	public void showStatistics() throws Exception {
		this.toggleDisabledButton(this.ivStatistics);
		this.ivActiveController = Optional.of(MonitoringStatisticsViewController.class);
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
		final File selectedDirectory = directoryChooser.showDialog((this.ivView.getScene().getWindow()));
		if (null != selectedDirectory) {
			this.ivView.setCursor(Cursor.WAIT);
			this.ivDataModel.loadMonitoringLogFromFS(selectedDirectory);
			this.ivView.setCursor(Cursor.DEFAULT);

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

		if (this.ivActiveController.isPresent()) {
			final long propertiesVersionPost = PropertiesModel.getInstance().getVersion();
			if (propertiesVersionPre != propertiesVersionPost) {
				this.loadPane(this.ivActiveController.get());
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
		final Window window = this.ivView.getScene().getWindow();
		if (window instanceof Stage) {
			((Stage) window).close();
		}
	}

	private void toggleDisabledButton(final Button aDisabledButton) {
		this.ivDisabledButton.ifPresent(b -> b.setDisable(false));
		this.ivDisabledButton = Optional.of(aDisabledButton);
		aDisabledButton.setDisable(true);
	}

	private void loadPane(final Class<?> aControllerClass, final ContextEntry... aArguments) throws Exception {
		final PaneData paneData = MainController.loadPaneData(aControllerClass, aArguments);

		this.ivContent.getChildren().clear();
		this.ivContent.getStylesheets().clear();

		this.ivContent.getStylesheets().add(paneData.getStylesheetURL());
		this.ivContent.getChildren().setAll(paneData.getNode());
	}

	private void loadDialogPane(final Class<?> aControllerClass) throws Exception {
		final PaneData paneData = MainController.loadPaneData(aControllerClass);

		final Scene scene = new Scene((Parent) paneData.getNode());
		scene.getStylesheets().add(paneData.ivStylesheetURL);

		final Stage dialogStage = new Stage();
		dialogStage.getIcons().add(new Image(KIEKER_LOGO_PNG));
		dialogStage.setTitle(paneData.getTitle());
		dialogStage.setResizable(false);
		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.initOwner((this.ivView.getScene().getWindow()));
		dialogStage.setScene(scene);
		dialogStage.showAndWait();

	}

	public static void loadMainPane(final Stage aStage) throws Exception {
		try {
			MainController.cvInstance = new MainController();
			final URL resource = MainController.class.getClassLoader()
					.getResource("views/kieker/diagnosis/view/View.fxml");
			final FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setResources(ResourceBundle.getBundle("locale.kieker.diagnosis.view.view", Locale.getDefault()));
			fxmlLoader.setLocation(resource);
			fxmlLoader.setController(MainController.cvInstance);
			final Pane pane = (Pane) fxmlLoader.load();

			final Scene root = new Scene(pane);
			aStage.setScene(root);

			aStage.getIcons().add(new Image(KIEKER_LOGO_PNG));
			aStage.setTitle("Kieker Trace Diagnosis - 1.2.0-SNAPSHOT");
			aStage.setMaximized(true);

			MainController.showSplashScreen(root);

			aStage.show();
		} catch (final IOException ex) {
			MainController.LOGGER.error(ex);
			throw ex;
		}
	}

	private static void showSplashScreen(final Scene aRoot) {
		final ImageView imageView = new ImageView("splashscreen.png");
		final Pane parent = new Pane(imageView);
		final Scene scene = new Scene(parent);

		final Stage stage = new Stage();
		stage.setResizable(false);
		stage.initStyle(StageStyle.UNDECORATED);
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(aRoot.getWindow());
		stage.setScene(scene);

		final FadeTransition transition = new FadeTransition(Duration.millis(3000), stage.getScene().getRoot());
		transition.setFromValue(1.0);
		transition.setToValue(0.0);
		final EventHandler<ActionEvent> handler = t -> stage.hide();
		transition.setOnFinished(handler);
		transition.play();

		stage.showAndWait();
	}

	private static PaneData loadPaneData(final Class<?> aControllerClass, final ContextEntry... aArguments)
			throws Exception {
		final long tin = System.currentTimeMillis();

		final String baseName = aControllerClass.getCanonicalName().replace("Controller", "").replace(".controller.",
				".view.");
		final String viewFXMLName = "views/" + baseName.replace(".", "/") + ".fxml";
		final String cssName = "views/" + baseName.replace(".", "/") + ".css";
		final String bundleBaseName = "locale." + baseName.toLowerCase(Locale.ROOT);

		final Constructor<?> constructor = aControllerClass.getConstructor(Context.class);
		final Context context = new Context(aArguments);
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
		MainController.LOGGER
				.info("View for '" + aControllerClass.getCanonicalName() + "' loaded in " + (tout - tin) + "ms");

		return paneData;
	}

	public static MainController instance() {
		return MainController.cvInstance;
	}

	public void jumpToTrace(final OperationCall aCall) throws Exception {
		this.showTraces(new ContextEntry(ContextKey.OPERATION_CALL, aCall));
	}

	public void jumpToCalls(final AggregatedOperationCall aCall) throws Exception {
		this.showCalls(new ContextEntry(ContextKey.AGGREGATED_OPERATION_CALL, aCall));
	}

	public void exportToCSV(final CSVDataCollector aDataCollector) throws IOException {
		final Preferences preferences = Preferences.userNodeForPackage(MainController.class);
		final File initialDirectory = new File(preferences.get(MainController.KEY_LAST_EXPORT_PATH, "."));

		final FileChooser fileChooser = new FileChooser();
		if (initialDirectory.exists()) {
			fileChooser.setInitialDirectory(initialDirectory);
		}

		final File selectedFile = fileChooser.showSaveDialog((this.ivView.getScene().getWindow()));
		if (null != selectedFile) {
			final CSVData data = aDataCollector.collectData();
			CSVExporter.exportToCSV(data, selectedFile);
			
			preferences.put(MainController.KEY_LAST_EXPORT_PATH, selectedFile.getParent());
			try {
				preferences.flush();
			} catch (final BackingStoreException ex) {
				MainController.LOGGER.error(ex);
			}
		}
	}

	private static class PaneData {

		private final Node ivNode;
		private final String ivTitle;
		private final String ivStylesheetURL;

		public PaneData(final Node aNode, final String aTitle, final String aStylesheetURL) {
			this.ivNode = aNode;
			this.ivTitle = aTitle;
			this.ivStylesheetURL = aStylesheetURL;
		}

		public Node getNode() {
			return this.ivNode;
		}

		public String getTitle() {
			return this.ivTitle;
		}

		public String getStylesheetURL() {
			return this.ivStylesheetURL;
		}

	}

}
