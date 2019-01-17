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

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import com.carrotsearch.hppc.ByteArrayList;
import com.google.common.io.Files;
import com.google.inject.Guice;
import com.google.inject.Injector;

import kieker.common.record.AbstractMonitoringRecord;
import kieker.common.record.flow.trace.TraceMetadata;
import kieker.common.record.flow.trace.operation.AfterOperationEvent;
import kieker.common.record.flow.trace.operation.AfterOperationFailedEvent;
import kieker.common.record.flow.trace.operation.BeforeOperationEvent;
import kieker.common.record.io.DefaultValueSerializer;
import kieker.common.record.io.IValueSerializer;
import kieker.common.record.misc.KiekerMetadataRecord;
import kieker.common.record.system.CPUUtilizationRecord;
import kieker.common.util.registry.IRegistry;
import kieker.common.util.registry.Registry;
import kieker.diagnosis.backend.data.exception.CorruptStreamException;
import kieker.diagnosis.backend.data.exception.ImportFailedException;

/**
 * Test class for the {@link MonitoringLogService}.
 *
 * @author Nils Christian Ehmke
 */
public class MonitoringLogServiceTest {

	@Rule
	public TemporaryFolder ivTemporaryFolder = new TemporaryFolder( );

	@Rule
	public ExpectedException ivExpectedException = ExpectedException.none( );

	private ByteArrayList ivByteList;
	private IRegistry<String> ivStringRegistry;
	private MonitoringLogService ivService;

	@Before
	public void setUp( ) {
		ivByteList = new ByteArrayList( );
		ivStringRegistry = new Registry<>( );

		final Injector injector = Guice.createInjector( );
		ivService = injector.getInstance( MonitoringLogService.class );
	}

	@Test
	public void testEmptyDirectory( ) throws CorruptStreamException, ImportFailedException {
		// Import the directory
		final File directory = ivTemporaryFolder.getRoot( );

		// Make sure that an exception occurs
		ivExpectedException.expect( ImportFailedException.class );
		ivService.importMonitoringLog( directory, ImportType.DIRECTORY );
	}

	@Test
	public void testSingleTrace( ) throws Exception {
		// Prepare the data
		writeRecord( new TraceMetadata( 1L, 0L, "0", "host", 0L, 0 ) );
		writeRecord( new BeforeOperationEvent( System.currentTimeMillis( ), 1L, 0, "op1", "class1" ) );
		writeRecord( new BeforeOperationEvent( System.currentTimeMillis( ), 1L, 0, "op2", "class1" ) );
		writeRecord( new AfterOperationEvent( System.currentTimeMillis( ), 1L, 0, "op2", "class1" ) );
		writeRecord( new AfterOperationEvent( System.currentTimeMillis( ), 1L, 0, "op1", "class1" ) );
		writeMappingFile( );
		finishWriting( );

		// Import the directory
		final File directory = ivTemporaryFolder.getRoot( );
		ivService.importMonitoringLog( directory, ImportType.DIRECTORY );

		// Make sure that the import worked as intended
		assertThat( ivService.getMethods( ), hasSize( 2 ) );
		assertThat( ivService.getAggreatedMethods( ), hasSize( 2 ) );
		assertThat( ivService.getTraceRoots( ), hasSize( 1 ) );
		assertThat( ivService.getProcessedBytes( ), is( greaterThan( 0L ) ) );
	}

	@Test
	public void testTwoInterleavedTrace( ) throws Exception {
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
		writeMappingFile( );
		finishWriting( );

		// Import the directory
		final File directory = ivTemporaryFolder.getRoot( );
		ivService.importMonitoringLog( directory, ImportType.DIRECTORY );

		// Make sure that the import worked as intended
		assertThat( ivService.getMethods( ), hasSize( 4 ) );
		assertThat( ivService.getAggreatedMethods( ), hasSize( 4 ) );
		assertThat( ivService.getTraceRoots( ), hasSize( 2 ) );
		assertThat( ivService.getProcessedBytes( ), is( greaterThan( 0L ) ) );
	}

