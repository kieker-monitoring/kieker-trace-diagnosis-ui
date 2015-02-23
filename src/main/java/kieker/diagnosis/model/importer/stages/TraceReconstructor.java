/***************************************************************************
 * Copyright 2014 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.model.importer.stages;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import kieker.common.record.flow.IFlowRecord;
import kieker.common.record.flow.trace.TraceMetadata;
import kieker.common.record.flow.trace.operation.AbstractOperationEvent;
import kieker.common.record.flow.trace.operation.AfterOperationEvent;
import kieker.common.record.flow.trace.operation.AfterOperationFailedEvent;
import kieker.common.record.flow.trace.operation.BeforeOperationEvent;
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.domain.Trace;

/**
 * Reconstruct traces based on the incoming instances of {@code IFlowRecord}. Currently only {@link TraceMetadata}, {@link BeforeOperationEvent} and {@link AfterOperationEvent}
 * instances are supported.
 * 
 * @author Nils Christian Ehmke
 */
final class TraceReconstructor extends AbstractStage<IFlowRecord, Trace> {

	private final Map<Long, TraceBuffer> traceBuffers = new HashMap<>();

	@Override
	protected void execute(final IFlowRecord input) {
		if (input instanceof TraceMetadata) {
			this.handleMetadataRecord((TraceMetadata) input);
		} else if (input instanceof AbstractOperationEvent) {
			this.handleOperationEventRecord((AbstractOperationEvent) input);
		}
	}

	private void handleMetadataRecord(final TraceMetadata record) {
		final long traceID = record.getTraceId();
		final TraceBuffer newTraceBuffer = new TraceBuffer(record);

		this.traceBuffers.put(traceID, newTraceBuffer);
	}

	private void handleOperationEventRecord(final AbstractOperationEvent input) {
		final long traceID = input.getTraceId();
		final TraceBuffer traceBuffer = this.traceBuffers.get(traceID);

		traceBuffer.handleEvent(input);
		if (traceBuffer.isTraceComplete()) {
			final Trace trace = traceBuffer.reconstructTrace();
			super.send(trace);
		}
	}

	private static final class TraceBuffer {

		private final String hostname;
		private final Deque<BeforeOperationEvent> stack = new LinkedList<>();
		private OperationCall root;
		private OperationCall header;
		private final long traceID;

		public TraceBuffer(final TraceMetadata traceMetadata) {
			this.hostname = traceMetadata.getHostname();
			this.traceID = traceMetadata.getTraceId();
		}

		public void handleEvent(final AbstractOperationEvent record) {
			if (record instanceof BeforeOperationEvent) {
				this.handleBeforeOperationEventRecord((BeforeOperationEvent) record);
			} else if (record instanceof AfterOperationEvent) {
				this.handleAferOperationEventRecord((AfterOperationEvent) record);
			}
		}

		private void handleBeforeOperationEventRecord(final BeforeOperationEvent record) {
			this.stack.push(record);

			final OperationCall newCall = new OperationCall(this.hostname, record.getClassSignature(), record.getOperationSignature(), this.traceID);
			if (this.root == null) {
				this.root = newCall;
			} else {
				this.header.addChild(newCall);
			}
			this.header = newCall;
		}

		private void handleAferOperationEventRecord(final AfterOperationEvent record) {
			final BeforeOperationEvent beforeEvent = this.stack.pop();

			this.header.setDuration(record.getTimestamp() - beforeEvent.getTimestamp());

			if (record instanceof AfterOperationFailedEvent) {
				this.header.setFailedCause(((AfterOperationFailedEvent) record).getCause());
			}

			this.header = this.header.getParent();
		}

		public Trace reconstructTrace() {
			return new Trace(this.root, this.traceID);
		}

		public boolean isTraceComplete() {
			return this.stack.isEmpty();
		}

	}

}
