package kieker.gui.controller;

import kieker.gui.model.DataModel;
import kieker.gui.view.RecordsSubView;

public class RecordsSubViewController {

	private final DataModel model;
	private final RecordsSubView view;

	public RecordsSubViewController(final DataModel model) {
		this.model = model;
		this.view = new RecordsSubView(this.model, this);
	}

	public RecordsSubView getView() {
		return this.view;
	}

}
