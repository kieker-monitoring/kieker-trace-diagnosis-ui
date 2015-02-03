package kieker.diagnosis.subview.aggregatedtraces.util;

import kieker.diagnosis.common.domain.AggregatedTrace;
import kieker.diagnosis.subview.util.AbstractTraceTreeColumnSortListener;

public final class MaxDurationSortListener extends AbstractTraceTreeColumnSortListener<AggregatedTrace> {

	@Override
	protected int compare(final AggregatedTrace fstTrace, final AggregatedTrace sndTrace) {
		final long fstMaxDuration = fstTrace.getRootOperationCall().getMaxDuration();
		final long sndMaxDuration = sndTrace.getRootOperationCall().getMaxDuration();

		return Long.compare(fstMaxDuration, sndMaxDuration);
	}

}
