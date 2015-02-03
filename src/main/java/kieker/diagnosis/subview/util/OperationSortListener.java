package kieker.diagnosis.subview.util;

import kieker.diagnosis.common.domain.AbstractTrace;

public final class OperationSortListener extends AbstractTraceTreeColumnSortListener<AbstractTrace<?>> {

	@Override
	protected int compare(final AbstractTrace<?> fstTrace, final AbstractTrace<?> sndTrace) {
		final String fstOperation = fstTrace.getRootOperationCall().getOperation();
		final String sndOperation = sndTrace.getRootOperationCall().getOperation();

		return fstOperation.compareTo(sndOperation);
	}

}
