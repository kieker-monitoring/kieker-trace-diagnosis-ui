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

package kieker.diagnosis.application.service.data.stages;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static teetime.framework.test.StageTester.test;

import kieker.diagnosis.application.service.data.domain.OperationCall;
import kieker.diagnosis.application.service.data.domain.Trace;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import kieker.common.record.flow.IFlowRecord;
import kieker.common.record.flow.trace.TraceMetadata;
import kieker.common.record.flow.trace.operation.AfterOperationEvent;
import kieker.common.record.flow.trace.operation.AfterOperationFailedEvent;
import kieker.common.record.flow.trace.operation.BeforeOperationEvent;

public final class TraceReconstructorTest {

	@Test
	public void reconstructionOfSingleTraceShouldWork( ) {
		final List<IFlowRecord> records = new ArrayList<>( );
		records.add( new TraceMetadata( 1, 1, TraceMetadata.NO_SESSION_ID, TraceMetadata.NO_HOSTNAME, TraceMetadata.NO_PARENT_TRACEID,
				TraceMetadata.NO_PARENT_ORDER_INDEX ) );
		records.add( new BeforeOperationEvent( 1, 1, 1, "main()", "Main" ) );
		records.add( new BeforeOperationEvent( 1, 1, 1, "Bookstore()", "Bookstore" ) );
		records.add( new AfterOperationEvent( 1, 1, 1, "Bookstore()", "Bookstore" ) );
		records.add( new BeforeOperationEvent( 1, 1, 1, "Catalog()", "Catalog" ) );
		records.add( new BeforeOperationEvent( 1, 1, 1, "CRM()", "CRM" ) );
		records.add( new AfterOperationEvent( 1, 1, 1, "CRM()", "CRM" ) );
		records.add( new AfterOperationEvent( 1, 1, 1, "Catalog()", "Catalog" ) );
		records.add( new AfterOperationEvent( 1, 1, 1, "main()", "Main" ) );

		final TraceReconstructor reconstructor = new TraceReconstructor( false );
		final List<Trace> result = new ArrayList<>( );
		test( reconstructor ).and( ).send( records ).to( reconstructor.getInputPort( ) ).and( ).receive( result ).from( reconstructor.getOutputPort( ) )
				.start( );

		Assert.assertThat( result, hasSize( 1 ) );

		final Trace trace = result.get( 0 );
		final OperationCall rootCall = trace.getRootOperationCall( );
		Assert.assertThat( rootCall.getOperation( ), Matchers.is( "main()" ) );
		Assert.assertThat( rootCall.getChildren( ), Matchers.hasSize( 2 ) );
		Assert.assertThat( rootCall.getChildren( ).get( 0 ).getOperation( ), Matchers.is( "Bookstore()" ) );
		Assert.assertThat( rootCall.getChildren( ).get( 1 ).getOperation( ), Matchers.is( "Catalog()" ) );
		Assert.assertThat( rootCall.getChildren( ).get( 1 ).getChildren( ), Matchers.hasSize( 1 ) );
		Assert.assertThat( rootCall.getChildren( ).get( 1 ).getChildren( ).get( 0 ).getOperation( ), Matchers.is( "CRM()" ) );
	}

	@Test
	public void reconstructionOfInterleavedTracesShouldWork( ) {
		final List<IFlowRecord> records = new ArrayList<>( );
		records.add( new TraceMetadata( 1, 1, TraceMetadata.NO_SESSION_ID, TraceMetadata.NO_HOSTNAME, TraceMetadata.NO_PARENT_TRACEID,
				TraceMetadata.NO_PARENT_ORDER_INDEX ) );
		records.add( new BeforeOperationEvent( 1, 1, 1, "main()", "Main" ) );
		records.add( new TraceMetadata( 2, 1, TraceMetadata.NO_SESSION_ID, TraceMetadata.NO_HOSTNAME, TraceMetadata.NO_PARENT_TRACEID,
				TraceMetadata.NO_PARENT_ORDER_INDEX ) );
		records.add( new BeforeOperationEvent( 1, 2, 1, "Bookstore()", "Bookstore" ) );
		records.add( new AfterOperationEvent( 1, 1, 1, "main()", "Main" ) );
		records.add( new AfterOperationEvent( 1, 2, 1, "Bookstore()", "Bookstore" ) );

		final TraceReconstructor reconstructor = new TraceReconstructor( false );
		final List<Trace> result = new ArrayList<>( );
		test( reconstructor ).and( ).send( records ).to( reconstructor.getInputPort( ) ).and( ).receive( result ).from( reconstructor.getOutputPort( ) )
				.start( );

		Assert.assertThat( result, hasSize( 2 ) );

		final Trace fstTrace = result.get( 0 );
		final Trace sndTrace = result.get( 1 );

		Assert.assertThat( fstTrace.getRootOperationCall( ).getOperation( ), Matchers.is( "main()" ) );
		Assert.assertThat( sndTrace.getRootOperationCall( ).getOperation( ), Matchers.is( "Bookstore()" ) );
	}

