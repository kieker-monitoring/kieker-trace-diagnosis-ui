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

package kieker.diagnosis.backend.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.carrotsearch.hppc.ByteArrayList;

import kieker.common.record.AbstractMonitoringRecord;
import kieker.common.record.flow.trace.TraceMetadata;
import kieker.common.record.flow.trace.operation.AfterOperationEvent;
import kieker.common.record.flow.trace.operation.AfterOperationFailedEvent;
import kieker.common.record.flow.trace.operation.BeforeOperationEvent;
import kieker.common.record.io.BinaryValueSerializer;
import kieker.common.record.io.IValueSerializer;
import kieker.common.record.misc.KiekerMetadataRecord;
import kieker.common.record.system.CPUUtilizationRecord;
import kieker.common.registry.writer.IWriterRegistry;
import kieker.common.registry.writer.WriterRegistry;
import kieker.diagnosis.backend.data.exception.CorruptStreamException;
import kieker.diagnosis.backend.data.exception.ImportFailedException;
import kieker.diagnosis.backend.data.reader.Repository;

/**
 * Test class for the {@link MonitoringLogService}.
 *
 * @author Nils Christian Ehmke
 */
@DisplayName( "Unit-Test for MonitoringLogService" )
public class MonitoringLogServiceTest {

	private ByteArrayList byteList;
	private IWriterRegistry<String> stringRegistry;
	private Map<Integer, String> stringRegistryContent;
	private MonitoringLogService service;
	private Repository repository;

	@BeforeEach
	public void setUp( ) {
		byteList = new ByteArrayList( );
		stringRegistryContent = new HashMap<>( );
		stringRegistry = new WriterRegistry( ( str, id ) -> stringRegistryContent.put( id, str ) );

		repository = new Repository( );
		service = new MonitoringLogService( repository );
	}

	@Test
	@DisplayName( "Test with an empty directory" )
	public void testEmptyDirectory( @TempDir final Path tempDir ) throws CorruptStreamException, ImportFailedException {
		assertThrows( ImportFailedException.class, ( ) -> service.importMonitoringLog( tempDir, ImportType.DIRECTORY ) );

	}

	@Test
	@DisplayName( "Test with a single trace" )
	public void testSingleTrace( @TempDir final Path tempDir ) throws Exception {
		// Prepare the data
		writeRecord( new TraceMetadata( 1L, 0L, "0", "host", 0L, 0 ) );
		writeRecord( new BeforeOperationEvent( System.currentTimeMillis( ), 1L, 0, "op1", "class1" ) );
		writeRecord( new BeforeOperationEvent( System.currentTimeMillis( ), 1L, 0, "op2", "class1" ) );
		writeRecord( new AfterOperationEvent( System.currentTimeMillis( ), 1L, 0, "op2", "class1" ) );
		writeRecord( new AfterOperationEvent( System.currentTimeMillis( ), 1L, 0, "op1", "class1" ) );
		writeMappingFile( tempDir );
		finishWriting( tempDir );

		// Import the directory
		service.importMonitoringLog( tempDir, ImportType.DIRECTORY );

		// Make sure that the import worked as intended
		assertThat( repository.getMethods( ) ).hasSize( 2 );
		assertThat( repository.getAggreatedMethods( ) ).hasSize( 2 );
		assertThat( repository.getTraceRoots( ) ).hasSize( 1 );
		assertThat( repository.getProcessedBytes( ) ).isGreaterThan( 0L );
	}

