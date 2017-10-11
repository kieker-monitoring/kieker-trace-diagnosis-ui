package kieker.diagnosis.ui.traces.aggregator;

import java.util.ArrayList;
import java.util.List;

import kieker.diagnosis.service.data.MethodCall;

public final class ThresholdAggregator extends Aggregator {

	private final float ivThreshold;

	public ThresholdAggregator( final float aThreshold ) {
		ivThreshold = aThreshold;
	}

	@Override
	public List<MethodCall> aggregate( final List<MethodCall> calls ) {
		final List<MethodCall> underThreshold = new ArrayList<>( );
		final List<MethodCall> overThreshold = new ArrayList<>( );

		for ( final MethodCall call : calls ) {
			if ( call.getPercent( ) < ivThreshold ) {
				underThreshold.add( call );
			} else {
				overThreshold.add( call );
			}
		}

		if ( underThreshold.size( ) > 1 ) {
			final MethodCall methodCall = aggregateToSingleCall( underThreshold );
			overThreshold.add( methodCall );
		} else {
			// if the list is empty or contains only a single method call, we do not want to aggregate
			overThreshold.addAll( underThreshold );
		}

		return overThreshold;
	}

}
