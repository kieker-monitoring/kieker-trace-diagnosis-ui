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

package kieker.gui.subview.util;

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
