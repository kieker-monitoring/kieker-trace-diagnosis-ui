package kieker.gui.subview.aggregatedtraces.util;

import kieker.gui.common.AbstractDirectedComparator;
import kieker.gui.common.domain.AggregatedExecution;

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
