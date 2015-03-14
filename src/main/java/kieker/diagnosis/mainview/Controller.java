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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import kieker.diagnosis.mainview.dialog.SettingsDialog;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.subview.Filter;
import kieker.diagnosis.subview.ISubController;
import kieker.diagnosis.subview.ISubView;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;

/**
 * The main controller of this application. It is responsible for creating top level models, further sub-controllers, and for creating and controlling the
 * application's main view. The sub-views and their corresponding models are created by the sub-controllers.
 *
 * @author Nils Christian Ehmke
 */
public final class Controller implements SelectionListener {

	private static final Logger LOGGER = Logger.getGlobal();

	private final View mainView;
	private final DataModel dataModel;
	private final Model mainViewModel;

	public Controller() {
		// Create the top models
		this.dataModel = new DataModel();
		final PropertiesModel propertiesModel = new PropertiesModel();

		// Create the sub-controllers
		final ISubController subViewController1 = new kieker.diagnosis.subview.aggregatedtraces.Controller(Filter.NONE, this.dataModel, propertiesModel);
		final ISubController subViewController2 = new kieker.diagnosis.subview.traces.Controller(Filter.JUST_FAILED, this.dataModel, propertiesModel);
		final ISubController subViewController3 = new kieker.diagnosis.subview.traces.Controller(Filter.NONE, this.dataModel, propertiesModel);
		final ISubController subViewController4 = new kieker.diagnosis.subview.traces.Controller(Filter.JUST_FAILURE_CONTAINING, this.dataModel, propertiesModel);
		final ISubController subViewController5 = new kieker.diagnosis.subview.aggregatedtraces.Controller(Filter.JUST_FAILED, this.dataModel, propertiesModel);
		final ISubController subViewController6 = new kieker.diagnosis.subview.aggregatedtraces.Controller(Filter.JUST_FAILURE_CONTAINING, this.dataModel, propertiesModel);
		final ISubController subViewController7 = new kieker.diagnosis.subview.aggregatedcalls.Controller(Filter.NONE, this.dataModel, propertiesModel);
		final ISubController subViewController8 = new kieker.diagnosis.subview.aggregatedcalls.Controller(Filter.JUST_FAILED, this.dataModel, propertiesModel);
		final ISubController subViewController9 = new kieker.diagnosis.subview.calls.Controller(Filter.NONE, this.dataModel, propertiesModel);
		final ISubController subViewController10 = new kieker.diagnosis.subview.calls.Controller(Filter.JUST_FAILED, this.dataModel, propertiesModel);

		// Get the sub-views from the controllers
		final Map<String, ISubView> subViews = new HashMap<>();
		subViews.put(SubView.AGGREGATED_TRACES_SUB_VIEW.name(), subViewController1.getView());
		subViews.put(SubView.FAILED_TRACES_SUB_VIEW.name(), subViewController2.getView());
		subViews.put(SubView.TRACES_SUB_VIEW.name(), subViewController3.getView());
		subViews.put(SubView.FAILURE_CONTAINING_TRACES_SUB_VIEW.name(), subViewController4.getView());
		subViews.put(SubView.FAILED_AGGREGATED_TRACES_SUB_VIEW.name(), subViewController5.getView());
		subViews.put(SubView.FAILURE_CONTAINING_AGGREGATED_TRACES_SUB_VIEW.name(), subViewController6.getView());
		subViews.put(SubView.AGGREGATED_OPERATION_CALLS_SUB_VIEW.name(), subViewController7.getView());
		subViews.put(SubView.FAILED_AGGREGATED_OPERATION_CALLS_SUB_VIEW.name(), subViewController8.getView());
		subViews.put(SubView.OPERATION_CALLS_SUB_VIEW.name(), subViewController9.getView());
		subViews.put(SubView.FAILED_OPERATION_CALLS_SUB_VIEW.name(), subViewController10.getView());

		// Create the main model and the main view
		this.mainViewModel = new Model();
		this.mainView = new View(this.mainViewModel, this, subViews, propertiesModel);
	}

	public void showView() {
		this.mainView.show();
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		this.handlePotentialTreeSelection(e);
		this.handlePotentialMenuSelection(e);
	}

