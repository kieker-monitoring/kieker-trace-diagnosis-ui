package kieker.diagnosis.guitest.mainview.subview;

import kieker.diagnosis.guitest.components.TextField;

import org.testfx.framework.junit.ApplicationTest;

public final class CallsView {

	private final ApplicationTest applicationTest;

	public CallsView(final ApplicationTest applicationTest) {
		this.applicationTest = applicationTest;
	}

	public TextField getCounterTextField() {
		return new TextField(this.applicationTest, "#counter");
	}

}
