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

package kieker.diagnosis.mainview.subview.database.preparedstatements;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import kieker.diagnosis.czi.Utils;
import kieker.diagnosis.domain.PreparedStatementCall;
import kieker.diagnosis.mainview.subview.ISubView;
import kieker.diagnosis.mainview.subview.database.preparedstatements.DatabasePreparedStatementCallsViewModel.Filter;
import kieker.diagnosis.mainview.subview.util.NameConverter;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Christian Zirkelbach
 */

@Component
public final class DatabasePreparedStatementCallsView implements ISubView,
		Observer {

	private static final ResourceBundle BUNDLE = ResourceBundle
			.getBundle("kieker.diagnosis.mainview.subview.database.preparedstatements.databasepreparedstatementcallsview"); //$NON-NLS-1$

	private static final String N_A = "N/A";

	@Autowired
	private DataModel dataModel;
	@Autowired
	private PropertiesModel propertiesModel;

	@Autowired
	private DatabasePreparedStatementCallsViewModel model;
	@Autowired
	private DatabasePreparedStatementCallsViewController controller;

	private List<PreparedStatementCall> cachedDataModelContent;

	private Composite composite;
	private Composite detailComposite;
	private Composite statusBar;
	private Label lbCounter;
	private Tree tree;
	private Text lblFailedDisplay;
	private Text lblMinimalDurationDisplay;
	private Text lblOperationDisplay;
	private Text lblAbstractStatementDisplay;
	private Text lblConcreteStatementDisplay;
	private Text lblReturnValueDisplay;
	private Label lblFailed;
	private Button btnShowAll;
	private Button btnShowJustFailed;
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
	public void createComposite(final Composite parent) { // NOPMD (This method
															// violates some
															// metrics)
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

		final Composite filterComposite = new Composite(this.composite,
				SWT.NONE);
		final GridLayout gl_filterComposite = new GridLayout(2, false);
		gl_composite.verticalSpacing = 0;
		gl_composite.marginHeight = 0;
		gl_composite.marginWidth = 0;
		gl_composite.horizontalSpacing = 0;
		filterComposite.setLayout(gl_filterComposite);

		this.btnShowAll = new Button(filterComposite, SWT.RADIO);
		this.btnShowAll
				.setText(DatabasePreparedStatementCallsView.BUNDLE
						.getString("DatabasePreparedStatementCallsView.btnShowAll.text")); //$NON-NLS-1$
		this.btnShowAll.setSelection(true);
		this.btnShowJustFailed = new Button(filterComposite, SWT.RADIO);
		this.btnShowJustFailed
				.setText(DatabasePreparedStatementCallsView.BUNDLE
						.getString("DatabasePreparedStatementCallsView.btnShowJustFailed.text")); //$NON-NLS-1$

		this.filterText = new Text(filterComposite, SWT.BORDER);
		this.filterText.setMessage(DatabasePreparedStatementCallsView.BUNDLE
				.getString("DatabasePreparedStatementCallsView.text.message")); //$NON-NLS-1$
		this.filterText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1));

		final SashForm sashForm = new SashForm(this.composite, SWT.VERTICAL);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));

		this.tree = new Tree(sashForm, SWT.BORDER | SWT.FULL_SELECTION
				| SWT.VIRTUAL);
		this.tree.setHeaderVisible(true);
		this.tree.setLinesVisible(true);

		final TreeColumn tblclmnStatement = new TreeColumn(this.tree, SWT.NONE);
		tblclmnStatement.setWidth(800);
		tblclmnStatement
				.setText(DatabasePreparedStatementCallsView.BUNDLE
						.getString("DatabasePreparedStatementCallsView.tblclmnStatement.text")); //$NON-NLS-1$

		final TreeColumn tblclmnReturnValue = new TreeColumn(this.tree,
				SWT.NONE);
		tblclmnReturnValue.setWidth(100);
		tblclmnReturnValue
				.setText(DatabasePreparedStatementCallsView.BUNDLE
						.getString("DatabasePreparedStatementCallsView.tblclmnReturnValue.text")); //$NON-NLS-1$

		final TreeColumn tblclmnDuration = new TreeColumn(this.tree, SWT.NONE);
		tblclmnDuration.setWidth(100);
		tblclmnDuration
				.setText(DatabasePreparedStatementCallsView.BUNDLE
						.getString("DatabasePreparedStatementCallsView.tblclmnDuration.text")); //$NON-NLS-1$

		final TreeColumn tblclmnTraceID = new TreeColumn(this.tree, SWT.NONE);
		tblclmnTraceID.setWidth(100);
		tblclmnTraceID
				.setText(DatabasePreparedStatementCallsView.BUNDLE
						.getString("DatabasePreparedStatementCallsView.tblclmnTraceID.text")); //$NON-NLS-1$

		final TreeColumn tblclmnTimestamp = new TreeColumn(this.tree, SWT.NONE);
		tblclmnTimestamp.setWidth(150);
		tblclmnTimestamp
				.setText(DatabasePreparedStatementCallsView.BUNDLE
						.getString("DatabasePreparedStatementCallsView.tblclmnTimestamp.text")); //$NON-NLS-1$

		this.ivSc = new ScrolledComposite(sashForm, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.BORDER);

		this.detailComposite = new Composite(this.ivSc, SWT.NONE);
		this.detailComposite.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		this.detailComposite.setLayout(new GridLayout(2, false));

		this.ivSc.setContent(this.detailComposite);
		this.ivSc.setExpandHorizontal(true);
		this.ivSc.setExpandVertical(true);

		final Label lblOperation = new Label(this.detailComposite, SWT.NONE);
		lblOperation
				.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblOperation
				.setText(DatabasePreparedStatementCallsView.BUNDLE
						.getString("DatabasePreparedStatementCallsView.lblOperation.text") + ":"); //$NON-NLS-1$

		this.lblOperationDisplay = new Text(this.detailComposite, SWT.READ_ONLY
				| SWT.NONE);
		this.lblOperationDisplay.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		this.lblOperationDisplay
				.setText(DatabasePreparedStatementCallsView.N_A);

		final Label lblAbstractStatement = new Label(this.detailComposite,
				SWT.NONE);
		lblAbstractStatement.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		lblAbstractStatement
				.setText(DatabasePreparedStatementCallsView.BUNDLE
						.getString("DatabasePreparedStatementCallsView.lblAbstractStatement.text") + ":"); //$NON-NLS-1$

		this.lblAbstractStatementDisplay = new Text(this.detailComposite,
				SWT.MULTI | SWT.WRAP | SWT.READ_ONLY | SWT.NONE);
		this.lblAbstractStatementDisplay.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		this.lblAbstractStatementDisplay
				.setText(DatabasePreparedStatementCallsView.N_A);

		final Label lblConcreteStatement = new Label(this.detailComposite,
				SWT.NONE);
		lblConcreteStatement.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		lblConcreteStatement
				.setText(DatabasePreparedStatementCallsView.BUNDLE
						.getString("DatabasePreparedStatementCallsView.lblConcreteStatement.text") + ":"); //$NON-NLS-1$

		this.lblConcreteStatementDisplay = new Text(this.detailComposite,
				SWT.MULTI | SWT.WRAP | SWT.READ_ONLY | SWT.NONE);
		this.lblConcreteStatementDisplay.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		this.lblConcreteStatementDisplay
				.setText(DatabasePreparedStatementCallsView.N_A);

		final Label lblReturnValue = new Label(this.detailComposite, SWT.NONE);
		lblReturnValue.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		lblReturnValue
				.setText(DatabasePreparedStatementCallsView.BUNDLE
						.getString("DatabasePreparedStatementCallsView.lblReturnValue.text") + ":"); //$NON-NLS-1$

		this.lblReturnValueDisplay = new Text(this.detailComposite,
				SWT.READ_ONLY | SWT.NONE);
		this.lblReturnValueDisplay.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		this.lblReturnValueDisplay
				.setText(DatabasePreparedStatementCallsView.N_A);

		final Label lblDuration = new Label(this.detailComposite, SWT.NONE);
		lblDuration.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblDuration
				.setText(DatabasePreparedStatementCallsView.BUNDLE
						.getString("DatabasePreparedStatementCallsView.lblDuration.text") + ":"); //$NON-NLS-1$

		this.lblMinimalDurationDisplay = new Text(this.detailComposite,
				SWT.READ_ONLY);
		this.lblMinimalDurationDisplay.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		this.lblMinimalDurationDisplay
				.setText(DatabasePreparedStatementCallsView.N_A);

		this.lblFailed = new Label(this.detailComposite, SWT.NONE);
		this.lblFailed.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		this.lblFailed
				.setText(DatabasePreparedStatementCallsView.BUNDLE
						.getString("DatabasePreparedStatementCallsView.lblFailed.text")); //$NON-NLS-1$

		this.lblFailedDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblFailedDisplay.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		this.lblFailedDisplay.setText(DatabasePreparedStatementCallsView.N_A);
		sashForm.setWeights(new int[] { 2, 1 });

		this.statusBar = new Composite(this.composite, SWT.NONE);
		this.statusBar.setLayout(new GridLayout(1, false));

		this.lbCounter = new Label(this.statusBar, SWT.NONE);
		this.lbCounter
				.setText("0 " + DatabasePreparedStatementCallsView.BUNDLE.getString("DatabasePreparedStatementCallsView.lbCounter.text")); //$NON-NLS-1$

		this.tree.addListener(SWT.SetData, new DataProvider());
		this.tree.addSelectionListener(this.controller);

		// TODO Listener
		// tblclmnOperation
		// .addSelectionListener(new
		// CallTreeColumnSortListener<DatabaseOperationCall>(
		// DatabaseOperationCall::getOperation));
		//
		// tblclmnStatement
		// .addSelectionListener(new
		// CallTreeColumnSortListener<DatabaseOperationCall>(
		// DatabaseOperationCall::getStringClassArgs));
		//
		// tblclmnReturnValue
		// .addSelectionListener(new
		// CallTreeColumnSortListener<DatabaseOperationCall>(
		// DatabaseOperationCall::getFormattedReturnValue));
		//
		// tblclmnTraceID.addSelectionListener(new
		// CallTreeColumnSortListener<DatabaseOperationCall>(DatabaseOperationCall::getTraceID));
		//
		// tblclmnDuration
		// .addSelectionListener(new
		// CallTreeColumnSortListener<DatabaseOperationCall>(
		// DatabaseOperationCall::getDuration));
		// tblclmnTimestamp
		// .addSelectionListener(new
		// CallTreeColumnSortListener<DatabaseOperationCall>(
		// DatabaseOperationCall::getTimestamp));

		this.filterText.addTraverseListener(this.controller);

		this.btnShowAll.addSelectionListener(this.controller);
		this.btnShowJustFailed.addSelectionListener(this.controller);
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

	public void notifyAboutChangedOperationCall() {
		this.updateDetailComposite();
	}

	public void notifyAboutChangedRegExpr() {
		this.updateCachedDataModelContent();
		this.updateTree();
		this.updateStatusBar();
		this.updateDetailComposite();
	}

	public void notifyAboutChangedFilter() {
		this.updateCachedDataModelContent();
		this.updateTree();
		this.updateStatusBar();
		this.updateDetailComposite();
	}

	private void updateCachedDataModelContent() {
		if (this.model.getFilter() == Filter.NONE) {
			this.cachedDataModelContent = this.dataModel
					.getRefinedDatabasePreparedStatementCalls(this.model
							.getRegExpr());
		} else {
			// TODO Failed Operations
			// this.cachedDataModelContent =
			// this.dataModel.getFailedOperationCalls(this.model.getRegExpr());
			this.cachedDataModelContent = this.dataModel
					.getRefinedDatabasePreparedStatementCalls(this.model
							.getRegExpr());
		}
	}

	/*
	 * Detailed Panel (bottom of the window)
	 */
	private void updateDetailComposite() {
		final PreparedStatementCall call = this.model
				.getPreparedStatementCall();

		if (call != null) {
			final String shortTimeUnit = NameConverter
					.toShortTimeUnit(this.propertiesModel.getTimeUnit());
			final long duration = this.propertiesModel.getTimeUnit().convert(
					call.getDuration(), this.dataModel.getTimeUnit());
			final String durationString = duration + " " + shortTimeUnit;

			this.lblMinimalDurationDisplay.setText(durationString);
			this.lblOperationDisplay.setText(call.getOperation());

			// abstract and concrete statements
			// customizes the SQL-Statement for visualization purposes
			String abstractStatement = call.getAbstractStatement();
			abstractStatement = Utils.formatSQLStatement(abstractStatement);
			String concreteStatement = call.getConcreteStatement();
			concreteStatement = Utils.formatSQLStatement(concreteStatement);

			this.lblAbstractStatementDisplay.setText(abstractStatement);
			this.lblConcreteStatementDisplay.setText(concreteStatement);

			this.lblReturnValueDisplay.setText(call.getFormattedReturnValue());

			if (call.isFailed()) {
				this.lblFailedDisplay.setText("Yes (" + call.getFailedCause()
						+ ")");
				this.lblFailedDisplay.setForeground(Display.getCurrent()
						.getSystemColor(SWT.COLOR_RED));
				this.lblFailed.setForeground(Display.getCurrent()
						.getSystemColor(SWT.COLOR_RED));
			} else {
				this.lblFailedDisplay.setText("No");
				this.lblFailedDisplay.setForeground(Display.getCurrent()
						.getSystemColor(SWT.COLOR_BLACK));
				this.lblFailed.setForeground(Display.getCurrent()
						.getSystemColor(SWT.COLOR_BLACK));
			}
		}

		this.detailComposite.layout();
		this.ivSc.setMinSize(this.detailComposite.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));
	}

	private void updateStatusBar() {
		this.lbCounter
				.setText(this.cachedDataModelContent.size()
						+ " "
						+ DatabasePreparedStatementCallsView.BUNDLE
								.getString("DatabasePreparedStatementCallsView.lbCounter.text"));
		this.statusBar.getParent().layout();
	}

	@Override
	public Composite getComposite() {
		return this.composite;
	}

	private void updateTree() {
		this.tree.setData(this.cachedDataModelContent);
		this.tree.setItemCount(Math.min(this.cachedDataModelContent.size(),
				this.propertiesModel.getMaxTracesToShow()));

		this.clearTree();
	}

	private void clearTree() {
		this.tree.clearAll(true);

		for (final TreeColumn column : this.tree.getColumns()) {
			column.pack();
		}
	}

	public Text getFilterText() {
		return this.filterText;
	}

	public Button getBtnShowAll() {
		return this.btnShowAll;
	}

	public Button getBtnShowJustFailed() {
		return this.btnShowJustFailed;
	}

	public Tree getTree() {
		return this.tree;
	}

	/**
	 * @author Nils Christian Ehmke
	 * @author Christian Zirkelbach
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
			PreparedStatementCall call;

			if (parent == null) {
				call = ((List<PreparedStatementCall>) tree.getData())
						.get(tableIndex);
			} else {
				call = ((PreparedStatementCall) parent.getData()).getChildren()
						.get(tableIndex);
			}

			String statement = new String();
			String returnValue = call.getFormattedReturnValue();
			String abstractStatement = call.getAbstractStatement();
			String concreteStatement = call.getConcreteStatement();

			final TimeUnit sourceTimeUnit = DatabasePreparedStatementCallsView.this.dataModel
					.getTimeUnit();
			final TimeUnit targetTimeUnit = DatabasePreparedStatementCallsView.this.propertiesModel
					.getTimeUnit();
			final String shortTimeUnit = NameConverter
					.toShortTimeUnit(targetTimeUnit);

			final String duration = targetTimeUnit.convert(call.getDuration(),
					sourceTimeUnit) + " " + shortTimeUnit;
		    
			if (parent == null) {
				statement = abstractStatement;
				item.setForeground(tree.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
				item.setText(new String[] { statement });
			} else {
				statement = concreteStatement;
				item.setText(new String[] { statement, returnValue,
						duration, Long.toString(call.getTraceID()),
						Long.toString(call.getTimestamp()) });
			}

			if (call.isFailed()) {
				final Color colorRed = Display.getCurrent().getSystemColor(
						SWT.COLOR_RED);
				item.setForeground(colorRed);
			}
			item.setData(call);
			item.setItemCount(call.getChildren().size());
		}
	}
}