	@Test
	@DisplayName( "Test with two interleaves traces" )
	public void testTwoInterleavedTrace( @TempDir final Path tempDir ) throws Exception {
		// Prepare the data
		writeRecord( new TraceMetadata( 1L, 0L, "0", "host", 0L, 0 ) );
		writeRecord( new BeforeOperationEvent( System.currentTimeMillis( ), 1L, 0, "op1", "class1" ) );
		writeRecord( new BeforeOperationEvent( System.currentTimeMillis( ), 1L, 0, "op2", "class1" ) );
		writeRecord( new TraceMetadata( 2L, 0L, "0", "host", 0L, 0 ) );
		writeRecord( new BeforeOperationEvent( System.currentTimeMillis( ), 2L, 0, "op1", "class2" ) );
		writeRecord( new AfterOperationEvent( System.currentTimeMillis( ), 1L, 0, "op2", "class1" ) );
		writeRecord( new BeforeOperationEvent( System.currentTimeMillis( ), 2L, 0, "op2", "class2" ) );
		writeRecord( new AfterOperationEvent( System.currentTimeMillis( ), 2L, 0, "op2", "class2" ) );
		writeRecord( new AfterOperationEvent( System.currentTimeMillis( ), 1L, 0, "op1", "class1" ) );
		writeRecord( new AfterOperationEvent( System.currentTimeMillis( ), 2L, 0, "op1", "class2" ) );
		writeMappingFile( tempDir );
		finishWriting( tempDir );

		// Import the directory
		service.importMonitoringLog( tempDir, ImportType.DIRECTORY );

		// Make sure that the import worked as intended
		assertThat( repository.getMethods( ) ).hasSize( 4 );
		assertThat( repository.getAggreatedMethods( ) ).hasSize( 4 );
		assertThat( repository.getTraceRoots( ) ).hasSize( 2 );
		assertThat( repository.getProcessedBytes( ) ).isGreaterThan( 0L );
	}

	@Test
	@DisplayName( "Test with a failed trace" )
	public void testFailedTrace( @TempDir final Path tempDir ) throws Exception {
		// Prepare the data
		writeRecord( new TraceMetadata( 1L, 0L, "0", "host", 0L, 0 ) );
		writeRecord( new BeforeOperationEvent( System.currentTimeMillis( ), 1L, 0, "op1", "class1" ) );
		writeRecord( new BeforeOperationEvent( System.currentTimeMillis( ), 1L, 0, "op2", "class1" ) );
		writeRecord( new AfterOperationFailedEvent( System.currentTimeMillis( ), 1L, 0, "op2", "class1", "cause" ) );
		writeRecord( new AfterOperationFailedEvent( System.currentTimeMillis( ), 1L, 0, "op1", "class1", "cause" ) );
		writeMappingFile( tempDir );
		finishWriting( tempDir );

		// Import the directory
		service.importMonitoringLog( tempDir, ImportType.DIRECTORY );

		// Make sure that the import worked as intended
		assertThat( repository.getMethods( ) ).hasSize( 2 );
		assertThat( repository.getAggreatedMethods( ) ).hasSize( 2 );
		assertThat( repository.getTraceRoots( ) ).hasSize( 1 );
		assertThat( repository.getProcessedBytes( ) ).isGreaterThan( 0L );
	}

	@Test
	@DisplayName( "Test the method aggregation" )
	public void testMethodAggregation( @TempDir final Path tempDir ) throws Exception {
		// Prepare the data
		writeRecord( new TraceMetadata( 1L, 0L, "0", "host", 0L, 0 ) );
		writeRecord( new BeforeOperationEvent( System.currentTimeMillis( ), 1L, 0, "op1", "class1" ) );
		writeRecord( new BeforeOperationEvent( System.currentTimeMillis( ), 1L, 0, "op1", "class1" ) );
		writeRecord( new AfterOperationEvent( System.currentTimeMillis( ), 1L, 0, "op1", "class1" ) );
		writeRecord( new BeforeOperationEvent( System.currentTimeMillis( ), 1L, 0, "op2", "class1" ) );
		writeRecord( new AfterOperationEvent( System.currentTimeMillis( ), 1L, 0, "op2", "class1" ) );
		writeRecord( new BeforeOperationEvent( System.currentTimeMillis( ), 1L, 0, "op1", "class2" ) );
		writeRecord( new AfterOperationEvent( System.currentTimeMillis( ), 1L, 0, "op1", "class2" ) );
		writeRecord( new AfterOperationEvent( System.currentTimeMillis( ), 1L, 0, "op1", "class1" ) );
		writeMappingFile( tempDir );
		finishWriting( tempDir );

		// Import the directory
		service.importMonitoringLog( tempDir, ImportType.DIRECTORY );

		// Make sure that the import worked as intended
		assertThat( repository.getMethods( ) ).hasSize( 4 );
		assertThat( repository.getAggreatedMethods( ) ).hasSize( 3 );
		assertThat( repository.getTraceRoots( ) ).hasSize( 1 );
		assertThat( repository.getProcessedBytes( ) ).isGreaterThan( 0L );
	}

