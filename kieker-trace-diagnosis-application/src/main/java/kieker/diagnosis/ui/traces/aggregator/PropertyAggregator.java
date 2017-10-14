package kieker.diagnosis.ui.traces.aggregator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import kieker.diagnosis.service.data.MethodCall;

public abstract class PropertyAggregator extends Aggregator {

	private final int ivMaxCalls;

	public PropertyAggregator( final int aMaxCalls ) {
		ivMaxCalls = aMaxCalls;
	}

	@Override
	public final List<MethodCall> aggregate( final List<MethodCall> aCalls ) {
		final List<MethodCall> accepted = new ArrayList<>( );

		// Sort the methods by the comparator of the concrete implementation
		final Iterator<MethodCall> iterator = aCalls.stream( ).sorted( getComparator( ) ).iterator( );

		// Get the first n method calls (where n is the maximal numbers of allowed method calls)
		int maxCalls = ivMaxCalls;
		while ( maxCalls > 0 && iterator.hasNext( ) ) {
			accepted.add( iterator.next( ) );
			maxCalls--;
		}

		// The remaining method calls - if there are any - have to be aggregated.
		final List<MethodCall> toBeAggregated = new ArrayList<>( );
		while ( iterator.hasNext( ) ) {
			toBeAggregated.add( iterator.next( ) );
		}

		if ( toBeAggregated.size( ) > 1 ) {
			final MethodCall methodCall = aggregateToSingleCall( toBeAggregated );
			accepted.add( methodCall );
		} else {
			// If the list is empty or contains only a single method call, we do not want to aggregate
			accepted.addAll( toBeAggregated );
		}

		return accepted;
	}

	protected abstract Comparator<MethodCall> getComparator( );

}
