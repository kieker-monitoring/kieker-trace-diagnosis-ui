/***************************************************************************
 * Copyright 2015-2016 Kieker Project (http://kieker-monitoring.net)
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