	@Test
	@DisplayName( "Test the method aggregation in detail" )
	public void testMethodAggregationValues1( @TempDir final Path tempDir ) throws Exception {
		// Prepare the data
		writeRecord( new TraceMetadata( 3L, 0L, "0", "host", 0L, 0 ) );
		writeRecord( new BeforeOperationEvent( 1, 3L, 0, "op1", "class1" ) );
		writeRecord( new AfterOperationEvent( 10, 3L, 0, "op1", "class1" ) );

		writeRecord( new TraceMetadata( 1L, 0L, "0", "host", 0L, 0 ) );
		writeRecord( new BeforeOperationEvent( 1, 1L, 0, "op1", "class1" ) );
		writeRecord( new AfterOperationEvent( 2, 1L, 0, "op1", "class1" ) );

		writeRecord( new TraceMetadata( 2L, 0L, "0", "host", 0L, 0 ) );
		writeRecord( new BeforeOperationEvent( 1, 2L, 0, "op1", "class1" ) );
		writeRecord( new AfterOperationEvent( 3, 2L, 0, "op1", "class1" ) );

		writeMappingFile( tempDir );
		finishWriting( tempDir );

		// Import the directory
		service.importMonitoringLog( tempDir, ImportType.DIRECTORY );

		// Make sure that the import worked as intended
		final List<AggregatedMethodCall> aggreatedMethods = repository.getAggreatedMethods( );
		assertThat( aggreatedMethods ).hasSize( 1 );

		final AggregatedMethodCall aggregatedMethodCall = aggreatedMethods.get( 0 );
		assertThat( aggregatedMethodCall.getCount( ) ).isEqualTo( 3 );
		assertThat( aggregatedMethodCall.getHost( ) ).isEqualTo( "host" );
		assertThat( aggregatedMethodCall.getClazz( ) ).isEqualTo( "class1" );
		assertThat( aggregatedMethodCall.getMethod( ) ).isEqualTo( "op1" );
		assertThat( aggregatedMethodCall.getMinDuration( ) ).isEqualTo( 1L );
		assertThat( aggregatedMethodCall.getMaxDuration( ) ).isEqualTo( 9L );
		assertThat( aggregatedMethodCall.getAvgDuration( ) ).isEqualTo( 4L );
		assertThat( aggregatedMethodCall.getMedianDuration( ) ).isEqualTo( 2L );
		assertThat( aggregatedMethodCall.getTotalDuration( ) ).isEqualTo( 12L );
	}

