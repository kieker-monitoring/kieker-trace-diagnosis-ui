/***************************************************************************
 * Copyright 2014 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.subview.aggregatedcalls;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import kieker.diagnosis.common.domain.AggregatedOperationCall;
import kieker.diagnosis.common.domain.OperationCall;
import kieker.diagnosis.common.model.PropertiesModel;
import kieker.diagnosis.subview.ISubView;
import kieker.diagnosis.subview.util.IModel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.wb.swt.SWTResourceManager;

public final class View implements ISubView, Observer {

	private static final String N_A = "N/A";
	private Composite composite;
	private Composite detailComposite;
	private Label lblComponentDisplay;
	private Label lblOperationDisplay;
	private Label lblNumberOfCallsDisplay;
	private Label lblMinimalDurationDisplay;
	private Label lblAverageDurationDisplay;
	private Label lblMeanDurationDisplay;
	private Label lblMaximalDurationDisplay;
	private Label lblFailedDisplay;
	private Label lblExecutionContainerDisplay;
	private Label lblFailed;
	private Label lblTotalDurationDisplay;
	private Composite statusBar;
	private Label lblTraceEquivalence;
	private Table table;
	private final Model model;
	private final IModel<OperationCall> modelProxy;
	private final PropertiesModel propertiesModel;
	private final Controller controller;

	public View(final IModel<OperationCall> modelProxy, final Model model, final PropertiesModel propertiesModel, final Controller controller) {
		this.model = model;
		this.modelProxy = modelProxy;
		this.propertiesModel = propertiesModel;
		this.controller = controller;

		modelProxy.addObserver(this);
		model.addObserver(this);
		propertiesModel.addObserver(this);
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void createComposite(final Composite parent) { // NOPMD (This method violates some metrics)
		if (this.composite != null) {
			this.composite.dispose();
		}

		this.composite = new Composite(parent, SWT.NONE);
		final GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.verticalSpacing = 0;
		gl_composite.marginHeight = 0;
		gl_composite.marginWidth = 0;
		gl_composite.horizontalSpacing = 0;
		this.composite.setLayout(gl_composite);

		final SashForm sashForm = new SashForm(this.composite, SWT.VERTICAL);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		this.table = new Table(sashForm, SWT.BORDER | SWT.FULL_SELECTION);
		this.table.setHeaderVisible(true);
		this.table.setLinesVisible(true);

		final TableColumn tblclmnExecutionContainer = new TableColumn(this.table, SWT.NONE);
		tblclmnExecutionContainer.setWidth(100);
		tblclmnExecutionContainer.setText("Execution Container");

		final TableColumn tblclmnComponent = new TableColumn(this.table, SWT.NONE);
		tblclmnComponent.setWidth(100);
		tblclmnComponent.setText("Component");

		final TableColumn tblclmnOperation = new TableColumn(this.table, SWT.NONE);
		tblclmnOperation.setWidth(100);
		tblclmnOperation.setText("Operation");

		final TableColumn tblclmnNumberOfCalls = new TableColumn(this.table, SWT.NONE);
		tblclmnNumberOfCalls.setWidth(100);
		tblclmnNumberOfCalls.setText("Number of Calls");

		final TableColumn tblclmnMinimalDuration = new TableColumn(this.table, SWT.NONE);
		tblclmnMinimalDuration.setWidth(100);
		tblclmnMinimalDuration.setText("Minimal Duration");

		final TableColumn tblclmnMeanDuration = new TableColumn(this.table, SWT.NONE);
		tblclmnMeanDuration.setWidth(100);
		tblclmnMeanDuration.setText("Mean Duration");

		final TableColumn tblclmnMedianDuration = new TableColumn(this.table, SWT.NONE);
		tblclmnMedianDuration.setWidth(100);
		tblclmnMedianDuration.setText("Median Duration");

		final TableColumn tblclmnTotalDuration = new TableColumn(this.table, SWT.NONE);
		tblclmnTotalDuration.setWidth(100);
		tblclmnTotalDuration.setText("Total Duration");

		this.detailComposite = new Composite(sashForm, SWT.BORDER);
		this.detailComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.detailComposite.setLayout(new GridLayout(2, false));

		final Label lblExecutionContainer = new Label(this.detailComposite, SWT.NONE);
		lblExecutionContainer.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblExecutionContainer.setText("Execution Container:");

		this.lblExecutionContainerDisplay = new Label(this.detailComposite, SWT.NONE);
		this.lblExecutionContainerDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblExecutionContainerDisplay.setText(View.N_A);

		final Label lblComponent = new Label(this.detailComposite, SWT.NONE);
		lblComponent.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblComponent.setText("Component:");

		this.lblComponentDisplay = new Label(this.detailComposite, SWT.NONE);
		this.lblComponentDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblComponentDisplay.setText(View.N_A);

		final Label lblOperation = new Label(this.detailComposite, SWT.NONE);
		lblOperation.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblOperation.setText("Operation:");

		this.lblOperationDisplay = new Label(this.detailComposite, SWT.NONE);
		this.lblOperationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblOperationDisplay.setText(View.N_A);

		final Label lblNumberOfCalls = new Label(this.detailComposite, SWT.NONE);
		lblNumberOfCalls.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNumberOfCalls.setText("Number of Calls:");

		this.lblNumberOfCallsDisplay = new Label(this.detailComposite, SWT.NONE);
		this.lblNumberOfCallsDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblNumberOfCallsDisplay.setText(View.N_A);

		final Label lblMinimalDuration = new Label(this.detailComposite, SWT.NONE);
		lblMinimalDuration.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblMinimalDuration.setText("Minimal Duration:");

		this.lblMinimalDurationDisplay = new Label(this.detailComposite, SWT.NONE);
		this.lblMinimalDurationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblMinimalDurationDisplay.setText(View.N_A);

		final Label lblAverageDuration = new Label(this.detailComposite, SWT.NONE);
		lblAverageDuration.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblAverageDuration.setText("Mean Duration:");

		this.lblAverageDurationDisplay = new Label(this.detailComposite, SWT.NONE);
		this.lblAverageDurationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblAverageDurationDisplay.setText(View.N_A);

		final Label lblMeanDuration = new Label(this.detailComposite, SWT.NONE);
		lblMeanDuration.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblMeanDuration.setText("Median Duration:");

		this.lblMeanDurationDisplay = new Label(this.detailComposite, SWT.NONE);
		this.lblMeanDurationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblMeanDurationDisplay.setText(View.N_A);

		final Label lblMaximalDuration = new Label(this.detailComposite, SWT.NONE);
		lblMaximalDuration.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblMaximalDuration.setText("Maximal Duration:");

		this.lblMaximalDurationDisplay = new Label(this.detailComposite, SWT.NONE);
		this.lblMaximalDurationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblMaximalDurationDisplay.setText(View.N_A);

		final Label lblTotalDuration = new Label(this.detailComposite, SWT.NONE);
		lblTotalDuration.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblTotalDuration.setText("Total Duration:");

		this.lblTotalDurationDisplay = new Label(this.detailComposite, SWT.NONE);
		this.lblTotalDurationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblTotalDurationDisplay.setText(View.N_A);

		this.lblFailed = new Label(this.detailComposite, SWT.NONE);
		this.lblFailed.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblFailed.setText("Failed:");

		this.lblFailedDisplay = new Label(this.detailComposite, SWT.NONE);
		this.lblFailedDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblFailedDisplay.setText(View.N_A);
		sashForm.setWeights(new int[] { 2, 1 });

		this.statusBar = new Composite(this.composite, SWT.NONE);
		this.statusBar.setLayout(new GridLayout(1, false));

		this.lblTraceEquivalence = new Label(this.statusBar, SWT.NONE);
		this.lblTraceEquivalence.setText("0 Aggregated Operation Calls");

		this.table.addSelectionListener(this.controller);
	}

	@Override
	public Composite getComposite() {
		return this.composite;
	}

	@Override
	public void update(final Observable observable, final Object obj) {
		if (observable == this.modelProxy) {
			this.updateTable();
			this.updateStatusBar();
		}
		if (observable == this.model) {
			this.updateDetailComposite();
		}
		if (observable == this.propertiesModel) {
			this.clearTable();
		}
	}

	private void updateStatusBar() {
		this.lblTraceEquivalence.setText(this.modelProxy.getContent().size() + " Aggregated Operation Call(s)");
		this.statusBar.getParent().layout();
	}

	private void updateTable() {
		final List<OperationCall> calls = this.modelProxy.getContent();

		this.table.setData(calls);
		this.table.setItemCount(calls.size());
		this.clearTable();
	}

	private void clearTable() {
		this.table.clearAll();

		for (final TableColumn column : this.table.getColumns()) {
			column.pack();
		}
	}

	private void updateDetailComposite() {
		final AggregatedOperationCall call = this.model.getCurrentActiveCall();

		final String minDuration = (call.getMinDuration() + " " + this.modelProxy.getShortTimeUnit()).trim();
		final String maxDuration = (call.getMaxDuration() + " " + this.modelProxy.getShortTimeUnit()).trim();
		final String meanDuration = (call.getMeanDuration() + " " + this.modelProxy.getShortTimeUnit()).trim();
		final String avgDuration = (call.getAvgDuration() + " " + this.modelProxy.getShortTimeUnit()).trim();
		final String totalDuration = (call.getTotalDuration() + " " + this.modelProxy.getShortTimeUnit()).trim();

		this.lblMinimalDurationDisplay.setText(minDuration);
		this.lblMaximalDurationDisplay.setText(maxDuration);
		this.lblAverageDurationDisplay.setText(avgDuration);
		this.lblMeanDurationDisplay.setText(meanDuration);
		this.lblTotalDurationDisplay.setText(totalDuration);

		this.lblExecutionContainerDisplay.setText(call.getContainer());
		this.lblComponentDisplay.setText(call.getComponent());
		this.lblOperationDisplay.setText(call.getOperation());
		this.lblNumberOfCallsDisplay.setText(Integer.toString(call.getCalls()));

		if (call.isFailed()) {
			this.lblFailedDisplay.setText("Yes (" + call.getFailedCause() + ")");
			this.lblFailedDisplay.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
			this.lblFailed.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		} else {
			this.lblFailedDisplay.setText("No");
			this.lblFailedDisplay.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
			this.lblFailed.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
		}

		this.detailComposite.layout();
	}

}
