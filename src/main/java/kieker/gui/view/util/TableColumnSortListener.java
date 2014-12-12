package kieker.gui.view.util;

import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public final class TableColumnSortListener<T> extends SelectionAdapter {

	private final AbstractDirectedComparator<T> comparator;

	public TableColumnSortListener(final AbstractDirectedComparator<T> comparator) {
		this.comparator = comparator;
	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
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
		this.comparator.setDirection(direction);
		final List<T> entries = (List<T>) table.getData();
		Collections.sort(entries, this.comparator);

		// Update the data displayed in the table
		table.setSortDirection(direction);
		table.clearAll();
	}

}
