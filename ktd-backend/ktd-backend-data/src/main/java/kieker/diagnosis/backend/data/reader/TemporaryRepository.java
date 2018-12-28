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

package kieker.diagnosis.backend.data.reader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.LongArrayList;
import com.carrotsearch.hppc.LongObjectHashMap;
import com.carrotsearch.hppc.LongObjectMap;

import kieker.diagnosis.backend.base.exception.BusinessException;
import kieker.diagnosis.backend.data.AggregatedMethodCall;
import kieker.diagnosis.backend.data.MethodCall;
import kieker.diagnosis.backend.data.MonitoringLogService;
import kieker.diagnosis.backend.monitoring.MonitoringProbe;
import kieker.diagnosis.backend.monitoring.MonitoringUtil;

/**
 * This is a temporary storage used during an import of monitoring logs. Readers can use it to store the read data.
 *
 * @author Nils Christian Ehmke
 */
public final class TemporaryRepository {

	private static final TimeUnit DESTINATION_TIMESTAMP_TIME_UNIT = TimeUnit.MILLISECONDS;
	private static final TimeUnit DESTINATION_DURATION_TIME_UNIT = TimeUnit.NANOSECONDS;

	private final ResourceBundle ivResourceBundle = ResourceBundle.getBundle( getClass( ).getName( ) );

	private final LongObjectMap<String> ivHostMap = new LongObjectHashMap<>( );
	private final LongObjectMap<List<MethodCall>> ivReconstructionMap = new LongObjectHashMap<>( );
	private boolean ivStreamCorrupt = false;
	private Exception ivException = null;
	private final MonitoringLogService ivMonitoringLogService;

	private int ivIgnoredRecords;
	private int ivDanglingRecords;

	private TimeUnit ivSourceTimeUnit;
	private long ivProcessedBytes;

	public TemporaryRepository( final MonitoringLogService aMonitoringLogService ) {
		ivMonitoringLogService = aMonitoringLogService;
	}

	void processBeforeOperationEvent( final long timestamp, final long traceId ) {
		final List<MethodCall> methodList = ivReconstructionMap.get( traceId );

		if ( methodList == null ) {
			// This can happen if the data is incomplete and we have a method call, but not a trace record
			ivDanglingRecords++;
		} else {

			final MethodCall methodCall = new MethodCall( );
			final String host = ivHostMap.get( traceId );
			methodCall.setHost( host );

			methodCall.setTraceId( traceId );
			methodCall.setTraceDepth( 1 );
			methodCall.setTraceSize( 1 );
			methodCall.setTimestamp( timestamp );

			if ( !methodList.isEmpty( ) ) {
				// This is not the first entry and thus not the root of a method. Which means that this method is the child of the
				// previous method.
				final int lastIndex = methodList.size( ) - 1;
				final MethodCall previousMethodCall = methodList.get( lastIndex );
				previousMethodCall.addChild( methodCall );
			}

			methodList.add( methodCall );
		}
	}

	MethodCall processAfterOperationEvent( final long timestamp, final long traceId, final String methodName, final String clazz ) {
		final List<MethodCall> methodList = ivReconstructionMap.get( traceId );

		if ( methodList == null ) {
			// This can happen if the data is incomplete and we have a method call, but not a trace record
			ivDanglingRecords++;
			return null;
		}

		// This event closes the last method call from the trace
		final int lastIndex = methodList.size( ) - 1;
		final MethodCall lastMethodCall = methodList.get( lastIndex );
		methodList.remove( lastIndex );

		long duration = timestamp - lastMethodCall.getTimestamp( );
		// Make sure that the duration is always in nanoseconds
		if ( DESTINATION_DURATION_TIME_UNIT != ivSourceTimeUnit ) {
			duration = DESTINATION_DURATION_TIME_UNIT.convert( duration, ivSourceTimeUnit );
		}
		lastMethodCall.setDuration( duration );
		lastMethodCall.setClazz( clazz );
		lastMethodCall.setMethod( methodName );

		// Make sure that the timestamp is always in milliseconds
		if ( DESTINATION_TIMESTAMP_TIME_UNIT != ivSourceTimeUnit ) {
			final long newTimestamp = DESTINATION_TIMESTAMP_TIME_UNIT.convert( lastMethodCall.getTimestamp( ), ivSourceTimeUnit );
			lastMethodCall.setTimestamp( newTimestamp );
		}

		// If the list is now empty, we just finished a whole trace
		if ( methodList.isEmpty( ) ) {
			// Remove the data we no longer need
			ivHostMap.remove( traceId );
			ivReconstructionMap.remove( traceId );

			// Add the trace to the container
			ivMonitoringLogService.addTraceRoot( lastMethodCall );
		} else {
			final MethodCall previousMethodCall = methodList.get( methodList.size( ) - 1 );
			// We can calculate the trace size and the trace depth on-the-fly
			previousMethodCall.addToTraceSize( lastMethodCall.getTraceSize( ) );
			previousMethodCall.setTraceDepth( Math.max( previousMethodCall.getTraceDepth( ), lastMethodCall.getTraceDepth( ) + 1 ) );
		}

		return lastMethodCall;
	}

