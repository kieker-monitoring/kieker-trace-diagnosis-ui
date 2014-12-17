package kieker.gui.subview.traces;

import kieker.gui.common.domain.Execution;
import kieker.gui.common.model.DataModel;
import kieker.gui.common.model.PropertiesModel;
import kieker.gui.subview.ISubController;
import kieker.gui.subview.ISubView;
import kieker.gui.subview.util.IModel;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public abstract class AbstractController implements ISubController, SelectionListener {

	private final ISubView view;
	private final Model model;

	public AbstractController(final DataModel dataModel, final PropertiesModel propertiesModel) {
		final IModel<Execution> modelProxy = this.createModelProxy(dataModel);
		this.model = new Model();

		this.view = new View(modelProxy, this.model, propertiesModel, this);
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
