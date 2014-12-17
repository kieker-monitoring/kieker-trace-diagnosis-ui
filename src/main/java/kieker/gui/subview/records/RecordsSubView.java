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

import kieker.gui.common.IModel;
import kieker.gui.common.ISubView;
import kieker.gui.common.TableColumnSortListener;
import kieker.gui.common.domain.Record;
import kieker.gui.subview.records.util.RecordTimestampComparator;
import kieker.gui.subview.records.util.RecordTypeComparator;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public final class RecordsSubView implements Observer, ISubView {

	private final IModel<Record> model;
	private Composite composite;
	private Table table;

	public RecordsSubView(final IModel<Record> model, final RecordsSubViewController controller) {
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
		this.composite.setLayout(new FillLayout(SWT.HORIZONTAL));

		final TableViewer tableViewer = new TableViewer(this.composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		this.table = tableViewer.getTable();
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
		}
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
