package kieker.gui.view.util;

import kieker.gui.model.domain.ExecutionEntry;

import org.eclipse.swt.SWT;

public class ExecutionEntryTraceIDComparator extends AbstractDirectedComparator<ExecutionEntry> {

	@Override
	public int compare(final ExecutionEntry arg0, final ExecutionEntry arg1) {
		int result = Long.compare(arg0.getTraceID(), arg1.getTraceID());
		if (this.getDirection() == SWT.UP) {
			result = -result;
		}
		return result;

	}

}
