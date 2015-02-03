package kieker.diagnosis.subview.aggregatedtraces.util;

import kieker.diagnosis.common.domain.AggregatedTrace;
import kieker.diagnosis.subview.util.AbstractTraceTreeColumnSortListener;

public final class AvgDurationSortListener extends AbstractTraceTreeColumnSortListener<AggregatedTrace> {

	private static final long serialVersionUID = 1L;

	@Override
	protected int compare(final AggregatedTrace fstTrace, final AggregatedTrace sndTrace) {
		final long fstAvgDuration = fstTrace.getRootOperationCall().getAvgDuration();
		final long sndAvgDuration = sndTrace.getRootOperationCall().getAvgDuration();

		return Long.compare(fstAvgDuration, sndAvgDuration);
	}

}
