package kieker.gui.subview.aggregatedtraces.util;

import kieker.gui.common.domain.AggregatedExecution;
import kieker.gui.subview.util.AbstractDirectedComparator;

import org.eclipse.swt.SWT;

public class AggregatedExecutionTotalDurationComparator extends AbstractDirectedComparator<AggregatedExecution> {

	private static final long serialVersionUID = 1L;

	@Override
	public int compare(final AggregatedExecution arg0, final AggregatedExecution arg1) {
		int result;

		if (this.getDirection() == SWT.UP) {
			result = Long.compare(arg1.getTotalDuration(), arg0.getTotalDuration());
		} else {
			result = Long.compare(arg0.getTotalDuration(), arg1.getTotalDuration());
		}

		return result;
	}

}
