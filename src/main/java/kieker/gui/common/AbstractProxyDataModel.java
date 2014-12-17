package kieker.gui.common;

import java.util.Observable;
import java.util.Observer;

public abstract class AbstractProxyDataModel<T> extends Observable implements IModel<T>, Observer {

	protected final DataModel dataModel;

	public AbstractProxyDataModel(final DataModel dataModel) {
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
