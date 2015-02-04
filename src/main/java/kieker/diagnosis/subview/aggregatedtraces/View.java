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

package kieker.diagnosis.subview.aggregatedtraces;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import kieker.diagnosis.common.domain.AggregatedOperationCall;
import kieker.diagnosis.common.domain.AggregatedTrace;
import kieker.diagnosis.common.model.PropertiesModel;
import kieker.diagnosis.common.model.PropertiesModel.ComponentNames;
import kieker.diagnosis.common.model.PropertiesModel.OperationNames;
import kieker.diagnosis.subview.ISubView;
import kieker.diagnosis.subview.aggregatedtraces.util.AvgDurationSortListener;
import kieker.diagnosis.subview.aggregatedtraces.util.CallsSortListener;
import kieker.diagnosis.subview.aggregatedtraces.util.MaxDurationSortListener;
import kieker.diagnosis.subview.aggregatedtraces.util.MeanDurationSortListener;
import kieker.diagnosis.subview.aggregatedtraces.util.MinDurationSortListener;
import kieker.diagnosis.subview.aggregatedtraces.util.TotalDurationSortListener;
import kieker.diagnosis.subview.util.ComponentSortListener;
import kieker.diagnosis.subview.util.ContainerSortListener;
import kieker.diagnosis.subview.util.IModel;
import kieker.diagnosis.subview.util.NameConverter;
import kieker.diagnosis.subview.util.OperationSortListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;

public final class View implements Observer, ISubView {

	private static final String N_A = "N/A";
	private final Model aggregatedTracesSubViewModel;
	private final SelectionListener controller;
	private final IModel<AggregatedTrace> model;
	private Composite composite;
	private Tree tree;
	private Composite detailComposite;
	private Label lblComponentDisplay;
	private Label lblOperationDisplay;
	private Label lblNumberOfCallsDisplay;
	private Label lblMinimalDurationDisplay;
	private Label lblAverageDurationDisplay;
	private Label lblMeanDurationDisplay;
	private Label lblMaximalDurationDisplay;
	private Label lblFailedDisplay;
	private Label lblTraceDepthDisplay;
	private Label lblTraceSizeDisplay;
	private Label lblExecutionContainerDisplay;
	private Label lblFailed;
	private final PropertiesModel propertiesModel;
	private Label lblTotalDurationDisplay;
	private Composite statusBar;
	private Label lblTraceEquivalence;

	public View(final IModel<AggregatedTrace> model, final Model aggregatedTracesSubViewModel, final PropertiesModel propertiesModel, final SelectionListener controller) {
		this.controller = controller;
		this.model = model;
		this.propertiesModel = propertiesModel;
		this.aggregatedTracesSubViewModel = aggregatedTracesSubViewModel;

		model.addObserver(this);
		aggregatedTracesSubViewModel.addObserver(this);
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

		final TreeColumn trclmnCalls = new TreeColumn(this.tree, SWT.RIGHT);
		trclmnCalls.setWidth(100);
		trclmnCalls.setText("Number of Calls");

		final TreeColumn trclmnMinimalDuration = new TreeColumn(this.tree, SWT.RIGHT);
		trclmnMinimalDuration.setWidth(100);
		trclmnMinimalDuration.setText("Minimal Duration");

		final TreeColumn trclmnAverageDuration = new TreeColumn(this.tree, SWT.RIGHT);
		trclmnAverageDuration.setWidth(100);
		trclmnAverageDuration.setText("Average Duration");

		final TreeColumn trclmnMeanDuration = new TreeColumn(this.tree, SWT.RIGHT);
		trclmnMeanDuration.setWidth(100);
		trclmnMeanDuration.setText("Mean Duration");

		final TreeColumn trclmnMaximalDuration = new TreeColumn(this.tree, SWT.RIGHT);
		trclmnMaximalDuration.setWidth(100);
		trclmnMaximalDuration.setText("Maximal Duration");

		final TreeColumn trclmnTotalDuration = new TreeColumn(this.tree, SWT.RIGHT);
		trclmnTotalDuration.setWidth(100);
		trclmnTotalDuration.setText("Total Duration");

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
		lblAverageDuration.setText("Average Duration:");

		this.lblAverageDurationDisplay = new Label(this.detailComposite, SWT.NONE);
		this.lblAverageDurationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblAverageDurationDisplay.setText(View.N_A);

		final Label lblMeanDuration = new Label(this.detailComposite, SWT.NONE);
		lblMeanDuration.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblMeanDuration.setText("Mean Duration:");

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

		final Label lblTraceDepth = new Label(this.detailComposite, SWT.NONE);
		lblTraceDepth.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblTraceDepth.setText("Trace Depth:");

		this.lblTraceDepthDisplay = new Label(this.detailComposite, SWT.NONE);
		this.lblTraceDepthDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblTraceDepthDisplay.setText(View.N_A);

		final Label lblTraceSize = new Label(this.detailComposite, SWT.NONE);
		lblTraceSize.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblTraceSize.setText("Trace Size:");

		this.lblTraceSizeDisplay = new Label(this.detailComposite, SWT.NONE);
		this.lblTraceSizeDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblTraceSizeDisplay.setText(View.N_A);
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
	}