	@Test
	public void testFailedTrace( ) throws Exception {
		// Prepare the data
		writeRecord( new TraceMetadata( 1L, 0L, "0", "host", 0L, 0 ) );
		writeRecord( new BeforeOperationEvent( System.currentTimeMillis( ), 1L, 0, "op1", "class1" ) );
		writeRecord( new BeforeOperationEvent( System.currentTimeMillis( ), 1L, 0, "op2", "class1" ) );
		writeRecord( new AfterOperationFailedEvent( System.currentTimeMillis( ), 1L, 0, "op2", "class1", "cause" ) );
		writeRecord( new AfterOperationFailedEvent( System.currentTimeMillis( ), 1L, 0, "op1", "class1", "cause" ) );
		writeMappingFile( );
		finishWriting( );

		// Import the directory
		final File directory = ivTemporaryFolder.getRoot( );
		ivService.importMonitoringLog( directory, ImportType.DIRECTORY );

		// Make sure that the import worked as intended
		assertThat( ivService.getMethods( ), hasSize( 2 ) );
		assertThat( ivService.getAggreatedMethods( ), hasSize( 2 ) );
		assertThat( ivService.getTraceRoots( ), hasSize( 1 ) );
		assertThat( ivService.getProcessedBytes( ), is( greaterThan( 0L ) ) );
	}

	@Test
	public void testMethodAggregation( ) throws Exception {
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
		writeMappingFile( );
		finishWriting( );

		// Import the directory
		final File directory = ivTemporaryFolder.getRoot( );
		ivService.importMonitoringLog( directory, ImportType.DIRECTORY );

		// Make sure that the import worked as intended
		assertThat( ivService.getMethods( ), hasSize( 4 ) );
		assertThat( ivService.getAggreatedMethods( ), hasSize( 3 ) );
		assertThat( ivService.getTraceRoots( ), hasSize( 1 ) );
		assertThat( ivService.getProcessedBytes( ), is( greaterThan( 0L ) ) );
	}

	@Test
	public void testMethodAggregationValues1( ) throws Exception {
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

		writeMappingFile( );
		finishWriting( );

		// Import the directory
		final File directory = ivTemporaryFolder.getRoot( );
		ivService.importMonitoringLog( directory, ImportType.DIRECTORY );

		// Make sure that the import worked as intended
		final List<AggregatedMethodCall> aggreatedMethods = ivService.getAggreatedMethods( );
		assertThat( aggreatedMethods, hasSize( 1 ) );

		final AggregatedMethodCall aggregatedMethodCall = aggreatedMethods.get( 0 );
		assertThat( aggregatedMethodCall.getCount( ), is( 3 ) );
		assertThat( aggregatedMethodCall.getHost( ), is( "host" ) );
		assertThat( aggregatedMethodCall.getClazz( ), is( "class1" ) );
		assertThat( aggregatedMethodCall.getMethod( ), is( "op1" ) );
		assertThat( aggregatedMethodCall.getMinDuration( ), is( 1L ) );
		assertThat( aggregatedMethodCall.getMaxDuration( ), is( 9L ) );
		assertThat( aggregatedMethodCall.getAvgDuration( ), is( 4L ) );
		assertThat( aggregatedMethodCall.getMedianDuration( ), is( 2L ) );
		assertThat( aggregatedMethodCall.getTotalDuration( ), is( 12L ) );
	}

	@Test
	public void testMethodAggregationValues2( ) throws Exception {
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

		writeMappingFile( );
		finishWriting( );

		// Import the directory
		final File directory = ivTemporaryFolder.getRoot( );
		ivService.importMonitoringLog( directory, ImportType.DIRECTORY );

		// Make sure that the import worked as intended
		final List<AggregatedMethodCall> aggreatedMethods = ivService.getAggreatedMethods( );
		assertThat( aggreatedMethods, hasSize( 1 ) );

		final AggregatedMethodCall aggregatedMethodCall = aggreatedMethods.get( 0 );
		assertThat( aggregatedMethodCall.getCount( ), is( 4 ) );
		assertThat( aggregatedMethodCall.getHost( ), is( "host" ) );
		assertThat( aggregatedMethodCall.getClazz( ), is( "class1" ) );
		assertThat( aggregatedMethodCall.getMethod( ), is( "op1" ) );
		assertThat( aggregatedMethodCall.getMinDuration( ), is( 1L ) );
		assertThat( aggregatedMethodCall.getMaxDuration( ), is( 20L ) );
		assertThat( aggregatedMethodCall.getAvgDuration( ), is( 8L ) );
		assertThat( aggregatedMethodCall.getMedianDuration( ), is( 9L ) );
		assertThat( aggregatedMethodCall.getTotalDuration( ), is( 32L ) );
	}

