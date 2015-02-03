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

import kieker.diagnosis.common.domain.AbstractTrace;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

public abstract class AbstractTraceTreeColumnSortListener<T extends AbstractTrace<?>> extends SelectionAdapter {

	private final TraceComparator comparator = new TraceComparator();
	private int direction;

	@Override
	@SuppressWarnings("unchecked")
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
		final List<T> entries = (List<T>) tree.getData();
		Collections.sort(entries, this.comparator);

		// Update the data displayed in the table
		tree.setSortDirection(this.direction);
		tree.clearAll(true);
	}

	protected abstract int compare(final T fstTrace, final T sndTrace);

	private final class TraceComparator implements Comparator<T>, Serializable {

		private static final long serialVersionUID = 1L;

		@Override
		public int compare(final T fstTrace, final T sndTrace) {
			int result;

			if (AbstractTraceTreeColumnSortListener.this.direction == SWT.DOWN) {
				result = AbstractTraceTreeColumnSortListener.this.compare(fstTrace, sndTrace);
			} else {
				result = AbstractTraceTreeColumnSortListener.this.compare(sndTrace, fstTrace);
			}

			return result;
		}

	}

}
