package kieker.gui.controller;

import kieker.gui.model.AggregatedTracesSubViewModel;
import kieker.gui.model.DataModel;
import kieker.gui.model.PropertiesModel;
import kieker.gui.model.domain.AggregatedExecutionEntry;
import kieker.gui.view.AggregatedTracesSubView;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public class AggregatedTracesSubViewController implements SelectionListener {

	private final DataModel model;
	private final AggregatedTracesSubView view;
	private final AggregatedTracesSubViewModel aggregatedTracesSubViewModel;

	public AggregatedTracesSubViewController(final DataModel model, final PropertiesModel propertiesModel) {
		this.model = model;
		this.aggregatedTracesSubViewModel = new AggregatedTracesSubViewModel();

		this.view = new AggregatedTracesSubView(this.model, this.aggregatedTracesSubViewModel, propertiesModel, this);
	}

	public AggregatedTracesSubView getView() {
		return this.view;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		if (e.item.getData() instanceof AggregatedExecutionEntry) {
			this.aggregatedTracesSubViewModel.setCurrentActiveTrace((AggregatedExecutionEntry) e.item.getData());
		}
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent e) {}

}
