package kieker.diagnosis.czi;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import kieker.common.record.IMonitoringRecord;
import kieker.diagnosis.czi.stages.CreateListOfDatabaseCalls;
import kieker.diagnosis.czi.stages.DatabaseCallMerger;
import kieker.diagnosis.czi.stages.DatabaseRecordTransformator;
import kieker.diagnosis.czi.stages.PreparedStatementExtactor;
import kieker.diagnosis.czi.stages.StatementAggregator;
import kieker.diagnosis.czi.stages.StatementExtractor;
import kieker.diagnosis.domain.AggregatedDatabaseOperationCall;
import kieker.diagnosis.domain.DatabaseOperationCall;
import kieker.diagnosis.domain.PreparedStatementCall;
import kieker.diagnosis.model.importer.stages.ReadingComposite;
import teetime.framework.AnalysisConfiguration;
import teetime.stage.CollectorSink;
import teetime.stage.MultipleInstanceOfFilter;
import teetime.stage.basic.distributor.CopyByReferenceStrategy;
import teetime.stage.basic.distributor.Distributor;

/**
 * A {@code TeeTime} configuration for the import and analysis of database
 * related monitoring logs.
 * 
 * @author Christian Zirkelbach
 */
public class DatabaseImportAnalysisConfiguration extends AnalysisConfiguration {

	private List<List<DatabaseOperationCall>> mergedDatabaseCalls = new LinkedList<List<DatabaseOperationCall>>();
	private List<List<DatabaseOperationCall>> statements = new LinkedList<List<DatabaseOperationCall>>();
	private List<List<AggregatedDatabaseOperationCall>> aggregatedStatements = new LinkedList<List<AggregatedDatabaseOperationCall>>();
	private List<List<PreparedStatementCall>> preparedStatements = new LinkedList<List<PreparedStatementCall>>();
	
	private final CollectorSink<List<DatabaseOperationCall>> collectorDatabaseCalls;
	private final CollectorSink<List<DatabaseOperationCall>> collectorStatements;
	private final CollectorSink<List<AggregatedDatabaseOperationCall>> collectorAggregatedStatements;
	private final CollectorSink<List<PreparedStatementCall>> collectorPreparedStatements;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DatabaseImportAnalysisConfiguration(final File importDirectory) {

		this.collectorDatabaseCalls = new CollectorSink(mergedDatabaseCalls);
		this.collectorStatements = new CollectorSink(statements);
		this.collectorAggregatedStatements = new CollectorSink(aggregatedStatements);
		this.collectorPreparedStatements = new CollectorSink(preparedStatements);

		// Create the stages
		final ReadingComposite reader = new ReadingComposite(importDirectory);
		final MultipleInstanceOfFilter<IMonitoringRecord> typeFilter = new MultipleInstanceOfFilter<>();
		final DatabaseRecordTransformator transformator = new DatabaseRecordTransformator();
		
		final CreateListOfDatabaseCalls createListOfDatabaseCalls = new CreateListOfDatabaseCalls();
		
		final Distributor<List<DatabaseOperationCall>> distributorMergedDatabaseCalls = new Distributor<>(new CopyByReferenceStrategy());
		final Distributor<List<DatabaseOperationCall>> distributorStatements = new Distributor<>(new CopyByReferenceStrategy());
		
		final DatabaseCallMerger databaseCallMerger = new DatabaseCallMerger();
		final StatementExtractor statementExtractor = new StatementExtractor();
		final StatementAggregator statementAggregator = new StatementAggregator();
		final PreparedStatementExtactor preparedStatementExtactor = new PreparedStatementExtactor();
		
		// Connect the stages
		AnalysisConfiguration.connectIntraThreads(reader.getOutputPort(), typeFilter.getInputPort());
		AnalysisConfiguration.connectIntraThreads(typeFilter.getOutputPortForType(IMonitoringRecord.class), transformator.getInputPort());
		
		// transformation from DatabaseOperationCall to List<DatabaseOperationCall>
		AnalysisConfiguration.connectIntraThreads(transformator.getOutputPort(), createListOfDatabaseCalls.getInputPort());
		AnalysisConfiguration.connectIntraThreads(createListOfDatabaseCalls.getOutputPort(), databaseCallMerger.getInputPort());
		
		AnalysisConfiguration.connectIntraThreads(databaseCallMerger.getOutputPort(), distributorMergedDatabaseCalls.getInputPort());
		AnalysisConfiguration.connectIntraThreads(distributorMergedDatabaseCalls.getNewOutputPort(), this.collectorDatabaseCalls.getInputPort());
		AnalysisConfiguration.connectIntraThreads(distributorMergedDatabaseCalls.getNewOutputPort(), statementExtractor.getInputPort());
		AnalysisConfiguration.connectIntraThreads(distributorMergedDatabaseCalls.getNewOutputPort(), preparedStatementExtactor.getInputPort());
		
		AnalysisConfiguration.connectIntraThreads(statementExtractor.getOutputPort(), distributorStatements.getInputPort());
		AnalysisConfiguration.connectIntraThreads(distributorStatements.getNewOutputPort(), this.collectorStatements.getInputPort());
		AnalysisConfiguration.connectIntraThreads(distributorStatements.getNewOutputPort(), statementAggregator.getInputPort());
		AnalysisConfiguration.connectIntraThreads(statementAggregator.getOutputPort(), this.collectorAggregatedStatements.getInputPort());
		
		AnalysisConfiguration.connectIntraThreads(preparedStatementExtactor.getOutputPort(), this.collectorPreparedStatements.getInputPort());
		
		// Make sure that the producer is executed by the analysis
		super.addThreadableStage(reader);
	}

	public List<DatabaseOperationCall> getDatabaseOperationCalls() {
		return this.mergedDatabaseCalls.get(0);
	}
	
	public List<DatabaseOperationCall> getStatements() {
		return this.statements.get(0);
	}
	
	public List<AggregatedDatabaseOperationCall> getAggregatedStatements() {
		return this.aggregatedStatements.get(0);
	}
	
	public List<PreparedStatementCall> getPreparedStatements() {
		return this.preparedStatements.get(0);
	}
}