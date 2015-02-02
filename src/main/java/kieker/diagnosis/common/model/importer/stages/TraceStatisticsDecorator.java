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

package kieker.diagnosis.common.model.importer.stages;

import kieker.diagnosis.common.domain.OperationCall;
import kieker.diagnosis.common.domain.StatisticType;
import kieker.diagnosis.common.domain.Trace;

public final class TraceStatisticsDecorator extends AbstractStage<Trace, Trace> {

	@Override
	public void execute(final Trace trace) {
		TraceStatisticsDecorator.addTraceDepth(trace.getRootOperationCall());
		TraceStatisticsDecorator.addTraceSize(trace.getRootOperationCall());
		TraceStatisticsDecorator.addPercentValues(trace.getRootOperationCall(), trace.getRootOperationCall().getDuration());

		super.send(trace);
	}

	private static int addTraceDepth(final OperationCall rootOperationCall) {
		int traceDepth = 0;

		if (!rootOperationCall.getChildren().isEmpty()) {
			int maxTraceDepthOfChildren = 0;
			for (final OperationCall child : rootOperationCall.getChildren()) {
				final int traceDepthOfChild = TraceStatisticsDecorator.addTraceDepth(child);
				maxTraceDepthOfChildren = (traceDepthOfChild > maxTraceDepthOfChildren) ? traceDepthOfChild : maxTraceDepthOfChildren;
			}

			traceDepth = 1 + maxTraceDepthOfChildren;
		}

		rootOperationCall.addStatistic(StatisticType.STACK_DEPTH, traceDepth);

		return traceDepth;
	}

	private static int addTraceSize(final OperationCall rootOperationCall) {
		int traceSize = 1;

		for (final OperationCall child : rootOperationCall.getChildren()) {
			final int traceSizeOfChild = TraceStatisticsDecorator.addTraceSize(child);
			traceSize += traceSizeOfChild;
		}

		rootOperationCall.addStatistic(StatisticType.STACK_SIZE, traceSize);

		return traceSize;
	}

	private static void addPercentValues(final OperationCall call, final long rootDuration) {
		call.addStatistic(StatisticType.PERCENT, (100.0f * call.getDuration()) / rootDuration);
		for (final OperationCall child : call.getChildren()) {
			TraceStatisticsDecorator.addPercentValues(child, call.getDuration());
		}
	}

}
