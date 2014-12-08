package kieker.gui.view;

import java.util.Comparator;

import kieker.gui.model.RecordEntry;

import org.eclipse.swt.SWT;

class RecordEntryTimestampComparator implements Comparator<RecordEntry> {

	private final int direction;

	public RecordEntryTimestampComparator(final int direction) {
		this.direction = direction;
	}

	@Override
	public int compare(final RecordEntry o1, final RecordEntry o2) {
		int result = Long.compare(o1.getTimestamp(), o2.getTimestamp());
		if (this.direction == SWT.UP) {
			result = -result;
		}
		return result;
	}

}