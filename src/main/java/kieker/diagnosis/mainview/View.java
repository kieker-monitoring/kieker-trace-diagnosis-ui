/***************************************************************************
 * Copyright 2014 Kieker Project (http://kieker-monitoring.net)
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

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import kieker.diagnosis.common.model.PropertiesModel;
import kieker.diagnosis.dialog.SettingsDialog;
import kieker.diagnosis.subview.ISubView;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

/**
 * The main view of the application. For the most part it uses sub-views to show data.
 *
 * @author Nils Christian Ehmke
 */
public final class View implements Observer {

	private final PropertiesModel propertiesModel;
	private final Model model;
	private final Controller controller;
	private final Map<String, ISubView> subViews;
	private Shell shell;
	private Composite subViewComposite;
	private StackLayout subViewLayout;

	private MessageBox aboutDialog;
	private DirectoryDialog directoryDialog;
	private SettingsDialog settingsDialog;

	private MenuItem mntmExit;
	private MenuItem mntmOpenMonitoringLog;

	private Tree tree;
	private TreeItem trtmExplorer;
	private TreeItem trtmTraces;
	private TreeItem trtmAggregatedTraces;
	private TreeItem trtmJustFailedTraces;
	private TreeItem trtmJustTracesContaining;
	private TreeItem trtmJustFailedAggTraces;
	private TreeItem trtmJustAggTracesContaining;
	private MenuItem mntmAbout;
	private MenuItem mntmSettings;
	private TreeItem trtmAggregatedOperationCalls;
	private TreeItem trtmJustFailedAggregated;
	private TreeItem trtmOperationCalls;
	private TreeItem trtmJustFailedOperation;

	public View(final Model mainViewModel, final Controller controller, final Map<String, ISubView> subViews, final PropertiesModel propertiesModel) {
		this.model = mainViewModel;
		this.propertiesModel = propertiesModel;
		this.subViews = subViews;
		this.controller = controller;
	}