	@Test
	public void testIncompleteTrace( ) throws Exception {
		// Prepare the data
		writeRecord( new TraceMetadata( 1L, 0L, "0", "host", 0L, 0 ) );
		writeRecord( new BeforeOperationEvent( System.currentTimeMillis( ), 1L, 0, "op1", "class1" ) );
		writeRecord( new BeforeOperationEvent( System.currentTimeMillis( ), 1L, 0, "op2", "class1" ) );
		writeRecord( new AfterOperationEvent( System.currentTimeMillis( ), 1L, 0, "op2", "class1" ) );
		writeMappingFile( );
		finishWriting( );

		// Import the directory
		final File directory = ivTemporaryFolder.getRoot( );
		ivService.importMonitoringLog( directory, ImportType.DIRECTORY );

		// Make sure that the import worked as intended
		assertThat( ivService.getMethods( ), hasSize( 0 ) );
		assertThat( ivService.getAggreatedMethods( ), hasSize( 0 ) );
		assertThat( ivService.getTraceRoots( ), hasSize( 0 ) );
		assertThat( ivService.getIncompleteTraces( ), is( 1 ) );
		assertThat( ivService.getProcessedBytes( ), is( greaterThan( 0L ) ) );
	}

	@Test
	public void testDanglingRecords( ) throws Exception {
		// Prepare the data
		writeRecord( new BeforeOperationEvent( System.currentTimeMillis( ), 1L, 0, "op1", "class1" ) );
		writeRecord( new BeforeOperationEvent( System.currentTimeMillis( ), 1L, 0, "op2", "class1" ) );
		writeRecord( new AfterOperationEvent( System.currentTimeMillis( ), 1L, 0, "op2", "class1" ) );
		writeRecord( new AfterOperationEvent( System.currentTimeMillis( ), 1L, 0, "op1", "class1" ) );
		writeMappingFile( );
		finishWriting( );

		// Import the directory
		final File directory = ivTemporaryFolder.getRoot( );
		ivService.importMonitoringLog( directory, ImportType.DIRECTORY );

		// Make sure that the import worked as intended
		assertThat( ivService.getMethods( ), hasSize( 0 ) );
		assertThat( ivService.getAggreatedMethods( ), hasSize( 0 ) );
		assertThat( ivService.getTraceRoots( ), hasSize( 0 ) );
		assertThat( ivService.getDanglingRecords( ), is( 4 ) );
		assertThat( ivService.getProcessedBytes( ), is( greaterThan( 0L ) ) );
	}

	@Test
	public void testDurationConversion( ) throws Exception {
		// Prepare the data
		writeRecord( new TraceMetadata( 1L, 0L, "0", "host", 0L, 0 ) );
		writeRecord( new KiekerMetadataRecord( "0", "0", "0", 0, false, 0L, TimeUnit.SECONDS.name( ), 0 ) );
		writeRecord( new BeforeOperationEvent( 10, 1L, 0, "op1", "class1" ) );
		writeRecord( new AfterOperationFailedEvent( 20, 1L, 0, "op1", "class1", "cause" ) );
		writeMappingFile( );
		finishWriting( );

		// Import the directory
		final File directory = ivTemporaryFolder.getRoot( );
		ivService.importMonitoringLog( directory, ImportType.DIRECTORY );

		// Make sure that the import worked as intended
		final MethodCall methodCall = ivService.getMethods( ).get( 0 );
		assertThat( methodCall.getDuration( ), is( 10000000000L ) );
	}

	@Test
	public void testTimestampConversion( ) throws Exception {
		// Prepare the data
		writeRecord( new TraceMetadata( 1L, 0L, "0", "host", 0L, 0 ) );
		writeRecord( new KiekerMetadataRecord( "0", "0", "0", 0, false, 0L, TimeUnit.SECONDS.name( ), 0 ) );
		writeRecord( new BeforeOperationEvent( 10, 1L, 0, "op1", "class1" ) );
		writeRecord( new AfterOperationFailedEvent( 20, 1L, 0, "op1", "class1", "cause" ) );
		writeMappingFile( );
		finishWriting( );

		// Import the directory
		final File directory = ivTemporaryFolder.getRoot( );
		ivService.importMonitoringLog( directory, ImportType.DIRECTORY );

		// Make sure that the import worked as intended
		final MethodCall methodCall = ivService.getMethods( ).get( 0 );
		assertThat( methodCall.getTimestamp( ), is( 10000L ) );
	}

