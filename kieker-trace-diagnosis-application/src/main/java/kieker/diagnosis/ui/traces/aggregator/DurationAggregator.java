package kieker.diagnosis.ui.traces.aggregator;

import java.util.Comparator;

import kieker.diagnosis.service.data.MethodCall;

public final class DurationAggregator extends PropertyAggregator {

	public DurationAggregator( final int aMaxCalls ) {
		super( aMaxCalls );
	}

	@Override
	protected Comparator<MethodCall> getComparator( ) {
		return ( aOp1, aOp2 ) -> Long.compare( aOp2.getDuration( ), aOp1.getDuration( ) );
	}

}
