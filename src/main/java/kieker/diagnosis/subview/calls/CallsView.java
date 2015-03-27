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

package kieker.diagnosis.subview.calls;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.annotation.PostConstruct;

import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.model.PropertiesModel.ComponentNames;
import kieker.diagnosis.model.PropertiesModel.OperationNames;
import kieker.diagnosis.subview.ISubView;
import kieker.diagnosis.subview.calls.CallsViewModel.Filter;
import kieker.diagnosis.subview.calls.util.ComponentSortListener;
import kieker.diagnosis.subview.calls.util.ContainerSortListener;
import kieker.diagnosis.subview.calls.util.DurationSortListener;
import kieker.diagnosis.subview.calls.util.OperationSortListener;
import kieker.diagnosis.subview.calls.util.TimestampSortListener;
import kieker.diagnosis.subview.calls.util.TraceIDSortListener;
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
public final class CallsView implements ISubView, Observer {

	private static final String N_A = "N/A";

	@Autowired
	private DataModel dataModel;

	@Autowired
	private CallsViewModel model;

	@Autowired
	private PropertiesModel propertiesModel;

	@Autowired
	private CallsViewController controller;

	private Composite composite;
	private Composite detailComposite;
	private Composite statusBar;
	private Label lblTraceEquivalence;
	private Table table;
	private Text lblFailedDisplay;
	private Text lblMinimalDurationDisplay;
	private Text lblOperationDisplay;
	private Text lblComponentDisplay;
	private Text lblExecutionContainerDisplay;
	private Label lblFailed;

	private Composite filterComposite;

	private Button ivBtn1;

	private Button ivBtn2;

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
		final GridLayout gl_filterComposite = new GridLayout(2, false);
		gl_composite.verticalSpacing = 0;
		gl_composite.marginHeight = 0;
		gl_composite.marginWidth = 0;
		gl_composite.horizontalSpacing = 0;
		this.filterComposite.setLayout(gl_filterComposite);

		ivBtn1 = new Button(filterComposite, SWT.RADIO);
		ivBtn1.setText("Show All Calls");
		ivBtn1.setSelection(true);
		ivBtn2 = new Button(filterComposite, SWT.RADIO);
		ivBtn2.setText("Show Only Failed Calls");

		final SashForm sashForm = new SashForm(this.composite, SWT.VERTICAL);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		this.table = new Table(sashForm, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
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

		final TableColumn tblclmnMinimalDuration = new TableColumn(this.table, SWT.NONE);
		tblclmnMinimalDuration.setWidth(100);
		tblclmnMinimalDuration.setText("Duration");

		final TableColumn tblclmnTotalDuration = new TableColumn(this.table, SWT.NONE);
		tblclmnTotalDuration.setWidth(100);
		tblclmnTotalDuration.setText("Trace ID");

		final TableColumn tblclmnTimestamp = new TableColumn(this.table, SWT.NONE);
		tblclmnTimestamp.setWidth(100);
		tblclmnTimestamp.setText("Timestamp");

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
		this.lblExecutionContainerDisplay.setText(CallsView.N_A);

		final Label lblComponent = new Label(this.detailComposite, SWT.NONE);
		lblComponent.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblComponent.setText("Component:");

		this.lblComponentDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblComponentDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblComponentDisplay.setText(CallsView.N_A);

		final Label lblOperation = new Label(this.detailComposite, SWT.NONE);
		lblOperation.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblOperation.setText("Operation:");

		this.lblOperationDisplay = new Text(this.detailComposite, SWT.READ_ONLY | SWT.NONE);
		this.lblOperationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblOperationDisplay.setText(CallsView.N_A);

		final Label lblMinimalDuration = new Label(this.detailComposite, SWT.NONE);
		lblMinimalDuration.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblMinimalDuration.setText("Duration:");

		this.lblMinimalDurationDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblMinimalDurationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblMinimalDurationDisplay.setText(CallsView.N_A);

		this.lblFailed = new Label(this.detailComposite, SWT.NONE);
		this.lblFailed.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblFailed.setText("Failed:");

		this.lblFailedDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblFailedDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblFailedDisplay.setText(CallsView.N_A);
		sashForm.setWeights(new int[] { 2, 1 });

		this.statusBar = new Composite(this.composite, SWT.NONE);
		this.statusBar.setLayout(new GridLayout(1, false));

		this.lblTraceEquivalence = new Label(this.statusBar, SWT.NONE);
		this.lblTraceEquivalence.setText("0 Operation Calls");

		this.table.addListener(SWT.SetData, new DataProvider());
		this.table.addSelectionListener(this.controller);

		tblclmnComponent.addSelectionListener(new ComponentSortListener());
		tblclmnExecutionContainer.addSelectionListener(new ContainerSortListener());
		tblclmnOperation.addSelectionListener(new OperationSortListener());
		tblclmnMinimalDuration.addSelectionListener(new DurationSortListener());
		tblclmnTotalDuration.addSelectionListener(new TraceIDSortListener());
		tblclmnTimestamp.addSelectionListener(new TimestampSortListener());

		ivBtn1.addSelectionListener(controller);
		ivBtn2.addSelectionListener(controller);
	}