	@Test
	public void testIgnoreRecordWithOtherTraces( ) throws Exception {
		// Prepare the data
		writeRecord( new CPUUtilizationRecord( 0L, "", "", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ) );
		writeRecord( new TraceMetadata( 1L, 0L, "0", "host", 0L, 0 ) );
		writeRecord( new KiekerMetadataRecord( "0", "0", "0", 0, false, 0L, TimeUnit.SECONDS.name( ), 0 ) );
		writeRecord( new BeforeOperationEvent( 10, 1L, 0, "op1", "class1" ) );
		writeRecord( new AfterOperationFailedEvent( 20, 1L, 0, "op1", "class1", "cause" ) );
		writeRecord( new CPUUtilizationRecord( 1L, "", "", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ) );

		writeMappingFile( );
		finishWriting( );

		// Import the directory
		final File directory = ivTemporaryFolder.getRoot( );
		ivService.importMonitoringLog( directory, ImportType.DIRECTORY );

		// Make sure that the import worked as intended
		assertThat( ivService.getTraceRoots( ), hasSize( 1 ) );
		assertThat( ivService.getIgnoredRecords( ), is( 2 ) );
	}

	@Test
	public void testIgnoreRecord( ) throws Exception {
		// Prepare the data
		writeRecord( new CPUUtilizationRecord( 0L, "", "", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ) );
		writeRecord( new CPUUtilizationRecord( 1L, "", "", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ) );

		writeMappingFile( );
		finishWriting( );

		// Import the directory and make sure that a business exception occurs (because
		// records where ignored, but no traces were reconstructed)
		final File directory = ivTemporaryFolder.getRoot( );
		ivExpectedException.expect( ImportFailedException.class );
		ivService.importMonitoringLog( directory, ImportType.DIRECTORY );
	}

	@Test
	public void testUnknownRecord( ) throws Exception {
		// Prepare the data
		writeRecord( new UnknownRecord( ) );

		writeMappingFile( );
		finishWriting( );

		// The import should not work
		ivExpectedException.expect( ImportFailedException.class );

		// Import the directory
		final File directory = ivTemporaryFolder.getRoot( );
		ivService.importMonitoringLog( directory, ImportType.DIRECTORY );
	}

	@Test
	public void testTraceInDetail( ) throws Exception {
		// Prepare the data
		writeRecord( new TraceMetadata( 1L, 0L, "0", "host", 0L, 0 ) );
		writeRecord( new BeforeOperationEvent( 1000000L, 1L, 0, "op1", "class1" ) );
		writeRecord( new BeforeOperationEvent( 2000000L, 1L, 0, "op2", "class2" ) );
		writeRecord( new AfterOperationEvent( 2500000L, 1L, 0, "op2", "class2" ) );
		writeRecord( new AfterOperationFailedEvent( 4000000L, 1L, 0, "op1", "class1", "cause" ) );
		writeMappingFile( );
		finishWriting( );

		// Import the directory
		final File directory = ivTemporaryFolder.getRoot( );
		ivService.importMonitoringLog( directory, ImportType.DIRECTORY );

		// Make sure that the import worked as intended
		assertThat( ivService.getMethods( ), hasSize( 2 ) );
		assertThat( ivService.getAggreatedMethods( ), hasSize( 2 ) );
		assertThat( ivService.getTraceRoots( ), hasSize( 1 ) );
		assertThat( ivService.getProcessedBytes( ), is( greaterThan( 0L ) ) );

		// Now some advanced checks
		final MethodCall firstMethod = ivService.getMethods( ).get( 0 );
		assertThat( firstMethod.getHost( ), is( "host" ) );
		assertThat( firstMethod.getClazz( ), is( "class1" ) );
		assertThat( firstMethod.getMethod( ), is( "op1" ) );
		assertThat( firstMethod.getException( ), is( "cause" ) );
		assertThat( firstMethod.getTimestamp( ), is( 1L ) );
		assertThat( firstMethod.getDuration( ), is( 3000000L ) );
		assertThat( (double) firstMethod.getPercent( ), is( closeTo( 100.0, 0.01 ) ) );
		assertThat( firstMethod.getTraceDepth( ), is( 2 ) );
		assertThat( firstMethod.getTraceId( ), is( 1L ) );
		assertThat( firstMethod.getTraceSize( ), is( 2 ) );

		final MethodCall secondMethod = ivService.getMethods( ).get( 1 );
		assertThat( secondMethod.getHost( ), is( "host" ) );
		assertThat( secondMethod.getClazz( ), is( "class2" ) );
		assertThat( secondMethod.getMethod( ), is( "op2" ) );
		assertThat( secondMethod.getException( ), is( nullValue( ) ) );
		assertThat( secondMethod.getTimestamp( ), is( 2L ) );
		assertThat( secondMethod.getDuration( ), is( 500000L ) );
		assertThat( (double) secondMethod.getPercent( ), is( closeTo( 16.66, 0.01 ) ) );
		assertThat( secondMethod.getTraceDepth( ), is( 1 ) );
		assertThat( secondMethod.getTraceId( ), is( 1L ) );
		assertThat( secondMethod.getTraceSize( ), is( 1 ) );

		assertThat( ivService.getTraceRoots( ).get( 0 ), is( firstMethod ) );
	}