	void processTraceMetadata( final long traceId, final String host ) {
		ivReconstructionMap.put( traceId, new ArrayList<>( ) );
		ivHostMap.put( traceId, host.intern( ) );
	}

	public void processSourceTimeUnit( final String aTimeUnitName ) {
		ivSourceTimeUnit = TimeUnit.valueOf( aTimeUnitName );
	}

	public void processIgnoredRecord( ) {
		ivIgnoredRecords++;
	}

	public void processProcessedBytes( final long aBytes ) {
		ivProcessedBytes += aBytes;
	}

	public void clearBeforeNextDirectory( ) {
		ivHostMap.clear( );
		ivReconstructionMap.clear( );

		// This is just necessary, if we have incomplete data and have to assume a time unit
		ivSourceTimeUnit = TimeUnit.NANOSECONDS;
	}

	public void processException( final Exception aEx ) {
		ivStreamCorrupt = true;
		ivException = aEx;
	}

	/**
	 * This method should be called after all readers performed their work and everything has been added to this repository.
	 * It performs the remaining calculation and transfers its data to the monitoring service (where still necessary).
	 *
	 * @throws BusinessException
	 *                           If the monitoring log stream was somehow corrupted.
	 */
	public void finish( ) throws BusinessException {
		calculatePercentAndCollectMethods( );
		aggregateMethods( );

		ivMonitoringLogService.setProcessedBytes( ivProcessedBytes );
		ivMonitoringLogService.setIgnoredRecords( ivIgnoredRecords );
		ivMonitoringLogService.setDanglingRecords( ivDanglingRecords );
		ivMonitoringLogService.setIncompleteTraces( ivReconstructionMap.size( ) );

		if ( ivStreamCorrupt ) {
			throw new BusinessException( ivResourceBundle.getString( "errorMessageStreamCorrupt" ), ivException );
		}
	}

	private void calculatePercentAndCollectMethods( ) {
		final MonitoringProbe probe = MonitoringUtil.createMonitoringProbe( getClass( ), "calculatePercentAndCollectMethods()" );

		try {

			final List<MethodCall> traceRoots = ivMonitoringLogService.getTraceRoots( );

			final Stack<MethodCall> stack = new Stack<>( );
			stack.addAll( traceRoots );

			// The trace roots have always 100% of the time
			for ( final MethodCall traceRoot : traceRoots ) {
				traceRoot.setPercent( 100.0f );
			}

			final List<MethodCall> methods = new ArrayList<>( );

			while ( !stack.isEmpty( ) ) {
				// Get the next method call
				final MethodCall methodCall = stack.pop( );
				final long duration = methodCall.getDuration( );

				// Calculate the percent of each child
				final List<MethodCall> children = methodCall.getChildren( );
				for ( final MethodCall child : children ) {
					child.setPercent( child.getDuration( ) * 100.0f / duration );

					// Add the child to the stack
					stack.push( child );
				}

				methodCall.trimToSize( );
				methods.add( methodCall );
			}

			ivMonitoringLogService.addMethods( methods );
		} catch ( final Throwable t ) {
			probe.fail( t );
			throw t;
		} finally {
			probe.stop( );
		}
	}

