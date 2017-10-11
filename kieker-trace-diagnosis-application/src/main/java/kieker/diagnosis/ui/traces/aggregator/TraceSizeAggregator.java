package kieker.diagnosis.ui.traces.aggregator;

import java.util.Comparator;

import kieker.diagnosis.service.data.MethodCall;

public final class TraceSizeAggregator extends PropertyAggregator {

	public TraceSizeAggregator( final int aMaxCalls ) {
		super( aMaxCalls );
	}

	@Override
	protected Comparator<MethodCall> getComparator( ) {
		return ( aOp1, aOp2 ) -> Integer.compare( aOp2.getTraceSize( ), aOp1.getTraceSize( ) );
	}

}