	@Test
	@DisplayName( "Test the method aggregation in detail" )
	public void testMethodAggregationValues2( @TempDir final Path tempDir ) throws Exception {
		// Prepare the data
		writeRecord( new TraceMetadata( 2L, 0L, "0", "host", 0L, 0 ) );
		writeRecord( new BeforeOperationEvent( 1, 2L, 0, "op1", "class1" ) );
		writeRecord( new AfterOperationEvent( 3, 2L, 0, "op1", "class1" ) );

		writeRecord( new TraceMetadata( 1L, 0L, "0", "host", 0L, 0 ) );
		writeRecord( new BeforeOperationEvent( 1, 1L, 0, "op1", "class1" ) );
		writeRecord( new AfterOperationEvent( 2, 1L, 0, "op1", "class1" ) );

		writeRecord( new TraceMetadata( 3L, 0L, "0", "host", 0L, 0 ) );
		writeRecord( new BeforeOperationEvent( 1, 3L, 0, "op1", "class1" ) );
		writeRecord( new AfterOperationEvent( 10, 3L, 0, "op1", "class1" ) );

		writeRecord( new TraceMetadata( 2L, 0L, "0", "host", 0L, 0 ) );
		writeRecord( new BeforeOperationEvent( 5, 2L, 0, "op1", "class1" ) );
		writeRecord( new AfterOperationEvent( 25, 2L, 0, "op1", "class1" ) );

		writeMappingFile( tempDir );
		finishWriting( tempDir );

		// Import the directory
		service.importMonitoringLog( tempDir, ImportType.DIRECTORY );

		// Make sure that the import worked as intended
		final List<AggregatedMethodCall> aggreatedMethods = repository.getAggreatedMethods( );
		assertThat( aggreatedMethods ).hasSize( 1 );

		final AggregatedMethodCall aggregatedMethodCall = aggreatedMethods.get( 0 );
		assertThat( aggregatedMethodCall.getCount( ) ).isEqualTo( 4 );
		assertThat( aggregatedMethodCall.getHost( ) ).isEqualTo( "host" );
		assertThat( aggregatedMethodCall.getClazz( ) ).isEqualTo( "class1" );
		assertThat( aggregatedMethodCall.getMethod( ) ).isEqualTo( "op1" );
		assertThat( aggregatedMethodCall.getMinDuration( ) ).isEqualTo( 1L );
		assertThat( aggregatedMethodCall.getMaxDuration( ) ).isEqualTo( 20L );
		assertThat( aggregatedMethodCall.getAvgDuration( ) ).isEqualTo( 8L );
		assertThat( aggregatedMethodCall.getMedianDuration( ) ).isEqualTo( 9L );
		assertThat( aggregatedMethodCall.getTotalDuration( ) ).isEqualTo( 32L );
	}

	@Test
	@DisplayName( "Test with an incomplete trace" )
	public void testIncompleteTrace( @TempDir final Path tempDir ) throws Exception {
		// Prepare the data
		writeRecord( new TraceMetadata( 1L, 0L, "0", "host", 0L, 0 ) );
		writeRecord( new BeforeOperationEvent( System.currentTimeMillis( ), 1L, 0, "op1", "class1" ) );
		writeRecord( new BeforeOperationEvent( System.currentTimeMillis( ), 1L, 0, "op2", "class1" ) );
		writeRecord( new AfterOperationEvent( System.currentTimeMillis( ), 1L, 0, "op2", "class1" ) );
		writeMappingFile( tempDir );
		finishWriting( tempDir );

		// Import the directory
		service.importMonitoringLog( tempDir, ImportType.DIRECTORY );

		// Make sure that the import worked as intended
		assertThat( repository.getMethods( ) ).isEmpty( );
		assertThat( repository.getAggreatedMethods( ) ).isEmpty( );
		assertThat( repository.getTraceRoots( ) ).isEmpty( );
		assertThat( repository.getIncompleteTraces( ) ).isEqualTo( 1 );
		assertThat( repository.getProcessedBytes( ) ).isGreaterThan( 0L );
	}

	@Test
	@DisplayName( "Test with some dangling records" )
	public void testDanglingRecords( @TempDir final Path tempDir ) throws Exception {
		// Prepare the data
		writeRecord( new BeforeOperationEvent( System.currentTimeMillis( ), 1L, 0, "op1", "class1" ) );
		writeRecord( new BeforeOperationEvent( System.currentTimeMillis( ), 1L, 0, "op2", "class1" ) );
		writeRecord( new AfterOperationEvent( System.currentTimeMillis( ), 1L, 0, "op2", "class1" ) );
		writeRecord( new AfterOperationEvent( System.currentTimeMillis( ), 1L, 0, "op1", "class1" ) );
		writeMappingFile( tempDir );
		finishWriting( tempDir );

		// Import the directory
		service.importMonitoringLog( tempDir, ImportType.DIRECTORY );

		// Make sure that the import worked as intended
		assertThat( repository.getMethods( ) ).isEmpty( );
		assertThat( repository.getAggreatedMethods( ) ).isEmpty( );
		assertThat( repository.getTraceRoots( ) ).isEmpty( );
		assertThat( repository.getDanglingRecords( ) ).isEqualTo( 4 );
		assertThat( repository.getProcessedBytes( ) ).isGreaterThan( 0L );
	}

