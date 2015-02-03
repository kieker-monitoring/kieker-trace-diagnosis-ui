package kieker.diagnosis.subview.traces.util;

import kieker.diagnosis.common.domain.Trace;
import kieker.diagnosis.subview.util.AbstractTraceTreeColumnSortListener;

public final class DurationSortListener extends AbstractTraceTreeColumnSortListener<Trace> {

	private static final long serialVersionUID = 1L;

	@Override
	protected int compare(final Trace fstTrace, final Trace sndTrace) {
		final long fstDuration = fstTrace.getRootOperationCall().getDuration();
		final long sndDuration = sndTrace.getRootOperationCall().getDuration();

		return Long.compare(fstDuration, sndDuration);
	}

}
