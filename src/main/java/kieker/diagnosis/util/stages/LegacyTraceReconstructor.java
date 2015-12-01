/***************************************************************************
 * Copyright 2015 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.util.stages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kieker.common.record.controlflow.OperationExecutionRecord;
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.domain.Trace;
import teetime.stage.basic.AbstractTransformation;

/**
 * Reconstruct traces based on the incoming instances of {@code OperationExecutionRecord}.
 *
 * @author Nils Christian Ehmke
 */
final class LegacyTraceReconstructor extends AbstractTransformation<OperationExecutionRecord, Trace> {

	private final Map<Long, TraceBuffer> traceBuffers = new HashMap<>();

	public int countIncompleteTraces() {
		return this.traceBuffers.size();
	}

	public int countDanglingRecords() {
		return 0;
	}

	@Override
	protected void execute(final OperationExecutionRecord input) {
		this.handleOperationExecutionRecord(input);
	}

	private void handleOperationExecutionRecord(final OperationExecutionRecord input) {
		final long traceID = input.getTraceId();
		if (!this.traceBuffers.containsKey(traceID)) {
			this.traceBuffers.put(traceID, new TraceBuffer(traceID));
		}
		final TraceBuffer traceBuffer = this.traceBuffers.get(traceID);

		traceBuffer.handleEvent(input);
		if (traceBuffer.isTraceComplete()) {
			final Trace trace = traceBuffer.reconstructTrace();
			this.traceBuffers.remove(traceID);
			super.getOutputPort().send(trace);
		}
	}

	/**
	 * @author Nils Christian Ehmke
	 */
	private static final class TraceBuffer {

		private final List<OperationExecutionRecord> records = new ArrayList<>();
		private final long traceID;
		private boolean traceComplete = false;

		public TraceBuffer(final long traceID) {
			this.traceID = traceID;
		}

		public void handleEvent(final OperationExecutionRecord record) {
			this.records.add(record);

			if ((record.getEoi() == 0) && (record.getEss() == 0)) {
				this.traceComplete = true;
			}
		}

		public Trace reconstructTrace() {
			Collections.sort(this.records, new EOIComparator());

			OperationCall root = null;
			OperationCall header = null;
			int ess = 0;
			for (final OperationExecutionRecord record : this.records) {
				final OperationCall newCall = new OperationCall(record.getHostname(), this.extractComponent(record.getOperationSignature()), record.getOperationSignature(),
						this.traceID, record.getLoggingTimestamp());
				newCall.setDuration(record.getTout() - record.getTin());

				// There can be "jumps" in the ess, as the operation execution records do not log the return jumps of methods. Therefore multiple of these jumps can
				// be hidden.
				int currentEss = record.getEss();
				while ((currentEss <= ess) && (ess != 0)) {
					header = header.getParent();
					currentEss++;
				}

				if (root == null) {
					root = newCall;
				} else {
					header.addChild(newCall);
				}
				header = newCall;
				ess = record.getEss();
			}

			return new Trace(root, this.traceID);
		}

		private String extractComponent(final String operationSignature) {
			// Remove modifiers and return values (Issue #26)
			final int firstOpeningParenthesisPos = operationSignature.indexOf('(');
			int gapPos = operationSignature.indexOf(' ');
			String result = operationSignature;
			while ((gapPos != -1) && (gapPos < firstOpeningParenthesisPos)) {
				result = result.substring(gapPos + 1);
				gapPos = result.indexOf(' ');
			}

			result = result.replaceFirst("\\.<?\\w*>?\\(.*", "");
			return result;
		}

		public boolean isTraceComplete() {
			return this.traceComplete;
		}

		/**
		 * @author Nils Christian Ehmke
		 */
		private static final class EOIComparator implements Comparator<OperationExecutionRecord>, Serializable {

			private static final long serialVersionUID = 1L;

			@Override
			public int compare(final OperationExecutionRecord o1, final OperationExecutionRecord o2) {
				return Long.compare(o1.getEoi(), o2.getEoi());
			}

		}

	}

}
