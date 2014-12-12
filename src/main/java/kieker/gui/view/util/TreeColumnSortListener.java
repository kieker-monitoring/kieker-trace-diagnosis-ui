package kieker.gui.view.util;

import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

public final class TreeColumnSortListener<T> extends SelectionAdapter {

	private final AbstractDirectedComparator<T> comparator;

	public TreeColumnSortListener(final AbstractDirectedComparator<T> comparator) {
		this.comparator = comparator;
	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		// Get the necessary information from the event
		final TreeColumn currentColumn = (TreeColumn) event.widget;
		final Tree tree = currentColumn.getParent();
		final TreeColumn sortColumn = tree.getSortColumn();

		// Determine new sort column and direction
		int direction = tree.getSortDirection();
		if (sortColumn == currentColumn) {
			direction = ((direction == SWT.UP) ? SWT.DOWN : SWT.UP);
		} else {
			tree.setSortColumn(currentColumn);
			direction = SWT.UP;
		}

		// Sort the data
		this.comparator.setDirection(direction);
		final List<T> entries = (List<T>) tree.getData();
		Collections.sort(entries, this.comparator);

		// Update the data displayed in the table
		tree.setSortDirection(direction);
		tree.clearAll(true);
	}

}
