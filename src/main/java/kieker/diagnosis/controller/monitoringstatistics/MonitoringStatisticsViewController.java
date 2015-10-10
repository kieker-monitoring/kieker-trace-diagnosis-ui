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

package kieker.diagnosis.controller.monitoringstatistics;

import java.io.File;
import java.util.Arrays;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import kieker.diagnosis.domain.AggregatedOperationCall;
import kieker.diagnosis.domain.AggregatedTrace;
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.domain.Trace;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.util.ErrorHandling;
import kieker.tools.util.LoggingTimestampConverter;

/**
 * @author Nils Christian Ehmke
 */
public final class MonitoringStatisticsViewController {

	private static final String[] UNITS = { "Bytes", "Kilobytes", "Megabytes", "Gigabytes", };
	private static final float SIZE_OF_BYTE = 1024.0f;

	private final DataModel dataModel = DataModel.getInstance();

	@FXML private TextField monitoringlog;
	@FXML private TextField monitoringsize;
	@FXML private TextField analysistime;
	@FXML private TextField beginofmonitoring;
	@FXML private TextField endofmonitoring;
	@FXML private TextField numberofcalls;
	@FXML private TextField numberoffailedcalls;
	@FXML private TextField numberofaggcalls;
	@FXML private TextField numberoffailedaggcalls;
	@FXML private TextField numberoftraces;
	@FXML private TextField numberoffailedtraces;
	@FXML private TextField numberoffailuretraces;
	@FXML private TextField numberofaggtraces;
	@FXML private TextField numberofaggfailedtraces;
	@FXML private TextField numberofaggfailuretraces;
	@FXML private TextField incompletetraces;
	@FXML private TextField danglingrecords;
	@FXML private TextField ignoredRecords;

