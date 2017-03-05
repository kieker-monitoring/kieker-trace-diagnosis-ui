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
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import kieker.common.record.flow.IFlowRecord;
import kieker.common.record.flow.trace.TraceMetadata;
import kieker.common.record.flow.trace.operation.AbstractOperationEvent;
import kieker.common.record.flow.trace.operation.AfterOperationEvent;
import kieker.common.record.flow.trace.operation.AfterOperationFailedEvent;
import kieker.common.record.flow.trace.operation.BeforeOperationEvent;
import kieker.diagnosis.service.data.domain.OperationCall;
import kieker.diagnosis.service.data.domain.Trace;
import teetime.stage.basic.AbstractTransformation;

/**
 * Reconstruct traces based on the incoming instances of {@code IFlowRecord}. Currently only {@link TraceMetadata}, {@link BeforeOperationEvent} and
 * {@link AfterOperationEvent} instances are supported.
 *
 * @author Nils Christian Ehmke
 */
final class TraceReconstructor extends AbstractTransformation<IFlowRecord, Trace> {

	private final Map<Long, TraceBuffer> ivTraceBuffers = new HashMap<>( );
	private final List<TraceBuffer> ivFaultyTraceBuffers = new ArrayList<>( );
	private final boolean ivActivateAdditionalLogChecks;
	private int ivDanglingRecords;

	public TraceReconstructor( final boolean aActivateAdditionalLogChecks ) {
		ivActivateAdditionalLogChecks = aActivateAdditionalLogChecks;
	}

	public int countIncompleteTraces( ) {
		return ivTraceBuffers.size( ) + ivFaultyTraceBuffers.size( );
	}

	public int countDanglingRecords( ) {
		return ivDanglingRecords - ivFaultyTraceBuffers.size( );
	}

	@Override
	protected void execute( final IFlowRecord aInput ) {
		if ( aInput instanceof TraceMetadata ) {
			handleMetadataRecord( (TraceMetadata) aInput );
		} else if ( aInput instanceof AbstractOperationEvent ) {
			handleOperationEventRecord( (AbstractOperationEvent) aInput );
		}
	}

	private void handleMetadataRecord( final TraceMetadata aRecord ) {
		final long traceID = aRecord.getTraceId( );
		final TraceBuffer newTraceBuffer = new TraceBuffer( aRecord );

		ivTraceBuffers.put( traceID, newTraceBuffer );
	}

	private void handleOperationEventRecord( final AbstractOperationEvent aInput ) {
		final long traceID = aInput.getTraceId( );
		final TraceBuffer traceBuffer = ivTraceBuffers.get( traceID );

		if ( traceBuffer != null ) {
			traceBuffer.handleEvent( aInput );
			if ( traceBuffer.isTraceComplete( ) ) {
				final Trace trace = traceBuffer.reconstructTrace( );
				ivTraceBuffers.remove( traceID );
				super.getOutputPort( ).send( trace );
			}
		} else {
			ivDanglingRecords++;
		}
	}

	/**
	 * @author Nils Christian Ehmke
	 */
	private final class TraceBuffer {

		private final String ivHostname;
		private final Deque<BeforeOperationEvent> ivStack = new LinkedList<>( );
		private OperationCall ivRoot;
		private OperationCall ivHeader;
		private final long ivTraceID;

		public TraceBuffer( final TraceMetadata aTraceMetadata ) {
			ivHostname = aTraceMetadata.getHostname( );
			ivTraceID = aTraceMetadata.getTraceId( );
		}

		public void handleEvent( final AbstractOperationEvent aRecord ) {
			if ( aRecord instanceof BeforeOperationEvent ) {
				handleBeforeOperationEventRecord( (BeforeOperationEvent) aRecord );
			} else if ( aRecord instanceof AfterOperationEvent ) {
				handleAfterOperationEventRecord( (AfterOperationEvent) aRecord );
			}
		}

		private void handleBeforeOperationEventRecord( final BeforeOperationEvent aRecord ) {
			ivStack.push( aRecord );

			final OperationCall newCall = new OperationCall( ivHostname, aRecord.getClassSignature( ), aRecord.getOperationSignature( ), ivTraceID,
					aRecord.getLoggingTimestamp( ) );
			if ( ivRoot == null ) {
				ivRoot = newCall;
			} else {
				ivHeader.addChild( newCall );
			}
			ivHeader = newCall;
		}

		private void handleAfterOperationEventRecord( final AfterOperationEvent aRecord ) {
			final BeforeOperationEvent beforeEvent = ivStack.pop( );

			ivHeader.setDuration( aRecord.getTimestamp( ) - beforeEvent.getTimestamp( ) );

			if ( aRecord instanceof AfterOperationFailedEvent ) {
				ivHeader.setFailedCause( ((AfterOperationFailedEvent) aRecord).getCause( ) );
			}

			ivHeader = ivHeader.getParent( );

			if ( ivActivateAdditionalLogChecks && !beforeEvent.getOperationSignature( ).equals( aRecord.getOperationSignature( ) ) ) {
				ivFaultyTraceBuffers.add( this );
				ivTraceBuffers.remove( ivTraceID );
			}
		}

		public Trace reconstructTrace( ) {
			return new Trace( ivRoot, ivTraceID );
		}

		public boolean isTraceComplete( ) {
			return ivStack.isEmpty( );
		}

	}

}
