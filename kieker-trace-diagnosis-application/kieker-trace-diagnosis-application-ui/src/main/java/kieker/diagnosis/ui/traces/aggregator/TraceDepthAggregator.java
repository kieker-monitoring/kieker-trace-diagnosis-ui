package kieker.diagnosis.ui.traces.aggregator;

import java.util.Comparator;

import kieker.diagnosis.service.data.MethodCall;

/**
 * This aggregator aggregates method calls by trace depth. That means that method calls in a trace with lower trace depth are aggregated into a single method
 * call.
 *
 * @author Nils Christian Ehmke
 */
public final class TraceDepthAggregator extends PropertyAggregator {

	public TraceDepthAggregator( final int aMaxCalls ) {
		super( aMaxCalls );
	}

	@Override
	protected Comparator<MethodCall> getComparator( ) {
		return ( aOp1, aOp2 ) -> Integer.compare( aOp2.getTraceDepth( ), aOp1.getTraceDepth( ) );
	}

}
