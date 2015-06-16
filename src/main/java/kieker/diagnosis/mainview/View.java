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

import java.util.ResourceBundle;

import kieker.diagnosis.mainview.dialog.SettingsDialog;
import kieker.diagnosis.mainview.subview.ISubView;
import kieker.diagnosis.model.PropertiesModel;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The main view of the application. For the most part it uses sub-views to show
 * data.
 * 
 * @author Nils Christian Ehmke
 */
@Component
public final class View {

	private static final ResourceBundle BUNDLE = ResourceBundle
			.getBundle("kieker.diagnosis.mainview.view"); //$NON-NLS-1$

	@Autowired
	private PropertiesModel propertiesModel;

	@Autowired
	private Model model;

	@Autowired
	private Controller controller;

	private Composite subViewComposite;
	private StackLayout subViewLayout;
	private Shell shell;

	private ProgressMonitorDialog progressMonitorDialog;
	private DirectoryDialog directoryDialog;
	private SettingsDialog settingsDialog;
	private MessageBox aboutDialog;

	private MenuItem mntmExit;
	private MenuItem mntmOpenMonitoringLog;
	private MenuItem mntmAbout;
	private MenuItem mntmSettings;

	private TreeItem trtmBusinessOperations;
	private TreeItem trtmAggregatedOperationCalls;
	private TreeItem trtmAggregatedTraces;
	private TreeItem trtmOperationCalls;

	private TreeItem trtmDatabaseOperations;
	private TreeItem trtmDatabaseStatementCalls;
	private TreeItem trtmAggregatedDatabaseStatementCalls;
	private TreeItem trtmDatabasePreparedStatementCalls;

	private TreeItem trtmExplorer;
	private TreeItem trtmTraces;
	private Tree tree;
	private TreeItem trtmMonitoringLogStatistics;

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

	public TreeItem getTrtmBusinessOperations() {
		return this.trtmBusinessOperations;
	}
	
	public TreeItem getTrtmTraces() {
		return this.trtmTraces;
	}

	public TreeItem getTrtmAggregatedTraces() {
		return this.trtmAggregatedTraces;
	}

	public Widget getTrtmAggregatedOperationCalls() {
		return this.trtmAggregatedOperationCalls;
	}

	public TreeItem getTrtmOperationCalls() {
		return this.trtmOperationCalls;
	}

	public TreeItem getTrtmDatabaseOperations() {
		return this.trtmDatabaseOperations;
	}
	
	public TreeItem getTrtmDatabaseStatementCalls() {
		return this.trtmDatabaseStatementCalls;
	}

	public TreeItem getTrtmAggregatedDatabaseStatementCalls() {
		return this.trtmAggregatedDatabaseStatementCalls;
	}
		
	public TreeItem getTrtmDatabasePreparedStatementCalls() {
		return this.trtmDatabasePreparedStatementCalls;
	}

