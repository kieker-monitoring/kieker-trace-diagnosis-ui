package kieker.gui.view;

import java.util.Observable;
import java.util.Observer;

import kieker.gui.controller.MainViewController;
import kieker.gui.model.DataModel;
import kieker.gui.model.MainViewModel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class MainView implements Observer {

	private final DataModel dataModel;
	private final MainViewModel mainViewModel;
	private final ISubView recordsSubView;
	private final ISubView tracesSubView;
	private final ISubView failedTracesSubView;
	private final ISubView aggregatedTracesSubView;
	private final ISubView failureContainingTracesSubView;
	private final MainViewController controller;

	private Shell shell;
	private Composite subViewComposite;
	private StackLayout subViewLayout;
	private Tree tree;
	private TreeItem trtmExplorer;
	private TreeItem trtmRecords;
	private TreeItem trtmTraces;
	private MenuItem mntmExit;
	private MenuItem mntmShortOperationNames;
	private MenuItem mntmLongOperationNames;
	private MenuItem mntmShortComponentNames;
	private MenuItem mntmLongComponentNames;
	private MenuItem mntmOpenMonitoringLog;
	private DirectoryDialog dialog;
	private TreeItem trtmAggregatedTraces;
	private TreeItem trtmJustFailedTraces;

	private TreeItem trtmJustTracesContaining;

	public MainView(final DataModel dataModel, final MainViewModel mainViewModel, final MainViewController controller, final ISubView recordsSubView, final ISubView tracesSubView,
			final ISubView failedTracesSubView, final ISubView aggregatedTracesSubView, final ISubView failureContainingTracesSubView) {
		this.dataModel = dataModel;
		this.mainViewModel = mainViewModel;
		this.recordsSubView = recordsSubView;
		this.tracesSubView = tracesSubView;
		this.failureContainingTracesSubView = failureContainingTracesSubView;
		this.failedTracesSubView = failedTracesSubView;
		this.aggregatedTracesSubView = aggregatedTracesSubView;

		this.controller = controller;
	}

	public void show() {
		final Display display = Display.getDefault();

		this.createContents();
		this.addLogic();
		this.shell.open();
		this.shell.layout();

		this.dataModel.loadMonitoringLogFromFS("kieker-20141209-135930886-UTC-SE-Nils-Ehmke-KIEKER");

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

	public TreeItem getTrtmRecords() {
		return this.trtmRecords;
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

	public MenuItem getMntmExit() {
		return this.mntmExit;
	}

	public MenuItem getMntmShortOperationNames() {
		return this.mntmShortOperationNames;
	}

	public MenuItem getMntmLongOperationNames() {
		return this.mntmLongOperationNames;
	}

	public MenuItem getMntmShortComponentNames() {
		return this.mntmShortComponentNames;
	}

	public MenuItem getMntmLongComponentNames() {
		return this.mntmLongComponentNames;
	}

	public MenuItem getMntmOpenMonitoringLog() {
		return this.mntmOpenMonitoringLog;
	}

	public DirectoryDialog getDialog() {
		return this.dialog;
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		this.shell = new Shell();
		this.shell.setImage(null);
		this.shell.setMaximized(true);
		this.shell.setText("Kieker's GUI");
		this.shell.setLayout(new FillLayout(SWT.HORIZONTAL));

		this.dialog = new DirectoryDialog(this.shell);

		final SashForm sashForm = new SashForm(this.shell, SWT.NONE);

		this.tree = new Tree(sashForm, SWT.BORDER);

		this.trtmExplorer = new TreeItem(this.tree, SWT.NONE);
		this.trtmExplorer.setText("Explorer");

		this.trtmRecords = new TreeItem(this.trtmExplorer, SWT.NONE);
		this.trtmRecords.setText("Records");

		this.trtmTraces = new TreeItem(this.trtmExplorer, SWT.NONE);
		this.trtmTraces.setText("Traces");

		this.trtmJustFailedTraces = new TreeItem(this.trtmTraces, SWT.NONE);
		this.trtmJustFailedTraces.setText("Just Failed Traces");

		this.trtmJustTracesContaining = new TreeItem(this.trtmTraces, SWT.NONE);
		this.trtmJustTracesContaining.setText("Just Traces Containing Failures");
		this.trtmTraces.setExpanded(true);

		this.trtmAggregatedTraces = new TreeItem(this.trtmExplorer, 0);
		this.trtmAggregatedTraces.setText("Aggregated Traces");
		this.trtmExplorer.setExpanded(true);

		this.subViewLayout = new StackLayout();
		this.subViewComposite = new Composite(sashForm, SWT.NONE);
		this.subViewComposite.setLayout(this.subViewLayout);
		sashForm.setWeights(new int[] { 1, 4 });

		this.recordsSubView.createComposite(this.subViewComposite);
		this.tracesSubView.createComposite(this.subViewComposite);
		this.failedTracesSubView.createComposite(this.subViewComposite);
		this.aggregatedTracesSubView.createComposite(this.subViewComposite);
		this.failureContainingTracesSubView.createComposite(this.subViewComposite);

		final Menu menu = new Menu(this.shell, SWT.BAR);
		this.shell.setMenuBar(menu);

		final MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
		mntmFile.setText("File");

		final Menu menu_1 = new Menu(mntmFile);
		mntmFile.setMenu(menu_1);

		this.mntmOpenMonitoringLog = new MenuItem(menu_1, SWT.NONE);
		this.mntmOpenMonitoringLog.setText("Open Monitoring Log");

		new MenuItem(menu_1, SWT.SEPARATOR);

		this.mntmExit = new MenuItem(menu_1, SWT.NONE);
		this.mntmExit.setText("Exit");

		final MenuItem mntmView = new MenuItem(menu, SWT.CASCADE);
		mntmView.setText("View");

		final Menu menu_2 = new Menu(mntmView);
		mntmView.setMenu(menu_2);

		this.mntmShortOperationNames = new MenuItem(menu_2, SWT.RADIO);
		this.mntmShortOperationNames.setSelection(true);
		this.mntmShortOperationNames.setText("Short Operation Names");

		this.mntmLongOperationNames = new MenuItem(menu_2, SWT.RADIO);
		this.mntmLongOperationNames.setText("Long Operation Names");

		new MenuItem(menu_2, SWT.SEPARATOR);

		this.mntmShortComponentNames = new MenuItem(menu_2, SWT.RADIO);
		this.mntmShortComponentNames.setText("Short Component Names");

		this.mntmLongComponentNames = new MenuItem(menu_2, SWT.RADIO);
		this.mntmLongComponentNames.setSelection(true);
		this.mntmLongComponentNames.setText("Long Component Names");
	}

	private void addLogic() {
		this.dataModel.addObserver(this);
		this.mainViewModel.addObserver(this);

		this.tree.addSelectionListener(this.controller);

		this.mntmExit.addSelectionListener(this.controller);
		this.mntmOpenMonitoringLog.addSelectionListener(this.controller);

		this.mntmShortOperationNames.addSelectionListener(this.controller);
		this.mntmLongOperationNames.addSelectionListener(this.controller);
		this.mntmShortComponentNames.addSelectionListener(this.controller);
		this.mntmLongOperationNames.addSelectionListener(this.controller);
	}

	@Override
	public void update(final Observable observable, final Object obj) {
		if (observable == this.dataModel) {

		}
		if (observable == this.mainViewModel) {
			this.handleChangedSubView();
		}
	}

	private void handleChangedSubView() {
		final Composite subViewToShow;
		switch (this.mainViewModel.getCurrentActiveSubView()) {
		case NONE:
			subViewToShow = null;
			break;
		case RECORDS_SUB_VIEW:
			subViewToShow = this.recordsSubView.getComposite();
			break;
		case TRACES_SUB_VIEW:
			subViewToShow = this.tracesSubView.getComposite();
			break;
		case AGGREGATED_TRACES_SUB_VIEW:
			subViewToShow = this.aggregatedTracesSubView.getComposite();
			break;
		case FAILED_TRACES_SUB_VIEW:
			subViewToShow = this.failedTracesSubView.getComposite();
			break;
		case FAILURE_CONTAINING_TRACES_SUB_VIEW:
			subViewToShow = this.failureContainingTracesSubView.getComposite();
			break;
		default:
			subViewToShow = null;
			break;
		}

		this.subViewLayout.topControl = subViewToShow;
		this.subViewComposite.layout();
	}
}