	@Test
	@DisplayName( "Test the duration conversion" )
	public void testDurationConversion( @TempDir final Path tempDir ) throws Exception {
		// Prepare the data
		writeRecord( new TraceMetadata( 1L, 0L, "0", "host", 0L, 0 ) );
		writeRecord( new KiekerMetadataRecord( "0", "0", "0", 0, false, 0L, TimeUnit.SECONDS.name( ), 0 ) );
		writeRecord( new BeforeOperationEvent( 10, 1L, 0, "op1", "class1" ) );
		writeRecord( new AfterOperationFailedEvent( 20, 1L, 0, "op1", "class1", "cause" ) );
		writeMappingFile( tempDir );
		finishWriting( tempDir );

		// Import the directory
		service.importMonitoringLog( tempDir, ImportType.DIRECTORY );

		// Make sure that the import worked as intended
		final MethodCall methodCall = repository.getMethods( ).get( 0 );
		assertThat( methodCall.getDuration( ) ).isEqualTo( 10000000000L );
	}

	@Test
	@DisplayName( "Test the timestamp conversion" )
	public void testTimestampConversion( @TempDir final Path tempDir ) throws Exception {
		// Prepare the data
		writeRecord( new TraceMetadata( 1L, 0L, "0", "host", 0L, 0 ) );
		writeRecord( new KiekerMetadataRecord( "0", "0", "0", 0, false, 0L, TimeUnit.SECONDS.name( ), 0 ) );
		writeRecord( new BeforeOperationEvent( 10, 1L, 0, "op1", "class1" ) );
		writeRecord( new AfterOperationFailedEvent( 20, 1L, 0, "op1", "class1", "cause" ) );
		writeMappingFile( tempDir );
		finishWriting( tempDir );

		// Import the directory
		service.importMonitoringLog( tempDir, ImportType.DIRECTORY );

		// Make sure that the import worked as intended
		final MethodCall methodCall = repository.getMethods( ).get( 0 );
		assertThat( methodCall.getTimestamp( ) ).isEqualTo( 10000L );
	}

	@Test
	@DisplayName( "Test record ignoring" )
	public void testIgnoreRecordWithOtherTraces( @TempDir final Path tempDir ) throws Exception {
		// Prepare the data
		writeRecord( new CPUUtilizationRecord( 0L, "", "", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ) );
		writeRecord( new TraceMetadata( 1L, 0L, "0", "host", 0L, 0 ) );
		writeRecord( new KiekerMetadataRecord( "0", "0", "0", 0, false, 0L, TimeUnit.SECONDS.name( ), 0 ) );
		writeRecord( new BeforeOperationEvent( 10, 1L, 0, "op1", "class1" ) );
		writeRecord( new AfterOperationFailedEvent( 20, 1L, 0, "op1", "class1", "cause" ) );
		writeRecord( new CPUUtilizationRecord( 1L, "", "", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ) );

		writeMappingFile( tempDir );
		finishWriting( tempDir );

		// Import the directory
		service.importMonitoringLog( tempDir, ImportType.DIRECTORY );

		// Make sure that the import worked as intended
		assertThat( repository.getTraceRoots( ) ).hasSize( 1 );
		assertThat( repository.getIgnoredRecords( ) ).isEqualTo( 2 );
	}

