package kieker.gui.subview.util;

import java.util.Observable;
import java.util.Observer;

import kieker.gui.common.model.DataModel;

public abstract class AbstractDataModelProxy<T> extends Observable implements IModel<T>, Observer {

	protected final DataModel dataModel;

	public AbstractDataModelProxy(final DataModel dataModel) {
		this.dataModel = dataModel;
		this.dataModel.addObserver(this);
	}

	@Override
	public final void update(final Observable o, final Object arg) {
		this.setChanged();
		this.notifyObservers(arg);
	}

	@Override
	public final String getShortTimeUnit() {
		return this.dataModel.getShortTimeUnit();
	}

}
