package kieker.diagnosis.mainview.subview.database.statements.aggregated;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import kieker.diagnosis.czi.Utils;
import kieker.diagnosis.domain.AggregatedDatabaseOperationCall;
import kieker.diagnosis.mainview.subview.ISubView;
import kieker.diagnosis.mainview.subview.util.CallTableColumnSortListener;
import kieker.diagnosis.mainview.subview.util.NameConverter;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
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

/**
 * @author Christian Zirkelbach
 */
@Component
public final class AggregatedDatabaseStatementCallsView implements ISubView,
		Observer {

	private static final ResourceBundle BUNDLE = ResourceBundle
			.getBundle("kieker.diagnosis.mainview.subview.database.statements.aggregated.aggregateddatabasestatementcallsView"); //$NON-NLS-1$

	private static final String N_A = "N/A";

	@Autowired
	private AggregatedDatabaseStatementCallsViewModel model;

	@Autowired
	private DataModel dataModel;

	@Autowired
	private PropertiesModel propertiesModel;

	@Autowired
	private AggregatedDatabaseStatementCallsViewController controller;

	private List<AggregatedDatabaseOperationCall> cachedDataModelContent;

	private Composite composite;
	private Composite detailComposite;
	private Text lblOperationDisplay;
	private Text lblStatementDisplay;
	private Text lblNumberOfCallsDisplay;
	private Text lblTotalDurationDisplay;
	private Text lblAverageDurationDisplay;
	private Text lblMinimalDurationDisplay;
	private Text lblMaximalDurationDisplay;
	
	private Composite statusBar;
	private Label lblCounter;
	private Table table;
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

		this.filterText = new Text(filterComposite, SWT.BORDER);
		this.filterText
				.setMessage(AggregatedDatabaseStatementCallsView.BUNDLE
						.getString("AggregatedDatabaseStatementCallsView.text.message")); //$NON-NLS-1$
		this.filterText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1));

		final SashForm sashForm = new SashForm(this.composite, SWT.VERTICAL);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));

		this.table = new Table(sashForm, SWT.BORDER | SWT.FULL_SELECTION
				| SWT.VIRTUAL);
		this.table.setHeaderVisible(true);
		this.table.setLinesVisible(true);

		final TableColumn tblclmnStatement = new TableColumn(this.table,
				SWT.NONE);
		tblclmnStatement.setWidth(400);
		tblclmnStatement
				.setText(AggregatedDatabaseStatementCallsView.BUNDLE
						.getString("AggregatedDatabaseStatementCallsView.tblclmnStatement.text")); //$NON-NLS-1$

		final TableColumn tblclmnNumberOfCalls = new TableColumn(this.table,
				SWT.NONE);
		tblclmnNumberOfCalls.setWidth(100);
		tblclmnNumberOfCalls
				.setText(AggregatedDatabaseStatementCallsView.BUNDLE
						.getString("AggregatedDatabaseStatementCallsView.tblclmnNumberOfCalls.text")); //$NON-NLS-1$

		final TableColumn tblclmnTotalDuration = new TableColumn(this.table,
				SWT.NONE);
		tblclmnTotalDuration.setWidth(100);
		tblclmnTotalDuration
				.setText(AggregatedDatabaseStatementCallsView.BUNDLE
						.getString("AggregatedDatabaseStatementCallsView.tblclmnTotalDuration.text")); //$NON-NLS-1$
		
		final TableColumn tblclmnAvgDuration = new TableColumn(this.table,
				SWT.NONE);
		tblclmnAvgDuration.setWidth(100);
		tblclmnAvgDuration
				.setText(AggregatedDatabaseStatementCallsView.BUNDLE
						.getString("AggregatedDatabaseStatementCallsView.tblclmnAverageDuration.text")); //$NON-NLS-1$

		final TableColumn tblclmnMinimalDuration = new TableColumn(this.table,
				SWT.NONE);
		tblclmnMinimalDuration.setWidth(100);
		tblclmnMinimalDuration
				.setText(AggregatedDatabaseStatementCallsView.BUNDLE
						.getString("AggregatedDatabaseStatementCallsView.tblclmnMinimalDuration.text")); //$NON-NLS-1$

		final TableColumn tblclmnMaximalDuration = new TableColumn(this.table,
				SWT.NONE);
		tblclmnMaximalDuration.setWidth(100);
		tblclmnMaximalDuration
				.setText(AggregatedDatabaseStatementCallsView.BUNDLE
						.getString("AggregatedDatabaseStatementCallsView.tblclmnMaximalDuration.text")); //$NON-NLS-1$

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
				.setText(AggregatedDatabaseStatementCallsView.BUNDLE
						.getString("AggregatedDatabaseStatementCallsView.lblOperation.text") + ":"); //$NON-NLS-1$

		this.lblOperationDisplay = new Text(this.detailComposite, SWT.READ_ONLY);
		this.lblOperationDisplay.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		this.lblOperationDisplay
				.setText(AggregatedDatabaseStatementCallsView.N_A);

		final Label lblStatement = new Label(this.detailComposite, SWT.NONE);
		lblStatement
				.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblStatement
				.setText(AggregatedDatabaseStatementCallsView.BUNDLE
						.getString("AggregatedDatabaseStatementCallsView.lblStatement.text") + ":"); //$NON-NLS-1$

		this.lblStatementDisplay = new Text(this.detailComposite, SWT.MULTI
				| SWT.WRAP | SWT.READ_ONLY | SWT.NONE);
		this.lblStatementDisplay.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		this.lblStatementDisplay
				.setText(AggregatedDatabaseStatementCallsView.N_A);

		final Label lblNumberOfCalls = new Label(this.detailComposite, SWT.NONE);
		lblNumberOfCalls.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		lblNumberOfCalls
				.setText(AggregatedDatabaseStatementCallsView.BUNDLE
						.getString("AggregatedDatabaseStatementCallsView.lblNumberOfCalls.text") + ":"); //$NON-NLS-1$

		this.lblNumberOfCallsDisplay = new Text(this.detailComposite,
				SWT.READ_ONLY);
		this.lblNumberOfCallsDisplay.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		this.lblNumberOfCallsDisplay
				.setText(AggregatedDatabaseStatementCallsView.N_A);

		final Label lblTotalDuration = new Label(this.detailComposite, SWT.NONE);
		lblTotalDuration.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		lblTotalDuration
				.setText(AggregatedDatabaseStatementCallsView.BUNDLE
						.getString("AggregatedDatabaseStatementCallsView.lblTotalDuration.text") + ":"); //$NON-NLS-1$

		this.lblTotalDurationDisplay = new Text(this.detailComposite,
				SWT.READ_ONLY);
		this.lblTotalDurationDisplay.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		this.lblTotalDurationDisplay
				.setText(AggregatedDatabaseStatementCallsView.N_A);

		final Label lblAverageDuration = new Label(this.detailComposite,
				SWT.NONE);
		lblAverageDuration.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		lblAverageDuration
				.setText(AggregatedDatabaseStatementCallsView.BUNDLE
						.getString("AggregatedDatabaseStatementCallsView.lblAverageDuration.text") + ":"); //$NON-NLS-1$

		this.lblAverageDurationDisplay = new Text(this.detailComposite,
				SWT.READ_ONLY);
		this.lblAverageDurationDisplay.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		this.lblAverageDurationDisplay
				.setText(AggregatedDatabaseStatementCallsView.N_A);

		final Label lblMinimalDuration = new Label(this.detailComposite,
				SWT.NONE);
		lblMinimalDuration.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		lblMinimalDuration
				.setText(AggregatedDatabaseStatementCallsView.BUNDLE
						.getString("AggregatedDatabaseStatementCallsView.lblMinimalDuration.text") + ":"); //$NON-NLS-1$

		this.lblMinimalDurationDisplay = new Text(this.detailComposite,
				SWT.READ_ONLY);
		this.lblMinimalDurationDisplay.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		this.lblMinimalDurationDisplay
				.setText(AggregatedDatabaseStatementCallsView.N_A);

		final Label lblMaximalDuration = new Label(this.detailComposite,
				SWT.NONE);
		lblMaximalDuration.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		lblMaximalDuration
				.setText(AggregatedDatabaseStatementCallsView.BUNDLE
						.getString("AggregatedDatabaseStatementCallsView.lblMaximalDuration.text")); //$NON-NLS-1$

		this.lblMaximalDurationDisplay = new Text(this.detailComposite,
				SWT.READ_ONLY);
		this.lblMaximalDurationDisplay.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		this.lblMaximalDurationDisplay
				.setText(AggregatedDatabaseStatementCallsView.N_A);

		sashForm.setWeights(new int[] { 2, 1 });

		this.statusBar = new Composite(this.composite, SWT.NONE);
		this.statusBar.setLayout(new GridLayout(1, false));

		this.lblCounter = new Label(this.statusBar, SWT.NONE);
		this.lblCounter
				.setText("0 " + AggregatedDatabaseStatementCallsView.BUNDLE.getString("AggregatedDatabaseStatementCallsView.lblCounter.text")); //$NON-NLS-1$

		this.table.addListener(SWT.SetData, new DataProvider());
		this.table.addSelectionListener(this.controller);

		tblclmnStatement
				.addSelectionListener(new CallTableColumnSortListener<AggregatedDatabaseOperationCall>(
						AggregatedDatabaseOperationCall::getStringClassArgs));
		tblclmnNumberOfCalls
				.addSelectionListener(new CallTableColumnSortListener<AggregatedDatabaseOperationCall>(
						AggregatedDatabaseOperationCall::getCalls));
		tblclmnTotalDuration
				.addSelectionListener(new CallTableColumnSortListener<AggregatedDatabaseOperationCall>(
						AggregatedDatabaseOperationCall::getTotalDuration));
		tblclmnAvgDuration
				.addSelectionListener(new CallTableColumnSortListener<AggregatedDatabaseOperationCall>(
						AggregatedDatabaseOperationCall::getAvgDuration));
		tblclmnMinimalDuration
				.addSelectionListener(new CallTableColumnSortListener<AggregatedDatabaseOperationCall>(
						AggregatedDatabaseOperationCall::getMinDuration));
		tblclmnMaximalDuration
				.addSelectionListener(new CallTableColumnSortListener<AggregatedDatabaseOperationCall>(
						AggregatedDatabaseOperationCall::getMaxDuration));

		this.filterText.addTraverseListener(this.controller);
	}

	public Text getFilterText() {
		return this.filterText;
	}

	@Override
	public Composite getComposite() {
		return this.composite;
	}

	private void updateCachedDataModelContent() {
		this.cachedDataModelContent = this.dataModel
				.getAggregatedDatabaseStatementCalls(this.model.getRegExpr());
	}

	@Override
	public void update(final Observable observable, final Object obj) {
		if (observable == this.dataModel) {
			this.updateCachedDataModelContent();
			this.updateTable();
			this.updateStatusBar();
		}
		if (observable == this.propertiesModel) {
			this.clearTable();
		}
	}

	public void notifyAboutChangedFilter() {
		this.updateCachedDataModelContent();
		this.updateTable();
		this.updateStatusBar();
		this.updateDetailComposite();
	}

	public void notifyAboutChangedOperationCall() {
		this.updateDetailComposite();
	}

	public void notifyAboutChangedRegExpr() {
		this.updateCachedDataModelContent();
		this.updateTable();
		this.updateStatusBar();
		this.updateDetailComposite();
	}

	private void updateStatusBar() {
		this.lblCounter
				.setText(this.cachedDataModelContent.size()
						+ " "
						+ AggregatedDatabaseStatementCallsView.BUNDLE
								.getString("AggregatedDatabaseStatementCallsView.lblCounter.text"));
		this.statusBar.getParent().layout();
	}

	private void updateTable() {
		this.table.setData(this.cachedDataModelContent);
		this.table.setItemCount(this.cachedDataModelContent.size());
		this.clearTable();
	}

	private void clearTable() {
		this.table.clearAll();
	}

	private void updateDetailComposite() {
		final AggregatedDatabaseOperationCall call = this.model
				.getDatabaseOperationCall();

		if (call != null) {
			final String shortTimeUnit = NameConverter
					.toShortTimeUnit(this.propertiesModel.getTimeUnit());
			final String totalDuration = this.propertiesModel.getTimeUnit()
					.convert(call.getTotalDuration(),
							this.dataModel.getTimeUnit())
					+ " " + shortTimeUnit;
			final String avgDuration = this.propertiesModel.getTimeUnit()
					.convert(call.getAvgDuration(),
							this.dataModel.getTimeUnit())
					+ " " + shortTimeUnit;
			final String minDuration = this.propertiesModel.getTimeUnit()
					.convert(call.getMinDuration(),
							this.dataModel.getTimeUnit())
					+ " " + shortTimeUnit;
			final String maxDuration = this.propertiesModel.getTimeUnit()
					.convert(call.getMaxDuration(),
							this.dataModel.getTimeUnit())
					+ " " + shortTimeUnit;

			this.lblTotalDurationDisplay.setText(totalDuration);
			this.lblMinimalDurationDisplay.setText(minDuration);
			this.lblMaximalDurationDisplay.setText(maxDuration);
			this.lblAverageDurationDisplay.setText(avgDuration);

			this.lblOperationDisplay.setText(call.getOperation());

			// customizes the SQL-Statement for visualization purposes
			final String statementText = call.getStringClassArgs();
			final String formattedStatementText = Utils
					.formatSQLStatementForDetailComposite(statementText);

			// TODO colors KEYWORDS (SELECT, FROM, WHERE) ?
			this.lblStatementDisplay.setText(formattedStatementText);

			this.lblNumberOfCallsDisplay.setText(Integer.toString(call
					.getCalls()));
		}
		this.detailComposite.layout();
		this.ivSc.setMinSize(this.detailComposite.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));
	}

	/**
	 * @author Christian Zirkelbach
	 */
	private class DataProvider implements Listener {

		@Override
		@SuppressWarnings("unchecked")
		public void handleEvent(final Event event) {
			// Get the necessary information from the event
			final Table table = (Table) event.widget;
			final TableItem item = (TableItem) event.item;
			final int tableIndex = event.index;

			// Get the data for the current row
			final List<AggregatedDatabaseOperationCall> calls = (List<AggregatedDatabaseOperationCall>) table
					.getData();
			final AggregatedDatabaseOperationCall call = calls.get(tableIndex);

			// customizes the SQL-Statement for visualization purposes
			final String statementText = call.getStringClassArgs();
			final String formattedStatementText = Utils
					.formatSQLStatementForTable(statementText);

			final TimeUnit sourceTimeUnit = AggregatedDatabaseStatementCallsView.this.dataModel
					.getTimeUnit();
			final TimeUnit targetTimeUnit = AggregatedDatabaseStatementCallsView.this.propertiesModel
					.getTimeUnit();
			final String shortTimeUnit = NameConverter
					.toShortTimeUnit(targetTimeUnit);

			final String minDuration = targetTimeUnit.convert(
					call.getMinDuration(), sourceTimeUnit)
					+ " " + shortTimeUnit;
			final String maxDuration = targetTimeUnit.convert(
					call.getMaxDuration(), sourceTimeUnit)
					+ " " + shortTimeUnit;
			final String avgDuration = targetTimeUnit.convert(
					call.getAvgDuration(), sourceTimeUnit)
					+ " " + shortTimeUnit;
			final String totalDuration = targetTimeUnit.convert(
					call.getTotalDuration(), sourceTimeUnit)
					+ " " + shortTimeUnit;

			item.setText(new String[] { formattedStatementText,
					Long.toString(call.getCalls()), totalDuration, avgDuration,
					minDuration, maxDuration});

			item.setData(call);
		}
	}
}
