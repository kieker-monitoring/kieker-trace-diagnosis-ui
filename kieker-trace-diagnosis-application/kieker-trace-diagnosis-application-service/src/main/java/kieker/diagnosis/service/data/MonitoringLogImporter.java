package kieker.diagnosis.service.data;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.carrotsearch.hppc.IntByteHashMap;
import com.carrotsearch.hppc.IntByteMap;
import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;
import com.carrotsearch.hppc.LongArrayList;
import com.carrotsearch.hppc.LongObjectHashMap;
import com.carrotsearch.hppc.LongObjectMap;

import kieker.common.record.flow.trace.TraceMetadata;
import kieker.common.record.flow.trace.operation.AfterOperationEvent;
import kieker.common.record.flow.trace.operation.AfterOperationFailedEvent;
import kieker.common.record.flow.trace.operation.BeforeOperationEvent;
import kieker.common.record.misc.KiekerMetadataRecord;
import kieker.diagnosis.architecture.exception.BusinessException;
import kieker.diagnosis.architecture.monitoring.MonitoringProbe;

final class MonitoringLogImporter {

	private static final TimeUnit DESTINATION_TIMESTAMP_TIME_UNIT = TimeUnit.MILLISECONDS;
	private static final TimeUnit DESTINATION_DURATION_TIME_UNIT = TimeUnit.NANOSECONDS;
	private static final Pattern cvMappingFileEntryPattern = Pattern.compile( "\\$(\\d*)=(.*)" );

	private final ResourceBundle ivResourceBundle = ResourceBundle.getBundle( getClass( ).getName( ) );

	private final LongObjectMap<String> ivHostMap = new LongObjectHashMap<>( );
	private final LongObjectMap<List<MethodCall>> ivReconstructionMap = new LongObjectHashMap<>( );
	private final IntObjectMap<String> ivStringMapping = new IntObjectHashMap<>( );
	private final IntByteMap ivIgnoredRecordsSizeMap = new IntByteHashMap( );
	private boolean ivStreamCorrupt = false;
	private Exception ivException = null;
	private MonitoringLogService ivMonitoringLogService;

	private int ivBeforeOperationEventKey;
	private int ivAfterOperationEventKey;
	private int ivAfterOperationFailedEventKey;
	private int ivTraceMetadataKey;
	private int ivKiekerMetadataRecordKey;
	private int ivIgnoredRecords;
	private int ivDanglingRecords;

	private TimeUnit ivSourceTimeUnit;

	public void importMonitoringLog( final File aDirectory, final MonitoringLogService aMonitoringLogService ) throws IOException, BusinessException {
		ivMonitoringLogService = aMonitoringLogService;

		final long tin = System.currentTimeMillis( );
		long processedBytes = 0;

		{
			final List<File> mappingFileContainingDirectories = findMappingFileContainingDirectories( aDirectory );

			for ( final File mappingFileContainingDirectory : mappingFileContainingDirectories ) {
				// Make sure that each directory starts with fresh fields
				clearFields( );

				loadMappingFile( mappingFileContainingDirectory );
				processedBytes = loadBinaryFiles( mappingFileContainingDirectory );
			}

			calculatePercentAndCollectMethods( );
			aggregateMethods( );
		}

		final long tout = System.currentTimeMillis( );
		final long duration = tout - tin;

		ivMonitoringLogService.setProcessDuration( duration );
		ivMonitoringLogService.setProcessedBytes( processedBytes );
		ivMonitoringLogService.setIgnoredRecords( ivIgnoredRecords );
		ivMonitoringLogService.setDanglingRecords( ivDanglingRecords );
		ivMonitoringLogService.setIncompleteTraces( ivReconstructionMap.size( ) );

		if ( ivStreamCorrupt ) {
			throw new BusinessException( ivResourceBundle.getString( "errorMessageStreamCorrupt" ), ivException );
		}
	}

	private void clearFields( ) {
		ivHostMap.clear( );
		ivReconstructionMap.clear( );
		ivStringMapping.clear( );
		ivIgnoredRecordsSizeMap.clear( );

		ivBeforeOperationEventKey = -1;
		ivAfterOperationEventKey = -1;
		ivAfterOperationFailedEventKey = -1;
		ivTraceMetadataKey = -1;
		ivKiekerMetadataRecordKey = -1;

		ivIgnoredRecords = 0;
		ivDanglingRecords = 0;

		// This is just necessary, if we have incomplete data and have to assume a time unit
		ivSourceTimeUnit = TimeUnit.NANOSECONDS;
	}

	private List<File> findMappingFileContainingDirectories( final File aDirectory ) throws IOException {
		final MonitoringProbe probe = new MonitoringProbe( getClass( ), "findMappingFileContainingDirectories(java.io.File)" );

		try {
			// Find recursive all directories which contain a mapping file from Kieker
			return Files.walk( aDirectory.toPath( ), Integer.MAX_VALUE, new FileVisitOption[0] ).map( path -> path.toFile( ) ).filter( file -> file.isDirectory( ) )
					.filter( directory -> new File( directory, "kieker.map" ).exists( ) ).collect( Collectors.toList( ) );
		} catch ( final Throwable t ) {
			probe.fail( t );
			throw t;
		} finally {
			probe.stop( );
		}
	}