	@Override
	public Composite getComposite() {
		return this.composite;
	}

	@Override
	public void update(final Observable observable, final Object obj) {
		if (observable == this.model) {
			this.updateTree();
			this.updateStatusBar();
		}
		if (observable == this.aggregatedTracesSubViewModel) {
			this.updateDetailComposite();
		}
		if (observable == this.propertiesModel) {
			this.clearTree();
		}
	}

	private void updateStatusBar() {
		this.lblTraceEquivalence.setText(this.model.getContent().size() + " Trace Equivalence Class(es)");
		this.statusBar.getParent().layout();
	}

	private void updateTree() {
		final List<AggregatedTrace> traces = this.model.getContent();

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
		final AggregatedOperationCall call = this.aggregatedTracesSubViewModel.getCurrentActiveCall();

		final String minDuration = (call.getMinDuration() + " " + this.model.getShortTimeUnit()).trim();
		final String maxDuration = (call.getMaxDuration() + " " + this.model.getShortTimeUnit()).trim();
		final String meanDuration = (call.getMeanDuration() + " " + this.model.getShortTimeUnit()).trim();
		final String avgDuration = (call.getAvgDuration() + " " + this.model.getShortTimeUnit()).trim();
		final String totalDuration = (call.getTotalDuration() + " " + this.model.getShortTimeUnit()).trim();

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

		this.detailComposite.layout();
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
			final AggregatedOperationCall operationCall;

			if (parent == null) {
				final AggregatedTrace trace = ((List<AggregatedTrace>) tree.getData()).get(tableIndex);

				operationCall = trace.getRootOperationCall();
			} else {
				operationCall = ((AggregatedOperationCall) parent.getData()).getChildren().get(tableIndex);
			}

			String componentName = operationCall.getComponent();
			if (View.this.propertiesModel.getComponentNames() == ComponentNames.SHORT) {
				componentName = NameConverter.toShortComponentName(componentName);
			}
			String operationString = operationCall.getOperation();
			if (View.this.propertiesModel.getOperationNames() == OperationNames.SHORT) {
				operationString = NameConverter.toShortOperationName(operationString);
			}

			final String minDuration = (operationCall.getMinDuration() + " " + View.this.model.getShortTimeUnit()).trim();
			final String maxDuration = (operationCall.getMaxDuration() + " " + View.this.model.getShortTimeUnit()).trim();
			final String avgDuration = (operationCall.getAvgDuration() + " " + View.this.model.getShortTimeUnit()).trim();
			final String meanDuration = (operationCall.getMeanDuration() + " " + View.this.model.getShortTimeUnit()).trim();
			final String totalDuration = (operationCall.getTotalDuration() + " " + View.this.model.getShortTimeUnit()).trim();

			if (parent != null) {
				item.setText(new String[] { operationCall.getContainer(), componentName, operationString, "", minDuration, avgDuration, meanDuration, maxDuration, totalDuration, });
			} else {
				item.setText(new String[] { operationCall.getContainer(), componentName, operationString, Integer.toString(operationCall.getCalls()), minDuration, avgDuration,
					meanDuration, maxDuration, totalDuration, });
			}

			if (operationCall.isFailed()) {
				final Color colorRed = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
				item.setForeground(colorRed);
			}

			item.setData(operationCall);
			item.setItemCount(operationCall.getChildren().size());
		}
	}

}