	private void handlePotentialMenuSelection(final SelectionEvent e) {
		if (e.widget == this.mainView.getMntmOpenMonitoringLog()) {
			final Preferences preferences = Preferences.userNodeForPackage(Controller.class);
			final String filterPath = preferences.get("lastimportpath", ".");

			this.mainView.getDirectoryDialog().setFilterPath(filterPath);
			final String selectedDirectory = this.mainView.getDirectoryDialog().open();

			if (null != selectedDirectory) {
				this.mainViewModel.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_WAIT));
				this.dataModel.loadMonitoringLogFromFS(selectedDirectory);
				this.mainViewModel.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_ARROW));

				preferences.put("lastimportpath", selectedDirectory);
				try {
					preferences.flush();
				} catch (final BackingStoreException ex) {
					Controller.LOGGER.warning(ex.getLocalizedMessage());
				}
			}
		}

		if (e.widget == this.mainView.getMntmSettings()) {
			final SettingsDialog settingsDialog = this.mainView.getSettingsDialog();
			settingsDialog.open();
		}

		if (e.widget == this.mainView.getMntmExit()) {
			this.mainView.close();
		}

		if (e.widget == this.mainView.getMntmAbout()) {
			this.mainView.getAboutDialog().open();
		}
	}

	private void handlePotentialTreeSelection(final SelectionEvent e) { // NOPMD (this method violates some metrics. This is acceptable, as it is readable)
		if (e.item == this.mainView.getTrtmExplorer()) {
			this.mainViewModel.setCurrentActiveSubView(SubView.NONE.name());
		}
		if (e.item == this.mainView.getTrtmTraces()) {
			this.mainViewModel.setCurrentActiveSubView(SubView.TRACES_SUB_VIEW.name());
		}
		if (e.item == this.mainView.getTrtmAggregatedTraces()) {
			this.mainViewModel.setCurrentActiveSubView(SubView.AGGREGATED_TRACES_SUB_VIEW.name());
		}
		if (e.item == this.mainView.getTrtmJustFailedTraces()) {
			this.mainViewModel.setCurrentActiveSubView(SubView.FAILED_TRACES_SUB_VIEW.name());
		}
		if (e.item == this.mainView.getTrtmJustTracesContaining()) {
			this.mainViewModel.setCurrentActiveSubView(SubView.FAILURE_CONTAINING_TRACES_SUB_VIEW.name());
		}
		if (e.item == this.mainView.getTrtmJustFailedAggTraces()) {
			this.mainViewModel.setCurrentActiveSubView(SubView.FAILED_AGGREGATED_TRACES_SUB_VIEW.name());
		}
		if (e.item == this.mainView.getTrtmJustAggTracesContaining()) {
			this.mainViewModel.setCurrentActiveSubView(SubView.FAILURE_CONTAINING_AGGREGATED_TRACES_SUB_VIEW.name());
		}
		if (e.item == this.mainView.getTrtmAggregatedOperationCalls()) {
			this.mainViewModel.setCurrentActiveSubView(SubView.AGGREGATED_OPERATION_CALLS_SUB_VIEW.name());
		}
		if (e.item == this.mainView.getTrtmFailedAggregatedOperationCalls()) {
			this.mainViewModel.setCurrentActiveSubView(SubView.FAILED_AGGREGATED_OPERATION_CALLS_SUB_VIEW.name());
		}
		if (e.item == this.mainView.getTrtmOperationCalls()) {
			this.mainViewModel.setCurrentActiveSubView(SubView.OPERATION_CALLS_SUB_VIEW.name());
		}
		if (e.item == this.mainView.getTrtmJustFailedOperation()) {
			this.mainViewModel.setCurrentActiveSubView(SubView.FAILED_OPERATION_CALLS_SUB_VIEW.name());
		}
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent e) {
		// Nothing to do here. This method is just required by the interface.
	}

	public enum SubView {
		TRACES_SUB_VIEW, FAILED_TRACES_SUB_VIEW, AGGREGATED_TRACES_SUB_VIEW, NONE, FAILURE_CONTAINING_TRACES_SUB_VIEW, FAILED_AGGREGATED_TRACES_SUB_VIEW,
		FAILURE_CONTAINING_AGGREGATED_TRACES_SUB_VIEW, AGGREGATED_OPERATION_CALLS_SUB_VIEW, FAILED_AGGREGATED_OPERATION_CALLS_SUB_VIEW, OPERATION_CALLS_SUB_VIEW,
		FAILED_OPERATION_CALLS_SUB_VIEW,
	}

}
