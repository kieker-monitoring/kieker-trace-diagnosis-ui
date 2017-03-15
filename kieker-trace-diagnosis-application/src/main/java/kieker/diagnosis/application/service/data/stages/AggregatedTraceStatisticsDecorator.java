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

package kieker.diagnosis.application.service.data.stages;

import kieker.diagnosis.application.service.data.domain.AggregatedOperationCall;
import kieker.diagnosis.application.service.data.domain.AggregatedTrace;
import kieker.diagnosis.application.service.data.domain.OperationCall;
import kieker.diagnosis.application.service.data.domain.Trace;

import java.util.ArrayList;
import java.util.List;

import teetime.stage.basic.AbstractTransformation;

/**
 * This class is a {@code TeeTime} stage adding statistics (via the corresponding setters) to instances of {@link AggregatedTrace}. The traces are forwarded to
 * the output port.
 *
 * @author Nils Christian Ehmke
 */
final class AggregatedTraceStatisticsDecorator extends AbstractTransformation<AggregatedTrace, AggregatedTrace> {

	@Override
	public void execute( final AggregatedTrace aTrace ) {
		addNumberOfCalls( aTrace.getRootOperationCall( ), aTrace.getTraces( ).size( ) );
		addDurationStatistics( aTrace );

		// The references are no longer needed
		aTrace.getTraces( ).clear( );

		getOutputPort( ).send( aTrace );
	}

	private void addNumberOfCalls( final AggregatedOperationCall aCall, final int aCalls ) {
		aCall.setCalls( aCalls );

		for ( final AggregatedOperationCall child : aCall.getChildren( ) ) {
			addNumberOfCalls( child, aCalls );
		}
	}

	private void addDurationStatistics( final AggregatedTrace aTrace ) {
		final TraceDurationVisitor traceDurationVisitor = new TraceDurationVisitor( );

		for ( final Trace t : aTrace.getTraces( ) ) {
			traceDurationVisitor.visit( t );
		}

		traceDurationVisitor.addDurationStatistics( aTrace );
	}

	/**
	 * @author Nils Christian Ehmke
	 */
	private static final class TraceDurationVisitor {

		private final List<List<Long>> ivDurationsPerEdge = new ArrayList<>( );
		private int ivEdgeIndex;

		public void visit( final Trace aTrace ) {
			ivEdgeIndex = -1;
			visit( aTrace.getRootOperationCall( ) );
		}

		private void visit( final OperationCall aRootOperationCall ) {
			ivEdgeIndex++;
			if ( ivDurationsPerEdge.size( ) <= ivEdgeIndex ) {
				ivDurationsPerEdge.add( new ArrayList<Long>( ) );
			}

			final List<Long> durationsOfCurrentEdge = ivDurationsPerEdge.get( ivEdgeIndex );

			durationsOfCurrentEdge.add( aRootOperationCall.getDuration( ) );

			for ( final OperationCall child : aRootOperationCall.getChildren( ) ) {
				visit( child );
			}
		}

		public void addDurationStatistics( final AggregatedTrace aTrace ) {
			ivEdgeIndex = -1;
			addDurationStatistics( aTrace.getRootOperationCall( ) );
		}

		private void addDurationStatistics( final AggregatedOperationCall aRootOperationCall ) {
			ivEdgeIndex++;

			final List<Long> durationsOfCurrentEdge = ivDurationsPerEdge.get( ivEdgeIndex );

			final Statistics statistics = StatisticsUtil.calculateStatistics( durationsOfCurrentEdge );
			aRootOperationCall.setMinDuration( statistics.getMinDuration( ) );
			aRootOperationCall.setMaxDuration( statistics.getMaxDuration( ) );
			aRootOperationCall.setMeanDuration( statistics.getMeanDuration( ) );
			aRootOperationCall.setTotalDuration( statistics.getTotalDuration( ) );
			aRootOperationCall.setMedianDuration( statistics.getMedianDuration( ) );

			for ( final AggregatedOperationCall child : aRootOperationCall.getChildren( ) ) {
				addDurationStatistics( child );
			}
		}

	}

}
