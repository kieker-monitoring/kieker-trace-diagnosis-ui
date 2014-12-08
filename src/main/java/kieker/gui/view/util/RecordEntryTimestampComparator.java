package kieker.gui.view.util;

import kieker.gui.model.domain.RecordEntry;

import org.eclipse.swt.SWT;

public class RecordEntryTimestampComparator extends AbstractDirectedComparator<RecordEntry> {

	@Override
	public int compare(final RecordEntry o1, final RecordEntry o2) {
		int result = Long.compare(o1.getTimestamp(), o2.getTimestamp());
		if (this.getDirection() == SWT.UP) {
			result = -result;
		}
		return result;
	}

}
