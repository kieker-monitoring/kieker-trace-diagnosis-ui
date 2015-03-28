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

package kieker.diagnosis.mainview.subview.monitoringstatistics;

import java.io.File;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import kieker.diagnosis.mainview.subview.ISubView;
import kieker.diagnosis.mainview.subview.util.NameConverter;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class MonitoringStatisticsView implements ISubView, Observer {

	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("kieker.diagnosis.mainview.subview.monitoringstatistics.monitoringstatisticsview"); //$NON-NLS-1$
	private static final String UNITS[] = { "Bytes", "Kilobytes", "Megabytes", "Gigabytes" };
	private static final String N_A = "N/A";

	private @Autowired MonitoringStatisticsViewController controller;
	private @Autowired MonitoringStatisticsViewModel model;

	private @Autowired PropertiesModel propertiesModel;
	private @Autowired DataModel dataModel;

	private Composite composite;
	private Label lblMonitoringLogDisplay;
	private Label lblMonitoringLogSizeDisplay;
	private Label lblAnalysisTimeDisplay;
	private Label lblBeginOfMonitoringDisplay;
	private Label lblEndOfMonitoringDisplay;
	private Label lblNumberOfCallsDisplay;
	private Label lblNumberOfFailedDisplay;
	private Label lblNumberOfAggregatedCallsDisplay;
	private Label lblNumberOfAggregatedFailedCallsDisplay;
	private Label lblNumberOfTracesDisplay;
	private Label lblNumberOfFailedTracesDisplay;
	private Label lblNumberOfFailureTracesDisplay;
	private Label lblNumberOfAggregatedTracesDisplay;
	private Label lblNumberOfAggregatedFailedTracesDisplay;
	private Label lblNumberOfAggregatedFailureTracesDisplay;
	private Label lblNonreconstructableTracesDisplay;

	@PostConstruct
	public void initialize() {
		this.dataModel.addObserver(this);
		this.propertiesModel.addObserver(this);
	}

	@Override
	public void update(final Observable observable, final Object o) {
		final long analysisDurationInMS = this.dataModel.getAnalysisDurationInMS();
		final TimeUnit targetTimeUnit = this.propertiesModel.getTimeUnit();
		final String shortTimeUnit = NameConverter.toShortTimeUnit(targetTimeUnit);
		final String analysisDuration = targetTimeUnit.convert(analysisDurationInMS, TimeUnit.MILLISECONDS) + " " + shortTimeUnit;

		final File importDirectory = this.dataModel.getImportDirectory();

		if (importDirectory != null) {
			float importDirectorySize = this.calculateDirectorySize(importDirectory);

			String importDirectorySizeString = N_A;
			for (final String unit : UNITS) {
				if (importDirectorySize >= 1024) {
					importDirectorySize /= 1024.0f;
				} else {
					importDirectorySizeString = String.format("%.1f %s", importDirectorySize, unit);
					break;
				}
			}

			this.lblMonitoringLogDisplay.setText(importDirectory.getAbsolutePath());
			this.lblMonitoringLogSizeDisplay.setText(importDirectorySizeString);
		}
		this.lblAnalysisTimeDisplay.setText(analysisDuration);
		// this.lblBeginOfMonitoringDisplay.setText(Integer.toString(this.dataModel.getOperationCalls(null).size()));
		// this.lblEndOfMonitoringDisplay.setText(Integer.toString(this.dataModel.getOperationCalls(null).size()));
		this.lblNumberOfCallsDisplay.setText(Integer.toString(this.dataModel.getOperationCalls(null).size()));
		this.lblNumberOfFailedDisplay.setText(Integer.toString(this.dataModel.getFailedOperationCalls(null).size()));
		this.lblNumberOfAggregatedCallsDisplay.setText(Integer.toString(this.dataModel.getAggregatedOperationCalls(null).size()));
		this.lblNumberOfAggregatedFailedCallsDisplay.setText(Integer.toString(this.dataModel.getAggregatedFailedOperationCalls(null).size()));
		this.lblNumberOfTracesDisplay.setText(Integer.toString(this.dataModel.getTraces(null).size()));
		this.lblNumberOfFailedTracesDisplay.setText(Integer.toString(this.dataModel.getFailedTraces(null).size()));
		this.lblNumberOfFailureTracesDisplay.setText(Integer.toString(this.dataModel.getFailureContainingTraces(null).size()));
		this.lblNumberOfAggregatedTracesDisplay.setText(Integer.toString(this.dataModel.getAggregatedTraces(null).size()));
		this.lblNumberOfAggregatedFailedTracesDisplay.setText(Integer.toString(this.dataModel.getFailedAggregatedTraces(null).size()));
		this.lblNumberOfAggregatedFailureTracesDisplay.setText(Integer.toString(this.dataModel.getFailureContainingAggregatedTraces(null).size()));
		// this.lblNonreconstructableTracesDisplay.setText(Integer.toString(this.dataModel.getOperationCalls(null).size()));

		this.composite.layout();
	}

	private long calculateDirectorySize(final File file) {
		if (file.isFile()) {
			return file.length();
		}

		long sum = 0;
		for (final File child : file.listFiles()) {
			sum += this.calculateDirectorySize(child);
		}
		return sum;
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void createComposite(final Composite parent) {
		if (this.composite != null) {
			this.composite.dispose();
		}

		this.composite = new Composite(parent, SWT.NONE);
		this.composite.setLayout(new GridLayout(2, false));

		final Label lblMonitoringLog = new Label(this.composite, SWT.NONE);
		lblMonitoringLog.setText(BUNDLE.getString("MonitoringStatisticsView.lblMonitoringLog.text") + ":"); //$NON-NLS-1$

		this.lblMonitoringLogDisplay = new Label(this.composite, SWT.NONE);
		this.lblMonitoringLogDisplay.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		this.lblMonitoringLogDisplay.setText(N_A);

		final Label lblLine1 = new Label(this.composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		lblLine1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));

		final Label lblMonitoringLogSize = new Label(this.composite, SWT.NONE);
		lblMonitoringLogSize.setText(BUNDLE.getString("MonitoringStatisticsView.lblMonitoringLogSize.text") + ":"); //$NON-NLS-1$

		this.lblMonitoringLogSizeDisplay = new Label(this.composite, SWT.NONE);
		this.lblMonitoringLogSizeDisplay.setText(N_A);

		final Label lblAnalysisTime = new Label(this.composite, SWT.NONE);
		lblAnalysisTime.setText(BUNDLE.getString("MonitoringStatisticsView.lblAnalysisTime.text") + ":"); //$NON-NLS-1$

		this.lblAnalysisTimeDisplay = new Label(this.composite, SWT.NONE);
		this.lblAnalysisTimeDisplay.setText(N_A);

		final Label lblLine2 = new Label(this.composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		lblLine2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));

		final Label lblBeginOfMonitoring = new Label(this.composite, SWT.NONE);
		lblBeginOfMonitoring.setText(BUNDLE.getString("MonitoringStatisticsView.lblBeginOfMonitoring.text") + ":"); //$NON-NLS-1$

		this.lblBeginOfMonitoringDisplay = new Label(this.composite, SWT.NONE);
		this.lblBeginOfMonitoringDisplay.setText(N_A);

		final Label lblEndOfMonitoring = new Label(this.composite, SWT.NONE);
		lblEndOfMonitoring.setText(BUNDLE.getString("MonitoringStatisticsView.lblEndOfMonitoring.text") + ":"); //$NON-NLS-1$

		this.lblEndOfMonitoringDisplay = new Label(this.composite, SWT.NONE);
		this.lblEndOfMonitoringDisplay.setText(N_A);

		final Label lblLine3 = new Label(this.composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		lblLine3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));

		final Label lblNumberOfCalls = new Label(this.composite, SWT.NONE);
		lblNumberOfCalls.setText(BUNDLE.getString("MonitoringStatisticsView.lblNumberOfCalls.text") + ":"); //$NON-NLS-1$

		this.lblNumberOfCallsDisplay = new Label(this.composite, SWT.NONE);
		this.lblNumberOfCallsDisplay.setText(N_A);

		final Label lblNumberOfFailed = new Label(this.composite, SWT.NONE);
		lblNumberOfFailed.setText(BUNDLE.getString("MonitoringStatisticsView.lblNumberOfFailed.text") + ":"); //$NON-NLS-1$

		this.lblNumberOfFailedDisplay = new Label(this.composite, SWT.NONE);
		this.lblNumberOfFailedDisplay.setText(N_A);

		final Label lblNumberOfAggregatedCalls = new Label(this.composite, SWT.NONE);
		lblNumberOfAggregatedCalls.setText(BUNDLE.getString("MonitoringStatisticsView.lblNumberOfAggregated.text") + ":"); //$NON-NLS-1$

		this.lblNumberOfAggregatedCallsDisplay = new Label(this.composite, SWT.NONE);
		this.lblNumberOfAggregatedCallsDisplay.setText(N_A);

		final Label lblNumberOfAggregatedFailedCalls = new Label(this.composite, SWT.NONE);
		lblNumberOfAggregatedFailedCalls.setText(BUNDLE.getString("MonitoringStatisticsView.lblNumberOfAggregated_1.text") + ":"); //$NON-NLS-1$

		this.lblNumberOfAggregatedFailedCallsDisplay = new Label(this.composite, SWT.NONE);
		this.lblNumberOfAggregatedFailedCallsDisplay.setText(N_A);

		final Label lblNumberOfTraces = new Label(this.composite, SWT.NONE);
		lblNumberOfTraces.setText(BUNDLE.getString("MonitoringStatisticsView.lblNumberOfReconstructed.text") + ":"); //$NON-NLS-1$

		this.lblNumberOfTracesDisplay = new Label(this.composite, SWT.NONE);
		this.lblNumberOfTracesDisplay.setText(N_A);

		final Label lblNumberOfFailedTraces = new Label(this.composite, SWT.NONE);
		lblNumberOfFailedTraces.setText(BUNDLE.getString("MonitoringStatisticsView.lblNumberOfReconstructed_2.text") + ":"); //$NON-NLS-1$

		this.lblNumberOfFailedTracesDisplay = new Label(this.composite, SWT.NONE);
		this.lblNumberOfFailedTracesDisplay.setText(N_A);

		final Label lblNumberOfFailureTraces = new Label(this.composite, SWT.NONE);
		lblNumberOfFailureTraces.setText(BUNDLE.getString("MonitoringStatisticsView.lblNumberOfReconstructed_3.text") + ":"); //$NON-NLS-1$

		this.lblNumberOfFailureTracesDisplay = new Label(this.composite, SWT.NONE);
		this.lblNumberOfFailureTracesDisplay.setText(N_A);

		final Label lblNumberOfAggregatedTraces = new Label(this.composite, SWT.NONE);
		lblNumberOfAggregatedTraces.setText(BUNDLE.getString("MonitoringStatisticsView.lblNumberOfReconstructed_1.text") + ":"); //$NON-NLS-1$

		this.lblNumberOfAggregatedTracesDisplay = new Label(this.composite, SWT.NONE);
		this.lblNumberOfAggregatedTracesDisplay.setText(N_A);

		final Label lblNumberOfAggregatedFailedTraces = new Label(this.composite, SWT.NONE);
		lblNumberOfAggregatedFailedTraces.setText(BUNDLE.getString("MonitoringStatisticsView.lblNumberOfReconstructed_4.text") + ":"); //$NON-NLS-1$

		this.lblNumberOfAggregatedFailedTracesDisplay = new Label(this.composite, SWT.NONE);
		this.lblNumberOfAggregatedFailedTracesDisplay.setText(N_A);

		final Label lblNumberOfAggregatedFailureTraces = new Label(this.composite, SWT.NONE);
		lblNumberOfAggregatedFailureTraces.setText(BUNDLE.getString("MonitoringStatisticsView.lblNumberOfReconstructed_5.text") + ":"); //$NON-NLS-1$

		this.lblNumberOfAggregatedFailureTracesDisplay = new Label(this.composite, SWT.NONE);
		this.lblNumberOfAggregatedFailureTracesDisplay.setText(N_A);

		final Label lblLine4 = new Label(this.composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		lblLine4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));

		final Label lblNonreconstructableTraces = new Label(this.composite, SWT.NONE);
		lblNonreconstructableTraces.setText(BUNDLE.getString("MonitoringStatisticsView.lblNonreconstructableTraces.text") + ":"); //$NON-NLS-1$

		this.lblNonreconstructableTracesDisplay = new Label(this.composite, SWT.NONE);
		this.lblNonreconstructableTracesDisplay.setText(N_A);
	}

	@Override
	public Composite getComposite() {
		return this.composite;
	}
}
