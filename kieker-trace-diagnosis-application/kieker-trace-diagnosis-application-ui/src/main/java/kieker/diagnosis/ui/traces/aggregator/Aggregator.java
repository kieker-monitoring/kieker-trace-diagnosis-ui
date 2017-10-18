/*************************************************************************** 
 * Copyright 2015-2017 Kieker Project (http://kieker-monitoring.net)         
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

package kieker.diagnosis.ui.traces.aggregator;

import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import kieker.diagnosis.service.data.MethodCall;

/**
 * Implementation of this class are responsible for aggregating method calls in a trace by various parameters.
 *
 * @author Nils Christian Ehmke
 */
public abstract class Aggregator {

	private final ResourceBundle ivResourceBundle = ResourceBundle.getBundle( Aggregator.class.getName( ) );

	/**
	 * Aggregates the given list of method calls. This means that the resulting list should contain real method calls and (aggregated) pseudo method calls.
	 *
	 * @param aCalls
	 *            The method calls to aggregate.
	 *
	 * @return The aggregated list.
	 */
	public abstract List<MethodCall> aggregate( List<MethodCall> aCalls );

	/**
	 * This is a helper method to aggregate a list of method calls into a single pseudo method call.
	 *
	 * @param aList
	 *            The methods to be aggregated.
	 *
	 * @return A single pseudo method call.
	 */
	protected final MethodCall aggregateToSingleCall( final List<MethodCall> aList ) {
		final double percent = aList.parallelStream( ).map( MethodCall::getPercent ).collect( Collectors.summingDouble( Float::doubleValue ) );
		final long duration = aList.parallelStream( ).map( MethodCall::getDuration ).collect( Collectors.summingLong( Long::longValue ) );
		final int traceDepth = aList.parallelStream( ).map( MethodCall::getTraceDepth ).max( Comparator.naturalOrder( ) ).get( );
		final int traceSize = aList.parallelStream( ).map( MethodCall::getTraceSize ).collect( Collectors.summingInt( Integer::intValue ) );

		// Create the dummy method call
		final MethodCall methodCall = new MethodCall( );

		methodCall.setHost( "-" );
		methodCall.setClazz( "-" );
		methodCall.setMethod( String.format( ivResourceBundle.getString( "methodCallsAggregated" ), aList.size( ) ) );
		methodCall.setTraceId( aList.get( 0 ).getTraceId( ) );
		methodCall.setPercent( (float) percent );
		methodCall.setDuration( duration );
		methodCall.setTraceDepth( traceDepth );
		methodCall.setTraceSize( traceSize );

		return methodCall;
	}

}
