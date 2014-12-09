package kieker.gui.model.importer;

import java.io.File;
import java.util.List;
import java.util.Vector;

import kieker.common.record.IMonitoringRecord;
import kieker.common.record.flow.IFlowRecord;
import kieker.gui.model.ExecutionEntry;
import kieker.gui.model.RecordEntry;
import kieker.gui.model.importer.filter.Cloner;
import kieker.gui.model.importer.filter.RecordSimplificator;
import kieker.gui.model.importer.filter.TraceReconstructor;
import teetime.framework.AnalysisConfiguration;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;
import teetime.stage.InstanceOfFilter;
import teetime.stage.className.ClassNameRegistryRepository;
import teetime.stage.io.filesystem.Dir2RecordsFilter;

public class ImportAnalysisConfiguration extends AnalysisConfiguration {

	private final List<RecordEntry> recordsList = new Vector<>(100000);
	private final List<ExecutionEntry> tracesList = new Vector<>(100000);

	public ImportAnalysisConfiguration(final File importDirectory) {
		// Create the stages
		final InitialElementProducer<File> producer = new InitialElementProducer<>(importDirectory);
		final Dir2RecordsFilter reader = new Dir2RecordsFilter(new ClassNameRegistryRepository());
		final InstanceOfFilter<IMonitoringRecord, IFlowRecord> typeFilter = new InstanceOfFilter<>(IFlowRecord.class);
		final Cloner<IFlowRecord> distributor = new Cloner<>();
		final RecordSimplificator recordSimplificator = new RecordSimplificator();
		final CollectorSink<RecordEntry> recordCollector = new CollectorSink<>(this.recordsList);
		final TraceReconstructor traceReconstructor = new TraceReconstructor();
		final CollectorSink<ExecutionEntry> traceCollector = new CollectorSink<>(this.tracesList);

		// Connect the stages
		final IPipeFactory pipeFactory = AnalysisConfiguration.PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false);
		pipeFactory.create(producer.getOutputPort(), reader.getInputPort());
		pipeFactory.create(reader.getOutputPort(), typeFilter.getInputPort());
		pipeFactory.create(typeFilter.getOutputPort(), distributor.getInputPort());
		pipeFactory.create(distributor.getFirstOutputPort(), recordSimplificator.getInputPort());
		pipeFactory.create(recordSimplificator.getOutputPort(), recordCollector.getInputPort());
		pipeFactory.create(distributor.getSecondOutputPort(), traceReconstructor.getInputPort());
		pipeFactory.create(traceReconstructor.getOutputPort(), traceCollector.getInputPort());

		super.addThreadableStage(producer);
	}

	public List<RecordEntry> getRecordsList() {
		return this.recordsList;
	}

	public List<ExecutionEntry> getTracesList() {
		return this.tracesList;
	}

}
