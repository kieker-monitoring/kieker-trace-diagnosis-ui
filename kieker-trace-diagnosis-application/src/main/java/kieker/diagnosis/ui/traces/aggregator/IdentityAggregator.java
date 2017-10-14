package kieker.diagnosis.ui.traces.aggregator;

import java.util.List;

import kieker.diagnosis.service.data.MethodCall;

/**
 * This aggregator is a pseudo aggregator which does not do anything. It is basically the identity function on method call aggregation.
 *
 * @author Nils Christian Ehmke
 */
public final class IdentityAggregator extends Aggregator {

	@Override
	public List<MethodCall> aggregate( final List<MethodCall> aCalls ) {
		return aCalls;
	}

}