	public Button getBtn1() {
		return ivBtn1;
	}

	public Button getBtn2() {
		return ivBtn2;
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

	private void updateDetailComposite() {
		final OperationCall call = this.model.getOperationCall();

		if (call != null) {
			final String shortTimeUnit = NameConverter.toShortTimeUnit(this.propertiesModel.getTimeUnit());
			final long duration = this.propertiesModel.getTimeUnit().convert(call.getDuration(), this.dataModel.getTimeUnit());
			final String durationString = duration + " " + shortTimeUnit;

			this.lblMinimalDurationDisplay.setText(durationString);

			this.lblExecutionContainerDisplay.setText(call.getContainer());
			this.lblComponentDisplay.setText(call.getComponent());
			this.lblOperationDisplay.setText(call.getOperation());

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

	private void updateStatusBar() {
		if (this.model.getFilter() == Filter.NONE) {
			this.lblTraceEquivalence.setText(this.dataModel.getOperationCalls().size() + " Operation Call(s)");
		} else {
			this.lblTraceEquivalence.setText(this.dataModel.getFailedOperationCalls().size() + " Failed Operation Call(s)");
		}
		this.statusBar.getParent().layout();
	}

	private void updateTable() {
		final List<OperationCall> calls;
		if (this.model.getFilter() == Filter.NONE) {
			calls = this.dataModel.getOperationCalls();
		} else {
			calls = this.dataModel.getFailedOperationCalls();
		}

		this.table.setData(calls);
		this.table.setItemCount(calls.size());
		this.clearTable();
	}

	private void clearTable() {
		this.table.clearAll();
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
			final List<OperationCall> calls = (List<OperationCall>) table.getData();
			final OperationCall call = calls.get(tableIndex);

			// Get the data to display
			String componentName = call.getComponent();
			if (CallsView.this.propertiesModel.getComponentNames() == ComponentNames.SHORT) {
				componentName = NameConverter.toShortComponentName(componentName);
			}
			String operationString = call.getOperation();
			if (CallsView.this.propertiesModel.getOperationNames() == OperationNames.SHORT) {
				operationString = NameConverter.toShortOperationName(operationString);
			}

			final String shortTimeUnit = NameConverter.toShortTimeUnit(CallsView.this.propertiesModel.getTimeUnit());
			final long duration = CallsView.this.propertiesModel.getTimeUnit().convert(call.getDuration(), CallsView.this.dataModel.getTimeUnit());

			item.setText(new String[] { call.getContainer(), componentName, operationString, Long.toString(duration) + " " + shortTimeUnit, Long.toString(call.getTraceID()),
				Long.toString(call.getTimestamp()) });

			if (call.isFailed()) {
				final Color colorRed = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
				item.setForeground(colorRed);
			}

			item.setData(call);
		}

	}

}
