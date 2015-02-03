package kieker.diagnosis.subview.traces.util;

import kieker.diagnosis.common.domain.Trace;
import kieker.diagnosis.subview.util.AbstractTraceTreeColumnSortListener;

public final class TraceIDSortListener extends AbstractTraceTreeColumnSortListener<Trace> {

	@Override
	protected int compare(final Trace fstTrace, final Trace sndTrace) {
		final long fstTraceID = fstTrace.getRootOperationCall().getTraceID();
		final long sndTraceID = sndTrace.getRootOperationCall().getTraceID();

		return Long.compare(fstTraceID, sndTraceID);
	}

}
