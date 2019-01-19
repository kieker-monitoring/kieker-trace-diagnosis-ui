/***************************************************************************
 * Copyright 2015-2019 Kieker Project (http://kieker-monitoring.net)
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

import java.io.File;
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

import kieker.diagnosis.backend.data.AggregatedMethodCall;
import kieker.diagnosis.backend.data.MethodCall;
import kieker.diagnosis.backend.data.exception.CorruptStreamException;

public class Repository {

	private static final TimeUnit DESTINATION_TIMESTAMP_TIME_UNIT = TimeUnit.MILLISECONDS;
	private static final TimeUnit DESTINATION_DURATION_TIME_UNIT = TimeUnit.NANOSECONDS;

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( Repository.class.getName( ) );

	private final LongObjectMap<String> hostMap = new LongObjectHashMap<>( );
	private final LongObjectMap<List<MethodCall>> reconstructionMap = new LongObjectHashMap<>( );
	private boolean streamCorrupt = false;
	private Exception exception = null;

	private final List<MethodCall> traceRoots = new ArrayList<>( );
	private final List<AggregatedMethodCall> aggreatedMethods = new ArrayList<>( );
	private final List<MethodCall> methods = new ArrayList<>( );

	private int ignoredRecords;
	private int danglingRecords;
	private TimeUnit sourceTimeUnit;
	private long processedBytes;
	private long processDuration;
	private boolean dataAvailable = false;
	private int incompleteTraces;
	private String directory;

	void processBeforeOperationEvent( final long timestamp, final long traceId ) {
		final List<MethodCall> methodList = reconstructionMap.get( traceId );

		if ( methodList == null ) {
			// This can happen if the data is incomplete and we have a method call, but not a trace record
			danglingRecords++;
		} else {

			final MethodCall methodCall = new MethodCall( );
			final String host = hostMap.get( traceId );
			methodCall.setHost( host );

			methodCall.setTraceId( traceId );
			methodCall.setTraceDepth( 1 );
			methodCall.setTraceSize( 1 );
			methodCall.setTimestamp( timestamp );

			if ( !methodList.isEmpty( ) ) {
				// This is not the first entry and thus not the root of a method. Which means that this method is the
				// child of the
				// previous method.
				final int lastIndex = methodList.size( ) - 1;
				final MethodCall previousMethodCall = methodList.get( lastIndex );
				previousMethodCall.addChild( methodCall );
			}

			methodList.add( methodCall );
		}
	}

	MethodCall processAfterOperationEvent( final long timestamp, final long traceId, final String methodName, final String clazz ) {
		final List<MethodCall> methodList = reconstructionMap.get( traceId );

		if ( methodList == null ) {
			// This can happen if the data is incomplete and we have a method call, but not a trace record
			danglingRecords++;
			return null;
		}

		// This event closes the last method call from the trace
		final int lastIndex = methodList.size( ) - 1;
		final MethodCall lastMethodCall = methodList.get( lastIndex );
		methodList.remove( lastIndex );

		long duration = timestamp - lastMethodCall.getTimestamp( );
		// Make sure that the duration is always in nanoseconds
		if ( DESTINATION_DURATION_TIME_UNIT != sourceTimeUnit ) {
			duration = DESTINATION_DURATION_TIME_UNIT.convert( duration, sourceTimeUnit );
		}
		lastMethodCall.setDuration( duration );
		lastMethodCall.setClazz( clazz );
		lastMethodCall.setMethod( methodName );

		// Make sure that the timestamp is always in milliseconds
		if ( DESTINATION_TIMESTAMP_TIME_UNIT != sourceTimeUnit ) {
			final long newTimestamp = DESTINATION_TIMESTAMP_TIME_UNIT.convert( lastMethodCall.getTimestamp( ), sourceTimeUnit );
			lastMethodCall.setTimestamp( newTimestamp );
		}

		// If the list is now empty, we just finished a whole trace
		if ( methodList.isEmpty( ) ) {
			// Remove the data we no longer need
			hostMap.remove( traceId );
			reconstructionMap.remove( traceId );

			// Add the trace to the container
			traceRoots.add( lastMethodCall );
		} else {
			final MethodCall previousMethodCall = methodList.get( methodList.size( ) - 1 );
			// We can calculate the trace size and the trace depth on-the-fly
			previousMethodCall.addToTraceSize( lastMethodCall.getTraceSize( ) );
			previousMethodCall.setTraceDepth( Math.max( previousMethodCall.getTraceDepth( ), lastMethodCall.getTraceDepth( ) + 1 ) );
		}

		return lastMethodCall;
	}

	void processTraceMetadata( final long traceId, final String host ) {
		reconstructionMap.put( traceId, new ArrayList<>( ) );
		hostMap.put( traceId, host.intern( ) );
	}

	public void processSourceTimeUnit( final String timeUnitName ) {
		sourceTimeUnit = TimeUnit.valueOf( timeUnitName );
	}

	public void processIgnoredRecord( ) {
		ignoredRecords++;
	}

	public void processProcessedBytes( final long bytes ) {
		processedBytes += bytes;
	}

	public void clearBeforeNextDirectory( ) {
		hostMap.clear( );
		reconstructionMap.clear( );

		// This is just necessary, if we have incomplete data and have to assume a time unit
		sourceTimeUnit = TimeUnit.NANOSECONDS;
	}

	public void processException( final Exception exception ) {
		streamCorrupt = true;
		this.exception = exception;
	}

	/**
	 * This method should be called after all readers performed their work and everything has been added to this
	 * repository. It performs the remaining calculation and transfers its data to the monitoring service (where still
	 * necessary).
	 *
	 * @throws CorruptStreamException
	 *             If the monitoring log stream was somehow corrupted.
	 */
	public void finish( ) throws CorruptStreamException {
		calculatePercentAndCollectMethods( );
		aggregateMethods( );

		incompleteTraces = reconstructionMap.size( );

		// We don't need the temporary data anymore
		hostMap.clear( );
		reconstructionMap.clear( );

		if ( streamCorrupt ) {
			throw new CorruptStreamException( RESOURCE_BUNDLE.getString( "errorMessageStreamCorrupt" ), exception );
		}
	}

	private void calculatePercentAndCollectMethods( ) {
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

		this.methods.addAll( methods );
	}

	private void aggregateMethods( ) {
		final Map<AggregationKey, MethodCall> aggregationMapWithExemplaricMethodCall = new HashMap<>( );
		final Map<AggregationKey, LongArrayList> aggregationMapWithDuration = new HashMap<>( );

		// Aggregate the methods. We perform only the key calculation in parallel, as the put into the aggregation
		// maps would be
		// slower due to synchronization.
		methods.parallelStream( ).map( method -> new AggregationKey( method.getHost( ), method.getClazz( ), method.getMethod( ), method.getException( ), method ) )
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

		// Now we can calculate the aggregated methods based on the aggregation maps. As we have no "complex"
		// put-if-absent-part
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
		aggreatedMethods.addAll( aggregatedMethodCalls );
	}

	public void clear( ) {
		dataAvailable = false;
		traceRoots.clear( );
		aggreatedMethods.clear( );
		methods.clear( );
	}

	public int getIgnoredRecords( ) {
		return ignoredRecords;
	}

	public List<MethodCall> getTraceRoots( ) {
		return traceRoots;
	}

	public void setDataAvailable( final File inputDirectory, final long tin ) {
		directory = inputDirectory.getAbsolutePath( );
		dataAvailable = true;

		final long tout = System.currentTimeMillis( );
		final long duration = tout - tin;
		processDuration = duration;
	}

	public List<AggregatedMethodCall> getAggreatedMethods( ) {
		return aggreatedMethods;
	}

	public List<MethodCall> getMethods( ) {
		return methods;
	}

	public long getProcessDuration( ) {
		return processDuration;
	}

	public long getProcessedBytes( ) {
		return processedBytes;
	}

	public int getDanglingRecords( ) {
		return danglingRecords;
	}

	public boolean isDataAvailable( ) {
		return dataAvailable;
	}

	public String getDirectory( ) {
		return directory;
	}

	public int getIncompleteTraces( ) {
		return incompleteTraces;
	}

	private static class AggregationKey {

		private final String host;
		private final String clazz;
		private final String method;
		private final String exception;
		private final MethodCall methodCall;
		private int ivHash;

		AggregationKey( final String host, final String clazz, final String method, final String exception, final MethodCall methodCall ) {
			this.host = host;
			this.clazz = clazz;
			this.method = method;
			this.exception = exception;
			this.methodCall = methodCall;

			calculateHash( );
		}

		private void calculateHash( ) {
			final int prime = 31;

			int result = 1;
			result = prime * result + ( clazz == null ? 0 : clazz.hashCode( ) );
			result = prime * result + ( exception == null ? 0 : exception.hashCode( ) );
			result = prime * result + ( host == null ? 0 : host.hashCode( ) );
			result = prime * result + ( method == null ? 0 : method.hashCode( ) );

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
			final AggregationKey other = (AggregationKey) obj;
			if ( clazz == null ) {
				if ( other.clazz != null ) {
					return false;
				}
			} else if ( !clazz.equals( other.clazz ) ) {
				return false;
			}
			if ( exception == null ) {
				if ( other.exception != null ) {
					return false;
				}
			} else if ( !exception.equals( other.exception ) ) {
				return false;
			}
			if ( host == null ) {
				if ( other.host != null ) {
					return false;
				}
			} else if ( !host.equals( other.host ) ) {
				return false;
			}
			if ( method == null ) {
				if ( other.method != null ) {
					return false;
				}
			} else if ( !method.equals( other.method ) ) {
				return false;
			}
			return true;
		}

		public MethodCall getMethodCall( ) {
			return methodCall;
		}

	}

}
