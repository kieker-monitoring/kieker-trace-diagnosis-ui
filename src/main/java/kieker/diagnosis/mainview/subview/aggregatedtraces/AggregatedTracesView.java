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

package kieker.diagnosis.mainview.subview.aggregatedtraces;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import kieker.diagnosis.domain.AggregatedOperationCall;
import kieker.diagnosis.domain.AggregatedTrace;
import kieker.diagnosis.mainview.subview.ISubView;
import kieker.diagnosis.mainview.subview.aggregatedtraces.util.AvgDurationSortListener;
import kieker.diagnosis.mainview.subview.aggregatedtraces.util.CallsSortListener;
import kieker.diagnosis.mainview.subview.aggregatedtraces.util.MaxDurationSortListener;
import kieker.diagnosis.mainview.subview.aggregatedtraces.util.MeanDurationSortListener;
import kieker.diagnosis.mainview.subview.aggregatedtraces.util.MinDurationSortListener;
import kieker.diagnosis.mainview.subview.aggregatedtraces.util.TotalDurationSortListener;
import kieker.diagnosis.mainview.subview.util.ComponentSortListener;
import kieker.diagnosis.mainview.subview.util.ContainerSortListener;
import kieker.diagnosis.mainview.subview.util.NameConverter;
import kieker.diagnosis.mainview.subview.util.OperationSortListener;
import kieker.diagnosis.mainview.subview.util.TraceDepthSortListener;
import kieker.diagnosis.mainview.subview.util.TraceSizeSortListener;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.model.PropertiesModel.ComponentNames;
import kieker.diagnosis.model.PropertiesModel.OperationNames;

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

/**
 * @author Nils Christian Ehmke
 */
@Component
public final class AggregatedTracesView implements Observer, ISubView {
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("kieker.diagnosis.mainview.subview.aggregatedtraces.aggregatedtracesview"); //$NON-NLS-1$

	private static final String N_A = "N/A";

	@Autowired private AggregatedTracesViewModel model;

	@Autowired private AggregatedTracesViewController controller;

	@Autowired private DataModel dataModel;

	@Autowired private PropertiesModel propertiesModel;

	private List<AggregatedTrace> cachedDataModelContent;

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
	private Label lblCounter;

	private Button btnShowAll;

	private Button btnShowJustFailed;

	private Button btnShowJustFailureContaining;

	private ScrolledComposite ivSc;
	private Text filterText;

