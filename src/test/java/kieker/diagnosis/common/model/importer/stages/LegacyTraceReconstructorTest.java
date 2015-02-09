package kieker.diagnosis.common.model.importer.stages;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import kieker.common.record.controlflow.OperationExecutionRecord;
import kieker.diagnosis.common.domain.Trace;

import org.junit.Before;
import org.junit.Test;

import teetime.framework.Analysis;
import teetime.framework.AnalysisConfiguration;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.SingleElementPipeFactory;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;
import teetime.stage.InstanceOfFilter;
import teetime.stage.className.ClassNameRegistryRepository;
import teetime.stage.io.filesystem.Dir2RecordsFilter;

public class LegacyTraceReconstructorTest {

	private List<Trace> traceCollectorList;
	private LegacyTraceReconstructor reconstructorUnderTest;
	private CollectorSink<Trace> traceCollector;

	@Before
	public void initializeTraceReconstructor() {
		this.traceCollectorList = new ArrayList<>();

		this.reconstructorUnderTest = new LegacyTraceReconstructor();
		this.traceCollector = new CollectorSink<>(this.traceCollectorList);

		final IPipeFactory pipeFactory = new SingleElementPipeFactory();
		pipeFactory.create(this.reconstructorUnderTest.getOutputPort(), this.traceCollector.getInputPort());
	}

	@Test
	public void singleCallReconstructionShouldWork() throws Exception {
		this.reconstructorUnderTest.execute(new OperationExecutionRecord("operation", OperationExecutionRecord.NO_SESSION_ID, 42, 15L, 20L, "localhost", 0, 0));

		assertThat(this.traceCollectorList, hasSize(1));
		assertThat(this.traceCollectorList.get(0).getRootOperationCall().getOperation(), is("operation"));
		assertThat(this.traceCollectorList.get(0).getRootOperationCall().getDuration(), is(5L));
		assertThat(this.traceCollectorList.get(0).getRootOperationCall().getTraceID(), is(42L));
	}

	@Test
	public void nestedCallReconstructionShouldWork() throws Exception {
		this.reconstructorUnderTest.execute(new OperationExecutionRecord("operation2", OperationExecutionRecord.NO_SESSION_ID, 42, 10L, 20L, "localhost", 1, 1));
		this.reconstructorUnderTest.execute(new OperationExecutionRecord("operation3", OperationExecutionRecord.NO_SESSION_ID, 42, 10L, 20L, "localhost", 2, 1));
		this.reconstructorUnderTest.execute(new OperationExecutionRecord("operation4", OperationExecutionRecord.NO_SESSION_ID, 42, 10L, 20L, "localhost", 3, 2));
		this.reconstructorUnderTest.execute(new OperationExecutionRecord("operation1", OperationExecutionRecord.NO_SESSION_ID, 42, 10L, 20L, "localhost", 0, 0));

		assertThat(this.traceCollectorList, hasSize(1));
		assertThat(this.traceCollectorList.get(0).getRootOperationCall().getOperation(), is("operation1"));
		assertThat(this.traceCollectorList.get(0).getRootOperationCall().getChildren().get(1).getOperation(), is("operation2"));
		assertThat(this.traceCollectorList.get(0).getRootOperationCall().getDuration(), is(10L));
	}

	@Test
	public void exampleLogReconstructionShouldWork() throws Exception {
		final AnalysisConfiguration analysisConfiguration = new AnalysisConfiguration();

		final InitialElementProducer<File> producer = new InitialElementProducer<>(new File("example/execution monitoring log"));
		final Dir2RecordsFilter reader = new Dir2RecordsFilter(new ClassNameRegistryRepository());
		final InstanceOfFilter<Object, OperationExecutionRecord> typeFilter = new InstanceOfFilter<>(OperationExecutionRecord.class);

		final IPipeFactory pipeFactory = new SingleElementPipeFactory();
		pipeFactory.create(producer.getOutputPort(), reader.getInputPort());
		pipeFactory.create(reader.getOutputPort(), typeFilter.getInputPort());
		pipeFactory.create(typeFilter.getOutputPort(), this.reconstructorUnderTest.getInputPort());

		analysisConfiguration.addThreadableStage(producer);
		final Analysis analysis = new Analysis(analysisConfiguration);
		analysis.start();

		assertThat(this.traceCollectorList, hasSize(1635));
	}

}
