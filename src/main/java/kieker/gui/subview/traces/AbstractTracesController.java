package kieker.gui.subview.traces;

import kieker.gui.common.DataModel;
import kieker.gui.common.IModel;
import kieker.gui.common.ISubController;
import kieker.gui.common.ISubView;
import kieker.gui.common.PropertiesModel;
import kieker.gui.common.domain.Execution;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public abstract class AbstractTracesController implements ISubController, SelectionListener {

	private final ISubView view;
	private final TracesSubViewModel model;

	public AbstractTracesController(final DataModel dataModel, final PropertiesModel propertiesModel) {
		final IModel<Execution> modelProxy = this.createModelProxy(dataModel);
		this.model = new TracesSubViewModel();

		this.view = new TracesSubView(modelProxy, this.model, propertiesModel, this);
	}

	@Override
	public ISubView getView() {
		return this.view;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		if (e.item.getData() instanceof Execution) {
			this.model.setCurrentActiveTrace((Execution) e.item.getData());
		}
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent e) {}

	protected abstract IModel<Execution> createModelProxy(final DataModel dataModel);

}
