/***************************************************************************
 * Copyright 2015-2017 Kieker Project (http://kieker-monitoring.net)
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
import kieker.diagnosis.application.service.data.stages.AllowedRecordsFilter;
import kieker.diagnosis.application.service.data.stages.BeginEndOfMonitoringDetector;
import kieker.diagnosis.application.service.data.stages.OperationCallHandlerComposite;
import kieker.diagnosis.application.service.data.stages.ReadingComposite;
import kieker.diagnosis.application.service.data.stages.TraceAggregationComposite;
import kieker.diagnosis.application.service.data.stages.TraceReconstructionComposite;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import kieker.common.record.IMonitoringRecord;
import kieker.common.record.misc.KiekerMetadataRecord;

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
@Component
@Scope ( ConfigurableBeanFactory.SCOPE_PROTOTYPE )
final class ImportAnalysisConfiguration extends Configuration {

	private final List<Trace> ivTraces = new ArrayList<>( 1000 );
	private final List<OperationCall> ivOperationCalls = new ArrayList<>( 1000 );
	private final List<AggregatedOperationCall> ivAggregatedOperationCalls = new ArrayList<>( 1000 );
	private final List<AggregatedTrace> ivAggregatedTraces = new ArrayList<>( 1000 );

	private final List<KiekerMetadataRecord> ivMetadataRecords = new ArrayList<>( 1000 );
	private final TraceReconstructionComposite ivReconstruction;
	private final BeginEndOfMonitoringDetector ivBeginEndOfMonitoringDetector;
	private final AllowedRecordsFilter ivAllowedRecordsFilter;

	ImportAnalysisConfiguration( final File aImportDirectory, final boolean aActivateAdditionalLogChecks, final boolean aPercentCalculationsRefersToTopMost ) {
		// Create the stages
		final ReadingComposite reader = new ReadingComposite( aImportDirectory );
		final MultipleInstanceOfFilter<IMonitoringRecord> typeFilter = new MultipleInstanceOfFilter<>( );
		final Distributor<Trace> distributor = new Distributor<>( new CopyByReferenceStrategy( ) );
		final TraceAggregationComposite aggregation = new TraceAggregationComposite( ivAggregatedTraces );
		final CollectorSink<KiekerMetadataRecord> metadataCollector = new CollectorSink<>( ivMetadataRecords );
		final OperationCallHandlerComposite operationCallHandler = new OperationCallHandlerComposite( ivOperationCalls, ivAggregatedOperationCalls );

		ivAllowedRecordsFilter = new AllowedRecordsFilter( );
		ivBeginEndOfMonitoringDetector = new BeginEndOfMonitoringDetector( );

		ivReconstruction = new TraceReconstructionComposite( ivTraces, aActivateAdditionalLogChecks, aPercentCalculationsRefersToTopMost );

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

	long getBeginTimestamp( ) {
		return ivBeginEndOfMonitoringDetector.getBeginTimestamp( );
	}

	long getEndTimestamp( ) {
		return ivBeginEndOfMonitoringDetector.getEndTimestamp( );
	}

	int countIncompleteTraces( ) {
		return ivReconstruction.countIncompleteTraces( );
	}

	int countDanglingEvents( ) {
		return ivReconstruction.countDanglingRecords( );
	}

	int countIgnoredRecords( ) {
		return ivAllowedRecordsFilter.getIgnoredRecords( );
	}

	List<Trace> getTracesList( ) {
		return ivTraces;
	}

	List<AggregatedTrace> getAggregatedTraces( ) {
		return ivAggregatedTraces;
	}

	List<KiekerMetadataRecord> getMetadataRecords( ) {
		return ivMetadataRecords;
	}

	List<OperationCall> getOperationCalls( ) {
		return ivOperationCalls;
	}

	List<AggregatedOperationCall> getAggregatedOperationCalls( ) {
		return ivAggregatedOperationCalls;
	}

}
