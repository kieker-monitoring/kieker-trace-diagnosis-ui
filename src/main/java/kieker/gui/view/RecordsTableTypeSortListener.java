package kieker.gui.view;

import java.util.Collections;
import java.util.List;

import kieker.gui.model.RecordEntry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

class RecordsTableTypeSortListener implements Listener {

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
		Collections.sort(records, new RecordEntryTypeComparator(direction));

		// Update the data displayed in the table
		table.setSortDirection(direction);
		table.clearAll();
	}
}
