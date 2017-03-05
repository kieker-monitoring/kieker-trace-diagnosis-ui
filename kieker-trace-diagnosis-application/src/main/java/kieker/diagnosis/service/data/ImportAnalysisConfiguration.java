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

package kieker.diagnosis.service.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import kieker.common.record.IMonitoringRecord;
import kieker.common.record.misc.KiekerMetadataRecord;
import kieker.diagnosis.service.ServiceUtil;
import kieker.diagnosis.service.data.domain.AggregatedOperationCall;
import kieker.diagnosis.service.data.domain.AggregatedTrace;
import kieker.diagnosis.service.data.domain.OperationCall;
import kieker.diagnosis.service.data.domain.Trace;
import kieker.diagnosis.service.data.stages.AllowedRecordsFilter;
import kieker.diagnosis.service.data.stages.BeginEndOfMonitoringDetector;
import kieker.diagnosis.service.data.stages.OperationCallHandlerComposite;
import kieker.diagnosis.service.data.stages.ReadingComposite;
import kieker.diagnosis.service.data.stages.TraceAggregationComposite;
import kieker.diagnosis.service.data.stages.TraceReconstructionComposite;
import kieker.diagnosis.service.properties.AdditionalLogChecksProperty;
import kieker.diagnosis.service.properties.PropertiesService;
import teetime.framework.Configuration;
import teetime.stage.CollectorSink;
import teetime.stage.MultipleInstanceOfFilter;
import teetime.stage.basic.distributor.Distributor;
import teetime.stage.basic.distributor.strategy.CopyByReferenceStrategy;

/**
 * A {@code TeeTime} configuration for the import and analysis of monitoring logs.
 *
 * @author Nils Christian Ehmke
 */
public final class ImportAnalysisConfiguration extends Configuration {

	private final PropertiesService ivPropertiesService = ServiceUtil.getService( PropertiesService.class );

	private final List<Trace> ivTraces = new ArrayList<>( 1000 );
	private final List<OperationCall> ivOperationCalls = new ArrayList<>( 1000 );
	private final List<AggregatedOperationCall> ivAggregatedOperationCalls = new ArrayList<>( 1000 );
	private final List<AggregatedTrace> ivAggregatedTraces = new ArrayList<>( 1000 );

	private final List<KiekerMetadataRecord> ivMetadataRecords = new ArrayList<>( 1000 );
	private final TraceReconstructionComposite ivReconstruction;
	private final BeginEndOfMonitoringDetector ivBeginEndOfMonitoringDetector;
	private final AllowedRecordsFilter ivAllowedRecordsFilter;

	public ImportAnalysisConfiguration( final File aImportDirectory ) {
		// Create the stages
		final ReadingComposite reader = new ReadingComposite( aImportDirectory );
		final MultipleInstanceOfFilter<IMonitoringRecord> typeFilter = new MultipleInstanceOfFilter<>( );
		final Distributor<Trace> distributor = new Distributor<>( new CopyByReferenceStrategy( ) );
		final TraceAggregationComposite aggregation = new TraceAggregationComposite( ivAggregatedTraces );
		final CollectorSink<KiekerMetadataRecord> metadataCollector = new CollectorSink<>( ivMetadataRecords );
		final OperationCallHandlerComposite operationCallHandler = new OperationCallHandlerComposite( ivOperationCalls, ivAggregatedOperationCalls );

		ivAllowedRecordsFilter = new AllowedRecordsFilter( );
		ivBeginEndOfMonitoringDetector = new BeginEndOfMonitoringDetector( );
		ivReconstruction = new TraceReconstructionComposite( ivTraces, ivPropertiesService.loadPrimitiveProperty( AdditionalLogChecksProperty.class ) );

		// Connect the stages
		connectPorts( reader.getOutputPort( ), ivAllowedRecordsFilter.getInputPort( ) );
		connectPorts( ivAllowedRecordsFilter.getOutputPort( ), typeFilter.getInputPort( ) );
		connectPorts( typeFilter.getOutputPortForType( IMonitoringRecord.class ), ivBeginEndOfMonitoringDetector.getInputPort( ) );
		connectPorts( ivBeginEndOfMonitoringDetector.getOutputPort( ), ivReconstruction.getInputPort( ) );
		connectPorts( ivReconstruction.getOutputPort( ), distributor.getInputPort( ) );
		connectPorts( distributor.getNewOutputPort( ), operationCallHandler.getInputPort( ) );
		connectPorts( distributor.getNewOutputPort( ), aggregation.getInputPort( ) );
		connectPorts( typeFilter.getOutputPortForType( KiekerMetadataRecord.class ), metadataCollector.getInputPort( ) );
	}

	public long getBeginTimestamp( ) {
		return ivBeginEndOfMonitoringDetector.getBeginTimestamp( );
	}

	public long getEndTimestamp( ) {
		return ivBeginEndOfMonitoringDetector.getEndTimestamp( );
	}

	public int countIncompleteTraces( ) {
		return ivReconstruction.countIncompleteTraces( );
	}

	public int countDanglingEvents( ) {
		return ivReconstruction.countDanglingRecords( );
	}

	public int countIgnoredRecords( ) {
		return ivAllowedRecordsFilter.getIgnoredRecords( );
	}

	public List<Trace> getTracesList( ) {
		return ivTraces;
	}

	public List<AggregatedTrace> getAggregatedTraces( ) {
		return ivAggregatedTraces;
	}

	public List<KiekerMetadataRecord> getMetadataRecords( ) {
		return ivMetadataRecords;
	}

	public List<OperationCall> getOperationCalls( ) {
		return ivOperationCalls;
	}

	public List<AggregatedOperationCall> getAggregatedOperationCalls( ) {
		return ivAggregatedOperationCalls;
	}

}
