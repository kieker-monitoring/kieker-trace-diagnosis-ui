package kieker.gui.model.importer.stages;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import kieker.common.record.flow.IFlowRecord;
import kieker.common.record.flow.trace.TraceMetadata;
import kieker.common.record.flow.trace.operation.AfterOperationEvent;
import kieker.common.record.flow.trace.operation.AfterOperationFailedEvent;
import kieker.common.record.flow.trace.operation.BeforeOperationEvent;
import kieker.gui.model.domain.Execution;

import org.junit.Before;
import org.junit.Test;

import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.SingleElementPipeFactory;
import teetime.stage.CollectorSink;

public class TraceReconstructorTest {

	private List<Execution> traceCollectorList;
	private TraceReconstructor reconstructorUnderTest;
	private CollectorSink<Execution> traceCollector;

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
		assertThat(this.traceCollectorList, hasSize(1));

		final Execution trace = this.traceCollectorList.get(0);
		assertThat(trace.getOperation(), is("main()"));
		assertThat(trace.getChildren(), hasSize(2));
		assertThat(trace.getChildren().get(0).getOperation(), is("Bookstore()"));
		assertThat(trace.getChildren().get(1).getOperation(), is("Catalog()"));
		assertThat(trace.getChildren().get(1).getChildren(), hasSize(1));
		assertThat(trace.getChildren().get(1).getChildren().get(0).getOperation(), is("CRM()"));
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
		assertThat(this.traceCollectorList, hasSize(2));

		final Execution fstTrace = this.traceCollectorList.get(0);
		final Execution sndTrace = this.traceCollectorList.get(1);

		assertThat(fstTrace.getOperation(), is("main()"));
		assertThat(sndTrace.getOperation(), is("Bookstore()"));
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
		assertThat(this.traceCollectorList, hasSize(1));

		final Execution trace = this.traceCollectorList.get(0);
		assertThat(trace.isFailed(), is(true));
		assertThat(trace.getFailedCause(), is("IllegalArgumentException"));
		assertThat(trace.getChildren().get(0).isFailed(), is(true));
		assertThat(trace.getChildren().get(0).getFailedCause(), is("NullPointerException"));
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
		assertThat(this.traceCollectorList, hasSize(1));

		final Execution trace = this.traceCollectorList.get(0);
		assertThat(trace.isFailed(), is(false));
		assertThat(trace.containsFailure(), is(true));
		assertThat(trace.getChildren().get(0).isFailed(), is(true));
		assertThat(trace.getChildren().get(0).getFailedCause(), is("NullPointerException"));
	}

}
