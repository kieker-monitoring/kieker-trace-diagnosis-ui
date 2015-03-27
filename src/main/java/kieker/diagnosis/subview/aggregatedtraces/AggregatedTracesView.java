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

package kieker.diagnosis.subview.aggregatedtraces;

import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.annotation.PostConstruct;

import kieker.diagnosis.domain.AggregatedOperationCall;
import kieker.diagnosis.domain.AggregatedTrace;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.model.PropertiesModel.ComponentNames;
import kieker.diagnosis.model.PropertiesModel.OperationNames;
import kieker.diagnosis.subview.ISubView;
import kieker.diagnosis.subview.aggregatedtraces.util.AvgDurationSortListener;
import kieker.diagnosis.subview.aggregatedtraces.util.CallsSortListener;
import kieker.diagnosis.subview.aggregatedtraces.util.MaxDurationSortListener;
import kieker.diagnosis.subview.aggregatedtraces.util.MeanDurationSortListener;
import kieker.diagnosis.subview.aggregatedtraces.util.MinDurationSortListener;
import kieker.diagnosis.subview.aggregatedtraces.util.TotalDurationSortListener;
import kieker.diagnosis.subview.util.ComponentSortListener;
import kieker.diagnosis.subview.util.ContainerSortListener;
import kieker.diagnosis.subview.util.NameConverter;
import kieker.diagnosis.subview.util.OperationSortListener;
import kieker.diagnosis.subview.util.TraceDepthSortListener;
import kieker.diagnosis.subview.util.TraceSizeSortListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class AggregatedTracesView implements Observer, ISubView {

	private static final String N_A = "N/A";

	@Autowired
	private AggregatedTracesViewModel model;

	@Autowired
	private AggregatedTracesViewController controller;

	@Autowired
	private DataModel dataModel;

	@Autowired
	private PropertiesModel propertiesModel;

	private Composite composite;
	private Tree tree;
	private Composite detailComposite;
	private Text lblComponentDisplay;
	private Text lblOperationDisplay;
	private Text lblNumberOfCallsDisplay;
	private Text lblMinimalDurationDisplay;
	private Text lblAverageDurationDisplay;
	private Text lblMeanDurationDisplay;
	private Text lblMaximalDurationDisplay;
	private Text lblFailedDisplay;
	private Text lblTraceDepthDisplay;
	private Text lblTraceSizeDisplay;
	private Text lblExecutionContainerDisplay;
	private Label lblFailed;
	private Text lblTotalDurationDisplay;
	private Composite statusBar;
	private Label lblTraceEquivalence;

	private Composite filterComposite;

	private Button ivBtn1;

	private Button ivBtn2;

	private Button ivBtn3;

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

		this.filterComposite = new Composite(composite, SWT.NONE);
		final GridLayout gl_filterComposite = new GridLayout(3, false);
		gl_composite.verticalSpacing = 0;
		gl_composite.marginHeight = 0;
		gl_composite.marginWidth = 0;
		gl_composite.horizontalSpacing = 0;
		this.filterComposite.setLayout(gl_filterComposite);

		ivBtn1 = new Button(filterComposite, SWT.RADIO);
		ivBtn1.setText("Show All Traces");
		ivBtn1.setSelection(true);
		ivBtn2 = new Button(filterComposite, SWT.RADIO);
		ivBtn2.setText("Show Only Failed Traces");
		ivBtn3 = new Button(filterComposite, SWT.RADIO);
		ivBtn3.setText("Show Only Traces Containing Failures");

		final SashForm sashForm = new SashForm(this.composite, SWT.VERTICAL);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		this.tree = new Tree(sashForm, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		this.tree.setHeaderVisible(true);

		final TreeColumn trclmnExecutionContainer = new TreeColumn(this.tree, SWT.NONE);
		trclmnExecutionContainer.setWidth(100);
		trclmnExecutionContainer.setText("Execution Container");

		final TreeColumn trclmnComponent = new TreeColumn(this.tree, SWT.NONE);
		trclmnComponent.setWidth(100);
		trclmnComponent.setText("Component");

		final TreeColumn trclmnOperation = new TreeColumn(this.tree, SWT.NONE);
		trclmnOperation.setWidth(100);
		trclmnOperation.setText("Operation");

		final TreeColumn trclmnTraceDepth = new TreeColumn(this.tree, SWT.RIGHT);
		trclmnTraceDepth.setWidth(100);
		trclmnTraceDepth.setText("Trace Depth");

		final TreeColumn trclmnTraceSize = new TreeColumn(this.tree, SWT.RIGHT);
		trclmnTraceSize.setWidth(100);
		trclmnTraceSize.setText("Trace Size");

		final TreeColumn trclmnCalls = new TreeColumn(this.tree, SWT.RIGHT);
		trclmnCalls.setWidth(100);
		trclmnCalls.setText("Number of Calls");

		final TreeColumn trclmnMinimalDuration = new TreeColumn(this.tree, SWT.RIGHT);
		trclmnMinimalDuration.setWidth(100);
		trclmnMinimalDuration.setText("Minimal Duration");

		final TreeColumn trclmnAverageDuration = new TreeColumn(this.tree, SWT.RIGHT);
		trclmnAverageDuration.setWidth(100);
		trclmnAverageDuration.setText("Mean Duration");

		final TreeColumn trclmnMeanDuration = new TreeColumn(this.tree, SWT.RIGHT);
		trclmnMeanDuration.setWidth(100);
		trclmnMeanDuration.setText("Median Duration");

		final TreeColumn trclmnMaximalDuration = new TreeColumn(this.tree, SWT.RIGHT);
		trclmnMaximalDuration.setWidth(100);
		trclmnMaximalDuration.setText("Maximal Duration");

		final TreeColumn trclmnTotalDuration = new TreeColumn(this.tree, SWT.RIGHT);
		trclmnTotalDuration.setWidth(100);
		trclmnTotalDuration.setText("Total Duration");

		ivSc = new ScrolledComposite(sashForm, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

		this.detailComposite = new Composite(ivSc, SWT.NONE);
		this.detailComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.detailComposite.setLayout(new GridLayout(2, false));

		ivSc.setContent(detailComposite);
		ivSc.setExpandHorizontal(true);
		ivSc.setExpandVertical(true); 

		final Label lblExecutionContainer = new Label(this.detailComposite, SWT.NONE);
		lblExecutionContainer.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblExecutionContainer.setText("Execution Container:");

		this.lblExecutionContainerDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblExecutionContainerDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblExecutionContainerDisplay.setText(AggregatedTracesView.N_A);

		final Label lblComponent = new Label(this.detailComposite, SWT.NONE);
		lblComponent.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblComponent.setText("Component:");

		this.lblComponentDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblComponentDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblComponentDisplay.setText(AggregatedTracesView.N_A);

		final Label lblOperation = new Label(this.detailComposite, SWT.NONE);
		lblOperation.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblOperation.setText("Operation:");

		this.lblOperationDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblOperationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblOperationDisplay.setText(AggregatedTracesView.N_A);

		final Label lblNumberOfCalls = new Label(this.detailComposite, SWT.NONE);
		lblNumberOfCalls.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNumberOfCalls.setText("Number of Calls:");

		this.lblNumberOfCallsDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblNumberOfCallsDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblNumberOfCallsDisplay.setText(AggregatedTracesView.N_A);

		final Label lblMinimalDuration = new Label(this.detailComposite, SWT.NONE);
		lblMinimalDuration.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblMinimalDuration.setText("Minimal Duration:");

		this.lblMinimalDurationDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblMinimalDurationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblMinimalDurationDisplay.setText(AggregatedTracesView.N_A);

		final Label lblAverageDuration = new Label(this.detailComposite, SWT.NONE);
		lblAverageDuration.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblAverageDuration.setText("Mean Duration:");

		this.lblAverageDurationDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblAverageDurationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblAverageDurationDisplay.setText(AggregatedTracesView.N_A);

		final Label lblMeanDuration = new Label(this.detailComposite, SWT.NONE);
		lblMeanDuration.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblMeanDuration.setText("Median Duration:");

		this.lblMeanDurationDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblMeanDurationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblMeanDurationDisplay.setText(AggregatedTracesView.N_A);

		final Label lblMaximalDuration = new Label(this.detailComposite, SWT.NONE);
		lblMaximalDuration.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblMaximalDuration.setText("Maximal Duration:");

		this.lblMaximalDurationDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblMaximalDurationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblMaximalDurationDisplay.setText(AggregatedTracesView.N_A);

		final Label lblTotalDuration = new Label(this.detailComposite, SWT.NONE);
		lblTotalDuration.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblTotalDuration.setText("Total Duration:");

		this.lblTotalDurationDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblTotalDurationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblTotalDurationDisplay.setText(AggregatedTracesView.N_A);

		this.lblFailed = new Label(this.detailComposite, SWT.NONE);
		this.lblFailed.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblFailed.setText("Failed:");

		this.lblFailedDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblFailedDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblFailedDisplay.setText(AggregatedTracesView.N_A);

		final Label lblTraceDepth = new Label(this.detailComposite, SWT.NONE);
		lblTraceDepth.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblTraceDepth.setText("Trace Depth:");

		this.lblTraceDepthDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblTraceDepthDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblTraceDepthDisplay.setText(AggregatedTracesView.N_A);

		final Label lblTraceSize = new Label(this.detailComposite, SWT.NONE);
		lblTraceSize.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblTraceSize.setText("Trace Size:");

		this.lblTraceSizeDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblTraceSizeDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblTraceSizeDisplay.setText(AggregatedTracesView.N_A);
		sashForm.setWeights(new int[] { 2, 1 });

		this.statusBar = new Composite(this.composite, SWT.NONE);
		this.statusBar.setLayout(new GridLayout(1, false));

		this.lblTraceEquivalence = new Label(this.statusBar, SWT.NONE);
		this.lblTraceEquivalence.setText("0 Trace Equivalence Classes");

		this.tree.addSelectionListener(this.controller);
		this.tree.addListener(SWT.SetData, new DataProvider());

		trclmnExecutionContainer.addSelectionListener(new ContainerSortListener());
		trclmnComponent.addSelectionListener(new ComponentSortListener());
		trclmnOperation.addSelectionListener(new OperationSortListener());
		trclmnMinimalDuration.addSelectionListener(new MinDurationSortListener());
		trclmnMaximalDuration.addSelectionListener(new MaxDurationSortListener());
		trclmnAverageDuration.addSelectionListener(new AvgDurationSortListener());
		trclmnMeanDuration.addSelectionListener(new MeanDurationSortListener());
		trclmnCalls.addSelectionListener(new CallsSortListener());
		trclmnTotalDuration.addSelectionListener(new TotalDurationSortListener());
		trclmnTraceDepth.addSelectionListener(new TraceDepthSortListener());
		trclmnTraceSize.addSelectionListener(new TraceSizeSortListener());

		ivBtn1.addSelectionListener(controller);
		ivBtn2.addSelectionListener(controller);
		ivBtn3.addSelectionListener(controller);
	}

	public Button getBtn1() {
		return ivBtn1;
	}

	public Button getBtn2() {
		return ivBtn2;
	}

	public Button getBtn3() {
		return ivBtn3;
	}

	@Override
	public Composite getComposite() {
		return this.composite;
	}

	@Override
	public void update(final Observable observable, final Object obj) {
		if (observable == this.dataModel) {
			this.updateTree();
			this.updateStatusBar();
		}
		if (observable == this.propertiesModel) {
			this.clearTree();
		}
	}

	public void notifyAboutChangedFilter() {
		this.updateTree();
		this.updateStatusBar();
		this.updateDetailComposite();
	}

	public void notifyAboutChangedOperationCall() {
		this.updateDetailComposite();
	}

	private void updateStatusBar() {
		switch (this.model.getFilter()) {
		case JUST_FAILED:
			this.lblTraceEquivalence.setText(this.dataModel.getFailedTracesCopy().size() + " Failed Trace Equivalence Class(es)");
			break;
		case JUST_FAILURE_CONTAINING:
			this.lblTraceEquivalence.setText(this.dataModel.getFailureContainingTracesCopy().size() + " Failure Containing Trace Equivalence Class(es)");
			break;
		case NONE:
			this.lblTraceEquivalence.setText(this.dataModel.getTracesCopy().size() + " Trace Equivalence Class(es)");
			break;
		}

		this.statusBar.getParent().layout();
	}

	private void updateTree() {
		final List<AggregatedTrace> traces;

		switch (this.model.getFilter()) {
		case JUST_FAILED:
			traces = this.dataModel.getFailedAggregatedTracesCopy();
			break;
		case JUST_FAILURE_CONTAINING:
			traces = this.dataModel.getFailureContainingAggregatedTracesCopy();
			break;
		case NONE:
			traces = this.dataModel.getAggregatedTracesCopy();
			break;
		default:
			traces = Collections.emptyList();
			break;
		}

		this.tree.setData(traces);
		this.tree.setItemCount(traces.size());

		this.clearTree();
	}

	private void clearTree() {
		this.tree.clearAll(true);

		for (final TreeColumn column : this.tree.getColumns()) {
			column.pack();
		}
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
			this.lblTraceDepthDisplay.setText(Integer.toString(call.getStackDepth()));
			this.lblTraceSizeDisplay.setText(Integer.toString(call.getStackSize()));
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
		ivSc.setMinSize(detailComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	private class DataProvider implements Listener {

		@Override
		@SuppressWarnings("unchecked")
		public void handleEvent(final Event event) {
			// Get the necessary information from the event
			final Tree tree = (Tree) event.widget;
			final TreeItem item = (TreeItem) event.item;
			final int tableIndex = event.index;
			final TreeItem parent = item.getParentItem();

			// Decide whether the current item is a root or not
			final AggregatedOperationCall call;

			if (parent == null) {
				final AggregatedTrace trace = ((List<AggregatedTrace>) tree.getData()).get(tableIndex);

				call = trace.getRootOperationCall();
			} else {
				call = ((AggregatedOperationCall) parent.getData()).getChildren().get(tableIndex);
			}

			String componentName = call.getComponent();
			if (AggregatedTracesView.this.propertiesModel.getComponentNames() == ComponentNames.SHORT) {
				componentName = NameConverter.toShortComponentName(componentName);
			}
			String operationString = call.getOperation();
			if (AggregatedTracesView.this.propertiesModel.getOperationNames() == OperationNames.SHORT) {
				operationString = NameConverter.toShortOperationName(operationString);
			}

			final String shortTimeUnit = NameConverter.toShortTimeUnit(AggregatedTracesView.this.propertiesModel.getTimeUnit());

			final String minDuration = AggregatedTracesView.this.propertiesModel.getTimeUnit().convert(call.getMinDuration(), AggregatedTracesView.this.dataModel.getTimeUnit())
					+ " " + shortTimeUnit;
			final String maxDuration = AggregatedTracesView.this.propertiesModel.getTimeUnit().convert(call.getMaxDuration(), AggregatedTracesView.this.dataModel.getTimeUnit())
					+ " " + shortTimeUnit;
			final String meanDuration = AggregatedTracesView.this.propertiesModel.getTimeUnit()
					.convert(call.getMedianDuration(), AggregatedTracesView.this.dataModel.getTimeUnit()) + " " + shortTimeUnit;
			final String avgDuration = AggregatedTracesView.this.propertiesModel.getTimeUnit().convert(call.getMeanDuration(), AggregatedTracesView.this.dataModel.getTimeUnit())
					+ " " + shortTimeUnit;
			final String totalDuration = AggregatedTracesView.this.propertiesModel.getTimeUnit()
					.convert(call.getTotalDuration(), AggregatedTracesView.this.dataModel.getTimeUnit()) + " " + shortTimeUnit;

			if (parent != null) {
				item.setText(new String[] { call.getContainer(), componentName, operationString, "", Integer.toString(call.getStackDepth()), Integer.toString(call.getStackSize()),
					minDuration, avgDuration, meanDuration, maxDuration, totalDuration, });
			} else {
				item.setText(new String[] { call.getContainer(), componentName, operationString, Integer.toString(call.getStackDepth()), Integer.toString(call.getStackSize()),
					Integer.toString(call.getCalls()), minDuration, avgDuration, meanDuration, maxDuration, totalDuration, });
			}

			if (call.isFailed()) {
				final Color colorRed = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
				item.setForeground(colorRed);
			}

			item.setData(call);
			item.setItemCount(call.getChildren().size());
		}
	}

}