	public void show() {
		final Display display = Display.getDefault();

		this.createContents();
		this.addLogic();
		this.shell.open();
		this.shell.layout();

		while (!this.shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public void close() {
		this.shell.close();
	}

	public TreeItem getTrtmExplorer() {
		return this.trtmExplorer;
	}

	public TreeItem getTrtmTraces() {
		return this.trtmTraces;
	}

	public TreeItem getTrtmJustFailedTraces() {
		return this.trtmJustFailedTraces;
	}

	public TreeItem getTrtmAggregatedTraces() {
		return this.trtmAggregatedTraces;
	}

	public TreeItem getTrtmJustTracesContaining() {
		return this.trtmJustTracesContaining;
	}

	public TreeItem getTrtmJustFailedAggTraces() {
		return this.trtmJustFailedAggTraces;
	}

	public TreeItem getTrtmJustAggTracesContaining() {
		return this.trtmJustAggTracesContaining;
	}

	public Widget getTrtmAggregatedOperationCalls() {
		return this.trtmAggregatedOperationCalls;
	}

	public Widget getTrtmFailedAggregatedOperationCalls() {
		return this.trtmJustFailedAggregated;
	}

	public TreeItem getTrtmOperationCalls() {
		return this.trtmOperationCalls;
	}

	public TreeItem getTrtmJustFailedOperation() {
		return this.trtmJustFailedOperation;
	}

	public MenuItem getMntmExit() {
		return this.mntmExit;
	}

	public Widget getMntmAbout() {
		return this.mntmAbout;
	}

	public MenuItem getMntmSettings() {
		return this.mntmSettings;
	}

	public MenuItem getMntmOpenMonitoringLog() {
		return this.mntmOpenMonitoringLog;
	}

	public DirectoryDialog getDirectoryDialog() {
		return this.directoryDialog;
	}

	public SettingsDialog getSettingsDialog() {
		return this.settingsDialog;
	}

	public MessageBox getAboutDialog() {
		return this.aboutDialog;
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	private void createContents() {
		this.shell = new Shell();
		this.shell.setImage(null);
		this.shell.setMaximized(true);
		this.shell.setText("Kieker Trace Diagnosis");

		this.shell.setImage(new Image(this.shell.getDisplay(), ClassLoader.getSystemClassLoader().getResourceAsStream("kieker-logo.png")));
		this.shell.setLayout(new FillLayout(SWT.HORIZONTAL));

		this.directoryDialog = new DirectoryDialog(this.shell);
		this.aboutDialog = new MessageBox(this.shell, SWT.ICON_INFORMATION);
		this.settingsDialog = new SettingsDialog(this.shell, SWT.NONE, this.propertiesModel);

		this.aboutDialog.setText("About...");
		this.aboutDialog.setMessage("Kieker Trace Diagnosis - 1.0-SNAPSHOT\n\nCopyright 2015 Kieker Project (http://kieker-monitoring.net)");

		final SashForm sashForm = new SashForm(this.shell, SWT.NONE);

		this.tree = new Tree(sashForm, SWT.BORDER);

		this.trtmExplorer = new TreeItem(this.tree, SWT.NONE);
		this.trtmExplorer.setText("Explorer");

		this.trtmTraces = new TreeItem(this.trtmExplorer, SWT.NONE);
		this.trtmTraces.setText("Traces");

		this.trtmJustFailedTraces = new TreeItem(this.trtmTraces, SWT.NONE);
		this.trtmJustFailedTraces.setText("Just Failed Traces");

		this.trtmJustTracesContaining = new TreeItem(this.trtmTraces, SWT.NONE);
		this.trtmJustTracesContaining.setText("Just Traces Containing Failures");
		this.trtmTraces.setExpanded(true);

		this.trtmAggregatedTraces = new TreeItem(this.trtmExplorer, 0);
		this.trtmAggregatedTraces.setText("Aggregated Traces");

		this.trtmJustFailedAggTraces = new TreeItem(this.trtmAggregatedTraces, SWT.NONE);
		this.trtmJustFailedAggTraces.setText("Just Failed Traces");

		this.trtmJustAggTracesContaining = new TreeItem(this.trtmAggregatedTraces, SWT.NONE);
		this.trtmJustAggTracesContaining.setText("Just Traces Containing Failures");
		this.trtmAggregatedTraces.setExpanded(true);

		this.trtmOperationCalls = new TreeItem(this.trtmExplorer, SWT.NONE);
		this.trtmOperationCalls.setText("Operation Calls");

		this.trtmJustFailedOperation = new TreeItem(this.trtmOperationCalls, SWT.NONE);
		this.trtmJustFailedOperation.setText("Just Failed Operation Calls");
		this.trtmOperationCalls.setExpanded(true);

		this.trtmAggregatedOperationCalls = new TreeItem(this.trtmExplorer, SWT.NONE);
		this.trtmAggregatedOperationCalls.setText("Aggregated Operation Calls");

		this.trtmJustFailedAggregated = new TreeItem(this.trtmAggregatedOperationCalls, SWT.NONE);
		this.trtmJustFailedAggregated.setText("Just Failed Operation Calls");
		this.trtmAggregatedOperationCalls.setExpanded(true);
		this.trtmExplorer.setExpanded(true);

		this.subViewLayout = new StackLayout();
		this.subViewComposite = new Composite(sashForm, SWT.NONE);
		this.subViewComposite.setLayout(this.subViewLayout);
		sashForm.setWeights(new int[] { 1, 4 });

		for (final ISubView subview : this.subViews.values()) {
			subview.createComposite(this.subViewComposite);
		}

		final Menu menu = new Menu(this.shell, SWT.BAR);
		this.shell.setMenuBar(menu);

		final MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
		mntmFile.setText("File");

		final Menu menu_1 = new Menu(mntmFile);
		mntmFile.setMenu(menu_1);

		this.mntmOpenMonitoringLog = new MenuItem(menu_1, SWT.NONE);
		this.mntmOpenMonitoringLog.setText("Open Monitoring Log");

		new MenuItem(menu_1, SWT.SEPARATOR);

		this.mntmSettings = new MenuItem(menu_1, SWT.NONE);
		this.mntmSettings.setText("Settings");

		new MenuItem(menu_1, SWT.SEPARATOR);

		this.mntmExit = new MenuItem(menu_1, SWT.NONE);
		this.mntmExit.setText("Exit");

		final MenuItem mntmHelp = new MenuItem(menu, SWT.CASCADE);
		mntmHelp.setText("Help");

		final Menu menu_3 = new Menu(mntmHelp);
		mntmHelp.setMenu(menu_3);

		this.mntmAbout = new MenuItem(menu_3, SWT.NONE);
		this.mntmAbout.setText("About...");
	}

	private void addLogic() {
		this.model.addObserver(this);

		this.tree.addSelectionListener(this.controller);

		this.mntmExit.addSelectionListener(this.controller);
		this.mntmOpenMonitoringLog.addSelectionListener(this.controller);
		this.mntmAbout.addSelectionListener(this.controller);

		this.mntmSettings.addSelectionListener(this.controller);
	}

	@Override
	public void update(final Observable observable, final Object obj) {
		if (observable == this.model) {
			this.handleChangedSubView();
			this.handleChangedCursor();
		}
	}

	private void handleChangedSubView() {
		final String subViewKey = this.model.getCurrentActiveSubViewKey();

		final ISubView subViewToShow = this.subViews.get(subViewKey);
		final Composite compositeToShow = (subViewToShow != null) ? subViewToShow.getComposite() : null; // NOPMD (null assigment)

		this.subViewLayout.topControl = compositeToShow;
		this.subViewComposite.layout();
	}

	private void handleChangedCursor() {
		this.shell.setCursor(this.model.getCursor());
	}

}
