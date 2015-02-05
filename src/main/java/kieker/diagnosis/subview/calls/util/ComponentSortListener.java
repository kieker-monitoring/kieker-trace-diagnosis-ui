package kieker.diagnosis.subview.calls.util;

import kieker.diagnosis.common.domain.OperationCall;
import kieker.diagnosis.subview.util.AbstractCallTableColumnSortListener;

public class ComponentSortListener extends AbstractCallTableColumnSortListener<OperationCall> {

	private static final long serialVersionUID = 1L;

	@Override
	protected int compare(final OperationCall fstCall, final OperationCall sndCall) {
		return fstCall.getComponent().compareTo(sndCall.getComponent());
	}

}
