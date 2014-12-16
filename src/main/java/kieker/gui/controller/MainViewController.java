package kieker.gui.controller;

import kieker.gui.model.DataModel;
import kieker.gui.model.MainViewModel;
import kieker.gui.model.MainViewModel.SubView;
import kieker.gui.model.PropertiesModel;
import kieker.gui.view.AggregatedTracesSubView;
import kieker.gui.view.MainView;
import kieker.gui.view.RecordsSubView;
import kieker.gui.view.TracesSubView;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public class MainViewController implements SelectionListener {

	private final DataModel dataModel;
	private final MainViewModel mainViewModel;
	private final PropertiesModel propertiesModel;
	private final MainView view;

	public MainViewController() {
		this.dataModel = new DataModel();
		this.mainViewModel = new MainViewModel();
		this.propertiesModel = new PropertiesModel();

		final RecordsSubViewController subView1Controller = new RecordsSubViewController(this.dataModel);
		final TracesSubViewController subView2Controller = new TracesSubViewController(this.dataModel, this.propertiesModel);
		final AggregatedTracesSubViewController subView3Controller = new AggregatedTracesSubViewController(this.dataModel, this.propertiesModel);
		final FailedTracesSubViewController subView4Controller = new FailedTracesSubViewController(this.dataModel, this.propertiesModel);
		final FailureContainingTracesSubViewController subView5Controller = new FailureContainingTracesSubViewController(this.dataModel, this.propertiesModel);

		final RecordsSubView subView1 = subView1Controller.getView();
		final TracesSubView subView2 = subView2Controller.getView();
		final AggregatedTracesSubView subView3 = subView3Controller.getView();
		final TracesSubView subView4 = subView4Controller.getView();
		final TracesSubView subView5 = subView5Controller.getView();

		this.view = new MainView(this.dataModel, this.mainViewModel, this, subView1, subView2, subView4, subView3, subView5);
	}

	public void showView() {
		this.view.show();
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		if (e.item == this.view.getTrtmExplorer()) {
			this.mainViewModel.setCurrentActiveSubView(SubView.NONE);
		}
		if (e.item == this.view.getTrtmRecords()) {
			this.mainViewModel.setCurrentActiveSubView(SubView.RECORDS_SUB_VIEW);
		}
		if (e.item == this.view.getTrtmTraces()) {
			this.mainViewModel.setCurrentActiveSubView(SubView.TRACES_SUB_VIEW);
		}
		if (e.item == this.view.getTrtmAggregatedTraces()) {
			this.mainViewModel.setCurrentActiveSubView(SubView.AGGREGATED_TRACES_SUB_VIEW);
		}
		if (e.item == this.view.getTrtmJustFailedTraces()) {
			this.mainViewModel.setCurrentActiveSubView(SubView.FAILED_TRACES_SUB_VIEW);
		}
		if (e.item == this.view.getTrtmJustTracesContaining()) {
			this.mainViewModel.setCurrentActiveSubView(SubView.FAILURE_CONTAINING_TRACES_SUB_VIEW);
		}

		if (e.widget == this.view.getMntmOpenMonitoringLog()) {
			final String selectedDirectory = this.view.getDialog().open();

			if (null != selectedDirectory) {
				this.dataModel.loadMonitoringLogFromFS(selectedDirectory);
			}
		}
		if (e.widget == this.view.getMntmExit()) {
			this.view.close();
		}

		if (e.widget == this.view.getMntmShortOperationNames()) {
			this.propertiesModel.setShortOperationNames(true);
		}
		if (e.widget == this.view.getMntmLongOperationNames()) {
			this.propertiesModel.setShortOperationNames(false);
		}
		if (e.widget == this.view.getMntmShortComponentNames()) {
			this.propertiesModel.setShortComponentNames(true);
		}
		if (e.widget == this.view.getMntmLongComponentNames()) {
			this.propertiesModel.setShortComponentNames(false);
		}
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent e) {

	}

}
