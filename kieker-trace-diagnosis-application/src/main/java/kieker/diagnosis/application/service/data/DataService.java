package kieker.diagnosis.application.service.data;

import kieker.diagnosis.application.service.data.domain.AggregatedOperationCall;
import kieker.diagnosis.application.service.data.domain.AggregatedTrace;
import kieker.diagnosis.application.service.data.domain.OperationCall;
import kieker.diagnosis.application.service.data.domain.Trace;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;

public interface DataService {

	public void loadMonitoringLogFromFS( File aImportDirectory );

	public ObjectProperty<Long> getBeginTimestamp( );

	public ObjectProperty<Long> getEndTimestamp( );

	public ObjectProperty<Integer> countIncompleteTraces( );

	public ObjectProperty<Integer> countDanglingRecords( );

	public ObjectProperty<Integer> countFaultyTraces( );

	public ObjectProperty<Integer> countIgnoredRecords( );

	public ObjectProperty<File> getImportDirectory( );

	public ObjectProperty<Long> getAnalysisDurationInMS( );

	public ObservableList<Trace> getTraces( );

	public ObservableList<AggregatedTrace> getAggregatedTraces( );

	public ObservableList<OperationCall> getOperationCalls( );

	public ObservableList<AggregatedOperationCall> getAggregatedOperationCalls( );

	public TimeUnit getTimeUnit( );

}