	@Test
	public void reconstructionOfCompleteFailedTraceShouldWork( ) {
		final List<IFlowRecord> records = new ArrayList<>( );
		records.add( new TraceMetadata( 1, 1, TraceMetadata.NO_SESSION_ID, TraceMetadata.NO_HOSTNAME, TraceMetadata.NO_PARENT_TRACEID,
				TraceMetadata.NO_PARENT_ORDER_INDEX ) );
		records.add( new BeforeOperationEvent( 1, 1, 1, "main()", "Main" ) );
		records.add( new BeforeOperationEvent( 1, 1, 1, "Bookstore()", "Bookstore" ) );
		records.add( new AfterOperationFailedEvent( 1, 1, 1, "Bookstore()", "Bookstore", "NullPointerException" ) );
		records.add( new AfterOperationFailedEvent( 1, 1, 1, "main()", "Main", "IllegalArgumentException" ) );

		final TraceReconstructor reconstructor = new TraceReconstructor( false );
		final List<Trace> result = new ArrayList<>( );
		test( reconstructor ).and( ).send( records ).to( reconstructor.getInputPort( ) ).and( ).receive( result ).from( reconstructor.getOutputPort( ) )
				.start( );

		Assert.assertThat( result, hasSize( 1 ) );

		final Trace trace = result.get( 0 );
		final OperationCall rootCall = trace.getRootOperationCall( );
		Assert.assertThat( rootCall.isFailed( ), Matchers.is( true ) );
		Assert.assertThat( rootCall.getFailedCause( ), Matchers.is( "IllegalArgumentException" ) );
		Assert.assertThat( rootCall.getChildren( ).get( 0 ).isFailed( ), Matchers.is( true ) );
		Assert.assertThat( rootCall.getChildren( ).get( 0 ).getFailedCause( ), Matchers.is( "NullPointerException" ) );
	}

	@Test
	public void reconstructionOfPartialFailedTraceShouldWork( ) {
		final List<IFlowRecord> records = new ArrayList<>( );
		records.add( new TraceMetadata( 1, 1, TraceMetadata.NO_SESSION_ID, TraceMetadata.NO_HOSTNAME, TraceMetadata.NO_PARENT_TRACEID,
				TraceMetadata.NO_PARENT_ORDER_INDEX ) );
		records.add( new BeforeOperationEvent( 1, 1, 1, "main()", "Main" ) );
		records.add( new BeforeOperationEvent( 1, 1, 1, "Bookstore()", "Bookstore" ) );
		records.add( new AfterOperationFailedEvent( 1, 1, 1, "Bookstore()", "Bookstore", "NullPointerException" ) );
		records.add( new AfterOperationEvent( 1, 1, 1, "main()", "Main" ) );

		final TraceReconstructor reconstructor = new TraceReconstructor( false );
		final List<Trace> result = new ArrayList<>( );
		test( reconstructor ).and( ).send( records ).to( reconstructor.getInputPort( ) ).and( ).receive( result ).from( reconstructor.getOutputPort( ) )
				.start( );

		Assert.assertThat( result, hasSize( 1 ) );

		final Trace trace = result.get( 0 );
		final OperationCall rootCall = trace.getRootOperationCall( );
		Assert.assertThat( rootCall.isFailed( ), is( false ) );
		Assert.assertThat( rootCall.containsFailure( ), is( true ) );
		Assert.assertThat( rootCall.getChildren( ).get( 0 ).isFailed( ), is( true ) );
		Assert.assertThat( rootCall.getChildren( ).get( 0 ).getFailedCause( ), is( "NullPointerException" ) );
	}

	@Test
	public void faultyTracesShouldBeDetected( ) {
		final List<IFlowRecord> records = new ArrayList<>( );
		records.add( new TraceMetadata( 1, 1, TraceMetadata.NO_SESSION_ID, TraceMetadata.NO_HOSTNAME, TraceMetadata.NO_PARENT_TRACEID,
				TraceMetadata.NO_PARENT_ORDER_INDEX ) );
		records.add( new BeforeOperationEvent( 1, 1, 1, "main()", "Main" ) );
		records.add( new BeforeOperationEvent( 1, 1, 1, "Bookstore()", "Bookstore" ) );
		records.add( new AfterOperationEvent( 1, 1, 1, "NotBookstore()", "Bookstore" ) );
		records.add( new AfterOperationEvent( 1, 1, 1, "main()", "Main" ) );

		final TraceReconstructor reconstructor = new TraceReconstructor( true );
		final List<Trace> result = new ArrayList<>( );
		test( reconstructor ).and( ).send( records ).to( reconstructor.getInputPort( ) ).and( ).receive( result ).from( reconstructor.getOutputPort( ) )
				.start( );

		Assert.assertThat( result, hasSize( 0 ) );
		Assert.assertThat( reconstructor.countIncompleteTraces( ), is( 1 ) );
	}

