package kieker.diagnosis.guitest.mainview.dialog;

import kieker.diagnosis.guitest.components.Button;

import org.testfx.framework.junit.ApplicationTest;

public final class SettingsDialog {

	private final ApplicationTest applicationTest;

	public SettingsDialog(final ApplicationTest applicationTest) {
		this.applicationTest = applicationTest;
	}

	public Button getCancelButton() {
		return new Button(this.applicationTest, "#cancel");
	}

	public Button getOkayButton() {
		return new Button(this.applicationTest, "#okay");
	}

}
