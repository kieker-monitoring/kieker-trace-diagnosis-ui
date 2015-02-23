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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kieker.diagnosis.domain.AggregatedOperationCall;
import kieker.diagnosis.domain.AggregatedTrace;
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.domain.Trace;

/**
 * This class is a {@code TeeTime} stage adding statistics (via the corresponding setters) to instances of {@link AggregatedTrace}. The traces are forwarded to the output port.
 *
 * @author Nils Christian Ehmke
 */
public final class AggregatedTraceStatisticsDecorator extends AbstractStage<AggregatedTrace, AggregatedTrace> {

	@Override
	public void execute(final AggregatedTrace trace) {
		addNumberOfCalls(trace.getRootOperationCall(), trace.getTraces().size());
		addDurationStatistics(trace);

		super.send(trace);
	}

	private static void addNumberOfCalls(final AggregatedOperationCall call, final int calls) {
		call.setCalls(calls);

		for (final AggregatedOperationCall child : call.getChildren()) {
			addNumberOfCalls(child, calls);
		}
	}

	private static void addDurationStatistics(final AggregatedTrace trace) {
		final TraceDurationVisitor traceDurationVisitor = new TraceDurationVisitor();

		for (final Trace t : trace.getTraces()) {
			traceDurationVisitor.visit(t);
		}

		traceDurationVisitor.addDurationStatistics(trace);
	}

	private static final class TraceDurationVisitor {

		private final List<List<Long>> durationsPerEdge = new ArrayList<>();
		private int edgeIndex;

		public void visit(final Trace trace) {
			this.edgeIndex = -1;
			this.visit(trace.getRootOperationCall());
		}

		private void visit(final OperationCall rootOperationCall) {
			this.edgeIndex++;
			if (this.durationsPerEdge.size() <= this.edgeIndex) {
				this.durationsPerEdge.add(new ArrayList<Long>());
			}

			final List<Long> durationsOfCurrentEdge = this.durationsPerEdge.get(this.edgeIndex);

			durationsOfCurrentEdge.add(rootOperationCall.getDuration());

			for (final OperationCall child : rootOperationCall.getChildren()) {
				this.visit(child);
			}
		}

		public void addDurationStatistics(final AggregatedTrace trace) {
			this.edgeIndex = -1;
			this.addDurationStatistics(trace.getRootOperationCall());
		}

		private void addDurationStatistics(final AggregatedOperationCall rootOperationCall) {
			this.edgeIndex++;

			final List<Long> durationsOfCurrentEdge = this.durationsPerEdge.get(this.edgeIndex);

			final Statistics statistics = this.calculateStatistics(durationsOfCurrentEdge);
			rootOperationCall.setMinDuration(statistics.getMinDuration());
			rootOperationCall.setMaxDuration(statistics.getMaxDuration());
			rootOperationCall.setMeanDuration(statistics.getMeanDuration());
			rootOperationCall.setTotalDuration(statistics.getTotalDuration());
			rootOperationCall.setMedianDuration(statistics.getMedianDuration());

			for (final AggregatedOperationCall child : rootOperationCall.getChildren()) {
				this.addDurationStatistics(child);
			}
		}

		private Statistics calculateStatistics(final List<Long> durations) {
			Collections.sort(durations);

			long totalDuration = 0;
			for (final Long duration : durations) {
				totalDuration += duration;
			}

			final long minDuration = durations.get(0);
			final long maxDuration = durations.get(durations.size() - 1);
			final long meanDuration = totalDuration / durations.size();
			final long medianDuration = durations.get(durations.size() / 2);

			return new Statistics(totalDuration, meanDuration, medianDuration, minDuration, maxDuration);
		}

		private static class Statistics {

			private final long totalDuration;
			private final long meanDuration;
			private final long medianDuration;
			private final long minDuration;
			private final long maxDuration;

			public Statistics(final long totalDuration, final long meanDuration, final long medianDuration, final long minDuration, final long maxDuration) {
				this.totalDuration = totalDuration;
				this.meanDuration = meanDuration;
				this.medianDuration = medianDuration;
				this.minDuration = minDuration;
				this.maxDuration = maxDuration;
			}

			public long getTotalDuration() {
				return this.totalDuration;
			}

			public long getMeanDuration() {
				return this.meanDuration;
			}

			public long getMedianDuration() {
				return this.medianDuration;
			}

			public long getMinDuration() {
				return this.minDuration;
			}

			public long getMaxDuration() {
				return this.maxDuration;
			}

		}

	}

}
