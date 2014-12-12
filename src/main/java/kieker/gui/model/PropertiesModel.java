package kieker.gui.model;

import java.util.Observable;

public final class PropertiesModel extends Observable {

	private boolean shortComponentNames = false;
	private boolean shortOperationNames = true;

	public boolean isShortComponentNames() {
		return this.shortComponentNames;
	}

	public void setShortComponentNames(final boolean shortComponentNames) {
		this.shortComponentNames = shortComponentNames;

		this.setChanged();
		this.notifyObservers();
	}

	public boolean isShortOperationNames() {
		return this.shortOperationNames;
	}

	public void setShortOperationNames(final boolean shortOperationNames) {
		this.shortOperationNames = shortOperationNames;

		this.setChanged();
		this.notifyObservers();
	}

}