	@Test
	public void testImportFromZipFile( ) throws Exception {
		final URL logFileUrl = getClass( ).getResource( "/kieker-log-binary.zip" );
		final File logFile = new File( logFileUrl.toURI( ) );

		ivService.importMonitoringLog( logFile, ImportType.ZIP_FILE );

		assertThat( ivService.getTraceRoots( ), hasSize( 2 ) );
		assertThat( ivService.getAggreatedMethods( ), hasSize( 3 ) );
		assertThat( ivService.getMethods( ), hasSize( 3 ) );
		assertTrue( ivService.isDataAvailable( ) );
	}

	@SuppressWarnings ( "deprecation" )
	private void writeRecord( final AbstractMonitoringRecord aRecord ) {
		// Register the record name
		final int recordKey = ivStringRegistry.get( aRecord.getClass( ).getName( ) );

		// Register the record's strings
		aRecord.registerStrings( ivStringRegistry );

		// Now write the record into our buffer
		final byte[] byteArray = new byte[aRecord.getSize( ) + 4 + 8];
		final ByteBuffer byteBuffer = ByteBuffer.wrap( byteArray );
		byteBuffer.putInt( recordKey );
		byteBuffer.putLong( System.currentTimeMillis( ) );
		aRecord.serialize( DefaultValueSerializer.create( byteBuffer, ivStringRegistry ) );
		byteBuffer.flip( );

		ivByteList.add( byteArray );
	}

	private void writeMappingFile( ) throws IOException {
		// Collect the mappings
		final StringBuilder stringBuilder = new StringBuilder( );

		final Object[] allStrings = ivStringRegistry.getAll( );
		for ( final Object string : allStrings ) {
			final int id = ivStringRegistry.get( (String) string );
			stringBuilder.append( "$" ).append( id ).append( "=" ).append( string ).append( "\n" );
		}

		// Write the mapping file
		final File mappingFile = new File( ivTemporaryFolder.getRoot( ), "kieker.map" );
		Files.asCharSink( mappingFile, Charset.forName( "UTF-8" ) ).write( stringBuilder );
	}

	private void finishWriting( ) throws IOException {
		final File binaryFile = new File( ivTemporaryFolder.getRoot( ), "kieker.bin" );
		ivByteList.trimToSize( );
		Files.write( ivByteList.buffer, binaryFile );
	}

	private static class UnknownRecord extends AbstractMonitoringRecord {

		private static final long serialVersionUID = 1L;

		@Override
		public Object[] toArray( ) {
			return null;
		}

		@Override
		public void registerStrings( final IRegistry<String> aStringRegistry ) {
		}

		@Override
		public void serialize( final IValueSerializer aSerializer ) throws BufferOverflowException {
		}

		@Override
		public String[] getValueNames( ) {
			return null;
		}

		@Override
		public void initFromArray( final Object[] aValues ) {
		}

		@Override
		public Class<?>[] getValueTypes( ) {
			return null;
		}

		@Override
		public int getSize( ) {
			return 0;
		}

	}

}
