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

import java.util.ArrayList;
import java.util.List;

import kieker.common.record.flow.IFlowRecord;
import kieker.common.record.flow.trace.TraceMetadata;
import kieker.common.record.flow.trace.operation.AfterOperationEvent;
import kieker.common.record.flow.trace.operation.AfterOperationFailedEvent;
import kieker.common.record.flow.trace.operation.BeforeOperationEvent;
import kieker.diagnosis.common.domain.OperationCall;
import kieker.diagnosis.common.domain.Trace;
import kieker.diagnosis.common.model.importer.stages.TraceReconstructor;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.SingleElementPipeFactory;
import teetime.stage.CollectorSink;

public final class TraceReconstructorTest {

	private List<Trace> traceCollectorList;
	private TraceReconstructor reconstructorUnderTest;
	private CollectorSink<Trace> traceCollector;

	@Before
	public void initializeTraceReconstructor() {
		this.traceCollectorList = new ArrayList<>();

		this.reconstructorUnderTest = new TraceReconstructor();
		this.traceCollector = new CollectorSink<>(this.traceCollectorList);

		final IPipeFactory pipeFactory = new SingleElementPipeFactory();
		pipeFactory.create(this.reconstructorUnderTest.getOutputPort(), this.traceCollector.getInputPort());
	}

	@Test
	public void reconstructionOfSingleTraceShouldWork() {
		final IFlowRecord[] records = new IFlowRecord[9];

		records[0] = new TraceMetadata(1, 1, TraceMetadata.NO_SESSION_ID, TraceMetadata.NO_HOSTNAME, TraceMetadata.NO_PARENT_TRACEID, TraceMetadata.NO_PARENT_ORDER_INDEX);
		records[1] = new BeforeOperationEvent(1, 1, 1, "main()", "Main");
		records[2] = new BeforeOperationEvent(1, 1, 1, "Bookstore()", "Bookstore");
		records[3] = new AfterOperationEvent(1, 1, 1, "Bookstore()", "Bookstore");
		records[4] = new BeforeOperationEvent(1, 1, 1, "Catalog()", "Catalog");
		records[5] = new BeforeOperationEvent(1, 1, 1, "CRM()", "CRM");
		records[6] = new AfterOperationEvent(1, 1, 1, "CRM()", "CRM");
		records[7] = new AfterOperationEvent(1, 1, 1, "Catalog()", "Catalog");
		records[8] = new AfterOperationEvent(1, 1, 1, "main()", "Main");

		for (final IFlowRecord record : records) {
			this.reconstructorUnderTest.execute(record);
		}
		Assert.assertThat(this.traceCollectorList, Matchers.hasSize(1));

		final Trace trace = this.traceCollectorList.get(0);
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
		final IFlowRecord[] records = new IFlowRecord[9];

		records[0] = new TraceMetadata(1, 1, TraceMetadata.NO_SESSION_ID, TraceMetadata.NO_HOSTNAME, TraceMetadata.NO_PARENT_TRACEID, TraceMetadata.NO_PARENT_ORDER_INDEX);
		records[1] = new BeforeOperationEvent(1, 1, 1, "main()", "Main");
		records[2] = new TraceMetadata(2, 1, TraceMetadata.NO_SESSION_ID, TraceMetadata.NO_HOSTNAME, TraceMetadata.NO_PARENT_TRACEID, TraceMetadata.NO_PARENT_ORDER_INDEX);
		records[3] = new BeforeOperationEvent(1, 2, 1, "Bookstore()", "Bookstore");
		records[4] = new AfterOperationEvent(1, 1, 1, "main()", "Main");
		records[5] = new AfterOperationEvent(1, 2, 1, "Bookstore()", "Bookstore");

		for (final IFlowRecord record : records) {
			this.reconstructorUnderTest.execute(record);
		}
		Assert.assertThat(this.traceCollectorList, Matchers.hasSize(2));

		final Trace fstTrace = this.traceCollectorList.get(0);
		final Trace sndTrace = this.traceCollectorList.get(1);

		Assert.assertThat(fstTrace.getRootOperationCall().getOperation(), Matchers.is("main()"));
		Assert.assertThat(sndTrace.getRootOperationCall().getOperation(), Matchers.is("Bookstore()"));
	}

	@Test
	public void reconstructionOfCompleteFailedTraceShouldWork() {
		final IFlowRecord[] records = new IFlowRecord[9];

		records[0] = new TraceMetadata(1, 1, TraceMetadata.NO_SESSION_ID, TraceMetadata.NO_HOSTNAME, TraceMetadata.NO_PARENT_TRACEID, TraceMetadata.NO_PARENT_ORDER_INDEX);
		records[1] = new BeforeOperationEvent(1, 1, 1, "main()", "Main");
		records[2] = new BeforeOperationEvent(1, 1, 1, "Bookstore()", "Bookstore");
		records[3] = new AfterOperationFailedEvent(1, 1, 1, "Bookstore()", "Bookstore", "NullPointerException");
		records[8] = new AfterOperationFailedEvent(1, 1, 1, "main()", "Main", "IllegalArgumentException");

		for (final IFlowRecord record : records) {
			this.reconstructorUnderTest.execute(record);
		}
		Assert.assertThat(this.traceCollectorList, Matchers.hasSize(1));

		final Trace trace = this.traceCollectorList.get(0);
		final OperationCall rootCall = trace.getRootOperationCall();
		Assert.assertThat(rootCall.isFailed(), Matchers.is(true));
		Assert.assertThat(rootCall.getFailedCause(), Matchers.is("IllegalArgumentException"));
		Assert.assertThat(rootCall.getChildren().get(0).isFailed(), Matchers.is(true));
		Assert.assertThat(rootCall.getChildren().get(0).getFailedCause(), Matchers.is("NullPointerException"));
	}

	@Test
	public void reconstructionOfPartialFailedTraceShouldWork() {
		final IFlowRecord[] records = new IFlowRecord[9];

		records[0] = new TraceMetadata(1, 1, TraceMetadata.NO_SESSION_ID, TraceMetadata.NO_HOSTNAME, TraceMetadata.NO_PARENT_TRACEID, TraceMetadata.NO_PARENT_ORDER_INDEX);
		records[1] = new BeforeOperationEvent(1, 1, 1, "main()", "Main");
		records[2] = new BeforeOperationEvent(1, 1, 1, "Bookstore()", "Bookstore");
		records[3] = new AfterOperationFailedEvent(1, 1, 1, "Bookstore()", "Bookstore", "NullPointerException");
		records[8] = new AfterOperationEvent(1, 1, 1, "main()", "Main");

		for (final IFlowRecord record : records) {
			this.reconstructorUnderTest.execute(record);
		}
		Assert.assertThat(this.traceCollectorList, Matchers.hasSize(1));

		final Trace trace = this.traceCollectorList.get(0);
		final OperationCall rootCall = trace.getRootOperationCall();
		Assert.assertThat(rootCall.isFailed(), Matchers.is(false));
		Assert.assertThat(rootCall.containsFailure(), Matchers.is(true));
		Assert.assertThat(rootCall.getChildren().get(0).isFailed(), Matchers.is(true));
		Assert.assertThat(rootCall.getChildren().get(0).getFailedCause(), Matchers.is("NullPointerException"));
	}

}
