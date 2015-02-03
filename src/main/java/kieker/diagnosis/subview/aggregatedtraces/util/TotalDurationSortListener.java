package kieker.diagnosis.subview.aggregatedtraces.util;

import kieker.diagnosis.common.domain.AggregatedTrace;
import kieker.diagnosis.subview.util.AbstractTraceTreeColumnSortListener;

public final class TotalDurationSortListener extends AbstractTraceTreeColumnSortListener<AggregatedTrace> {

	@Override
	protected int compare(final AggregatedTrace fstTrace, final AggregatedTrace sndTrace) {
		final long fstToalDuration = fstTrace.getRootOperationCall().getTotalDuration();
		final long sndTotalDuration = sndTrace.getRootOperationCall().getTotalDuration();

		return Long.compare(fstToalDuration, sndTotalDuration);
	}

}
