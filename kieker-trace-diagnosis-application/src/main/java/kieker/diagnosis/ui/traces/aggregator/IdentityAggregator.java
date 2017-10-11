package kieker.diagnosis.ui.traces.aggregator;

import java.util.List;

import kieker.diagnosis.service.data.MethodCall;

public final class IdentityAggregator extends Aggregator {

	@Override
	public List<MethodCall> aggregate( final List<MethodCall> aCalls ) {
		return aCalls;
	}

}