	public TreeItem getTrtmMonitoringLogStatistics() {
		return this.trtmMonitoringLogStatistics;
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

	public ProgressMonitorDialog getProgressMonitorDialog() {
		return this.progressMonitorDialog;
	}

	public Tree getTree() {
		return this.tree;
	}

	public void notifyAboutChangedCursor() {
		this.handleChangedCursor();
	}

	public void notifyAboutChangedSubView() {
		this.handleChangedSubView();
	}

	private void handleChangedSubView() {
		final ISubView subView = this.model.getActiveSubView();
		final Composite compositeToShow = (subView != null) ? subView
				.getComposite() : null; // NOPMD (null assignment)

		this.subViewLayout.topControl = compositeToShow;

		this.subViewComposite.layout();
	}

	private void handleChangedCursor() {
		this.shell.setCursor(this.model.getCursor());
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	private void createContents() {
		this.shell = new Shell();
		this.shell.setImage(null);
		this.shell.setMaximized(true);
		this.shell.setText(BUNDLE.getString("View.shell.text")); //$NON-NLS-1$ 

		this.shell
				.setImage(new Image(this.shell.getDisplay(), ClassLoader
						.getSystemClassLoader().getResourceAsStream(
								"kieker-logo.png")));
		this.shell.setLayout(new FillLayout(SWT.HORIZONTAL));

		this.directoryDialog = new DirectoryDialog(this.shell);
		this.settingsDialog = new SettingsDialog(this.shell, SWT.NONE,
				this.propertiesModel);

		this.progressMonitorDialog = new ProgressMonitorDialog(this.shell);
		this.progressMonitorDialog.setCancelable(false);

		this.aboutDialog = new MessageBox(this.shell, SWT.ICON_INFORMATION);
		this.aboutDialog.setText(BUNDLE.getString("View.mntmAbout.text"));
		this.aboutDialog
				.setMessage("Kieker Trace Diagnosis - 1.0-SNAPSHOT\n\nCopyright 2015 Kieker Project (http://kieker-monitoring.net)");

		final SashForm sashForm = new SashForm(this.shell, SWT.NONE);

		this.tree = new Tree(sashForm, SWT.BORDER);

		this.trtmExplorer = new TreeItem(this.tree, SWT.NONE);
		this.trtmExplorer.setText(BUNDLE
				.getString("View.trtmExplorer.text(java.lang.String)")); //$NON-NLS-1$ 

		this.trtmBusinessOperations = new TreeItem(this.trtmExplorer, SWT.NONE);
		this.trtmBusinessOperations.setText(BUNDLE
				.getString("View.trtmBusinessOperations.text(java.lang.String)")); //$NON-NLS-1$
		
		this.trtmTraces = new TreeItem(this.trtmBusinessOperations, SWT.NONE);
		this.trtmTraces.setText(BUNDLE
				.getString("View.trtmTraces.text(java.lang.String)")); //$NON-NLS-1$ 
		this.trtmTraces.setExpanded(true);

		this.trtmAggregatedTraces = new TreeItem(this.trtmBusinessOperations, SWT.NONE);
		this.trtmAggregatedTraces.setText(BUNDLE
				.getString("View.trtmAggregatedTraces.text(java.lang.String)")); //$NON-NLS-1$ 

		this.trtmAggregatedTraces.setExpanded(true);

		this.trtmOperationCalls = new TreeItem(this.trtmBusinessOperations, SWT.NONE);
		this.trtmOperationCalls.setText(BUNDLE
				.getString("View.trtmOperationCalls.text(java.lang.String)")); //$NON-NLS-1$ 

		this.trtmAggregatedOperationCalls = new TreeItem(this.trtmBusinessOperations,
				SWT.NONE);
		this.trtmAggregatedOperationCalls
				.setText(BUNDLE
						.getString("View.trtmAggregatedOperationCalls.text(java.lang.String)")); //$NON-NLS-1$ 

		this.trtmAggregatedOperationCalls.setExpanded(true);

		this.trtmDatabaseOperations = new TreeItem(this.trtmExplorer, SWT.NONE);
		this.trtmDatabaseOperations.setText(BUNDLE
				.getString("View.trtmDatabaseOperations.text(java.lang.String)")); //$NON-NLS-1$
		
		this.trtmDatabaseStatementCalls = new TreeItem(this.trtmDatabaseOperations,
				SWT.NONE);
		this.trtmDatabaseStatementCalls
				.setText(BUNDLE
						.getString("View.trtmDatabaseStatementCalls.text(java.lang.String)")); //$NON-NLS-1$ 

		this.trtmAggregatedDatabaseStatementCalls = new TreeItem(this.trtmDatabaseOperations,
				SWT.NONE);
		this.trtmAggregatedDatabaseStatementCalls
				.setText(BUNDLE
						.getString("View.trtmAggregatedDatabaseStatementCalls.text(java.lang.String)")); //$NON-NLS-1$ 
		
		this.trtmDatabasePreparedStatementCalls = new TreeItem(
				this.trtmDatabaseOperations, SWT.NONE);
		this.trtmDatabasePreparedStatementCalls
				.setText(BUNDLE
						.getString("View.trtmDatabasePreparedStatementCalls.text(java.lang.String)")); //$NON-NLS-1$

		this.trtmMonitoringLogStatistics = new TreeItem(this.trtmExplorer,
				SWT.NONE);
		this.trtmMonitoringLogStatistics
				.setText(BUNDLE
						.getString("View.trtmMonitoringLogStatistics.text(java.lang.String)")); //$NON-NLS-1$
		
		this.trtmExplorer.setExpanded(true);
		this.trtmBusinessOperations.setExpanded(true);
		this.trtmDatabaseOperations.setExpanded(true);

		this.subViewLayout = new StackLayout();
		this.subViewComposite = new Composite(sashForm, SWT.NONE);
		this.subViewComposite.setLayout(this.subViewLayout);
		sashForm.setWeights(new int[] { 1, 4 });

		for (final ISubView subview : this.controller.getSubViews().values()) {
			subview.createComposite(this.subViewComposite);
		}

		final Menu menu = new Menu(this.shell, SWT.BAR);
		this.shell.setMenuBar(menu);

		final MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
		mntmFile.setText(BUNDLE.getString("View.mntmFile.text")); //$NON-NLS-1$ 

		final Menu menu_1 = new Menu(mntmFile);
		mntmFile.setMenu(menu_1);

		this.mntmOpenMonitoringLog = new MenuItem(menu_1, SWT.NONE);
		this.mntmOpenMonitoringLog.setText(BUNDLE
				.getString("View.mntmOpenMonitoringLog.text")); //$NON-NLS-1$ 

		new MenuItem(menu_1, SWT.SEPARATOR);

		this.mntmSettings = new MenuItem(menu_1, SWT.NONE);
		this.mntmSettings.setText(BUNDLE.getString("View.mntmSettings.text")); //$NON-NLS-1$ 

		new MenuItem(menu_1, SWT.SEPARATOR);

		this.mntmExit = new MenuItem(menu_1, SWT.NONE);
		this.mntmExit.setText(BUNDLE.getString("View.mntmExit.text")); //$NON-NLS-1$ 

		final MenuItem mntmHelp = new MenuItem(menu, SWT.CASCADE);
		mntmHelp.setText(BUNDLE.getString("View.mntmHelp.text")); //$NON-NLS-1$ 

		final Menu menu_3 = new Menu(mntmHelp);
		mntmHelp.setMenu(menu_3);

		this.mntmAbout = new MenuItem(menu_3, SWT.NONE);
		this.mntmAbout.setText(BUNDLE.getString("View.mntmAbout.text")); //$NON-NLS-1$ 
	}

	private void addLogic() {
		this.tree.addSelectionListener(this.controller);

		this.mntmExit.addSelectionListener(this.controller);
		this.mntmOpenMonitoringLog.addSelectionListener(this.controller);
		this.mntmAbout.addSelectionListener(this.controller);
		this.mntmSettings.addSelectionListener(this.controller);
	}

}