	@PostConstruct
	public void initialize() {
		this.updateCachedDataModelContent();
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
		final GridLayout gl_filterComposite = new GridLayout(3, false);
		gl_composite.verticalSpacing = 0;
		gl_composite.marginHeight = 0;
		gl_composite.marginWidth = 0;
		gl_composite.horizontalSpacing = 0;
		filterComposite.setLayout(gl_filterComposite);

		this.btnShowAll = new Button(filterComposite, SWT.RADIO);
		this.btnShowAll.setText(BUNDLE.getString("AggregatedTracesView.btnShowAll.text")); //$NON-NLS-1$ 
		this.btnShowAll.setSelection(true);
		this.btnShowJustFailed = new Button(filterComposite, SWT.RADIO);
		this.btnShowJustFailed.setText(BUNDLE.getString("AggregatedTracesView.btnShowJustFailed.text")); //$NON-NLS-1$ 
		this.btnShowJustFailureContaining = new Button(filterComposite, SWT.RADIO);
		this.btnShowJustFailureContaining.setText(BUNDLE.getString("AggregatedTracesView.btnShowJustFailureContaining.text")); //$NON-NLS-1$ 

		this.filterText = new Text(filterComposite, SWT.BORDER);
		this.filterText.setMessage(BUNDLE.getString("AggregatedTracesView.text.message")); //$NON-NLS-1$
		this.filterText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		final SashForm sashForm = new SashForm(this.composite, SWT.VERTICAL);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		this.tree = new Tree(sashForm, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		this.tree.setHeaderVisible(true);

		final TreeColumn trclmnExecutionContainer = new TreeColumn(this.tree, SWT.NONE);
		trclmnExecutionContainer.setWidth(100);
		trclmnExecutionContainer.setText(BUNDLE.getString("AggregatedTracesView.trclmnExecutionContainer.text")); //$NON-NLS-1$ 

		final TreeColumn trclmnComponent = new TreeColumn(this.tree, SWT.NONE);
		trclmnComponent.setWidth(100);
		trclmnComponent.setText(BUNDLE.getString("AggregatedTracesView.trclmnComponent.text")); //$NON-NLS-1$ 

		final TreeColumn trclmnOperation = new TreeColumn(this.tree, SWT.NONE);
		trclmnOperation.setWidth(100);
		trclmnOperation.setText(BUNDLE.getString("AggregatedTracesView.trclmnOperation.text")); //$NON-NLS-1$ 

		final TreeColumn trclmnTraceDepth = new TreeColumn(this.tree, SWT.RIGHT);
		trclmnTraceDepth.setWidth(100);
		trclmnTraceDepth.setText(BUNDLE.getString("AggregatedTracesView.trclmnTraceDepth.text")); //$NON-NLS-1$ 

		final TreeColumn trclmnTraceSize = new TreeColumn(this.tree, SWT.RIGHT);
		trclmnTraceSize.setWidth(100);
		trclmnTraceSize.setText(BUNDLE.getString("AggregatedTracesView.trclmnTraceSize.text")); //$NON-NLS-1$ 

		final TreeColumn trclmnCalls = new TreeColumn(this.tree, SWT.RIGHT);
		trclmnCalls.setWidth(100);
		trclmnCalls.setText(BUNDLE.getString("AggregatedTracesView.trclmnCalls.text")); //$NON-NLS-1$ 

		final TreeColumn trclmnMinimalDuration = new TreeColumn(this.tree, SWT.RIGHT);
		trclmnMinimalDuration.setWidth(100);
		trclmnMinimalDuration.setText(BUNDLE.getString("AggregatedTracesView.trclmnMinimalDuration.text")); //$NON-NLS-1$ 

		final TreeColumn trclmnAverageDuration = new TreeColumn(this.tree, SWT.RIGHT);
		trclmnAverageDuration.setWidth(100);
		trclmnAverageDuration.setText(BUNDLE.getString("AggregatedTracesView.trclmnAverageDuration.text")); //$NON-NLS-1$ 

		final TreeColumn trclmnMeanDuration = new TreeColumn(this.tree, SWT.RIGHT);
		trclmnMeanDuration.setWidth(100);
		trclmnMeanDuration.setText(BUNDLE.getString("AggregatedTracesView.trclmnMeanDuration.text")); //$NON-NLS-1$ 

		final TreeColumn trclmnMaximalDuration = new TreeColumn(this.tree, SWT.RIGHT);
		trclmnMaximalDuration.setWidth(100);
		trclmnMaximalDuration.setText(BUNDLE.getString("AggregatedTracesView.trclmnMaximalDuration.text")); //$NON-NLS-1$ 

		final TreeColumn trclmnTotalDuration = new TreeColumn(this.tree, SWT.RIGHT);
		trclmnTotalDuration.setWidth(100);
		trclmnTotalDuration.setText(BUNDLE.getString("AggregatedTracesView.trclmnTotalDuration.text")); //$NON-NLS-1$ 

		this.ivSc = new ScrolledComposite(sashForm, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

		this.detailComposite = new Composite(this.ivSc, SWT.NONE);
		this.detailComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.detailComposite.setLayout(new GridLayout(2, false));

		this.ivSc.setContent(this.detailComposite);
		this.ivSc.setExpandHorizontal(true);
		this.ivSc.setExpandVertical(true);

		final Label lblExecutionContainer = new Label(this.detailComposite, SWT.NONE);
		lblExecutionContainer.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblExecutionContainer.setText(BUNDLE.getString("AggregatedTracesView.lblExecutionContainer.text") + ":"); //$NON-NLS-1$ 

		this.lblExecutionContainerDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblExecutionContainerDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblExecutionContainerDisplay.setText(AggregatedTracesView.N_A);

		final Label lblComponent = new Label(this.detailComposite, SWT.NONE);
		lblComponent.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblComponent.setText(BUNDLE.getString("AggregatedTracesView.lblComponent.text") + ":"); //$NON-NLS-1$ 

		this.lblComponentDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblComponentDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblComponentDisplay.setText(AggregatedTracesView.N_A);

		final Label lblOperation = new Label(this.detailComposite, SWT.NONE);
		lblOperation.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblOperation.setText(BUNDLE.getString("AggregatedTracesView.lblOperation.text") + ":"); //$NON-NLS-1$ 

		this.lblOperationDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblOperationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblOperationDisplay.setText(AggregatedTracesView.N_A);

		final Label lblNumberOfCalls = new Label(this.detailComposite, SWT.NONE);
		lblNumberOfCalls.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNumberOfCalls.setText(BUNDLE.getString("AggregatedTracesView.lblNumberOfCalls.text") + ":"); //$NON-NLS-1$ 

		this.lblNumberOfCallsDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblNumberOfCallsDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblNumberOfCallsDisplay.setText(AggregatedTracesView.N_A);

		final Label lblMinimalDuration = new Label(this.detailComposite, SWT.NONE);
		lblMinimalDuration.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblMinimalDuration.setText(BUNDLE.getString("AggregatedTracesView.lblMinimalDuration.text") + ":"); //$NON-NLS-1$ 

		this.lblMinimalDurationDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblMinimalDurationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblMinimalDurationDisplay.setText(AggregatedTracesView.N_A);

		final Label lblAverageDuration = new Label(this.detailComposite, SWT.NONE);
		lblAverageDuration.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblAverageDuration.setText(BUNDLE.getString("AggregatedTracesView.lblAverageDuration.text") + ":"); //$NON-NLS-1$ 

		this.lblAverageDurationDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblAverageDurationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblAverageDurationDisplay.setText(AggregatedTracesView.N_A);

		final Label lblMeanDuration = new Label(this.detailComposite, SWT.NONE);
		lblMeanDuration.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblMeanDuration.setText(BUNDLE.getString("AggregatedTracesView.lblMeanDuration.text") + ":"); //$NON-NLS-1$ 

		this.lblMeanDurationDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblMeanDurationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblMeanDurationDisplay.setText(AggregatedTracesView.N_A);

		final Label lblMaximalDuration = new Label(this.detailComposite, SWT.NONE);
		lblMaximalDuration.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblMaximalDuration.setText(BUNDLE.getString("AggregatedTracesView.lblMaximalDuration.text") + ":"); //$NON-NLS-1$ 

		this.lblMaximalDurationDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblMaximalDurationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblMaximalDurationDisplay.setText(AggregatedTracesView.N_A);

		final Label lblTotalDuration = new Label(this.detailComposite, SWT.NONE);
		lblTotalDuration.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblTotalDuration.setText(BUNDLE.getString("AggregatedTracesView.lblTotalDuration.text") + ":"); //$NON-NLS-1$ 

		this.lblTotalDurationDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblTotalDurationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblTotalDurationDisplay.setText(AggregatedTracesView.N_A);

		this.lblFailed = new Label(this.detailComposite, SWT.NONE);
		this.lblFailed.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblFailed.setText(BUNDLE.getString("AggregatedTracesView.lblFailed.text") + ":"); //$NON-NLS-1$ 

		this.lblFailedDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblFailedDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblFailedDisplay.setText(AggregatedTracesView.N_A);

		final Label lblTraceDepth = new Label(this.detailComposite, SWT.NONE);
		lblTraceDepth.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblTraceDepth.setText(BUNDLE.getString("AggregatedTracesView.lblTraceDepth.text") + ":"); //$NON-NLS-1$ 

		this.lblTraceDepthDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblTraceDepthDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblTraceDepthDisplay.setText(AggregatedTracesView.N_A);

		final Label lblTraceSize = new Label(this.detailComposite, SWT.NONE);
		lblTraceSize.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblTraceSize.setText(BUNDLE.getString("AggregatedTracesView.lblTraceSize.text") + ":"); //$NON-NLS-1$ 

		this.lblTraceSizeDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblTraceSizeDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblTraceSizeDisplay.setText(AggregatedTracesView.N_A);
		sashForm.setWeights(new int[] { 2, 1 });

		this.statusBar = new Composite(this.composite, SWT.NONE);
		this.statusBar.setLayout(new GridLayout(1, false));

		this.lblCounter = new Label(this.statusBar, SWT.NONE);
		this.lblCounter.setText("0 " + BUNDLE.getString("AggregatedTracesView.lblCounter.text") + ":"); //$NON-NLS-1$ 

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

		this.filterText.addTraverseListener(this.controller);

		this.btnShowAll.addSelectionListener(this.controller);
		this.btnShowJustFailed.addSelectionListener(this.controller);
		this.btnShowJustFailureContaining.addSelectionListener(this.controller);
	}

	public Text getFilterText() {
		return this.filterText;
	}

	public Button getBtn1() {
		return this.btnShowAll;
	}

	public Button getBtn2() {
		return this.btnShowJustFailed;
	}

	public Button getBtn3() {
		return this.btnShowJustFailureContaining;
	}

	@Override
	public Composite getComposite() {
		return this.composite;
	}

	private void updateCachedDataModelContent() {
		switch (this.model.getFilter()) {
		case JUST_FAILED:
			this.cachedDataModelContent = this.dataModel.getFailedAggregatedTraces(this.model.getRegExpr());
			break;
		case JUST_FAILURE_CONTAINING:
			this.cachedDataModelContent = this.dataModel.getFailureContainingAggregatedTraces(this.model.getRegExpr());
			break;
		case NONE:
			this.cachedDataModelContent = this.dataModel.getAggregatedTraces(this.model.getRegExpr());
			break;
		default:
			break;
		}
	}

	@Override
	public void update(final Observable observable, final Object obj) {
		if (observable == this.dataModel) {
			this.updateCachedDataModelContent();
			this.updateTree();
			this.updateStatusBar();
		}
		if (observable == this.propertiesModel) {
			this.clearTree();
		}
	}

	public void notifyAboutChangedFilter() {
		this.updateCachedDataModelContent();
		this.updateTree();
		this.updateStatusBar();
		this.updateDetailComposite();
	}

	public void notifyAboutChangedOperationCall() {
		this.updateDetailComposite();
	}

	public void notifyAboutChangedRegExpr() {
		this.updateCachedDataModelContent();
		this.updateTree();
		this.updateStatusBar();
		this.updateDetailComposite();
	}

	private void updateStatusBar() {
		this.lblCounter.setText(this.cachedDataModelContent.size() + " " + BUNDLE.getString("AggregatedTracesView.lblCounter.text"));
		this.statusBar.getParent().layout();
	}

	private void updateTree() {
		this.tree.setData(this.cachedDataModelContent);
		this.tree.setItemCount(this.cachedDataModelContent.size());

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
		this.ivSc.setMinSize(this.detailComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	/**
	 * @author Nils Christian Ehmke
	 */
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

			final TimeUnit sourceTimeUnit = AggregatedTracesView.this.dataModel.getTimeUnit();
			final TimeUnit targetTimeUnit = AggregatedTracesView.this.propertiesModel.getTimeUnit();
			final String shortTimeUnit = NameConverter.toShortTimeUnit(targetTimeUnit);

			final String minDuration = targetTimeUnit.convert(call.getMinDuration(), sourceTimeUnit) + " " + shortTimeUnit;
			final String maxDuration = targetTimeUnit.convert(call.getMaxDuration(), sourceTimeUnit) + " " + shortTimeUnit;
			final String meanDuration = targetTimeUnit.convert(call.getMedianDuration(), sourceTimeUnit) + " " + shortTimeUnit;
			final String avgDuration = targetTimeUnit.convert(call.getMeanDuration(), sourceTimeUnit) + " " + shortTimeUnit;
			final String totalDuration = targetTimeUnit.convert(call.getTotalDuration(), sourceTimeUnit) + " " + shortTimeUnit;

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
