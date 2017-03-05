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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kieker.common.record.controlflow.OperationExecutionRecord;
import kieker.diagnosis.service.data.domain.OperationCall;
import kieker.diagnosis.service.data.domain.Trace;
import teetime.stage.basic.AbstractTransformation;

/**
 * Reconstruct traces based on the incoming instances of {@code OperationExecutionRecord}.
 *
 * @author Nils Christian Ehmke
 */
final class LegacyTraceReconstructor extends AbstractTransformation<OperationExecutionRecord, Trace> {

	private final Map<Long, TraceBuffer> ivTraceBuffers = new HashMap<>( );

	public int countIncompleteTraces( ) {
		return ivTraceBuffers.size( );
	}

	public int countDanglingRecords( ) {
		return 0;
	}

	@Override
	protected void execute( final OperationExecutionRecord aInput ) {
		handleOperationExecutionRecord( aInput );
	}

	private void handleOperationExecutionRecord( final OperationExecutionRecord aInput ) {
		final long traceID = aInput.getTraceId( );
		if ( !ivTraceBuffers.containsKey( traceID ) ) {
			ivTraceBuffers.put( traceID, new TraceBuffer( traceID ) );
		}
		final TraceBuffer traceBuffer = ivTraceBuffers.get( traceID );

		traceBuffer.handleEvent( aInput );
		if ( traceBuffer.isTraceComplete( ) ) {
			final Trace trace = traceBuffer.reconstructTrace( );
			ivTraceBuffers.remove( traceID );
			super.getOutputPort( ).send( trace );
		}
	}

	/**
	 * @author Nils Christian Ehmke
	 */
	private static final class TraceBuffer {

		private final List<OperationExecutionRecord> ivRecords = new ArrayList<>( );
		private final long ivTraceID;
		private boolean ivTraceComplete = false;

		public TraceBuffer( final long aTraceID ) {
			ivTraceID = aTraceID;
		}

		public void handleEvent( final OperationExecutionRecord aRecord ) {
			ivRecords.add( aRecord );

			if ( ( aRecord.getEoi( ) == 0 ) && ( aRecord.getEss( ) == 0 ) ) {
				ivTraceComplete = true;
			}
		}

		public Trace reconstructTrace( ) {
			Collections.sort( ivRecords, new EOIComparator( ) );

			OperationCall root = null;
			OperationCall header = null;
			int ess = 0;
			for ( final OperationExecutionRecord record : ivRecords ) {
				final OperationCall newCall = new OperationCall( record.getHostname( ), extractComponent( record.getOperationSignature( ) ),
						record.getOperationSignature( ), ivTraceID, record.getLoggingTimestamp( ) );
				newCall.setDuration( record.getTout( ) - record.getTin( ) );

				// There can be "jumps" in the ess, as the operation execution records do not log the return jumps of methods. Therefore multiple of these jumps
				// can
				// be hidden.
				int currentEss = record.getEss( );
				while ( ( currentEss <= ess ) && ( ess != 0 ) ) {
					header = header.getParent( );
					currentEss++;
				}

				if ( root == null ) {
					root = newCall;
				} else {
					header.addChild( newCall );
				}
				header = newCall;
				ess = record.getEss( );
			}

			return new Trace( root, ivTraceID );
		}

		private String extractComponent( final String aOperationSignature ) {
			// Remove modifiers and return values (Issue #26)
			final int firstOpeningParenthesisPos = aOperationSignature.indexOf( '(' );
			int gapPos = aOperationSignature.indexOf( ' ' );
			String result = aOperationSignature;
			while ( ( gapPos != -1 ) && ( gapPos < firstOpeningParenthesisPos ) ) {
				result = result.substring( gapPos + 1 );
				gapPos = result.indexOf( ' ' );
			}

			result = result.replaceFirst( "\\.<?\\w*>?\\(.*", "" );
			return result;
		}

		public boolean isTraceComplete( ) {
			return ivTraceComplete;
		}

		/**
		 * @author Nils Christian Ehmke
		 */
		private static final class EOIComparator implements Comparator<OperationExecutionRecord>, Serializable {

			private static final long serialVersionUID = 1L;

			@Override
			public int compare( final OperationExecutionRecord aO1, final OperationExecutionRecord aO2 ) {
				return Long.compare( aO1.getEoi( ), aO2.getEoi( ) );
			}

		}

	}

}
