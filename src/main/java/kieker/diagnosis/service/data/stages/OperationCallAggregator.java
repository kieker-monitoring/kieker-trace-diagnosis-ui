/***************************************************************************
 * Copyright 2015-2016 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.service.data.stages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kieker.diagnosis.service.data.domain.AggregatedOperationCall;
import kieker.diagnosis.service.data.domain.OperationCall;
import teetime.stage.basic.AbstractTransformation;

/**
 * @author Nils Christian Ehmke
 */
public final class OperationCallAggregator extends AbstractTransformation<OperationCall, AggregatedOperationCall> {

	private final Map<String, List<OperationCall>> ivAggregationMap = new HashMap<>( );

	@Override
	protected void execute( final OperationCall aCall ) {
		final String key = aCall.getContainer( ) + "," + aCall.getComponent( ) + "," + aCall.getOperation( ) + ", " + aCall.getFailedCause( );

		if ( !this.ivAggregationMap.containsKey( key ) ) {
			final List<OperationCall> aggregationList = new ArrayList<>( );
			this.ivAggregationMap.put( key, aggregationList );
		}
		this.ivAggregationMap.get( key ).add( aCall );
	}

	@Override
	public void onTerminating( ) throws Exception {
		for ( final List<OperationCall> aggregationList : this.ivAggregationMap.values( ) ) {
			final List<Long> durations = this.extractDurations( aggregationList );
			final Statistics statistics = StatisticsUtility.calculateStatistics( durations );
			super.getOutputPort( ).send( new AggregatedOperationCall( aggregationList.get( 0 ).getContainer( ), aggregationList.get( 0 ).getComponent( ),
					aggregationList.get( 0 ).getOperation( ), aggregationList.get( 0 ).getFailedCause( ), statistics.getTotalDuration( ),
					statistics.getMedianDuration( ), statistics.getMinDuration( ), statistics.getMaxDuration( ), statistics.getMeanDuration( ),
					aggregationList.size( ) ) );
		}

		super.onTerminating( );
	}

	private List<Long> extractDurations( final List<OperationCall> aCallList ) {
		final List<Long> result = new ArrayList<>( );

		for ( final OperationCall call : aCallList ) {
			result.add( call.getDuration( ) );
		}

		return result;
	}

}
