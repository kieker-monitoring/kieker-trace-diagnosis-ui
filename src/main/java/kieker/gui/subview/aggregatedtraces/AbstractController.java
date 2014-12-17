package kieker.gui.subview.aggregatedtraces;

import kieker.gui.common.IModel;
import kieker.gui.common.ISubController;
import kieker.gui.common.ISubView;
import kieker.gui.common.domain.AggregatedExecution;
import kieker.gui.common.model.DataModel;
import kieker.gui.common.model.PropertiesModel;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public abstract class AbstractController implements ISubController, SelectionListener {

	private final ISubView view;
	private final Model model;

	public AbstractController(final DataModel dataModel, final PropertiesModel propertiesModel) {
		final IModel<AggregatedExecution> modelProxy = this.createModelProxy(dataModel);
		this.model = new Model();

		this.view = new View(modelProxy, this.model, propertiesModel, this);
	}

	@Override
	public ISubView getView() {
		return this.view;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		if (e.item.getData() instanceof AggregatedExecution) {
			this.model.setCurrentActiveTrace((AggregatedExecution) e.item.getData());
		}
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent e) {}

	protected abstract IModel<AggregatedExecution> createModelProxy(final DataModel dataModel);

}
