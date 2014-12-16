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

package kieker.gui.view;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import kieker.gui.model.DataModel;
import kieker.gui.model.PropertiesModel;
import kieker.gui.model.TracesSubViewModel;
import kieker.gui.model.domain.ExecutionEntry;
import kieker.gui.view.util.ExecutionEntryComponentComparator;
import kieker.gui.view.util.ExecutionEntryContainerComparator;
import kieker.gui.view.util.ExecutionEntryDurationComparator;
import kieker.gui.view.util.ExecutionEntryOperationComparator;
import kieker.gui.view.util.ExecutionEntryTraceIDComparator;
import kieker.gui.view.util.TreeColumnSortListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
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

public class TracesSubView implements Observer, ISubView {

	private final DataModel model;
	private final TracesSubViewModel tracesSubViewModel;
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
	private final Type type;

	public TracesSubView(final Type type, final DataModel model, final TracesSubViewModel tracesSubViewModel, final PropertiesModel propertiesModel,
			final SelectionListener controller) {
		this.model = model;
		this.propertiesModel = propertiesModel;
		this.tracesSubViewModel = tracesSubViewModel;
		this.controller = controller;

		model.addObserver(this);
		tracesSubViewModel.addObserver(this);
		propertiesModel.addObserver(this);

		this.type = type;
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
		this.composite.setLayout(new FillLayout(SWT.HORIZONTAL));

		final SashForm sashForm = new SashForm(this.composite, SWT.VERTICAL);

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
		this.lblExecutionContainerDisplay.setText("N/A");

		final Label lblComponent = new Label(this.detailComposite, SWT.NONE);
		lblComponent.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblComponent.setText("Component:");

		this.lblComponentDisplay = new Label(this.detailComposite, SWT.NONE);
		this.lblComponentDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblComponentDisplay.setText("N/A");

		final Label lblOperation = new Label(this.detailComposite, SWT.NONE);
		lblOperation.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblOperation.setText("Operation:");

		this.lblOperationDisplay = new Label(this.detailComposite, SWT.NONE);
		this.lblOperationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblOperationDisplay.setText("N/A");

		final Label lblDuration = new Label(this.detailComposite, SWT.NONE);
		lblDuration.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblDuration.setText("Duration:");

		this.lblDurationDisplay = new Label(this.detailComposite, SWT.NONE);
		this.lblDurationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblDurationDisplay.setText("N/A");

		final Label lblTraceId = new Label(this.detailComposite, SWT.NONE);
		lblTraceId.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblTraceId.setBounds(0, 0, 55, 15);
		lblTraceId.setText("Trace ID:");

		this.lblTraceIdDisplay = new Label(this.detailComposite, SWT.NONE);
		this.lblTraceIdDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblTraceIdDisplay.setText("N/A");

		this.lblFailed = new Label(this.detailComposite, SWT.NONE);
		this.lblFailed.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblFailed.setText("Failed:");

		this.lblFailedDisplay = new Label(this.detailComposite, SWT.NONE);
		this.lblFailedDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblFailedDisplay.setText("N/A");

		final Label lblTraceDepth = new Label(this.detailComposite, SWT.NONE);
		lblTraceDepth.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblTraceDepth.setText("Trace Depth:");

		this.lblTraceDepthDisplay = new Label(this.detailComposite, SWT.NONE);
		this.lblTraceDepthDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblTraceDepthDisplay.setText("N/A");

		final Label lblTraceSize = new Label(this.detailComposite, SWT.NONE);
		lblTraceSize.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblTraceSize.setText("Trace Size:");

		this.lblTraceSizeDisplay = new Label(this.detailComposite, SWT.NONE);
		this.lblTraceSizeDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblTraceSizeDisplay.setText("N/A");

		sashForm.setWeights(new int[] { 2, 1 });

		this.tree.addSelectionListener(this.controller);
		this.tree.addListener(SWT.SetData, new DataProvider());

		trclmnExecutionContainer.addSelectionListener(new TreeColumnSortListener<>(new ExecutionEntryContainerComparator()));
		trclmnComponent.addSelectionListener(new TreeColumnSortListener<>(new ExecutionEntryComponentComparator()));
		trclmnOperation.addSelectionListener(new TreeColumnSortListener<>(new ExecutionEntryOperationComparator()));
		trclmnDuration.addSelectionListener(new TreeColumnSortListener<>(new ExecutionEntryDurationComparator()));
		trclmnTraceId.addSelectionListener(new TreeColumnSortListener<>(new ExecutionEntryTraceIDComparator()));
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
		}
		if (observable == this.tracesSubViewModel) {
			this.updateDetailComposite();
		}
		if (observable == this.propertiesModel) {
			this.clearTree();
		}
	}

	private void updateTree() {
		final List<ExecutionEntry> records;
		if (this.type == Type.SHOW_JUST_FAILED_TRACES) {
			records = this.model.getFailedTracesCopy();
		} else if (this.type == Type.SHOW_JUST_FAILURE_CONTAINING_TRACES) {
			records = this.model.getFailureContainingTracesCopy();
		} else {
			records = this.model.getTracesCopy();
		}

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
		final ExecutionEntry trace = this.tracesSubViewModel.getCurrentActiveTrace();

		final String duration = (Long.toString(trace.getDuration()) + " " + this.model.getShortTimeUnit()).trim();

		this.lblTraceIdDisplay.setText(Long.toString(trace.getTraceID()));
		this.lblDurationDisplay.setText(duration);

		this.lblExecutionContainerDisplay.setText(trace.getContainer());
		this.lblComponentDisplay.setText(trace.getComponent());
		this.lblOperationDisplay.setText(trace.getOperation());
		this.lblTraceDepthDisplay.setText(Integer.toString(trace.getTraceDepth()));
		this.lblTraceSizeDisplay.setText(Integer.toString(trace.getTraceSize()));

		if (trace.isFailed()) {
			this.lblFailedDisplay.setText("Yes (" + trace.getFailedCause() + ")");
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
			final ExecutionEntry executionEntry;
			final String traceID;

			if (parent == null) {
				executionEntry = ((List<ExecutionEntry>) tree.getData()).get(tableIndex);
				traceID = Long.toString(executionEntry.getTraceID());
			} else {
				executionEntry = ((ExecutionEntry) parent.getData()).getChildren().get(tableIndex);
				traceID = "";
			}

			String componentName = executionEntry.getComponent();
			if (TracesSubView.this.propertiesModel.isShortComponentNames()) {
				final int lastPointPos = componentName.lastIndexOf('.');
				componentName = componentName.substring(lastPointPos + 1);
			}
			String operationString = executionEntry.getOperation();
			if (TracesSubView.this.propertiesModel.isShortOperationNames()) {
				operationString = operationString.replaceAll("\\(..*\\)", "(...)");

				final int lastPointPos = operationString.lastIndexOf('.', operationString.length() - 5);
				operationString = operationString.substring(lastPointPos + 1);
			}
			final String duration = (Long.toString(executionEntry.getDuration()) + " " + TracesSubView.this.model.getShortTimeUnit()).trim();
			item.setText(new String[] { executionEntry.getContainer(), componentName, operationString, duration, String.format("%.1f%%", executionEntry.getPercent()), traceID });

			if (executionEntry.isFailed()) {
				final Color colorRed = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
				item.setForeground(colorRed);
			}

			item.setData(executionEntry);
			item.setItemCount(executionEntry.getChildren().size());
		}
	}

	public enum Type {
		SHOW_ALL_TRACES, SHOW_JUST_FAILED_TRACES, SHOW_JUST_FAILURE_CONTAINING_TRACES
	}

}