	@Test
	@DisplayName( "Test record ignoring" )
	public void testIgnoreRecord( @TempDir final Path tempDir ) throws Exception {
		// Prepare the data
		writeRecord( new CPUUtilizationRecord( 0L, "", "", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ) );
		writeRecord( new CPUUtilizationRecord( 1L, "", "", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ) );

		writeMappingFile( tempDir );
		finishWriting( tempDir );

		// Import the directory and make sure that a business exception occurs (because
		// records where ignored, but no traces were reconstructed)
		assertThrows( ImportFailedException.class, ( ) -> service.importMonitoringLog( tempDir, ImportType.DIRECTORY ) );
	}

	@Test
	@DisplayName( "Test with an unknown record" )
	public void testUnknownRecord( @TempDir final Path tempDir ) throws Exception {
		// Prepare the data
		writeRecord( new UnknownRecord( ) );

		writeMappingFile( tempDir );
		finishWriting( tempDir );

		// Import the directory
		assertThrows( ImportFailedException.class, ( ) -> service.importMonitoringLog( tempDir, ImportType.DIRECTORY ) );
	}

	@Test
	@DisplayName( "Test a trace in detail" )
	public void testTraceInDetail( @TempDir final Path tempDir ) throws Exception {
		// Prepare the data
		writeRecord( new TraceMetadata( 1L, 0L, "0", "host", 0L, 0 ) );
		writeRecord( new BeforeOperationEvent( 1000000L, 1L, 0, "op1", "class1" ) );
		writeRecord( new BeforeOperationEvent( 2000000L, 1L, 0, "op2", "class2" ) );
		writeRecord( new AfterOperationEvent( 2500000L, 1L, 0, "op2", "class2" ) );
		writeRecord( new AfterOperationFailedEvent( 4000000L, 1L, 0, "op1", "class1", "cause" ) );
		writeMappingFile( tempDir );
		finishWriting( tempDir );

		// Import the directory
		service.importMonitoringLog( tempDir, ImportType.DIRECTORY );

		// Make sure that the import worked as intended
		assertThat( repository.getMethods( ) ).hasSize( 2 );
		assertThat( repository.getAggreatedMethods( ) ).hasSize( 2 );
		assertThat( repository.getTraceRoots( ) ).hasSize( 1 );
		assertThat( repository.getProcessedBytes( ) ).isGreaterThan( 0L );

		// Now some advanced checks
		final MethodCall firstMethod = repository.getMethods( ).get( 0 );
		assertThat( firstMethod.getHost( ) ).isEqualTo( "host" );
		assertThat( firstMethod.getClazz( ) ).isEqualTo( "class1" );
		assertThat( firstMethod.getMethod( ) ).isEqualTo( "op1" );
		assertThat( firstMethod.getException( ) ).isEqualTo( "cause" );
		assertThat( firstMethod.getTimestamp( ) ).isEqualTo( 1L );
		assertThat( firstMethod.getDuration( ) ).isEqualTo( 3000000L );
		assertThat( ( double ) firstMethod.getPercent( ) ).isCloseTo( 100.0, Offset.offset( 0.01 ) );
		assertThat( firstMethod.getTraceDepth( ) ).isEqualTo( 2 );
		assertThat( firstMethod.getTraceId( ) ).isEqualTo( 1L );
		assertThat( firstMethod.getTraceSize( ) ).isEqualTo( 2 );

		final MethodCall secondMethod = repository.getMethods( ).get( 1 );
		assertThat( secondMethod.getHost( ) ).isEqualTo( "host" );
		assertThat( secondMethod.getClazz( ) ).isEqualTo( "class2" );
		assertThat( secondMethod.getMethod( ) ).isEqualTo( "op2" );
		assertThat( secondMethod.getException( ) ).isNull( );
		assertThat( secondMethod.getTimestamp( ) ).isEqualTo( 2L );
		assertThat( secondMethod.getDuration( ) ).isEqualTo( 500000L );
		assertThat( ( double ) secondMethod.getPercent( ) ).isCloseTo( 16.66, Offset.offset( 0.01 ) );
		assertThat( secondMethod.getTraceDepth( ) ).isEqualTo( 1 );
		assertThat( secondMethod.getTraceId( ) ).isEqualTo( 1L );
		assertThat( secondMethod.getTraceSize( ) ).isEqualTo( 1 );

		assertThat( repository.getTraceRoots( ).get( 0 ) ).isEqualTo( firstMethod );
	}

