package kieker.gui.view.util;

import kieker.gui.model.domain.AggregatedExecution;

import org.eclipse.swt.SWT;

public class AggregatedExecutionTotalDurationComparator extends AbstractDirectedComparator<AggregatedExecution> {

	@Override
	public int compare(final AggregatedExecution arg0, final AggregatedExecution arg1) {
		int result = Long.compare(arg0.getTotalDuration(), arg1.getTotalDuration());
		if (this.getDirection() == SWT.UP) {
			result = -result;
		}
		return result;

	}

}
