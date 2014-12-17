package kieker.gui.subview.aggregatedtraces;

import kieker.gui.common.DataModel;
import kieker.gui.common.IModel;
import kieker.gui.common.ISubController;
import kieker.gui.common.ISubView;
import kieker.gui.common.PropertiesModel;
import kieker.gui.common.domain.AggregatedExecution;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public abstract class AbstractAggregatedTracesController implements ISubController, SelectionListener {

	private final ISubView view;
	private final AggregatedTracesSubViewModel model;

	public AbstractAggregatedTracesController(final DataModel dataModel, final PropertiesModel propertiesModel) {
		final IModel<AggregatedExecution> modelProxy = this.createModelProxy(dataModel);
		this.model = new AggregatedTracesSubViewModel();

		this.view = new AggregatedTracesSubView(modelProxy, this.model, propertiesModel, this);
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
