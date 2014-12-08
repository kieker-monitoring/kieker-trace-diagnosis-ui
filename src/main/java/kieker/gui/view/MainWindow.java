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

package kieker.gui.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import kieker.common.record.IMonitoringRecord;
import kieker.gui.model.DataSource;
import kieker.gui.model.ExecutionEntry;
import kieker.gui.model.Properties;
import kieker.gui.model.RecordEntry;
import kieker.tools.traceAnalysis.systemModel.ComponentType;
import kieker.tools.traceAnalysis.systemModel.Operation;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * The main window of this application. This file should probably be maintained with the Eclipse GUI builder.
 * 
 * @author Nils Christian Ehmke
 */
public class MainWindow {

	protected Shell shell;
	private Composite mainComposite;
	private Table recordsTable;
	private TreeViewer treeViewer_4;
	private SashForm sashForm;
	private TreeViewer explorerTreeViewer;
	private TreeItem explorerTreeItem;
	private TreeItem recordsTreeItem;
	private TreeItem executionTracesTreeItem;
	private TableViewer recordsTableViewer;
	private TableColumn recordsTableTimestampColumn;
	private TableColumn recordsTableRecordsColumn;
	private Menu menuBar;
	private MenuItem fileMenuItem;
	private Menu fileMenu;
	private MenuItem openMonitoringLogMenuItem;
	private MenuItem exitMenuItem;
	private MenuItem helpMenuItem;
	private Menu helpMenu;
	private MenuItem aboutMenuItem;
	private Tree explorerTree;
	private Tree executionTracesTree;
	private TreeColumn treeColumn_7;
	private TreeColumn treeColumn_8;
	private TreeColumn treeColumn_10;
	private TreeColumn treeColumn_11;
	private TreeColumn treeColumn_16;
	private MenuItem mntmView_1;
	private Menu menu;
	private MenuItem mntmShortComponentNames;
	private MenuItem mntmLongComponentNames;
	private MenuItem mntmShortOperationParameters;
	private MenuItem mntmLongOperationParameters;
	private TableColumn tblclmnType;
	private SashForm sashForm_1;
	private final DataSource model = new DataSource();
	private TreeColumn trclmnPercent;
	private Label lblNa;
	private SashForm sashForm_2;
	private Composite composite;
	private Label lblTraceId;
	private Label lblNa_1;
	private Label lblFailed;
	private Label lblNa_2;
	private Label lblDuration;
	private Label lblNa_3;
	private Label lblExecutionContainer;
	private Label lblComponent;
	private Label lblOperation;
	private Label lblNa_4;
	private Label lblNa_5;
	private Label lblNa_6;
	private Label lblStackDepth;
	private Label lblNa_7;

	public static void main(final String[] args) {
		final MainWindow window = new MainWindow();
		window.open();
	}

