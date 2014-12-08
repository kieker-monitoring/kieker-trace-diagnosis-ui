package kieker.gui.view;

import java.util.Comparator;

import kieker.gui.model.RecordEntry;

import org.eclipse.swt.SWT;

class RecordEntryTypeComparator implements Comparator<RecordEntry> {

	private final int direction;

	public RecordEntryTypeComparator(final int direction) {
		this.direction = direction;
	}

	@Override
	public int compare(final RecordEntry o1, final RecordEntry o2) {
		int result = o1.getType().compareTo(o2.getType());
		if (this.direction == SWT.UP) {
			result = -result;
		}
		return result;
	}
}