	private void aggregateMethods( ) {
		final MonitoringProbe probe = MonitoringUtil.createMonitoringProbe( getClass( ), "aggregateMethods()" );

		try {
			final Map<AggregationKey, MethodCall> aggregationMapWithExemplaricMethodCall = new HashMap<>( );
			final Map<AggregationKey, LongArrayList> aggregationMapWithDuration = new HashMap<>( );

			// Aggregate the methods. We perform only the key calculation in parallel, as the put into the aggregation maps would be
			// slower due to synchronization.
			final List<MethodCall> methodCalls = ivMonitoringLogService.getMethods( );
			methodCalls.parallelStream( ).map( method -> new AggregationKey( method.getHost( ), method.getClazz( ), method.getMethod( ), method.getException( ), method ) )
					.sequential( ).forEach( key -> {
						LongArrayList durationlist = aggregationMapWithDuration.get( key );

						if ( durationlist == null ) {
							durationlist = new LongArrayList( );

							aggregationMapWithDuration.put( key, durationlist );
							aggregationMapWithExemplaricMethodCall.put( key, key.getMethodCall( ) );
						}

						durationlist.add( key.getMethodCall( ).getDuration( ) );
					} );

			// As we need the median, we have to have sorted lists. The sorting can be performed in parallel.
			aggregationMapWithDuration.values( ).parallelStream( ).forEach( list -> Arrays.sort( list.buffer, 0, list.size( ) ) );

			// Now we can calculate the aggregated methods based on the aggregation maps. As we have no "complex" put-if-absent-part
			// here (as above), we do this in parallel.
			final Queue<AggregatedMethodCall> aggregatedMethodCalls = new ConcurrentLinkedQueue<>( );
			aggregationMapWithExemplaricMethodCall.keySet( ).parallelStream( ).forEach( key -> {
				final MethodCall exemplaricMethodCall = aggregationMapWithExemplaricMethodCall.get( key );
				final LongArrayList durationList = aggregationMapWithDuration.get( key );

				// We need the sum of the durations
				long durationSum = 0;
				final int size = durationList.size( );
				final long[] array = durationList.buffer;
				for ( int index = 0; index < size; index++ ) {
					durationSum += array[index];
				}

				// Now assemble the aggregated method call
				final AggregatedMethodCall aggregatedMethodCall = new AggregatedMethodCall( );
				aggregatedMethodCall.setAvgDuration( durationSum / size );
				aggregatedMethodCall.setTotalDuration( durationSum );
				aggregatedMethodCall.setHost( exemplaricMethodCall.getHost( ) );
				aggregatedMethodCall.setClazz( exemplaricMethodCall.getClazz( ) );
				aggregatedMethodCall.setMethod( exemplaricMethodCall.getMethod( ) );
				aggregatedMethodCall.setException( exemplaricMethodCall.getException( ) );
				aggregatedMethodCall.setCount( size );
				aggregatedMethodCall.setMedianDuration( array[size / 2] );
				aggregatedMethodCall.setMinDuration( array[0] );
				aggregatedMethodCall.setMaxDuration( array[size - 1] );

				aggregatedMethodCalls.add( aggregatedMethodCall );
			} );

			// Add them now to the service, as we are out of the parallel stream
			ivMonitoringLogService.addAggregatedMethods( aggregatedMethodCalls );
		} catch ( final Throwable t ) {
			probe.fail( t );
			throw t;
		} finally {
			probe.stop( );
		}
	}

	private static class AggregationKey {

		private final String ivHost;
		private final String ivClass;
		private final String ivMethod;
		private final String ivException;
		private final MethodCall ivMethodCall;
		private int ivHash;

		public AggregationKey( final String aHost, final String aClass, final String aMethod, final String aException, final MethodCall aMethodCall ) {
			ivHost = aHost;
			ivClass = aClass;
			ivMethod = aMethod;
			ivException = aException;
			ivMethodCall = aMethodCall;

			calculateHash( );
		}

		private void calculateHash( ) {
			final int prime = 31;

			int result = 1;
			result = prime * result + ( ivClass == null ? 0 : ivClass.hashCode( ) );
			result = prime * result + ( ivException == null ? 0 : ivException.hashCode( ) );
			result = prime * result + ( ivHost == null ? 0 : ivHost.hashCode( ) );
			result = prime * result + ( ivMethod == null ? 0 : ivMethod.hashCode( ) );

			// We calculate the hash eagerly and only once.
			ivHash = result;
		}

		@Override
		public int hashCode( ) {
			return ivHash;
		}

		@Override
		public boolean equals( final Object obj ) {
			if ( this == obj ) {
				return true;
			}
			if ( obj == null ) {
				return false;
			}
			if ( getClass( ) != obj.getClass( ) ) {
				return false;
			}
			final AggregationKey other = ( AggregationKey ) obj;
			if ( ivClass == null ) {
				if ( other.ivClass != null ) {
					return false;
				}
			} else if ( !ivClass.equals( other.ivClass ) ) {
				return false;
			}
			if ( ivException == null ) {
				if ( other.ivException != null ) {
					return false;
				}
			} else if ( !ivException.equals( other.ivException ) ) {
				return false;
			}
			if ( ivHost == null ) {
				if ( other.ivHost != null ) {
					return false;
				}
			} else if ( !ivHost.equals( other.ivHost ) ) {
				return false;
			}
			if ( ivMethod == null ) {
				if ( other.ivMethod != null ) {
					return false;
				}
			} else if ( !ivMethod.equals( other.ivMethod ) ) {
				return false;
			}
			return true;
		}

		public MethodCall getMethodCall( ) {
			return ivMethodCall;
		}

	}

}
