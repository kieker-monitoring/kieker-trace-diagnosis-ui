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

package kieker.gui.common;

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
