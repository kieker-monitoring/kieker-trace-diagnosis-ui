package kieker.diagnosis.czi;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import kieker.diagnosis.domain.PreparedStatementCall;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * Sorting Listener for PreparedStatements
 * 
 * @author Christian Zirkelbach
 *
 */
public class PreparedStatementCallTreeColumnSortListener extends
		SelectionAdapter implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Comparator<PreparedStatementCall> comparator;
	private int direction;

	public <R extends Comparable<R>> PreparedStatementCallTreeColumnSortListener(
			final Function<PreparedStatementCall, R> attributeExtractor) {
		this.comparator = Comparator.comparing(attributeExtractor);
	}

	@SuppressWarnings("unchecked")
	@Override
	public final void widgetSelected(final SelectionEvent event) {
		// Get the necessary information from the event
		final TreeColumn currentColumn = (TreeColumn) event.widget;
		final Tree tree = currentColumn.getParent();
		final TreeColumn sortColumn = tree.getSortColumn();

		// Determine new sort column and direction
		this.direction = tree.getSortDirection();
		if (sortColumn == currentColumn) {
			this.direction = ((this.direction == SWT.UP) ? SWT.DOWN : SWT.UP);
		} else {
			tree.setSortColumn(currentColumn);
			this.direction = SWT.UP;
		}

		// Sort the data
		final List<PreparedStatementCall> entries = (List<PreparedStatementCall>) tree
				.getData();
		Collections.sort(entries, this.direction == SWT.UP ? this.comparator
				: this.comparator.reversed());

		// Update the data displayed in the table
		tree.setSortDirection(this.direction);
		tree.clearAll(true);
	}

}