	@ErrorHandling
	public void initialize() {
		final ObjectProperty<File> importDirectory = this.dataModel.getImportDirectory();
		this.monitoringlog.textProperty().bind(Bindings.createStringBinding(() -> this.assemblePathString(importDirectory.get()), importDirectory));
		this.monitoringsize.textProperty().bind(Bindings.createStringBinding(() -> this.assembleSizeString(importDirectory.get()), importDirectory));

		final ObjectProperty<Long> duration = this.dataModel.getAnalysisDurationInMS();
		this.analysistime.textProperty().bind(Bindings.createStringBinding(() -> this.assembleDurationString(duration.get()), duration));

		final ObjectProperty<Long> beginTimestamp = this.dataModel.getBeginTimestamp();
		final ObjectProperty<Long> endTimestamp = this.dataModel.getEndTimestamp();
		this.beginofmonitoring.textProperty().bind(Bindings.createStringBinding(() -> this.assembleTimeString(beginTimestamp.get()), beginTimestamp));
		this.endofmonitoring.textProperty().bind(Bindings.createStringBinding(() -> this.assembleTimeString(endTimestamp.get()), endTimestamp));

		final ObservableList<OperationCall> operationCalls = this.dataModel.getOperationCalls();
		final FilteredList<OperationCall> failedOperationCalls = new FilteredList<>(operationCalls, OperationCall::isFailed);
		this.numberofcalls.textProperty().bind(Bindings.createStringBinding(() -> Integer.toString(operationCalls.size()), operationCalls));
		this.numberoffailedcalls.textProperty().bind(Bindings.createStringBinding(() -> Integer.toString(failedOperationCalls.size()), failedOperationCalls));

		final ObservableList<AggregatedOperationCall> aggOperationCalls = this.dataModel.getAggregatedOperationCalls();
		final FilteredList<AggregatedOperationCall> failedAggOperationCalls = new FilteredList<>(aggOperationCalls, AggregatedOperationCall::isFailed);
		this.numberofaggcalls.textProperty().bind(Bindings.createStringBinding(() -> Integer.toString(aggOperationCalls.size()), aggOperationCalls));
		this.numberoffailedaggcalls.textProperty().bind(Bindings.createStringBinding(() -> Integer.toString(failedAggOperationCalls.size()), failedAggOperationCalls));

		final ObservableList<Trace> traces = this.dataModel.getTraces();
		final FilteredList<Trace> failedTraces = new FilteredList<>(traces, t -> t.getRootOperationCall().isFailed());
		final FilteredList<Trace> failureTraces = new FilteredList<>(traces, t -> t.getRootOperationCall().containsFailure());
		this.numberoftraces.textProperty().bind(Bindings.createStringBinding(() -> Integer.toString(traces.size()), traces));
		this.numberoffailedtraces.textProperty().bind(Bindings.createStringBinding(() -> Integer.toString(failedTraces.size()), failedTraces));
		this.numberoffailuretraces.textProperty().bind(Bindings.createStringBinding(() -> Integer.toString(failureTraces.size()), failureTraces));

		final ObservableList<AggregatedTrace> aggTraces = this.dataModel.getAggregatedTraces();
		final FilteredList<AggregatedTrace> failedAggTraces = new FilteredList<>(aggTraces, t -> t.getRootOperationCall().isFailed());
		final FilteredList<AggregatedTrace> failureAggTraces = new FilteredList<>(aggTraces, t -> t.getRootOperationCall().containsFailure());
		this.numberofaggtraces.textProperty().bind(Bindings.createStringBinding(() -> Integer.toString(aggTraces.size()), aggTraces));
		this.numberofaggfailedtraces.textProperty().bind(Bindings.createStringBinding(() -> Integer.toString(failedAggTraces.size()), failedAggTraces));
		this.numberofaggfailuretraces.textProperty().bind(Bindings.createStringBinding(() -> Integer.toString(failureAggTraces.size()), failureAggTraces));

		final ObjectProperty<Integer> countIncompleteTraces = this.dataModel.countIncompleteTraces();
		this.incompletetraces.textProperty().bind(Bindings.createStringBinding(() -> Integer.toString(countIncompleteTraces.get()), countIncompleteTraces));

		final ObjectProperty<Integer> countDanglingRecords = this.dataModel.countDanglingRecords();
		this.danglingrecords.textProperty().bind(Bindings.createStringBinding(() -> Integer.toString(countDanglingRecords.get()), countDanglingRecords));

		final ObjectProperty<Integer> countIgnoredRecords = this.dataModel.countIgnoredRecords();
		this.ignoredRecords.textProperty().bind(Bindings.createStringBinding(() -> Integer.toString(countIgnoredRecords.get()), countIgnoredRecords));
	}

	private String assembleTimeString(final Long timestamp) {
		return (timestamp == null) ? "N/A" : LoggingTimestampConverter.convertLoggingTimestampLocalTimeZoneString(timestamp);
	}

	private String assembleDurationString(final Long duration) {
		return (duration == null) ? "N/A" : duration + " ms";
	}

	private String assemblePathString(final File file) {
		return (file == null) ? "N/A" : file.getAbsolutePath();
	}

	private String assembleSizeString(final File file) {
		String importDirectorySizeString = "N/A";
		if (file == null) {
			return importDirectorySizeString;
		}
		final float size = MonitoringStatisticsViewController.calculateDirectorySize(file);

		float newSize = size;
		for (final String unit : MonitoringStatisticsViewController.UNITS) {
			if (newSize >= MonitoringStatisticsViewController.SIZE_OF_BYTE) {
				newSize /= MonitoringStatisticsViewController.SIZE_OF_BYTE;
			} else {
				importDirectorySizeString = String.format("%.1f %s", newSize, unit);
				break;
			}
		}

		return importDirectorySizeString;
	}

	private static long calculateDirectorySize(final File file) {
		return (file.isFile()) ? file.length() : Arrays.stream(file.listFiles()).mapToLong(MonitoringStatisticsViewController::calculateDirectorySize).sum();
	}

}
