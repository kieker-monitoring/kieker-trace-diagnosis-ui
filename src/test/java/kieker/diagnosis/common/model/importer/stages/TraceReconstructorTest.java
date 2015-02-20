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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import java.util.ArrayList;
import java.util.List;

import kieker.common.record.flow.IFlowRecord;
import kieker.common.record.flow.trace.TraceMetadata;
import kieker.common.record.flow.trace.operation.AfterOperationEvent;
import kieker.common.record.flow.trace.operation.AfterOperationFailedEvent;
import kieker.common.record.flow.trace.operation.BeforeOperationEvent;
import kieker.diagnosis.common.domain.OperationCall;
import kieker.diagnosis.common.domain.Trace;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import teetime.framework.Analysis;
import teetime.framework.AnalysisConfiguration;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;
import teetime.stage.CollectorSink;
import teetime.stage.IterableProducer;

public final class TraceReconstructorTest {

	@Test
	public void reconstructionOfSingleTraceShouldWork() {
		final List<IFlowRecord> input = new ArrayList<>();
		input.add(new TraceMetadata(1, 1, TraceMetadata.NO_SESSION_ID, TraceMetadata.NO_HOSTNAME, TraceMetadata.NO_PARENT_TRACEID, TraceMetadata.NO_PARENT_ORDER_INDEX));
		input.add(new BeforeOperationEvent(1, 1, 1, "main()", "Main"));
		input.add(new BeforeOperationEvent(1, 1, 1, "Bookstore()", "Bookstore"));
		input.add(new AfterOperationEvent(1, 1, 1, "Bookstore()", "Bookstore"));
		input.add(new BeforeOperationEvent(1, 1, 1, "Catalog()", "Catalog"));
		input.add(new BeforeOperationEvent(1, 1, 1, "CRM()", "CRM"));
		input.add(new AfterOperationEvent(1, 1, 1, "CRM()", "CRM"));
		input.add(new AfterOperationEvent(1, 1, 1, "Catalog()", "Catalog"));
		input.add(new AfterOperationEvent(1, 1, 1, "main()", "Main"));

		final ReconstructionConfiguration configuration = new ReconstructionConfiguration(input);
		final Analysis analysis = new Analysis(configuration);
		analysis.start();

		Assert.assertThat(configuration.getOutput(), hasSize(1));

		final Trace trace = configuration.getOutput().get(0);
		final OperationCall rootCall = trace.getRootOperationCall();
		Assert.assertThat(rootCall.getOperation(), Matchers.is("main()"));
		Assert.assertThat(rootCall.getChildren(), Matchers.hasSize(2));
		Assert.assertThat(rootCall.getChildren().get(0).getOperation(), Matchers.is("Bookstore()"));
		Assert.assertThat(rootCall.getChildren().get(1).getOperation(), Matchers.is("Catalog()"));
		Assert.assertThat(rootCall.getChildren().get(1).getChildren(), Matchers.hasSize(1));
		Assert.assertThat(rootCall.getChildren().get(1).getChildren().get(0).getOperation(), Matchers.is("CRM()"));
	}

	@Test
	public void reconstructionOfInterleavedTracesShouldWork() {
		final List<IFlowRecord> input = new ArrayList<>();
		input.add(new TraceMetadata(1, 1, TraceMetadata.NO_SESSION_ID, TraceMetadata.NO_HOSTNAME, TraceMetadata.NO_PARENT_TRACEID, TraceMetadata.NO_PARENT_ORDER_INDEX));
		input.add(new BeforeOperationEvent(1, 1, 1, "main()", "Main"));
		input.add(new TraceMetadata(2, 1, TraceMetadata.NO_SESSION_ID, TraceMetadata.NO_HOSTNAME, TraceMetadata.NO_PARENT_TRACEID, TraceMetadata.NO_PARENT_ORDER_INDEX));
		input.add(new BeforeOperationEvent(1, 2, 1, "Bookstore()", "Bookstore"));
		input.add(new AfterOperationEvent(1, 1, 1, "main()", "Main"));
		input.add(new AfterOperationEvent(1, 2, 1, "Bookstore()", "Bookstore"));

		final ReconstructionConfiguration configuration = new ReconstructionConfiguration(input);
		final Analysis analysis = new Analysis(configuration);
		analysis.start();

		Assert.assertThat(configuration.getOutput(), hasSize(2));

		final Trace fstTrace = configuration.getOutput().get(0);
		final Trace sndTrace = configuration.getOutput().get(1);

		Assert.assertThat(fstTrace.getRootOperationCall().getOperation(), Matchers.is("main()"));
		Assert.assertThat(sndTrace.getRootOperationCall().getOperation(), Matchers.is("Bookstore()"));
	}

