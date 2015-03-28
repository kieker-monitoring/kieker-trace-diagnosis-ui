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

import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.annotation.PostConstruct;

import kieker.diagnosis.common.Mapper;
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.mainview.dialog.SettingsDialog;
import kieker.diagnosis.mainview.subview.ISubView;
import kieker.diagnosis.mainview.subview.aggregatedcalls.AggregatedCallsViewController;
import kieker.diagnosis.mainview.subview.aggregatedtraces.AggregatedTracesViewController;
import kieker.diagnosis.mainview.subview.calls.CallsViewController;
import kieker.diagnosis.mainview.subview.traces.TracesViewController;
import kieker.diagnosis.model.DataModel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The main controller of this application. It is responsible for controlling the application's main window.
 * 
 * @author Nils Christian Ehmke
 */
@Component
public final class Controller implements SelectionListener {

	private static final String KEY_LAST_IMPORT_PATH = "lastimportpath";

	private static final Logger LOGGER = Logger.getGlobal();

	@Autowired
	private DataModel dataModel;

	@Autowired
	private AggregatedTracesViewController aggregatedTracesViewController;

	@Autowired
	private CallsViewController callsViewController;

	@Autowired
	private TracesViewController tracesViewController;

	@Autowired
	private AggregatedCallsViewController aggregatedCallsViewController;

	@Autowired
	private View view;

	@Autowired
	private Model model;

	private Mapper<SubView, ISubView> subViewMapper;

	@PostConstruct
	public void initialize() {
		this.subViewMapper = new Mapper<>();
		this.subViewMapper.map(SubView.AGGREGATED_TRACES_SUB_VIEW).to(this.aggregatedTracesViewController.getView());
		this.subViewMapper.map(SubView.TRACES_SUB_VIEW).to(this.tracesViewController.getView());
		this.subViewMapper.map(SubView.AGGREGATED_OPERATION_CALLS_SUB_VIEW).to(this.aggregatedCallsViewController.getView());
		this.subViewMapper.map(SubView.OPERATION_CALLS_SUB_VIEW).to(this.callsViewController.getView());
	}

	public void showView() {
		this.view.show();
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		this.handlePotentialTreeSelection(e);
		this.handlePotentialMenuSelection(e);
	}

	public void jumpToCorrespondingTrace(final OperationCall call) {
		this.view.getTree().select(this.view.getTrtmTraces());
		this.model.setActiveSubView(this.subViewMapper.resolve(SubView.TRACES_SUB_VIEW));
		this.tracesViewController.jumpToCorrespondingTrace(call);
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent e) {
		// Nothing to do here. This method is just required by the interface.
	}

	public Mapper<SubView, ISubView> getSubViews() {
		return this.subViewMapper;
	}

	private void handlePotentialMenuSelection(final SelectionEvent e) {
		if (e.widget == this.view.getMntmOpenMonitoringLog()) {
			this.openMonitoringLog();
		}
		if (e.widget == this.view.getMntmSettings()) {
			final SettingsDialog settingsDialog = this.view.getSettingsDialog();
			settingsDialog.open();
		}
		if (e.widget == this.view.getMntmExit()) {
			this.view.close();
		}
		if (e.widget == this.view.getMntmAbout()) {
			this.view.getAboutDialog().open();
		}
	}

	private void handlePotentialTreeSelection(final SelectionEvent e) { // NOPMD (this method violates some metrics. This is acceptable, as it is readable)
		if (e.item == this.view.getTrtmExplorer()) {
			this.model.setActiveSubView(this.subViewMapper.resolve(SubView.NONE));
		}
		if (e.item == this.view.getTrtmTraces()) {
			this.model.setActiveSubView(this.subViewMapper.resolve(SubView.TRACES_SUB_VIEW));
		}
		if (e.item == this.view.getTrtmAggregatedTraces()) {
			this.model.setActiveSubView(this.subViewMapper.resolve(SubView.AGGREGATED_TRACES_SUB_VIEW));
		}
		if (e.item == this.view.getTrtmAggregatedOperationCalls()) {
			this.model.setActiveSubView(this.subViewMapper.resolve(SubView.AGGREGATED_OPERATION_CALLS_SUB_VIEW));
		}
		if (e.item == this.view.getTrtmOperationCalls()) {
			this.model.setActiveSubView(this.subViewMapper.resolve(SubView.OPERATION_CALLS_SUB_VIEW));
		}
	}

	private void openMonitoringLog() {
		final Preferences preferences = Preferences.userNodeForPackage(Controller.class);
		final String filterPath = preferences.get(KEY_LAST_IMPORT_PATH, ".");

		this.view.getDirectoryDialog().setFilterPath(filterPath);
		final String selectedDirectory = this.view.getDirectoryDialog().open();

		if (null != selectedDirectory) {
			this.model.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_WAIT));

			this.view.getProgressMonitorDialog().open();
			this.dataModel.loadMonitoringLogFromFS(selectedDirectory);
			this.view.getProgressMonitorDialog().close();

			this.model.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_ARROW));

			preferences.put(KEY_LAST_IMPORT_PATH, selectedDirectory);
			try {
				preferences.flush();
			} catch (final BackingStoreException ex) {
				Controller.LOGGER.warning(ex.getLocalizedMessage());
			}
		}
	}

	public enum SubView {
		TRACES_SUB_VIEW, AGGREGATED_TRACES_SUB_VIEW, NONE, AGGREGATED_OPERATION_CALLS_SUB_VIEW, OPERATION_CALLS_SUB_VIEW,
	}

}