	@Test
	public void faultyTracesShouldNotBeDetected( ) {
		final List<IFlowRecord> records = new ArrayList<>( );
		records.add( new TraceMetadata( 1, 1, TraceMetadata.NO_SESSION_ID, TraceMetadata.NO_HOSTNAME, TraceMetadata.NO_PARENT_TRACEID,
				TraceMetadata.NO_PARENT_ORDER_INDEX ) );
		records.add( new BeforeOperationEvent( 1, 1, 1, "main()", "Main" ) );
		records.add( new BeforeOperationEvent( 1, 1, 1, "Bookstore()", "Bookstore" ) );
		records.add( new AfterOperationEvent( 1, 1, 1, "NotBookstore()1", "Bookstore" ) );
		records.add( new AfterOperationEvent( 1, 1, 1, "main()", "Main" ) );

		final TraceReconstructor reconstructor = new TraceReconstructor( false );
		final List<Trace> result = new ArrayList<>( );
		test( reconstructor ).and( ).send( records ).to( reconstructor.getInputPort( ) ).and( ).receive( result ).from( reconstructor.getOutputPort( ) )
				.start( );

		Assert.assertThat( result, hasSize( 1 ) );
		Assert.assertThat( reconstructor.countIncompleteTraces( ), is( 0 ) );
	}

	@Test
	public void danglingRecordsOfCorrectTracesShouldBeDetected( ) {
		final List<IFlowRecord> records = new ArrayList<>( );
		records.add( new TraceMetadata( 1, 1, TraceMetadata.NO_SESSION_ID, TraceMetadata.NO_HOSTNAME, TraceMetadata.NO_PARENT_TRACEID,
				TraceMetadata.NO_PARENT_ORDER_INDEX ) );
		records.add( new BeforeOperationEvent( 1, 1, 1, "main()", "Main" ) );
		records.add( new BeforeOperationEvent( 1, 1, 1, "Bookstore()", "Bookstore" ) );
		records.add( new AfterOperationEvent( 1, 1, 1, "Bookstore()", "Bookstore" ) );
		records.add( new AfterOperationEvent( 1, 1, 1, "main()", "Main" ) );
		records.add( new AfterOperationEvent( 1, 1, 1, "main()", "Main" ) );
		records.add( new AfterOperationEvent( 1, 1, 1, "main()", "Main" ) );

		final TraceReconstructor reconstructor = new TraceReconstructor( true );
		final List<Trace> result = new ArrayList<>( );
		test( reconstructor ).and( ).send( records ).to( reconstructor.getInputPort( ) ).and( ).receive( result ).from( reconstructor.getOutputPort( ) )
				.start( );

		Assert.assertThat( result, hasSize( 1 ) );
		Assert.assertThat( reconstructor.countDanglingRecords( ), is( 2 ) );
		Assert.assertThat( reconstructor.countIncompleteTraces( ), is( 0 ) );
	}

	@Test
	public void danglingRecordsInFaultyTracesShouldBeDetected( ) {
		final List<IFlowRecord> records = new ArrayList<>( );
		records.add( new TraceMetadata( 1, 1, TraceMetadata.NO_SESSION_ID, TraceMetadata.NO_HOSTNAME, TraceMetadata.NO_PARENT_TRACEID,
				TraceMetadata.NO_PARENT_ORDER_INDEX ) );
		records.add( new BeforeOperationEvent( 1, 1, 1, "main()", "Main" ) );
		records.add( new BeforeOperationEvent( 1, 1, 1, "Bookstore()", "Bookstore" ) );
		records.add( new AfterOperationEvent( 1, 1, 1, "NotBookstore()", "Bookstore" ) );
		records.add( new AfterOperationEvent( 1, 1, 1, "main()", "Main" ) );
		records.add( new AfterOperationEvent( 1, 1, 1, "main()", "Main" ) );
		records.add( new AfterOperationEvent( 1, 1, 1, "main()", "Main" ) );

		final TraceReconstructor reconstructor = new TraceReconstructor( true );
		final List<Trace> result = new ArrayList<>( );
		test( reconstructor ).and( ).send( records ).to( reconstructor.getInputPort( ) ).and( ).receive( result ).from( reconstructor.getOutputPort( ) )
				.start( );

		Assert.assertThat( result, hasSize( 0 ) );
		Assert.assertThat( reconstructor.countDanglingRecords( ), is( 2 ) );
		Assert.assertThat( reconstructor.countIncompleteTraces( ), is( 1 ) );
	}

}