	@Test
	public void reconstructionOfCompleteFailedTraceShouldWork() {
		final List<IFlowRecord> input = new ArrayList<>();
		input.add(new TraceMetadata(1, 1, TraceMetadata.NO_SESSION_ID, TraceMetadata.NO_HOSTNAME, TraceMetadata.NO_PARENT_TRACEID, TraceMetadata.NO_PARENT_ORDER_INDEX));
		input.add(new BeforeOperationEvent(1, 1, 1, "main()", "Main"));
		input.add(new BeforeOperationEvent(1, 1, 1, "Bookstore()", "Bookstore"));
		input.add(new AfterOperationFailedEvent(1, 1, 1, "Bookstore()", "Bookstore", "NullPointerException"));
		input.add(new AfterOperationFailedEvent(1, 1, 1, "main()", "Main", "IllegalArgumentException"));

		final ReconstructionConfiguration configuration = new ReconstructionConfiguration(input);
		final Analysis analysis = new Analysis(configuration);
		analysis.start();

		Assert.assertThat(configuration.getOutput(), hasSize(1));

		final Trace trace = configuration.getOutput().get(0);
		final OperationCall rootCall = trace.getRootOperationCall();
		Assert.assertThat(rootCall.isFailed(), Matchers.is(true));
		Assert.assertThat(rootCall.getFailedCause(), Matchers.is("IllegalArgumentException"));
		Assert.assertThat(rootCall.getChildren().get(0).isFailed(), Matchers.is(true));
		Assert.assertThat(rootCall.getChildren().get(0).getFailedCause(), Matchers.is("NullPointerException"));
	}

	@Test
	public void reconstructionOfPartialFailedTraceShouldWork() {
		final List<IFlowRecord> input = new ArrayList<>();
		input.add(new TraceMetadata(1, 1, TraceMetadata.NO_SESSION_ID, TraceMetadata.NO_HOSTNAME, TraceMetadata.NO_PARENT_TRACEID, TraceMetadata.NO_PARENT_ORDER_INDEX));
		input.add(new BeforeOperationEvent(1, 1, 1, "main()", "Main"));
		input.add(new BeforeOperationEvent(1, 1, 1, "Bookstore()", "Bookstore"));
		input.add(new AfterOperationFailedEvent(1, 1, 1, "Bookstore()", "Bookstore", "NullPointerException"));
		input.add(new AfterOperationEvent(1, 1, 1, "main()", "Main"));

		final ReconstructionConfiguration configuration = new ReconstructionConfiguration(input);
		final Analysis analysis = new Analysis(configuration);
		analysis.start();

		Assert.assertThat(configuration.getOutput(), hasSize(1));

		final Trace trace = configuration.getOutput().get(0);
		final OperationCall rootCall = trace.getRootOperationCall();
		Assert.assertThat(rootCall.isFailed(), is(false));
		Assert.assertThat(rootCall.containsFailure(), is(true));
		Assert.assertThat(rootCall.getChildren().get(0).isFailed(), is(true));
		Assert.assertThat(rootCall.getChildren().get(0).getFailedCause(), is("NullPointerException"));
	}

	private static class ReconstructionConfiguration extends AnalysisConfiguration {

		private final List<Trace> traceCollectorList = new ArrayList<>();

		public ReconstructionConfiguration(final List<IFlowRecord> input) {
			final IterableProducer<List<IFlowRecord>, IFlowRecord> producer = new IterableProducer<>(input);
			final TraceReconstructor reconstructor = new TraceReconstructor();
			final CollectorSink<Trace> collector = new CollectorSink<>(this.traceCollectorList);

			final IPipeFactory pipeFactory = AnalysisConfiguration.PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false);
			pipeFactory.create(producer.getOutputPort(), reconstructor.getInputPort());
			pipeFactory.create(reconstructor.getOutputPort(), collector.getInputPort());

			this.addThreadableStage(producer);
		}

		public List<Trace> getOutput() {
			return this.traceCollectorList;
		}

	}

}
