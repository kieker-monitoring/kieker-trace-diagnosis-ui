package kieker.gui.common.importer.stages;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import kieker.common.record.flow.trace.operation.BeforeOperationEvent;
import kieker.gui.common.domain.Record;
import kieker.gui.common.importer.stages.RecordSimplificator;

import org.junit.Before;
import org.junit.Test;

import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.SingleElementPipeFactory;
import teetime.stage.CollectorSink;

public class RecordSimplificatorTest {

	private List<Record> recordCollectorList;
	private RecordSimplificator simplificatorUnderTest;
	private CollectorSink<Record> recordCollector;

	@Before
	public void initializeRecordSimplificator() {
		this.recordCollectorList = new ArrayList<>();

		this.simplificatorUnderTest = new RecordSimplificator();
		this.recordCollector = new CollectorSink<>(this.recordCollectorList);

		final IPipeFactory pipeFactory = new SingleElementPipeFactory();
		pipeFactory.create(this.simplificatorUnderTest.getOutputPort(), this.recordCollector.getInputPort());
	}

	@Test
	public void simplificationShouldPreserveContent() {
		final BeforeOperationEvent record = new BeforeOperationEvent(1, 2, 3, "Bookstore()", "Bookstore");
		this.simplificatorUnderTest.execute(record);

		assertThat(this.recordCollectorList, hasSize(1));

		final Record simplifiedRecord = this.recordCollectorList.get(0);
		assertThat(simplifiedRecord.getType(), is(record.getClass().getCanonicalName()));
		assertThat(simplifiedRecord.getRepresentation(), is(record.toString()));
		assertThat(simplifiedRecord.getTimestamp(), is(record.getLoggingTimestamp()));
	}

}
