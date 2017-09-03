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

package kieker.diagnosis.application.gui.components.treetable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.scene.control.TreeItem;
import kieker.diagnosis.application.service.data.domain.OperationCall;
import kieker.diagnosis.application.service.properties.MethodCallAggregation;

/**
 * @author Nils Christian Ehmke
 */
public final class LazyOperationCallTreeItem extends AbstractLazyOperationCallTreeItem<OperationCall> {

	private static final String BLANK = "-";
	private static final String METHOD_CALLS_AGGREGATED;
	private static final String UNMONITORED_TIME;

	private final boolean ivShowUnmonitoredTime;
	private final boolean ivPercentCalculation;
	private final MethodCallAggregation ivMethodCallAggregation;
	private final float ivThreshold;
	private final int ivMaxCalls;

	static {
		final String bundleBaseName = "kieker.diagnosis.application.gui.components.components";
		final ResourceBundle resourceBundle = ResourceBundle.getBundle( bundleBaseName, Locale.getDefault( ) );

		METHOD_CALLS_AGGREGATED = resourceBundle.getString( "methodCallsAggregated" );
		UNMONITORED_TIME = resourceBundle.getString( "unmonitoredTime" );
	}

	public LazyOperationCallTreeItem( final OperationCall aValue, final boolean aShowUnmonitoredTime, final boolean aPercentCalculation,
			final MethodCallAggregation aMethodCallAggregation, final float aThreshold, final int aMaxCalls ) {
		super( aValue );
		ivShowUnmonitoredTime = aShowUnmonitoredTime;
		ivPercentCalculation = aPercentCalculation;
		ivMethodCallAggregation = aMethodCallAggregation;
		ivThreshold = aThreshold;
		ivMaxCalls = aMaxCalls;
	}

	@Override
	protected void initializeChildren( ) {
		final List<TreeItem<OperationCall>> result = new ArrayList<>( );

		final List<OperationCall> childrenOperationCalls = getValue( ).getChildren( );

		// Don't initialize, if our list is empty. Otherwise we will run into an infinite loop with
		// some methods calling getChildren on the aggregation pseudo-node.
		if ( childrenOperationCalls.isEmpty( ) ) {
			return;
		}

		if ( ivShowUnmonitoredTime ) {
			double percent = ivPercentCalculation ? getValue( ).getPercent( ) : 100.0;
			long duration = getValue( ).getDuration( );

			for ( final OperationCall child : childrenOperationCalls ) {
				percent -= child.getPercent( );
				duration -= child.getDuration( );
			}

			final OperationCall call = new OperationCall( BLANK, BLANK, UNMONITORED_TIME, getValue( ).getTraceID( ), getValue( ).getTimestamp( ) );
			call.setPercent( (float) percent );
			call.setDuration( duration );
			result.add( new LazyOperationCallTreeItem( call, ivShowUnmonitoredTime, ivPercentCalculation, ivMethodCallAggregation, ivThreshold, ivMaxCalls ) );
		}

		switch ( ivMethodCallAggregation ) {
			case BY_DURATION:
				aggregateByDuration( result );
			break;
			case BY_THRESHOLD:
				aggregateByThreshold( result );
			break;
			case BY_TRACE_DEPTH:
				aggregateByDepth( result );
			break;
			case BY_TRACE_SIZE:
				aggregateBySize( result );
			break;
			case NONE:
			default:
				for ( final OperationCall child : childrenOperationCalls ) {
					result.add( new LazyOperationCallTreeItem( child, ivShowUnmonitoredTime, ivPercentCalculation, ivMethodCallAggregation, ivThreshold,
							ivMaxCalls ) );
				}
			break;

		}

		getChildren( ).setAll( result );
	}

	private void aggregateByThreshold( final List<TreeItem<OperationCall>> aResult ) {
		final List<OperationCall> underThreshold = new ArrayList<>( );
		for ( final OperationCall child : getValue( ).getChildren( ) ) {
			if ( child.getPercent( ) < ivThreshold ) {
				underThreshold.add( child );
			} else {
				aResult.add(
						new LazyOperationCallTreeItem( child, ivShowUnmonitoredTime, ivPercentCalculation, ivMethodCallAggregation, ivThreshold, ivMaxCalls ) );
			}
		}
		aggregate( aResult, underThreshold );
	}

	private void aggregateBySize( final List<TreeItem<OperationCall>> aResult ) {
		aggregateByProperty( aResult, ( aOp1, aOp2 ) -> Integer.compare( aOp2.getStackSize( ), aOp1.getStackSize( ) ) );
	}

	private void aggregateByDuration( final List<TreeItem<OperationCall>> aResult ) {
		aggregateByProperty( aResult, ( aOp1, aOp2 ) -> Long.compare( aOp2.getDuration( ), aOp1.getDuration( ) ) );
	}

	private void aggregateByDepth( final List<TreeItem<OperationCall>> aResult ) {
		aggregateByProperty( aResult, ( aOp1, aOp2 ) -> Integer.compare( aOp2.getStackDepth( ), aOp1.getStackDepth( ) ) );
	}

	private void aggregateByProperty( final List<TreeItem<OperationCall>> aResult, final Comparator<OperationCall> aComparator ) {
		final Iterator<OperationCall> iterator = getValue( ).getChildren( ).stream( ).sorted( aComparator ).iterator( );
		int maxCalls = ivMaxCalls;
		while ( maxCalls > 0 && iterator.hasNext( ) ) {
			aResult.add( new LazyOperationCallTreeItem( iterator.next( ), ivShowUnmonitoredTime, ivPercentCalculation, ivMethodCallAggregation, ivThreshold,
					ivMaxCalls ) );
			maxCalls--;
		}

		final List<OperationCall> toBeAggregated = new ArrayList<>( );
		while ( iterator.hasNext( ) ) {
			toBeAggregated.add( iterator.next( ) );
		}

		aggregate( aResult, toBeAggregated );
	}

	private void aggregate( final List<TreeItem<OperationCall>> aResult, final List<OperationCall> aToBeAggregated ) {
		if ( !aToBeAggregated.isEmpty( ) ) {
			final double percent = aToBeAggregated.parallelStream( ).map( OperationCall::getPercent ).collect( Collectors.summingDouble( Float::doubleValue ) );
			final long duration = aToBeAggregated.parallelStream( ).map( OperationCall::getDuration ).collect( Collectors.summingLong( Long::longValue ) );
			final int traceDepth = aToBeAggregated.parallelStream( ).map( OperationCall::getStackDepth ).max( Comparator.naturalOrder( ) ).get( );
			final int traceSize = aToBeAggregated.parallelStream( ).map( OperationCall::getStackSize ).collect( Collectors.summingInt( Integer::intValue ) );
			final OperationCall call = new OperationCall( BLANK, BLANK, aToBeAggregated.size( ) + " " + METHOD_CALLS_AGGREGATED, getValue( ).getTraceID( ),
					-1 );
			call.setPercent( (float) percent );
			call.setDuration( duration );
			call.setStackDepth( traceDepth );
			call.setStackSize( traceSize );
			aResult.add( new LazyOperationCallTreeItem( call, ivShowUnmonitoredTime, ivPercentCalculation, ivMethodCallAggregation, ivThreshold, ivMaxCalls ) );
		}
	}

}
