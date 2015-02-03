package kieker.diagnosis.subview.aggregatedtraces.util;

import kieker.diagnosis.common.domain.AggregatedTrace;
import kieker.diagnosis.subview.util.AbstractTraceTreeColumnSortListener;

public final class CallsSortListener extends AbstractTraceTreeColumnSortListener<AggregatedTrace> {

	private static final long serialVersionUID = 1L;

	@Override
	protected int compare(final AggregatedTrace fstTrace, final AggregatedTrace sndTrace) {
		final int fstCalls = fstTrace.getRootOperationCall().getCalls();
		final int sndCalls = sndTrace.getRootOperationCall().getCalls();

		return Integer.compare(fstCalls, sndCalls);
	}

}
