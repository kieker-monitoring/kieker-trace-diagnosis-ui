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

package kieker.diagnosis.subview.util;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import kieker.diagnosis.domain.AbstractOperationCall;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public abstract class AbstractCallTableColumnSortListener<T extends AbstractOperationCall<?>> extends SelectionAdapter implements Serializable {

	private static final long serialVersionUID = 1L;

	private final CallComparator comparator = new CallComparator();
	private int direction;

	@Override
	@SuppressWarnings("unchecked")
	public final void widgetSelected(final SelectionEvent event) {
		// Get the necessary information from the event
		final TableColumn currentColumn = (TableColumn) event.widget;
		final Table table = currentColumn.getParent();
		final TableColumn sortColumn = table.getSortColumn();

		// Determine new sort column and direction
		this.direction = table.getSortDirection();
		if (sortColumn == currentColumn) {
			this.direction = ((this.direction == SWT.UP) ? SWT.DOWN : SWT.UP);
		} else {
			table.setSortColumn(currentColumn);
			this.direction = SWT.DOWN;
		}

		// Sort the data
		final List<T> entries = (List<T>) table.getData();
		Collections.sort(entries, this.comparator);

		// Update the data displayed in the table
		table.setSortDirection(this.direction);
		table.clearAll();
	}

	protected abstract int compare(final T fstCall, final T sndCall);

	private final class CallComparator implements Comparator<T>, Serializable {

		private static final long serialVersionUID = 1L;

		@Override
		public int compare(final T fstCall, final T sndCall) {
			int result;

			if (AbstractCallTableColumnSortListener.this.direction == SWT.UP) {
				result = AbstractCallTableColumnSortListener.this.compare(fstCall, sndCall);
			} else {
				result = AbstractCallTableColumnSortListener.this.compare(sndCall, fstCall);
			}

			return result;
		}

	}

}