	public void open() {
		final Display display = Display.getDefault();
		this.createContents();

		this.shell.open();
		this.shell.layout();
		while (!this.shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	protected void createContents() {
		this.shell = new Shell();
		this.shell.setImage(null);
		this.shell.setMaximized(true);
		this.shell.setText("Kieker's GUI");
		this.shell.setLayout(new GridLayout(1, false));

		this.sashForm = new SashForm(this.shell, SWT.NONE);
		this.sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		this.sashForm_1 = new SashForm(this.sashForm, SWT.VERTICAL);

		this.explorerTreeViewer = new TreeViewer(this.sashForm_1, SWT.BORDER);
		this.explorerTree = this.explorerTreeViewer.getTree();

		this.explorerTreeItem = new TreeItem(this.explorerTree, SWT.NONE);
		this.explorerTreeItem.setText("Explorer");

		this.recordsTreeItem = new TreeItem(this.explorerTreeItem, SWT.NONE);
		this.recordsTreeItem.setText("Records");

		this.executionTracesTreeItem = new TreeItem(this.explorerTreeItem, SWT.NONE);
		this.executionTracesTreeItem.setText("Execution Traces");

		this.executionTracesTreeItem.setExpanded(true);
		this.explorerTreeItem.setExpanded(true);

		this.explorerTree.addSelectionListener(new ExplorerTreeSelectionAdapter());
		this.sashForm_1.setWeights(new int[] { 3 });

		this.mainComposite = new Composite(this.sashForm, SWT.NONE);
		this.mainComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.mainComposite.setLayout(new StackLayout());

		this.recordsTableViewer = new TableViewer(this.mainComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		this.recordsTable = this.recordsTableViewer.getTable();
		this.recordsTable.setHeaderVisible(true);
		this.recordsTable.addListener(SWT.SetData, new RecordsTableSetDataListener());

		this.recordsTableTimestampColumn = new TableColumn(this.recordsTable, SWT.NONE);
		this.recordsTableTimestampColumn.setWidth(100);
		this.recordsTableTimestampColumn.setText("Timestamp");
		this.recordsTableTimestampColumn.addListener(SWT.Selection, new RecordsTableTimestampSortListener());

		this.tblclmnType = new TableColumn(this.recordsTable, SWT.NONE);
		this.tblclmnType.setWidth(100);
		this.tblclmnType.setText("Type");
		this.tblclmnType.addListener(SWT.Selection, new RecordsTableTypeSortListener());

		this.recordsTableRecordsColumn = new TableColumn(this.recordsTable, SWT.NONE);
		this.recordsTableRecordsColumn.setWidth(100);
		this.recordsTableRecordsColumn.setText("Record");
		this.recordsTableRecordsColumn.addListener(SWT.Selection, new RecordsTableTimestampSortListener());

		this.sashForm_2 = new SashForm(this.mainComposite, SWT.VERTICAL);

		this.executionTracesTree = new Tree(this.sashForm_2, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		this.executionTracesTree.setHeaderVisible(true);
		this.executionTracesTree.addListener(SWT.SetData, new ExecutionTracesTreeSetDataListener());
		this.executionTracesTree.addSelectionListener(new ExecutionTraceTreeSelectionListener());
		Properties.getInstance().addObserver(new TreeUpdateObserver(this.executionTracesTree));

		this.treeColumn_7 = new TreeColumn(this.executionTracesTree, SWT.NONE);
		this.treeColumn_7.setWidth(100);
		this.treeColumn_7.setText("Execution Container");

		this.treeColumn_8 = new TreeColumn(this.executionTracesTree, SWT.NONE);
		this.treeColumn_8.setWidth(100);
		this.treeColumn_8.setText("Component");

		this.treeColumn_10 = new TreeColumn(this.executionTracesTree, SWT.NONE);
		this.treeColumn_10.setWidth(100);
		this.treeColumn_10.setText("Operation");

		this.treeColumn_11 = new TreeColumn(this.executionTracesTree, SWT.NONE);
		this.treeColumn_11.setWidth(100);
		this.treeColumn_11.setText("Duration");

		this.trclmnPercent = new TreeColumn(this.executionTracesTree, SWT.NONE);
		this.trclmnPercent.setWidth(100);
		this.trclmnPercent.setText("Percent");

		this.treeColumn_16 = new TreeColumn(this.executionTracesTree, SWT.NONE);
		this.treeColumn_16.setWidth(100);
		this.treeColumn_16.setText("Trace ID");

		this.composite = new Composite(this.sashForm_2, SWT.BORDER);
		this.composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.composite.setLayout(new GridLayout(2, false));

		this.lblExecutionContainer = new Label(this.composite, SWT.NONE);
		this.lblExecutionContainer.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblExecutionContainer.setText("Execution Container:");

		this.lblNa_4 = new Label(this.composite, SWT.NONE);
		this.lblNa_4.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		this.lblNa_4.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblNa_4.setText("N/A");

		this.lblComponent = new Label(this.composite, SWT.NONE);
		this.lblComponent.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblComponent.setText("Component:");

		this.lblNa_5 = new Label(this.composite, SWT.NONE);
		this.lblNa_5.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblNa_5.setText("N/A");

		this.lblOperation = new Label(this.composite, SWT.NONE);
		this.lblOperation.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblOperation.setText("Operation:");

		this.lblNa_6 = new Label(this.composite, SWT.NONE);
		this.lblNa_6.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblNa_6.setText("N/A");

		this.lblTraceId = new Label(this.composite, SWT.NONE);
		this.lblTraceId.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblTraceId.setText("Trace ID:");

		this.lblNa_1 = new Label(this.composite, SWT.NONE);
		this.lblNa_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblNa_1.setText("N/A");

		this.lblDuration = new Label(this.composite, SWT.NONE);
		this.lblDuration.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblDuration.setText("Duration:");

		this.lblNa_3 = new Label(this.composite, SWT.NONE);
		this.lblNa_3.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblNa_3.setText("N/A");

		this.lblStackDepth = new Label(this.composite, SWT.NONE);
		this.lblStackDepth.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblStackDepth.setText("Stack Depth:");

		this.lblNa_7 = new Label(this.composite, SWT.NONE);
		this.lblNa_7.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblNa_7.setText("N/A");

		this.lblFailed = new Label(this.composite, SWT.NONE);
		this.lblFailed.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblFailed.setText("Failed:");

		this.lblNa_2 = new Label(this.composite, SWT.NONE);
		this.lblNa_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblNa_2.setText("N/A");
		this.sashForm_2.setWeights(new int[] { 2, 1 });

		this.sashForm.setWeights(new int[] { 2, 4 });

		this.menuBar = new Menu(this.shell, SWT.BAR);
		this.shell.setMenuBar(this.menuBar);

		this.fileMenuItem = new MenuItem(this.menuBar, SWT.CASCADE);
		this.fileMenuItem.setText("File");

		this.fileMenu = new Menu(this.fileMenuItem);
		this.fileMenuItem.setMenu(this.fileMenu);

		this.openMonitoringLogMenuItem = new MenuItem(this.fileMenu, SWT.NONE);
		this.openMonitoringLogMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				MainWindow.this.showOpenMonitoringLogDialog();
			}
		});
		this.openMonitoringLogMenuItem.setText("Open Monitoring Log");

		new MenuItem(this.fileMenu, SWT.SEPARATOR);

		this.exitMenuItem = new MenuItem(this.fileMenu, SWT.NONE);
		this.exitMenuItem.setText("Exit");

		this.mntmView_1 = new MenuItem(this.menuBar, SWT.CASCADE);
		this.mntmView_1.setText("View");

		this.menu = new Menu(this.mntmView_1);
		this.mntmView_1.setMenu(this.menu);

		this.mntmShortOperationParameters = new MenuItem(this.menu, SWT.RADIO);
		this.mntmShortOperationParameters.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				Properties.getInstance().setShortOperationParameters(true);
			}
		});
		this.mntmShortOperationParameters.setText("Short Operation Parameters");

		this.mntmLongOperationParameters = new MenuItem(this.menu, SWT.RADIO);
		this.mntmLongOperationParameters.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				Properties.getInstance().setShortOperationParameters(false);
			}
		});
		this.mntmLongOperationParameters.setSelection(true);
		this.mntmLongOperationParameters.setText("Long Operation Parameters");

		new MenuItem(this.menu, SWT.SEPARATOR);

		this.mntmShortComponentNames = new MenuItem(this.menu, SWT.RADIO);
		this.mntmShortComponentNames.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				Properties.getInstance().setShortComponentNames(true);
			}
		});
		this.mntmShortComponentNames.setText("Short Component Names");

		this.mntmLongComponentNames = new MenuItem(this.menu, SWT.RADIO);
		this.mntmLongComponentNames.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				Properties.getInstance().setShortComponentNames(false);
			}
		});
		this.mntmLongComponentNames.setSelection(true);
		this.mntmLongComponentNames.setText("Long Component Names");

		this.helpMenuItem = new MenuItem(this.menuBar, SWT.CASCADE);
		this.helpMenuItem.setText("Help");

		this.helpMenu = new Menu(this.helpMenuItem);
		this.helpMenuItem.setMenu(this.helpMenu);

		this.aboutMenuItem = new MenuItem(this.helpMenu, SWT.NONE);
		this.aboutMenuItem.setText("About...");

		this.lblNa = new Label(this.shell, SWT.NONE);
		this.lblNa.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
	}

	protected void showSystemModel() {
		// Show the tree

	}

	protected void showAggregatedExecutionTraces() {
		// Show the tree

	}

	protected void showAggregatedExecutionTracesByContainer() {

	}

	protected void showAggregatedExecutionTracesByComponent() {

	}

	protected void showExecutionTraces() {
		// Show the tree
		this.setVisibleMainComponent(this.sashForm_2);

		// Reload the data from the model...
		final List<ExecutionEntry> traces = this.model.getTraces();

		// ...and write it into the tree
		this.executionTracesTree.setItemCount(traces.size());
		this.executionTracesTree.setData(traces);
		this.lblNa.setText(Integer.toString(traces.size()) + " Traces");
		this.lblNa.pack();

		// Resize the columns
		for (final TreeColumn column : this.executionTracesTree.getColumns()) {
			column.pack();
		}
	}

	protected void showRecords() {
		// Show the table
		this.setVisibleMainComponent(this.recordsTable);

		// Reload the data from the model...
		final List<RecordEntry> records = this.model.getRecords();

		// ...and write it into the table
		this.recordsTable.setItemCount(records.size());
		this.recordsTable.setData(records);
		this.lblNa.setText(Integer.toString(records.size()) + " Records");
		this.lblNa.pack();

		// Resize the columns
		for (final TableColumn column : this.recordsTable.getColumns()) {
			column.pack();
		}
	}

	protected void showOpenMonitoringLogDialog() {
		final DirectoryDialog dialog = new DirectoryDialog(this.shell);
		final String selectedDirectory = dialog.open();

		if (null != selectedDirectory) {
			this.model.loadMonitoringLogFromFS(selectedDirectory);
		}
	}

	private void setVisibleMainComponent(final Control component) {
		final StackLayout layout = (StackLayout) this.mainComposite.getLayout();
		layout.topControl = component;

		this.mainComposite.layout();
	}

	private class ExplorerTreeSelectionAdapter extends SelectionAdapter {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			final Widget selectedWidget = e.item;

			if (MainWindow.this.recordsTreeItem == selectedWidget) {
				MainWindow.this.showRecords();
			} else if (MainWindow.this.executionTracesTreeItem == selectedWidget) {
				MainWindow.this.showExecutionTraces();
			} else {
				MainWindow.this.setVisibleMainComponent(null);
				MainWindow.this.lblNa.setText("");
			}
		}

	}

	private class SystemModelTreeSetDataListener implements Listener {

		@Override
		public void handleEvent(final Event event) {
			// Get the necessary information from the event
			final Tree tree = (Tree) event.widget;
			final TreeItem item = (TreeItem) event.item;
			final int tableIndex = event.index;
			final TreeItem parent = item.getParentItem();

			// Decide whether the current item is a root or not
			if (parent == null) {
				final List<ComponentType> components = new ArrayList<>((Collection<ComponentType>) tree.getData());
				final ComponentType component = components.get(tableIndex);

				item.setText(component.getFullQualifiedName());

				item.setData(component.getOperations());
				item.setItemCount(component.getOperations().size());
			} else {
				final List<Operation> operations = new ArrayList<>((Collection<Operation>) parent.getData());
				final Operation operation = operations.get(tableIndex);
				item.setText(operation.getSignature().toString());
			}
		}
	}

	private class ExecutionTracesTreeSetDataListener implements Listener {

		@Override
		public void handleEvent(final Event event) {
			// Get the necessary information from the event
			final Tree tree = (Tree) event.widget;
			final TreeItem item = (TreeItem) event.item;
			final int tableIndex = event.index;
			final TreeItem parent = item.getParentItem();

			// Decide whether the current item is a root or not
			final ExecutionEntry executionEntry;
			final List<ExecutionEntry> executions;
			final String traceID;

			if (parent == null) {
				executionEntry = ((List<ExecutionEntry>) tree.getData()).get(tableIndex);
				traceID = Long.toString(executionEntry.getTraceID());
			} else {
				executionEntry = ((ExecutionEntry) parent.getData()).getChildren().get(tableIndex);
				traceID = "";
			}

			String componentName = executionEntry.getComponent();
			if (Properties.getInstance().isShortComponentNames()) {
				final int lastPointPos = componentName.lastIndexOf('.');
				componentName = componentName.substring(lastPointPos + 1);
			}
			String operationString = executionEntry.getOperation();
			if (Properties.getInstance().isShortOperationParameters()) {
				operationString = operationString.replaceAll("\\(..*\\)", "(...)");

				final int lastPointPos = operationString.lastIndexOf('.', operationString.length() - 5);
				operationString = operationString.substring(lastPointPos + 1);
			}
			item.setText(new String[] { executionEntry.getContainer(), componentName, operationString, Long.toString(executionEntry.getDuration()),
					String.format("%.1f%%", executionEntry.getPercent()), traceID });

			if (executionEntry.isFailed()) {
				final Color colorRed = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
				item.setForeground(colorRed);
			}

			item.setData(executionEntry);
			item.setItemCount(executionEntry.getChildren().size());
		}
	}

	private class RecordsTableSetDataListener implements Listener {

		@Override
		public void handleEvent(final Event event) {
			// Get the necessary information from the event
			final Table table = (Table) event.widget;
			final TableItem item = (TableItem) event.item;
			final int tableIndex = event.index;

			// Get the data for the current row
			final List<RecordEntry> records = (List<RecordEntry>) table.getData();
			final RecordEntry record = records.get(tableIndex);

			// Get the data to display
			final String timestampStr = Long.toString(record.getTimestamp());
			final String type = record.getType();
			final String recordStr = record.getRepresentation();
			item.setText(new String[] { timestampStr, type, recordStr });
		}

	}

	private class RecordsTableTimestampSortListener implements Listener {

		@Override
		public void handleEvent(final Event event) {
			// Get the necessary information from the event
			final TableColumn currentColumn = (TableColumn) event.widget;
			final Table table = currentColumn.getParent();
			final TableColumn sortColumn = table.getSortColumn();

			// Determine new sort column and direction
			int direction = table.getSortDirection();
			if (sortColumn == currentColumn) {
				direction = ((direction == SWT.UP) ? SWT.DOWN : SWT.UP);
			} else {
				table.setSortColumn(currentColumn);
				direction = SWT.UP;
			}

			// Sort the data
			final List<RecordEntry> records = (List<RecordEntry>) table.getData();
			Collections.sort(records, new RecordEntryTimestampComparator(direction));

			// Update the data displayed in the table
			table.setSortDirection(direction);
			table.clearAll();
		}
	}

	private class RecordsTableTypeSortListener implements Listener {

		@Override
		public void handleEvent(final Event event) {
			// Get the necessary information from the event
			final TableColumn currentColumn = (TableColumn) event.widget;
			final Table table = currentColumn.getParent();
			final TableColumn sortColumn = table.getSortColumn();

			// Determine new sort column and direction
			int direction = table.getSortDirection();
			if (sortColumn == currentColumn) {
				direction = ((direction == SWT.UP) ? SWT.DOWN : SWT.UP);
			} else {
				table.setSortColumn(currentColumn);
				direction = SWT.UP;
			}

			// Sort the data
			final List<IMonitoringRecord> records = (List<IMonitoringRecord>) table.getData();
			Collections.sort(records, new IMonitoringRecordTypeComparator(direction));

			// Update the data displayed in the table
			table.setSortDirection(direction);
			table.clearAll();
		}
	}

	private class RecordEntryTimestampComparator implements Comparator<RecordEntry> {

		private final int direction;

		public RecordEntryTimestampComparator(final int direction) {
			this.direction = direction;
		}

		@Override
		public int compare(final RecordEntry o1, final RecordEntry o2) {
			int result = Long.compare(o1.getTimestamp(), o2.getTimestamp());
			if (this.direction == SWT.UP) {
				result = -result;
			}
			return result;
		}

	}

	private class IMonitoringRecordTypeComparator implements Comparator<IMonitoringRecord> {

		private final int direction;

		public IMonitoringRecordTypeComparator(final int direction) {
			this.direction = direction;
		}

		@Override
		public int compare(final IMonitoringRecord o1, final IMonitoringRecord o2) {
			int result = o1.getClass().getCanonicalName().compareTo(o2.getClass().getCanonicalName());
			if (this.direction == SWT.UP) {
				result = -result;
			}
			return result;
		}
	}

	private static class TreeUpdateObserver implements Observer {

		private final Tree tree;

		public TreeUpdateObserver(final Tree tree) {
			this.tree = tree;
		}

		@Override
		public void update(final Observable observable, final Object obj) {
			this.tree.clearAll(true);
		}

	}

	private class ExecutionTraceTreeSelectionListener implements SelectionListener {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			final Object data = e.item.getData();
			if (data instanceof ExecutionEntry) {
				MainWindow.this.lblNa_1.setText(Long.toString(((ExecutionEntry) data).getTraceID()));
				MainWindow.this.lblNa_3.setText(Long.toString(((ExecutionEntry) data).getDuration()));

				MainWindow.this.lblNa_4.setText(((ExecutionEntry) data).getContainer());
				MainWindow.this.lblNa_5.setText(((ExecutionEntry) data).getComponent());
				MainWindow.this.lblNa_6.setText(((ExecutionEntry) data).getOperation());
				MainWindow.this.lblNa_7.setText(Integer.toString(((ExecutionEntry) data).getStackDepth()));

				if (((ExecutionEntry) data).isFailed()) {
					MainWindow.this.lblNa_2.setText("Yes (" + ((ExecutionEntry) data).getFailedCause() + ")");
					MainWindow.this.lblNa_2.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
					MainWindow.this.lblFailed.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
				} else {
					MainWindow.this.lblNa_2.setText("No");
					MainWindow.this.lblNa_2.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
					MainWindow.this.lblFailed.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
				}

				MainWindow.this.lblNa_1.pack();
				MainWindow.this.lblNa_2.pack();
				MainWindow.this.lblNa_3.pack();
				MainWindow.this.lblNa_4.pack();
				MainWindow.this.lblNa_5.pack();
				MainWindow.this.lblNa_6.pack();
				MainWindow.this.lblNa_7.pack();
			}
		}

		@Override
		public void widgetDefaultSelected(final SelectionEvent e) {

		}

	}
}
