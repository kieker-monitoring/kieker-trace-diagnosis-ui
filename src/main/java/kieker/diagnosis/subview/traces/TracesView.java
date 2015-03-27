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

package kieker.diagnosis.subview.traces; // NOPMD (to many imports)

import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;

import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.domain.Trace;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.model.PropertiesModel.ComponentNames;
import kieker.diagnosis.model.PropertiesModel.OperationNames;
import kieker.diagnosis.subview.ISubView;
import kieker.diagnosis.subview.traces.util.DurationSortListener;
import kieker.diagnosis.subview.traces.util.TimestampSortListener;
import kieker.diagnosis.subview.traces.util.TraceIDSortListener;
import kieker.diagnosis.subview.util.ComponentSortListener;
import kieker.diagnosis.subview.util.ContainerSortListener;
import kieker.diagnosis.subview.util.NameConverter;
import kieker.diagnosis.subview.util.OperationSortListener;
import kieker.diagnosis.subview.util.TraceDepthSortListener;
import kieker.diagnosis.subview.util.TraceSizeSortListener;

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
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class TracesView implements Observer, ISubView {
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("kieker.diagnosis.subview.traces.tracesview"); //$NON-NLS-1$

	private static final String N_A = "N/A";

	@Autowired
	private DataModel dataModel;

	@Autowired
	private TracesViewModel model;

	@Autowired
	private TracesViewController controller;

	@Autowired
	private PropertiesModel propertiesModel;

	private Composite composite;
	private Tree tree;
	private Composite detailComposite;
	private Text lblTraceIdDisplay;
	private Text lblDurationDisplay;
	private Text lblFailedDisplay;
	private Text lblTraceDepthDisplay;
	private Text lblTraceSizeDisplay;
	private Text lblOperationDisplay;
	private Text lblComponentDisplay;
	private Text lblExecutionContainerDisplay;
	private Label lblFailed;
	private Label lblCounter;
	private Composite statusBar;
	private Button btnShowAll;
	private Button btnShowJustFailed;
	private Button showJustFailureContaining;
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

		final Composite filterComposite = new Composite(this.composite, SWT.NONE);
		final GridLayout gl_filterComposite = new GridLayout(3, false);
		gl_composite.verticalSpacing = 0;
		gl_composite.marginHeight = 0;
		gl_composite.marginWidth = 0;
		gl_composite.horizontalSpacing = 0;
		filterComposite.setLayout(gl_filterComposite);

		this.btnShowAll = new Button(filterComposite, SWT.RADIO);
		this.btnShowAll.setText(BUNDLE.getString("TracesView.btnShowAll.text")); //$NON-NLS-1$ 
		this.btnShowAll.setSelection(true);
		this.btnShowJustFailed = new Button(filterComposite, SWT.RADIO);
		this.btnShowJustFailed.setText(BUNDLE.getString("TracesView.btnShowJustFailed.text")); //$NON-NLS-1$ 
		this.showJustFailureContaining = new Button(filterComposite, SWT.RADIO);
		this.showJustFailureContaining.setText(BUNDLE.getString("TracesView.showJustFailureContaining.text")); //$NON-NLS-1$ 

		final SashForm sashForm = new SashForm(this.composite, SWT.VERTICAL);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		this.tree = new Tree(sashForm, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		this.tree.setHeaderVisible(true);

		final TreeColumn trclmnExecutionContainer = new TreeColumn(this.tree, SWT.NONE);
		trclmnExecutionContainer.setWidth(100);
		trclmnExecutionContainer.setText(BUNDLE.getString("TracesView.trclmnExecutionContainer.text")); //$NON-NLS-1$ 

		final TreeColumn trclmnComponent = new TreeColumn(this.tree, SWT.NONE);
		trclmnComponent.setWidth(100);
		trclmnComponent.setText(BUNDLE.getString("TracesView.trclmnComponent.text")); //$NON-NLS-1$ 

		final TreeColumn trclmnOperation = new TreeColumn(this.tree, SWT.NONE);
		trclmnOperation.setWidth(100);
		trclmnOperation.setText(BUNDLE.getString("TracesView.trclmnOperation.text")); //$NON-NLS-1$ 

		final TreeColumn trclmnTraceDepth = new TreeColumn(this.tree, SWT.RIGHT);
		trclmnTraceDepth.setWidth(100);
		trclmnTraceDepth.setText(BUNDLE.getString("TracesView.trclmnTraceDepth.text")); //$NON-NLS-1$ 

		final TreeColumn trclmnTraceSize = new TreeColumn(this.tree, SWT.RIGHT);
		trclmnTraceSize.setWidth(100);
		trclmnTraceSize.setText(BUNDLE.getString("TracesView.trclmnTraceSize.text")); //$NON-NLS-1$ 

		final TreeColumn trclmnDuration = new TreeColumn(this.tree, SWT.RIGHT);
		trclmnDuration.setWidth(100);
		trclmnDuration.setText(BUNDLE.getString("TracesView.trclmnDuration.text")); //$NON-NLS-1$ 

		final TreeColumn trclmnPercent = new TreeColumn(this.tree, SWT.RIGHT);
		trclmnPercent.setWidth(100);
		trclmnPercent.setText(BUNDLE.getString("TracesView.trclmnPercent.text")); //$NON-NLS-1$ 

		final TreeColumn trclmnTraceId = new TreeColumn(this.tree, SWT.RIGHT);
		trclmnTraceId.setWidth(100);
		trclmnTraceId.setText(BUNDLE.getString("TracesView.trclmnTraceId.text")); //$NON-NLS-1$ 

		final TreeColumn trclmnTimestamp = new TreeColumn(this.tree, SWT.NONE);
		trclmnTimestamp.setWidth(100);
		trclmnTimestamp.setText(BUNDLE.getString("TracesView.trclmnTimestamp.text")); //$NON-NLS-1$ 

		this.ivSc = new ScrolledComposite(sashForm, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

		this.detailComposite = new Composite(this.ivSc, SWT.NONE);
		this.detailComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.detailComposite.setLayout(new GridLayout(2, false));

		this.ivSc.setContent(this.detailComposite);
		this.ivSc.setExpandHorizontal(true);
		this.ivSc.setExpandVertical(true);

		final Label lblExecutionContainer = new Label(this.detailComposite, SWT.NONE);
		lblExecutionContainer.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblExecutionContainer.setText(BUNDLE.getString("TracesView.lblExecutionContainer.text") + ":"); //$NON-NLS-1$ 

		this.lblExecutionContainerDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblExecutionContainerDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblExecutionContainerDisplay.setText(TracesView.N_A);

		final Label lblComponent = new Label(this.detailComposite, SWT.NONE);
		lblComponent.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblComponent.setText(BUNDLE.getString("TracesView.lblComponent.text") + ":"); //$NON-NLS-1$ 

		this.lblComponentDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblComponentDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblComponentDisplay.setText(TracesView.N_A);

		final Label lblOperation = new Label(this.detailComposite, SWT.NONE);
		lblOperation.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblOperation.setText(BUNDLE.getString("TracesView.lblOperation.text") + ":"); //$NON-NLS-1$ 

		this.lblOperationDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblOperationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblOperationDisplay.setText(TracesView.N_A);

		final Label lblDuration = new Label(this.detailComposite, SWT.NONE);
		lblDuration.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblDuration.setText(BUNDLE.getString("TracesView.lblDuration.text") + ":"); //$NON-NLS-1$ 

		this.lblDurationDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblDurationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblDurationDisplay.setText(TracesView.N_A);

		final Label lblTraceId = new Label(this.detailComposite, SWT.NONE);
		lblTraceId.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblTraceId.setBounds(0, 0, 55, 15);
		lblTraceId.setText(BUNDLE.getString("TracesView.lblTraceId.text") + ":"); //$NON-NLS-1$ 

		this.lblTraceIdDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblTraceIdDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblTraceIdDisplay.setText(TracesView.N_A);

		this.lblFailed = new Label(this.detailComposite, SWT.NONE);
		this.lblFailed.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblFailed.setText(BUNDLE.getString("TracesView.lblFailed.text") + ":"); //$NON-NLS-1$ 

		this.lblFailedDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblFailedDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblFailedDisplay.setText(TracesView.N_A);

		final Label lblTraceDepth = new Label(this.detailComposite, SWT.NONE);
		lblTraceDepth.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblTraceDepth.setText(BUNDLE.getString("TracesView.lblTraceDepth.text") + ":"); //$NON-NLS-1$ 

		this.lblTraceDepthDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblTraceDepthDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblTraceDepthDisplay.setText(TracesView.N_A);

		final Label lblTraceSize = new Label(this.detailComposite, SWT.NONE);
		lblTraceSize.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblTraceSize.setText(BUNDLE.getString("TracesView.lblTraceSize.text") + ":"); //$NON-NLS-1$ 

		this.lblTraceSizeDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblTraceSizeDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblTraceSizeDisplay.setText(TracesView.N_A);

		sashForm.setWeights(new int[] { 2, 1 });

		this.statusBar = new Composite(this.composite, SWT.NONE);

		this.statusBar.setLayout(new GridLayout(1, false));

		this.lblCounter = new Label(this.statusBar, SWT.NONE);
		this.lblCounter.setText("0 " + BUNDLE.getString("TracesView.lblCounter.text")); //$NON-NLS-1$ 

		this.tree.addSelectionListener(this.controller);
		this.tree.addListener(SWT.SetData, new DataProvider());

		trclmnExecutionContainer.addSelectionListener(new ContainerSortListener());
		trclmnComponent.addSelectionListener(new ComponentSortListener());
		trclmnOperation.addSelectionListener(new OperationSortListener());
		trclmnDuration.addSelectionListener(new DurationSortListener());
		trclmnTraceId.addSelectionListener(new TraceIDSortListener());
		trclmnTraceDepth.addSelectionListener(new TraceDepthSortListener());
		trclmnTraceSize.addSelectionListener(new TraceSizeSortListener());
		trclmnTimestamp.addSelectionListener(new TimestampSortListener());

		this.btnShowAll.addSelectionListener(this.controller);
		this.btnShowJustFailed.addSelectionListener(this.controller);
		this.showJustFailureContaining.addSelectionListener(this.controller);
	}

	public Button getBtn1() {
		return this.btnShowAll;
	}

	public Button getBtn2() {
		return this.btnShowJustFailed;
	}

	public Button getBtn3() {
		return this.showJustFailureContaining;
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
			this.lblCounter.setText(this.dataModel.getFailedTracesCopy().size() + " " + BUNDLE.getString("TracesView.lblCounter.text"));
			break;
		case JUST_FAILURE_CONTAINING:
			this.lblCounter.setText(this.dataModel.getFailureContainingTracesCopy().size() + " " + BUNDLE.getString("TracesView.lblCounter.text"));
			break;
		case NONE:
			this.lblCounter.setText(this.dataModel.getTracesCopy().size() + " " + BUNDLE.getString("TracesView.lblCounter.text"));
			break;
		default:
			break;
		}

		this.statusBar.getParent().layout();
	}

	private void updateTree() {
		final List<Trace> records;
		switch (this.model.getFilter()) {
		case JUST_FAILED:
			records = this.dataModel.getFailedTracesCopy();
			break;
		case JUST_FAILURE_CONTAINING:
			records = this.dataModel.getFailureContainingTracesCopy();
			break;
		case NONE:
			records = this.dataModel.getTracesCopy();
			break;
		default:
			records = Collections.emptyList();
			break;

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
		final OperationCall call = this.model.getOperationCall();

		if (call != null) {
			final String shortTimeUnit = NameConverter.toShortTimeUnit(TracesView.this.propertiesModel.getTimeUnit());
			final long duration = this.propertiesModel.getTimeUnit().convert(call.getDuration(), this.dataModel.getTimeUnit());
			final String durationString = duration + " " + shortTimeUnit;

			this.lblTraceIdDisplay.setText(Long.toString(call.getTraceID()));
			this.lblDurationDisplay.setText(durationString);

			this.lblExecutionContainerDisplay.setText(call.getContainer());
			this.lblComponentDisplay.setText(call.getComponent());
			this.lblOperationDisplay.setText(call.getOperation());
			this.lblTraceDepthDisplay.setText(Integer.toString(call.getStackDepth()));
			this.lblTraceSizeDisplay.setText(Integer.toString(call.getStackSize()));

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

	@SuppressWarnings("unchecked")
	public void jumpToCorrespondingTrace(final OperationCall call) {
		final List<Trace> traces = (List<Trace>) this.tree.getData();
		int idxCounter = 0;
		for (final Trace trace : traces) {
			if (trace.getTraceID() == call.getTraceID()) {
				final TreeItem item = this.tree.getItem(idxCounter);
				this.tree.setSelection(item);
			}
			idxCounter++;
		}
		this.btnShowAll.setSelection(true);
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
			final OperationCall call;
			final String traceID;

			if (parent == null) {
				final Trace trace = ((List<Trace>) tree.getData()).get(tableIndex);

				call = trace.getRootOperationCall();
				traceID = Long.toString(trace.getTraceID());
			} else {
				call = ((OperationCall) parent.getData()).getChildren().get(tableIndex);
				traceID = "";
			}

			String componentName = call.getComponent();
			if (TracesView.this.propertiesModel.getComponentNames() == ComponentNames.SHORT) {
				componentName = NameConverter.toShortComponentName(componentName);
			}
			String operationString = call.getOperation();
			if (TracesView.this.propertiesModel.getOperationNames() == OperationNames.SHORT) {
				operationString = NameConverter.toShortOperationName(operationString);
			}

			final String shortTimeUnit = NameConverter.toShortTimeUnit(TracesView.this.propertiesModel.getTimeUnit());
			final long duration = TracesView.this.propertiesModel.getTimeUnit().convert(call.getDuration(), TracesView.this.dataModel.getTimeUnit());
			final String durationString = duration + " " + shortTimeUnit;

			item.setText(new String[] { call.getContainer(), componentName, operationString, Integer.toString(call.getStackDepth()), Integer.toString(call.getStackSize()),
				durationString, String.format("%.1f%%", call.getPercent()), traceID, Long.toString(call.getTimestamp()) });

			if (call.isFailed()) {
				final Color colorRed = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
				item.setForeground(colorRed);
			}

			item.setData(call);
			item.setItemCount(call.getChildren().size());
		}
	}

}
