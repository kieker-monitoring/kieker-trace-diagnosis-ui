package kieker.gui.controller;

import kieker.gui.model.DataModel;
import kieker.gui.model.PropertiesModel;
import kieker.gui.model.TracesSubViewModel;
import kieker.gui.model.domain.ExecutionEntry;
import kieker.gui.view.TracesSubView;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public class FailedTracesSubViewController implements SelectionListener {

	private final TracesSubViewModel tracesSubViewModel;
	private final TracesSubView view;

	public FailedTracesSubViewController(final DataModel model, final PropertiesModel propertiesModel) {
		this.tracesSubViewModel = new TracesSubViewModel();
		this.view = new TracesSubView(TracesSubView.Type.SHOW_JUST_FAILED_TRACES, model, this.tracesSubViewModel, propertiesModel, this);
	}

	public TracesSubView getView() {
		return this.view;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		if (e.item.getData() instanceof ExecutionEntry) {
			this.tracesSubViewModel.setCurrentActiveTrace((ExecutionEntry) e.item.getData());
		}
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent e) {}

}
