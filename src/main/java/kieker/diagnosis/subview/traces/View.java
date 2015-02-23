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

package kieker.diagnosis.subview.traces; // NOPMD (to many imports)

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.domain.Trace;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.model.PropertiesModel.ComponentNames;
import kieker.diagnosis.model.PropertiesModel.OperationNames;
import kieker.diagnosis.subview.ISubView;
import kieker.diagnosis.subview.traces.util.DurationSortListener;
import kieker.diagnosis.subview.traces.util.TraceIDSortListener;
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
	private final IModel<Trace> model;
	private final Model tracesSubViewModel;
	private final SelectionListener controller;
	private Composite composite;
	private Tree tree;
	private Composite detailComposite;
	private Label lblTraceIdDisplay;
	private Label lblDurationDisplay;
	private Label lblFailedDisplay;
	private Label lblTraceDepthDisplay;
	private Label lblTraceSizeDisplay;
	private Label lblOperationDisplay;
	private Label lblComponentDisplay;
	private Label lblExecutionContainerDisplay;
	private Label lblFailed;
	private final PropertiesModel propertiesModel;
	private Label lblTraces;
	private Composite statusBar;

	public View(final IModel<Trace> model, final Model tracesSubViewModel, final PropertiesModel propertiesModel, final SelectionListener controller) {
		this.model = model;
		this.propertiesModel = propertiesModel;
		this.tracesSubViewModel = tracesSubViewModel;
		this.controller = controller;

		model.addObserver(this);
		tracesSubViewModel.addObserver(this);
		propertiesModel.addObserver(this);
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

		final TreeColumn trclmnDuration = new TreeColumn(this.tree, SWT.RIGHT);
		trclmnDuration.setWidth(100);
		trclmnDuration.setText("Duration");

		final TreeColumn trclmnPercent = new TreeColumn(this.tree, SWT.RIGHT);
		trclmnPercent.setWidth(100);
		trclmnPercent.setText("Percent");

		final TreeColumn trclmnTraceId = new TreeColumn(this.tree, SWT.NONE);
		trclmnTraceId.setWidth(100);
		trclmnTraceId.setText("Trace ID");

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

		final Label lblDuration = new Label(this.detailComposite, SWT.NONE);
		lblDuration.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblDuration.setText("Duration:");

		this.lblDurationDisplay = new Label(this.detailComposite, SWT.NONE);
		this.lblDurationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblDurationDisplay.setText(View.N_A);

		final Label lblTraceId = new Label(this.detailComposite, SWT.NONE);
		lblTraceId.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblTraceId.setBounds(0, 0, 55, 15);
		lblTraceId.setText("Trace ID:");

		this.lblTraceIdDisplay = new Label(this.detailComposite, SWT.NONE);
		this.lblTraceIdDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblTraceIdDisplay.setText(View.N_A);

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

		this.lblTraces = new Label(this.statusBar, SWT.NONE);
		this.lblTraces.setText("0 Traces");

		this.tree.addSelectionListener(this.controller);
		this.tree.addListener(SWT.SetData, new DataProvider());

		trclmnExecutionContainer.addSelectionListener(new ContainerSortListener());
		trclmnComponent.addSelectionListener(new ComponentSortListener());
		trclmnOperation.addSelectionListener(new OperationSortListener());
		trclmnDuration.addSelectionListener(new DurationSortListener());
		trclmnTraceId.addSelectionListener(new TraceIDSortListener());
	}

	public Tree getTree() {
		return this.tree;
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
		if (observable == this.tracesSubViewModel) {
			this.updateDetailComposite();
		}
		if (observable == this.propertiesModel) {
			this.clearTree();
		}
	}

	private void updateStatusBar() {
		this.lblTraces.setText(this.model.getContent().size() + " Trace(s)");
		this.statusBar.getParent().layout();
	}

	private void updateTree() {
		final List<Trace> records = this.model.getContent();

		this.tree.setData(records);
		this.tree.setItemCount(records.size());

		this.clearTree();
	}

	private void clearTree() {
		this.tree.clearAll(true);

		for (final TreeColumn column : this.tree.getColumns()) {
			column.pack();
		}
	}

	private void updateDetailComposite() {
		final OperationCall operationCall = this.tracesSubViewModel.getCurrentActiveCall();

		final String duration = (Long.toString(operationCall.getDuration()) + " " + this.model.getShortTimeUnit()).trim();

		this.lblTraceIdDisplay.setText(Long.toString(operationCall.getTraceID()));
		this.lblDurationDisplay.setText(duration);

		this.lblExecutionContainerDisplay.setText(operationCall.getContainer());
		this.lblComponentDisplay.setText(operationCall.getComponent());
		this.lblOperationDisplay.setText(operationCall.getOperation());
		this.lblTraceDepthDisplay.setText(Integer.toString(operationCall.getStackDepth()));
		this.lblTraceSizeDisplay.setText(Integer.toString(operationCall.getStackSize()));

		if (operationCall.isFailed()) {
			this.lblFailedDisplay.setText("Yes (" + operationCall.getFailedCause() + ")");
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
			final OperationCall operationCall;
			final String traceID;

			if (parent == null) {
				final Trace trace = ((List<Trace>) tree.getData()).get(tableIndex);

				operationCall = trace.getRootOperationCall();
				traceID = Long.toString(trace.getTraceID());
			} else {
				operationCall = ((OperationCall) parent.getData()).getChildren().get(tableIndex);
				traceID = "";
			}

			String componentName = operationCall.getComponent();
			if (View.this.propertiesModel.getComponentNames() == ComponentNames.SHORT) {
				componentName = NameConverter.toShortComponentName(componentName);
			}
			String operationString = operationCall.getOperation();
			if (View.this.propertiesModel.getOperationNames() == OperationNames.SHORT) {
				operationString = NameConverter.toShortOperationName(operationString);
			}
			final String duration = (Long.toString(operationCall.getDuration()) + " " + View.this.model.getShortTimeUnit()).trim();
			item.setText(new String[] { operationCall.getContainer(), componentName, operationString, duration, String.format("%.1f%%", operationCall.getPercent()), traceID });

			if (operationCall.isFailed()) {
				final Color colorRed = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
				item.setForeground(colorRed);
			}

			item.setData(operationCall);
			item.setItemCount(operationCall.getChildren().size());
		}
	}

}
