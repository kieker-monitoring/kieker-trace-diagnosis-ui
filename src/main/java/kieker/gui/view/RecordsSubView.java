package kieker.gui.view;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import kieker.gui.controller.RecordsSubViewController;
import kieker.gui.model.DataModel;
import kieker.gui.model.domain.RecordEntry;
import kieker.gui.view.util.RecordEntryTimestampComparator;
import kieker.gui.view.util.RecordEntryTypeComparator;
import kieker.gui.view.util.TableColumnSortListener;

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

	private final DataModel model;
	private Composite composite;
	private Table table;

	public RecordsSubView(final DataModel model, final RecordsSubViewController controller) {
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

		tblclmnTimestamp.addSelectionListener(new TableColumnSortListener<>(new RecordEntryTimestampComparator()));
		tblclmnRecordType.addSelectionListener(new TableColumnSortListener<>(new RecordEntryTypeComparator()));
		tblclmnRecordContent.addSelectionListener(new TableColumnSortListener<>(new RecordEntryTimestampComparator()));
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
		final List<RecordEntry> records = this.model.getRecordsCopy();

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
			final List<RecordEntry> records = (List<RecordEntry>) table.getData();
			final RecordEntry record = records.get(tableIndex);

			// Get the data to display
			final String timestampStr = Long.toString(record.getTimestamp());
			final String type = record.getType();
			final String recordStr = record.getRepresentation();
			item.setText(new String[] { timestampStr, type, recordStr });
		}

	}

}
