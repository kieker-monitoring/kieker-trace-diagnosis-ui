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

package kieker.gui.subview.records;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import kieker.gui.common.domain.Record;
import kieker.gui.subview.ISubView;
import kieker.gui.subview.records.util.RecordTimestampComparator;
import kieker.gui.subview.records.util.RecordTypeComparator;
import kieker.gui.subview.util.IModel;
import kieker.gui.subview.util.TableColumnSortListener;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public final class View implements Observer, ISubView {

	private final IModel<Record> model;
	private Composite composite;
	private Table table;
	private Label lblRecords;
	private Composite statusBar;

	public View(final IModel<Record> model, final Controller controller) {
		this.model = model;

		model.addObserver(this);
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void createComposite(final Composite parent) {
		if (this.composite != null) {
			this.composite.dispose();
		}

		this.composite = new Composite(parent, SWT.NONE);
		final GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.verticalSpacing = 0;
		gl_composite.marginHeight = 0;
		gl_composite.marginWidth = 0;
		gl_composite.horizontalSpacing = 0;
		this.composite.setLayout(gl_composite);

		final TableViewer tableViewer = new TableViewer(this.composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		this.table = tableViewer.getTable();
		this.table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		this.table.setHeaderVisible(true);
		this.table.setLinesVisible(true);

		final TableColumn tblclmnTimestamp = new TableColumn(this.table, SWT.NONE);
		tblclmnTimestamp.setWidth(100);
		tblclmnTimestamp.setText("Timestamp");

		final TableColumn tblclmnRecordType = new TableColumn(this.table, SWT.NONE);
		tblclmnRecordType.setWidth(100);
		tblclmnRecordType.setText("Record Type");

		final TableColumn tblclmnRecordContent = new TableColumn(this.table, SWT.NONE);
		tblclmnRecordContent.setWidth(100);
		tblclmnRecordContent.setText("Record Content");

		this.statusBar = new Composite(this.composite, SWT.NONE);
		this.statusBar.setLayout(new GridLayout(1, false));

		this.lblRecords = new Label(this.statusBar, SWT.NONE);
		this.lblRecords.setText("0 Records");

		this.table.addListener(SWT.SetData, new DataProvider());

		tblclmnTimestamp.addSelectionListener(new TableColumnSortListener<>(new RecordTimestampComparator()));
		tblclmnRecordType.addSelectionListener(new TableColumnSortListener<>(new RecordTypeComparator()));
		tblclmnRecordContent.addSelectionListener(new TableColumnSortListener<>(new RecordTimestampComparator()));
	}

	public Table getTable() {
		return this.table;
	}

	@Override
	public Composite getComposite() {
		return this.composite;
	}

	@Override
	public void update(final Observable observable, final Object obj) {
		if (observable == this.model) {
			this.updateTable();
			this.updateStatusBar();
		}
	}

	private void updateStatusBar() {
		this.lblRecords.setText(this.model.getContent().size() + " Record(s)");
		this.statusBar.getParent().layout();
	}

	private void updateTable() {
		final List<Record> records = this.model.getContent();

		this.table.setData(records);
		this.table.setItemCount(records.size());
		this.table.clearAll();

		for (final TableColumn column : this.table.getColumns()) {
			column.pack();
		}
	}

	private class DataProvider implements Listener {

		@Override
		@SuppressWarnings("unchecked")
		public void handleEvent(final Event event) {
			// Get the necessary information from the event
			final Table table = (Table) event.widget;
			final TableItem item = (TableItem) event.item;
			final int tableIndex = event.index;

			// Get the data for the current row
			final List<Record> records = (List<Record>) table.getData();
			final Record record = records.get(tableIndex);

			// Get the data to display
			final String timestampStr = Long.toString(record.getTimestamp());
			final String type = record.getType();
			final String recordStr = record.getRepresentation();
			item.setText(new String[] { timestampStr, type, recordStr });
		}

	}
}
