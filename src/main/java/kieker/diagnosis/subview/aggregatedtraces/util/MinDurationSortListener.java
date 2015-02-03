package kieker.diagnosis.subview.aggregatedtraces.util;

import kieker.diagnosis.common.domain.AggregatedTrace;
import kieker.diagnosis.subview.util.AbstractTraceTreeColumnSortListener;

public final class MinDurationSortListener extends AbstractTraceTreeColumnSortListener<AggregatedTrace> {

	private static final long serialVersionUID = 1L;

	@Override
	protected int compare(final AggregatedTrace fstTrace, final AggregatedTrace sndTrace) {
		final long fstMinDuration = fstTrace.getRootOperationCall().getMinDuration();
		final long sndMinDuration = sndTrace.getRootOperationCall().getMinDuration();

		return Long.compare(fstMinDuration, sndMinDuration);
	}

}
