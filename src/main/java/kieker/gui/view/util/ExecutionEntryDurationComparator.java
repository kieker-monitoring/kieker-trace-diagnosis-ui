package kieker.gui.view.util;

import kieker.gui.model.domain.ExecutionEntry;

import org.eclipse.swt.SWT;

public class ExecutionEntryDurationComparator extends AbstractDirectedComparator<ExecutionEntry> {

	@Override
	public int compare(final ExecutionEntry arg0, final ExecutionEntry arg1) {
		int result = Long.compare(arg0.getDuration(), arg1.getDuration());
		if (this.getDirection() == SWT.UP) {
			result = -result;
		}
		return result;

	}

}
