/***************************************************************************
 * Copyright 2015-2018 Kieker Project (http://kieker-monitoring.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/

package kieker.diagnosis.ui.complex.traces.aggregator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import kieker.diagnosis.backend.data.MethodCall;

/**
 * An abstract base class for aggregators which aggregate method calls within a trace by a specific property of the method call.
 *
 * @author Nils Christian Ehmke
 */
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
