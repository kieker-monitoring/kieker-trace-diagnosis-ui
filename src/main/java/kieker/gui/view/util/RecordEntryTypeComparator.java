package kieker.gui.view.util;

import kieker.gui.model.domain.RecordEntry;

import org.eclipse.swt.SWT;

public class RecordEntryTypeComparator extends AbstractDirectedComparator<RecordEntry> {

	@Override
	public int compare(final RecordEntry o1, final RecordEntry o2) {
		int result = o1.getType().compareTo(o2.getType());
		if (this.getDirection() == SWT.UP) {
			result = -result;
		}
		return result;
	}

}
