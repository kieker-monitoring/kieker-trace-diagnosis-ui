package kieker.gui.model;

import java.util.Observable;

public class Properties extends Observable {

	private static final Properties INSTANCE = new Properties();
	private boolean shortComponentNames = false;
	private boolean shortOperationParameters = false;

	private Properties() {}

	public static Properties getInstance() {
		return Properties.INSTANCE;
	}

	public boolean isShortComponentNames() {
		return this.shortComponentNames;
	}

	public void setShortComponentNames(final boolean shortComponentNames) {
		this.shortComponentNames = shortComponentNames;

		this.setChanged();
		this.notifyObservers();
	}

	public boolean isShortOperationParameters() {
		return this.shortOperationParameters;
	}

	public void setShortOperationParameters(final boolean shortOperationParameters) {
		this.shortOperationParameters = shortOperationParameters;

		this.setChanged();
		this.notifyObservers();
	}

}
