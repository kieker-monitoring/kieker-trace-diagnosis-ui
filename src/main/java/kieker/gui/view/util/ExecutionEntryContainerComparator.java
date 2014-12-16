package kieker.gui.view.util;

import kieker.gui.model.domain.ExecutionEntry;

import org.eclipse.swt.SWT;

public class ExecutionEntryContainerComparator extends AbstractDirectedComparator<ExecutionEntry> {

	@Override
	public int compare(final ExecutionEntry arg0, final ExecutionEntry arg1) {
		int result = arg0.getContainer().compareTo(arg1.getContainer());
		if (this.getDirection() == SWT.UP) {
			result = -result;
		}
		return result;

	}

}
