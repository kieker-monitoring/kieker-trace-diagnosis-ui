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

package kieker.diagnosis.frontend.complex.traces.aggregator;

import java.util.ArrayList;
import java.util.List;

import kieker.diagnosis.backend.data.MethodCall;

/**
 * This aggregator aggregates method calls by a given threshold. That means that method calls below this threshold are aggregated into a single method call.
 *
 * @author Nils Christian Ehmke
 */
public final class ThresholdAggregator extends Aggregator {

	private final float ivThreshold;

	public ThresholdAggregator( final float aThreshold ) {
		ivThreshold = aThreshold;
	}

	@Override
	public List<MethodCall> aggregate( final List<MethodCall> calls ) {
		final List<MethodCall> underThreshold = new ArrayList<>( );
		final List<MethodCall> overThreshold = new ArrayList<>( );

		// Separate the method calls with the threshold.
		for ( final MethodCall call : calls ) {
			if ( call.getPercent( ) < ivThreshold ) {
				underThreshold.add( call );
			} else {
				overThreshold.add( call );
			}
		}

		if ( underThreshold.size( ) > 1 ) {
			// If there are multiple method calls below the threshold, we want to aggregate them
			final MethodCall methodCall = aggregateToSingleCall( underThreshold );
			overThreshold.add( methodCall );
		} else {
			// If the list is empty or contains only a single method call, we do not want to aggregate
			overThreshold.addAll( underThreshold );
		}

		return overThreshold;
	}

}