	@Test
	@DisplayName( "Test the import from a zip file" )
	public void testImportFromZipFile( ) throws Exception {
		final URL logFileUrl = getClass( ).getResource( "/kieker-log-binary.zip" );
		final Path logFile = Paths.get( logFileUrl.toURI( ) );

		service.importMonitoringLog( logFile, ImportType.ZIP_FILE );

		assertThat( repository.getTraceRoots( ) ).hasSize( 2 );
		assertThat( repository.getAggreatedMethods( ) ).hasSize( 3 );
		assertThat( repository.getMethods( ) ).hasSize( 3 );
		assertThat( repository.isDataAvailable( ) ).isTrue( );
	}

	@Test
	@DisplayName( "Test with corrupt logs" )
	public void testCorruptLogs( ) throws URISyntaxException, IOException, CorruptStreamException, ImportFailedException {
		final URL logDirectoryUrl = getClass( ).getResource( "/kieker-log-binary-corrupt" );
		final Path logDirectory = Paths.get( logDirectoryUrl.toURI( ) );

		assertThrows( CorruptStreamException.class, ( ) -> service.importMonitoringLog( logDirectory, ImportType.DIRECTORY ) );

		assertThat( repository.isDataAvailable( ) ).isTrue( );
		assertThat( repository.getTraceRoots( ) ).hasSize( 1 );
		assertThat( repository.getAggreatedMethods( ) ).hasSize( 2 );
		assertThat( repository.getMethods( ) ).hasSize( 2 );
	}

	private void writeRecord( final AbstractMonitoringRecord aRecord ) {
		// Register the record name
		final int recordKey = stringRegistry.getId( aRecord.getClass( ).getName( ) );

		// Now write the record into our buffer
		final byte[] byteArray = new byte[aRecord.getSize( ) + 4 + 8];
		final ByteBuffer byteBuffer = ByteBuffer.wrap( byteArray );
		byteBuffer.putInt( recordKey );
		byteBuffer.putLong( System.currentTimeMillis( ) );
		aRecord.serialize( BinaryValueSerializer.create( byteBuffer, stringRegistry ) );
		byteBuffer.flip( );

		byteList.add( byteArray );
	}

	private void writeMappingFile( final Path tempDir ) throws IOException {
		// Collect the mappings
		final StringBuilder stringBuilder = new StringBuilder( );

		for ( final Entry<Integer, String> entry : stringRegistryContent.entrySet( ) ) {
			stringBuilder.append( "$" ).append( entry.getKey( ) ).append( "=" ).append( entry.getValue( ) ).append( "\n" );
		}

		// Write the mapping file
		final Path mappingFile = tempDir.resolve( "kieker.map" );
		Files.write( mappingFile, Collections.singletonList( stringBuilder.toString( ) ), StandardCharsets.UTF_8 );
	}

	private void finishWriting( final Path tempDir ) throws IOException {
		final Path binaryFile = tempDir.resolve( "kieker.bin" );
		byteList.trimToSize( );
		Files.write( binaryFile, byteList.buffer );
	}

	private static class UnknownRecord extends AbstractMonitoringRecord {

		private static final long serialVersionUID = 1L;

		@Override
		public void serialize( final IValueSerializer serializer ) throws BufferOverflowException {
		}

		@Override
		public Class<?>[] getValueTypes( ) {
			return null;
		}

		@Override
		public String[] getValueNames( ) {
			return null;
		}

		@Override
		public int getSize( ) {
			return 0;
		}

	}

}
