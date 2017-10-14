package kieker.diagnosis.ui.traces.aggregator;

import java.util.Comparator;

import kieker.diagnosis.service.data.MethodCall;

/**
 * This aggregator aggregates method calls by duration. That means that method calls in a trace with lower duration are aggregated into a single method call.
 *
 * @author Nils Christian Ehmke
 */
public final class DurationAggregator extends PropertyAggregator {

	public DurationAggregator( final int aMaxCalls ) {
		super( aMaxCalls );
	}

	@Override
	protected Comparator<MethodCall> getComparator( ) {
		return ( aOp1, aOp2 ) -> Long.compare( aOp2.getDuration( ), aOp1.getDuration( ) );
	}

}
