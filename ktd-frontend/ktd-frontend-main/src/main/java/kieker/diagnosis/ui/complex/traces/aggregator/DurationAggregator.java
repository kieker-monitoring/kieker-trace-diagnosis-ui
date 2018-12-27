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

import java.util.Comparator;

import kieker.diagnosis.backend.data.MethodCall;

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