	private void loadMappingFile( final File aDirectory ) throws IOException {
		final MonitoringProbe probe = new MonitoringProbe( getClass( ), "loadMappingFile(java.io.File)" );

		try {

			final File mappingFile = new File( aDirectory, "kieker.map" );
			final List<String> lines = Files.readAllLines( mappingFile.toPath( ) );

			for ( final String line : lines ) {
				final Matcher matcher = cvMappingFileEntryPattern.matcher( line );

				if ( matcher.find( ) ) {
					// Split the line into key and value
					final String key = matcher.group( 1 );
					final String value = matcher.group( 2 ).intern( );

					// Store the entry in our internal map
					final int intKey = Integer.parseInt( key );
					ivStringMapping.put( intKey, value );

					// We have to remember the mapping, if it is one of the records we are interested in.
					if ( ivBeforeOperationEventKey == -1 && BeforeOperationEvent.class.getName( ).equals( value ) ) {
						ivBeforeOperationEventKey = intKey;
					} else if ( ivAfterOperationEventKey == -1 && AfterOperationEvent.class.getName( ).equals( value ) ) {
						ivAfterOperationEventKey = intKey;
					} else if ( ivAfterOperationFailedEventKey == -1 && AfterOperationFailedEvent.class.getName( ).equals( value ) ) {
						ivAfterOperationFailedEventKey = intKey;
					} else if ( ivTraceMetadataKey == -1 && TraceMetadata.class.getName( ).equals( value ) ) {
						ivTraceMetadataKey = intKey;
					} else if ( ivKiekerMetadataRecordKey == -1 && KiekerMetadataRecord.class.getName( ).equals( value ) ) {
						ivKiekerMetadataRecordKey = intKey;
					}
				}
			}
		} catch ( final Throwable t ) {
			probe.fail( t );
			throw t;
		} finally {
			probe.stop( );
		}
	}

	private long loadBinaryFiles( final File aDirectory ) throws IOException {
		final MonitoringProbe probe = new MonitoringProbe( getClass( ), "loadBinaryFiles(java.io.File)" );

		try {
			// Find non-recursive all binary files and load them
			final List<Path> binaryFiles = Files.list( aDirectory.toPath( ) ).filter( f -> f.toString( ).toLowerCase( ).endsWith( ".bin" ) ).collect( Collectors.toList( ) );

			// Java cannot handle an exception in the map-part of the stream. Therefore we make the rest manually.
			long processedBytes = 0;
			for ( final Path binaryFile : binaryFiles ) {
				processedBytes += loadBinaryFile( binaryFile );
			}

			return processedBytes;
		} catch ( final Throwable t ) {
			probe.fail( t );
			throw t;
		} finally {
			probe.stop( );
		}
	}

	private int loadBinaryFile( final Path aBinaryFile ) throws IOException {
		final MonitoringProbe probe = new MonitoringProbe( getClass( ), "loadBinaryFile(java.nio.file.Path)" );

		try {
			final byte[] binaryContent = Files.readAllBytes( aBinaryFile );
			final ByteBuffer byteBuffer = ByteBuffer.wrap( binaryContent );

			try {
				while ( byteBuffer.hasRemaining( ) ) {
					final int recordKey = byteBuffer.getInt( );
					skipBytes( (byte) 8, byteBuffer ); // Ignore the logging timestamp

					if ( recordKey == ivBeforeOperationEventKey ) {
						readBeforeOperationEvent( byteBuffer );
					} else if ( recordKey == ivAfterOperationEventKey ) {
						readAfterOperationEvent( byteBuffer );
					} else if ( recordKey == ivAfterOperationFailedEventKey ) {
						readAfterOperationFailedEvent( byteBuffer );
					} else if ( recordKey == ivTraceMetadataKey ) {
						readTraceMetadata( byteBuffer );
					} else if ( recordKey == ivKiekerMetadataRecordKey ) {
						readKiekerMetadataRecord( byteBuffer );
					} else {
						// Expensive case. We have to find out which record we are dealing with and skip it
						readUnknownRecord( byteBuffer, recordKey );
					}
				}
			} catch ( final BufferUnderflowException ex ) {
				// The stream is incomplete. We still want to terminate the whole import in a useful manner.
				ivStreamCorrupt = true;
				ivException = ex;
			}

			return binaryContent.length;
		} catch ( final Throwable t ) {
			probe.fail( t );
			throw t;
		} finally {
			probe.stop( );
		}
	}

	private void skipBytes( final byte aCountBytes, final ByteBuffer aByteBuffer ) {
		aByteBuffer.position( aByteBuffer.position( ) + aCountBytes );
	}

