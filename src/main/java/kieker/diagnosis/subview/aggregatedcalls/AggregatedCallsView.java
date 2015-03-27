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

package kieker.diagnosis.subview.aggregatedcalls;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;

import kieker.diagnosis.domain.AggregatedOperationCall;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.model.PropertiesModel.ComponentNames;
import kieker.diagnosis.model.PropertiesModel.OperationNames;
import kieker.diagnosis.subview.ISubView;
import kieker.diagnosis.subview.aggregatedcalls.AggregatedCallsViewModel.Filter;
import kieker.diagnosis.subview.aggregatedcalls.util.AverageDurationSortListener;
import kieker.diagnosis.subview.aggregatedcalls.util.CallsSortListener;
import kieker.diagnosis.subview.aggregatedcalls.util.ComponentSortListener;
import kieker.diagnosis.subview.aggregatedcalls.util.ContainerSortListener;
import kieker.diagnosis.subview.aggregatedcalls.util.MaximalDurationSortListener;
import kieker.diagnosis.subview.aggregatedcalls.util.MedianDurationSortListener;
import kieker.diagnosis.subview.aggregatedcalls.util.MinimalDurationSortListener;
import kieker.diagnosis.subview.aggregatedcalls.util.OperationSortListener;
import kieker.diagnosis.subview.aggregatedcalls.util.TotalDurationSortListener;
import kieker.diagnosis.subview.util.NameConverter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class AggregatedCallsView implements ISubView, Observer {
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("kieker.diagnosis.subview.aggregatedcalls.aggregatedcallsview"); //$NON-NLS-1$

	private static final String N_A = "N/A";

	@Autowired
	private AggregatedCallsViewModel model;

	@Autowired
	private DataModel dataModel;

	@Autowired
	private PropertiesModel propertiesModel;

	@Autowired
	private AggregatedCallsViewController controller;

	private Composite composite;
	private Composite detailComposite;
	private Text lblComponentDisplay;
	private Text lblOperationDisplay;
	private Text lblNumberOfCallsDisplay;
	private Text lblMinimalDurationDisplay;
	private Text lblAverageDurationDisplay;
	private Text lblMeanDurationDisplay;
	private Text lblMaximalDurationDisplay;
	private Text lblFailedDisplay;
	private Text lblExecutionContainerDisplay;
	private Label lblFailed;
	private Text lblTotalDurationDisplay;
	private Composite statusBar;
	private Label lblCounter;
	private Table table;
	private Button btnShowAll;
	private Button btnShowJustFailed;
	private ScrolledComposite ivSc;

	@PostConstruct
	public void initialize() {
		this.dataModel.addObserver(this);
		this.propertiesModel.addObserver(this);
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

		final Composite filterComposite = new Composite(this.composite, SWT.NONE);
		final GridLayout gl_filterComposite = new GridLayout(2, false);
		gl_composite.verticalSpacing = 0;
		gl_composite.marginHeight = 0;
		gl_composite.marginWidth = 0;
		gl_composite.horizontalSpacing = 0;
		filterComposite.setLayout(gl_filterComposite);

		this.btnShowAll = new Button(filterComposite, SWT.RADIO);
		this.btnShowAll.setText(BUNDLE.getString("AggregatedCallsView.btnShowAll.text")); //$NON-NLS-1$ 
		this.btnShowAll.setSelection(true);
		this.btnShowJustFailed = new Button(filterComposite, SWT.RADIO);
		this.btnShowJustFailed.setText(BUNDLE.getString("AggregatedCallsView.btnShowJustFailed.text")); //$NON-NLS-1$ 

		final SashForm sashForm = new SashForm(this.composite, SWT.VERTICAL);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		this.table = new Table(sashForm, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		this.table.setHeaderVisible(true);
		this.table.setLinesVisible(true);

		final TableColumn tblclmnExecutionContainer = new TableColumn(this.table, SWT.NONE);
		tblclmnExecutionContainer.setWidth(100);
		tblclmnExecutionContainer.setText(BUNDLE.getString("AggregatedCallsView.tblclmnExecutionContainer.text")); //$NON-NLS-1$ 

		final TableColumn tblclmnComponent = new TableColumn(this.table, SWT.NONE);
		tblclmnComponent.setWidth(100);
		tblclmnComponent.setText(BUNDLE.getString("AggregatedCallsView.tblclmnComponent.text")); //$NON-NLS-1$ 

		final TableColumn tblclmnOperation = new TableColumn(this.table, SWT.NONE);
		tblclmnOperation.setWidth(100);
		tblclmnOperation.setText(BUNDLE.getString("AggregatedCallsView.tblclmnOperation.text")); //$NON-NLS-1$ 

		final TableColumn tblclmnNumberOfCalls = new TableColumn(this.table, SWT.NONE);
		tblclmnNumberOfCalls.setWidth(100);
		tblclmnNumberOfCalls.setText(BUNDLE.getString("AggregatedCallsView.tblclmnNumberOfCalls.text")); //$NON-NLS-1$ 

		final TableColumn tblclmnMinimalDuration = new TableColumn(this.table, SWT.NONE);
		tblclmnMinimalDuration.setWidth(100);
		tblclmnMinimalDuration.setText(BUNDLE.getString("AggregatedCallsView.tblclmnMinimalDuration.text")); //$NON-NLS-1$ 

		final TableColumn tblclmnMeanDuration = new TableColumn(this.table, SWT.NONE);
		tblclmnMeanDuration.setWidth(100);
		tblclmnMeanDuration.setText(BUNDLE.getString("AggregatedCallsView.tblclmnMeanDuration.text")); //$NON-NLS-1$ 

		final TableColumn tblclmnMedianDuration = new TableColumn(this.table, SWT.NONE);
		tblclmnMedianDuration.setWidth(100);
		tblclmnMedianDuration.setText(BUNDLE.getString("AggregatedCallsView.tblclmnMedianDuration.text")); //$NON-NLS-1$ 

		final TableColumn tblclmnMaximalDuration = new TableColumn(this.table, SWT.NONE);
		tblclmnMaximalDuration.setWidth(100);
		tblclmnMaximalDuration.setText(BUNDLE.getString("AggregatedCallsView.tblclmnMaximalDuration.text")); //$NON-NLS-1$ 

		final TableColumn tblclmnTotalDuration = new TableColumn(this.table, SWT.NONE);
		tblclmnTotalDuration.setWidth(100);
		tblclmnTotalDuration.setText(BUNDLE.getString("AggregatedCallsView.tblclmnTotalDuration.text")); //$NON-NLS-1$ 

		this.ivSc = new ScrolledComposite(sashForm, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

		this.detailComposite = new Composite(this.ivSc, SWT.NONE);
		this.detailComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.detailComposite.setLayout(new GridLayout(2, false));

		this.ivSc.setContent(this.detailComposite);
		this.ivSc.setExpandHorizontal(true);
		this.ivSc.setExpandVertical(true);

		final Label lblExecutionContainer = new Label(this.detailComposite, SWT.NONE);
		lblExecutionContainer.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblExecutionContainer.setText(BUNDLE.getString("AggregatedCallsView.lblExecutionContainer.text") + ":"); //$NON-NLS-1$ 

		this.lblExecutionContainerDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblExecutionContainerDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblExecutionContainerDisplay.setText(AggregatedCallsView.N_A);

		final Label lblComponent = new Label(this.detailComposite, SWT.NONE);
		lblComponent.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblComponent.setText(BUNDLE.getString("AggregatedCallsView.lblComponent.text") + ":"); //$NON-NLS-1$ 

		this.lblComponentDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblComponentDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblComponentDisplay.setText(AggregatedCallsView.N_A);

		final Label lblOperation = new Label(this.detailComposite, SWT.NONE);
		lblOperation.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblOperation.setText(BUNDLE.getString("AggregatedCallsView.lblOperation.text") + ":"); //$NON-NLS-1$ 

		this.lblOperationDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblOperationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblOperationDisplay.setText(AggregatedCallsView.N_A);

		final Label lblNumberOfCalls = new Label(this.detailComposite, SWT.NONE);
		lblNumberOfCalls.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNumberOfCalls.setText(BUNDLE.getString("AggregatedCallsView.lblNumberOfCalls.text") + ":"); //$NON-NLS-1$ 

		this.lblNumberOfCallsDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblNumberOfCallsDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblNumberOfCallsDisplay.setText(AggregatedCallsView.N_A);

		final Label lblMinimalDuration = new Label(this.detailComposite, SWT.NONE);
		lblMinimalDuration.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblMinimalDuration.setText(BUNDLE.getString("AggregatedCallsView.lblMinimalDuration.text") + ":"); //$NON-NLS-1$ 

		this.lblMinimalDurationDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblMinimalDurationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblMinimalDurationDisplay.setText(AggregatedCallsView.N_A);

		final Label lblAverageDuration = new Label(this.detailComposite, SWT.NONE);
		lblAverageDuration.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblAverageDuration.setText(BUNDLE.getString("AggregatedCallsView.lblAverageDuration.text") + ":"); //$NON-NLS-1$ 

		this.lblAverageDurationDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblAverageDurationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblAverageDurationDisplay.setText(AggregatedCallsView.N_A);

		final Label lblMeanDuration = new Label(this.detailComposite, SWT.NONE);
		lblMeanDuration.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblMeanDuration.setText(BUNDLE.getString("AggregatedCallsView.lblMeanDuration.text") + ":"); //$NON-NLS-1$ 

		this.lblMeanDurationDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblMeanDurationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblMeanDurationDisplay.setText(AggregatedCallsView.N_A);

		final Label lblMaximalDuration = new Label(this.detailComposite, SWT.NONE);
		lblMaximalDuration.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblMaximalDuration.setText(BUNDLE.getString("AggregatedCallsView.lblMaximalDuration.text")); //$NON-NLS-1$ 

		this.lblMaximalDurationDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblMaximalDurationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblMaximalDurationDisplay.setText(AggregatedCallsView.N_A);

		final Label lblTotalDuration = new Label(this.detailComposite, SWT.NONE);
		lblTotalDuration.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblTotalDuration.setText(BUNDLE.getString("AggregatedCallsView.lblTotalDuration.text") + ":"); //$NON-NLS-1$ 

		this.lblTotalDurationDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblTotalDurationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblTotalDurationDisplay.setText(AggregatedCallsView.N_A);

		this.lblFailed = new Label(this.detailComposite, SWT.NONE);
		this.lblFailed.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblFailed.setText(BUNDLE.getString("AggregatedCallsView.lblFailed.text") + ":"); //$NON-NLS-1$ 

		this.lblFailedDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblFailedDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblFailedDisplay.setText(AggregatedCallsView.N_A);
		sashForm.setWeights(new int[] { 2, 1 });

		this.statusBar = new Composite(this.composite, SWT.NONE);
		this.statusBar.setLayout(new GridLayout(1, false));

		this.lblCounter = new Label(this.statusBar, SWT.NONE);
		this.lblCounter.setText("0 " + BUNDLE.getString("AggregatedCallsView.lblCounter.text")); //$NON-NLS-1$ 

		this.table.addListener(SWT.SetData, new DataProvider());
		this.table.addSelectionListener(this.controller);

		tblclmnComponent.addSelectionListener(new ComponentSortListener());
		tblclmnExecutionContainer.addSelectionListener(new ContainerSortListener());
		tblclmnOperation.addSelectionListener(new OperationSortListener());
		tblclmnMinimalDuration.addSelectionListener(new MinimalDurationSortListener());
		tblclmnMaximalDuration.addSelectionListener(new MaximalDurationSortListener());
		tblclmnMedianDuration.addSelectionListener(new MedianDurationSortListener());
		tblclmnTotalDuration.addSelectionListener(new TotalDurationSortListener());
		tblclmnNumberOfCalls.addSelectionListener(new CallsSortListener());
		tblclmnMeanDuration.addSelectionListener(new AverageDurationSortListener());

		this.btnShowAll.addSelectionListener(this.controller);
		this.btnShowJustFailed.addSelectionListener(this.controller);
	}

	public Button getBtn1() {
		return this.btnShowAll;
	}

	public Button getBtn2() {
		return this.btnShowJustFailed;
	}

	@Override
	public Composite getComposite() {
		return this.composite;
	}

	@Override
	public void update(final Observable observable, final Object obj) {
		if (observable == this.dataModel) {
			this.updateTable();
			this.updateStatusBar();
		}
		if (observable == this.propertiesModel) {
			this.clearTable();
		}
	}

	public void notifyAboutChangedFilter() {
		this.updateTable();
		this.updateStatusBar();
		this.updateDetailComposite();
	}

	public void notifyAboutChangedOperationCall() {
		this.updateDetailComposite();
	}

	private void updateStatusBar() {
		if (this.model.getFilter() == Filter.NONE) {
			this.lblCounter.setText(this.dataModel.getAggregatedOperationCalls().size() + " " + BUNDLE.getString("AggregatedCallsView.lblCounter.text"));
		} else {
			this.lblCounter.setText(this.dataModel.getAggregatedFailedOperationCalls().size() + " " + BUNDLE.getString("AggregatedCallsView.lblCounter.text"));
		}
		this.statusBar.getParent().layout();
	}

	private void updateTable() {
		final List<AggregatedOperationCall> calls;

		if (this.model.getFilter() == Filter.NONE) {
			calls = this.dataModel.getAggregatedOperationCalls();
		} else {
			calls = this.dataModel.getAggregatedFailedOperationCalls();
		}

		this.table.setData(calls);
		this.table.setItemCount(calls.size());
		this.clearTable();
	}

	private void clearTable() {
		this.table.clearAll();
	}

	private void updateDetailComposite() {
		final AggregatedOperationCall call = this.model.getOperationCall();

		if (call != null) {
			final String shortTimeUnit = NameConverter.toShortTimeUnit(this.propertiesModel.getTimeUnit());

			final String minDuration = this.propertiesModel.getTimeUnit().convert(call.getMinDuration(), this.dataModel.getTimeUnit()) + " " + shortTimeUnit;
			final String maxDuration = this.propertiesModel.getTimeUnit().convert(call.getMaxDuration(), this.dataModel.getTimeUnit()) + " " + shortTimeUnit;
			final String meanDuration = this.propertiesModel.getTimeUnit().convert(call.getMedianDuration(), this.dataModel.getTimeUnit()) + " " + shortTimeUnit;
			final String avgDuration = this.propertiesModel.getTimeUnit().convert(call.getMeanDuration(), this.dataModel.getTimeUnit()) + " " + shortTimeUnit;
			final String totalDuration = this.propertiesModel.getTimeUnit().convert(call.getTotalDuration(), this.dataModel.getTimeUnit()) + " " + shortTimeUnit;

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
		}
		this.detailComposite.layout();
		this.ivSc.setMinSize(this.detailComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	private class DataProvider implements Listener {

		@Override
		@SuppressWarnings("unchecked")
		public void handleEvent(final Event event) {
			// Get the necessary information from the event
			final Table table = (Table) event.widget;
			final TableItem item = (TableItem) event.item;
			final int tableIndex = event.index;

			// Get the data for the current row
			final List<AggregatedOperationCall> calls = (List<AggregatedOperationCall>) table.getData();
			final AggregatedOperationCall call = calls.get(tableIndex);

			// Get the data to display
			String componentName = call.getComponent();
			if (AggregatedCallsView.this.propertiesModel.getComponentNames() == ComponentNames.SHORT) {
				componentName = NameConverter.toShortComponentName(componentName);
			}
			String operationString = call.getOperation();
			if (AggregatedCallsView.this.propertiesModel.getOperationNames() == OperationNames.SHORT) {
				operationString = NameConverter.toShortOperationName(operationString);
			}

			final String shortTimeUnit = NameConverter.toShortTimeUnit(AggregatedCallsView.this.propertiesModel.getTimeUnit());

			final String minDuration = AggregatedCallsView.this.propertiesModel.getTimeUnit().convert(call.getMinDuration(),
					AggregatedCallsView.this.dataModel.getTimeUnit())
					+ " " + shortTimeUnit;
			final String maxDuration = AggregatedCallsView.this.propertiesModel.getTimeUnit().convert(call.getMaxDuration(),
					AggregatedCallsView.this.dataModel.getTimeUnit())
					+ " " + shortTimeUnit;
			final String meanDuration = AggregatedCallsView.this.propertiesModel.getTimeUnit().convert(call.getMedianDuration(),
					AggregatedCallsView.this.dataModel.getTimeUnit())
					+ " " + shortTimeUnit;
			final String avgDuration = AggregatedCallsView.this.propertiesModel.getTimeUnit().convert(call.getMeanDuration(),
					AggregatedCallsView.this.dataModel.getTimeUnit())
					+ " " + shortTimeUnit;
			final String totalDuration = AggregatedCallsView.this.propertiesModel.getTimeUnit().convert(call.getTotalDuration(),
					AggregatedCallsView.this.dataModel.getTimeUnit())
					+ " " + shortTimeUnit;

			item.setText(new String[] { call.getContainer(), componentName, operationString, Long.toString(call.getCalls()), minDuration, avgDuration, meanDuration, maxDuration,
				totalDuration });

			if (call.isFailed()) {
				final Color colorRed = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
				item.setForeground(colorRed);
			}

			item.setData(call);
		}

	}

}
