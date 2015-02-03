package kieker.diagnosis.subview.aggregatedtraces.util;

import kieker.diagnosis.common.domain.AggregatedTrace;
import kieker.diagnosis.subview.util.AbstractTraceTreeColumnSortListener;

public final class MeanDurationSortListener extends AbstractTraceTreeColumnSortListener<AggregatedTrace> {

	private static final long serialVersionUID = 1L;

	@Override
	protected int compare(final AggregatedTrace fstTrace, final AggregatedTrace sndTrace) {
		final long fstMeanDuration = fstTrace.getRootOperationCall().getMeanDuration();
		final long sndMeanDuration = sndTrace.getRootOperationCall().getMeanDuration();

		return Long.compare(fstMeanDuration, sndMeanDuration);
	}

}