	private void readBeforeOperationEvent( final ByteBuffer aByteBuffer ) {
		final long timestamp = aByteBuffer.getLong( ); // Timestamp
		final long traceId = aByteBuffer.getLong( ); // Trace Id
		skipBytes( (byte) 4, aByteBuffer ); // Ignore order index
		final String methodName = ivStringMapping.get( aByteBuffer.getInt( ) ); // Method name
		final String clazz = ivStringMapping.get( aByteBuffer.getInt( ) ); // Class name

		final List<MethodCall> methodList = ivReconstructionMap.get( traceId );

		if ( methodList == null ) {
			// This can happen if the data is incomplete and we have a method call, but not a trace record
			ivDanglingRecords++;
		} else {

			final MethodCall methodCall = new MethodCall( );
			final String host = ivHostMap.get( traceId );
			methodCall.setHost( host );
			methodCall.setClazz( clazz );
			methodCall.setMethod( methodName );

			methodCall.setTraceId( traceId );
			methodCall.setTraceDepth( 1 );
			methodCall.setTraceSize( 1 );
			methodCall.setTimestamp( timestamp );

			if ( !methodList.isEmpty( ) ) {
				// This is not the first entry and thus not the root of a method. Which means that this method is the child of the previous method.
				final int lastIndex = methodList.size( ) - 1;
				final MethodCall previousMethodCall = methodList.get( lastIndex );
				previousMethodCall.addChild( methodCall );
			}

			methodList.add( methodCall );
		}
	}

	private MethodCall readAfterOperationEvent( final ByteBuffer aByteBuffer ) {
		final long timestamp = aByteBuffer.getLong( ); // Timestamp
		final long traceId = aByteBuffer.getLong( ); // Trace Id
		skipBytes( (byte) ( 3 * 4 ), aByteBuffer ); // Ignore order index, method name and class name

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

	private void readAfterOperationFailedEvent( final ByteBuffer aByteBuffer ) {
		final MethodCall lastMethodCall = readAfterOperationEvent( aByteBuffer );

		final String exception = ivStringMapping.get( aByteBuffer.getInt( ) );

		// This can happen if the data is incomplete and we have a method call, but not a trace record

		if ( lastMethodCall != null ) {
			lastMethodCall.setException( exception );
		}
	}

	private void readTraceMetadata( final ByteBuffer aByteBuffer ) {
		final long traceId = aByteBuffer.getLong( );
		skipBytes( (byte) ( 8 + 4 ), aByteBuffer ); // Ignore thread id and session id

		final String host = ivStringMapping.get( aByteBuffer.getInt( ) ); // Hostname
		skipBytes( (byte) ( 8 + 4 ), aByteBuffer ); // Ignore parent trace Id and parent order Id

		ivReconstructionMap.put( traceId, new ArrayList<>( ) );
		ivHostMap.put( traceId, host.intern( ) );
	}

	private void readKiekerMetadataRecord( final ByteBuffer aByteBuffer ) {
		skipBytes( (byte) ( 4 * 4 + 1 + 8 ), aByteBuffer ); // Ignore a lot of fields...
		final String timeUnitName = ivStringMapping.get( aByteBuffer.getInt( ) ); // Time unit
		skipBytes( (byte) 8, aByteBuffer ); // Ignore the number of records

		ivSourceTimeUnit = TimeUnit.valueOf( timeUnitName );
	}

	private void readUnknownRecord( final ByteBuffer aByteBuffer, final int aRecordKey ) {
		byte size;

		if ( !ivIgnoredRecordsSizeMap.containsKey( aRecordKey ) ) {
			final String recordName = ivStringMapping.get( aRecordKey );
			try {
				final Class<?> recordClass = Class.forName( recordName );
				final Field sizeField = recordClass.getDeclaredField( "SIZE" );
				size = (byte) (int) sizeField.get( null );

				ivIgnoredRecordsSizeMap.put( aRecordKey, size );
			} catch ( final Exception ex ) {
				// We have no chance. We cannot skip the record, as we don't know its size.
				throw new RuntimeException( String.format( ivResourceBundle.getString( "errorMessageUnknownRecord" ), recordName ), ex );
			}
		} else {
			size = ivIgnoredRecordsSizeMap.get( aRecordKey );
		}

		ivIgnoredRecords++;
		skipBytes( size, aByteBuffer );
	}

	private void calculatePercentAndCollectMethods( ) {
		final MonitoringProbe probe = new MonitoringProbe( getClass( ), "calculatePercentAndCollectMethods()" );

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
		final MonitoringProbe probe = new MonitoringProbe( getClass( ), "aggregateMethods()" );

		try {
			final Map<AggregationKey, MethodCall> aggregationMapWithExemplaricMethodCall = new HashMap<>( );
			final Map<AggregationKey, LongArrayList> aggregationMapWithDuration = new HashMap<>( );

			// Aggregate the methods. We perform only the key calculation in parallel, as the put into the aggregation maps would be slower due to
			// synchronization.
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

			// Now we can calculate the aggregated methods based on the aggregation maps. As we have no "complex" put-if-absent-part here (as above), we do this
			// in parallel.
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
			final AggregationKey other = (AggregationKey) obj;
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
