/***************************************************************************
 * Copyright 2015 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import kieker.common.record.IMonitoringRecord;
import kieker.common.record.misc.KiekerMetadataRecord;
import kieker.diagnosis.domain.AggregatedOperationCall;
import kieker.diagnosis.domain.AggregatedTrace;
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.domain.Trace;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.util.stages.AllowedRecordsFilter;
import kieker.diagnosis.util.stages.BeginEndOfMonitoringDetector;
import kieker.diagnosis.util.stages.OperationCallHandlerComposite;
import kieker.diagnosis.util.stages.ReadingComposite;
import kieker.diagnosis.util.stages.TraceAggregationComposite;
import kieker.diagnosis.util.stages.TraceReconstructionComposite;
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

	private final List<Trace> ivTraces = new ArrayList<>(1000);
	private final List<OperationCall> ivOperationCalls = new ArrayList<>(1000);
	private final List<AggregatedOperationCall> ivAggregatedOperationCalls = new ArrayList<>(1000);
	private final List<AggregatedTrace> ivAggregatedTraces = new ArrayList<>(1000);

	private final List<KiekerMetadataRecord> ivMetadataRecords = new ArrayList<>(1000);
	private final TraceReconstructionComposite ivReconstruction;
	private final BeginEndOfMonitoringDetector ivBeginEndOfMonitoringDetector;
	private final AllowedRecordsFilter ivAllowedRecordsFilter;

	public ImportAnalysisConfiguration(final File aImportDirectory) {
		// Create the stages
		final ReadingComposite reader = new ReadingComposite(aImportDirectory);
		final MultipleInstanceOfFilter<IMonitoringRecord> typeFilter = new MultipleInstanceOfFilter<>();
		final Distributor<Trace> distributor = new Distributor<>(new CopyByReferenceStrategy());
		final TraceAggregationComposite aggregation = new TraceAggregationComposite(this.ivAggregatedTraces);
		final CollectorSink<KiekerMetadataRecord> metadataCollector = new CollectorSink<>(this.ivMetadataRecords);
		final OperationCallHandlerComposite operationCallHandler = new OperationCallHandlerComposite(this.ivOperationCalls, this.ivAggregatedOperationCalls);

		this.ivAllowedRecordsFilter = new AllowedRecordsFilter();
		this.ivBeginEndOfMonitoringDetector = new BeginEndOfMonitoringDetector();
		this.ivReconstruction = new TraceReconstructionComposite(this.ivTraces, PropertiesModel.getInstance().isAdditionalLogChecksActive());

		// Connect the stages
		super.connectPorts(reader.getOutputPort(), this.ivAllowedRecordsFilter.getInputPort());
		super.connectPorts(this.ivAllowedRecordsFilter.getOutputPort(), typeFilter.getInputPort());
		super.connectPorts(typeFilter.getOutputPortForType(IMonitoringRecord.class), this.ivBeginEndOfMonitoringDetector.getInputPort());
		super.connectPorts(this.ivBeginEndOfMonitoringDetector.getOutputPort(), this.ivReconstruction.getInputPort());
		super.connectPorts(this.ivReconstruction.getOutputPort(), distributor.getInputPort());
		super.connectPorts(distributor.getNewOutputPort(), operationCallHandler.getInputPort());
		super.connectPorts(distributor.getNewOutputPort(), aggregation.getInputPort());
		super.connectPorts(typeFilter.getOutputPortForType(KiekerMetadataRecord.class), metadataCollector.getInputPort());
	}

	public long getBeginTimestamp() {
		return this.ivBeginEndOfMonitoringDetector.getBeginTimestamp();
	}

	public long getEndTimestamp() {
		return this.ivBeginEndOfMonitoringDetector.getEndTimestamp();
	}

	public int countIncompleteTraces() {
		return this.ivReconstruction.countIncompleteTraces();
	}

	public int countDanglingEvents() {
		return this.ivReconstruction.countDanglingRecords();
	}

	public int countIgnoredRecords() {
		return this.ivAllowedRecordsFilter.getIgnoredRecords();
	}

	public List<Trace> getTracesList() {
		return this.ivTraces;
	}

	public List<AggregatedTrace> getAggregatedTraces() {
		return this.ivAggregatedTraces;
	}

	public List<KiekerMetadataRecord> getMetadataRecords() {
		return this.ivMetadataRecords;
	}

	public List<OperationCall> getOperationCalls() {
		return this.ivOperationCalls;
	}

	public List<AggregatedOperationCall> getAggregatedOperationCalls() {
		return this.ivAggregatedOperationCalls;
	}

}
