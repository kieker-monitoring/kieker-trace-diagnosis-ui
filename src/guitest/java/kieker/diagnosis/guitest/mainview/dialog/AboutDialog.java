package kieker.diagnosis.guitest.mainview.dialog;

import kieker.diagnosis.guitest.components.Button;
import kieker.diagnosis.guitest.components.Label;

import org.testfx.framework.junit.ApplicationTest;

public final class AboutDialog {

	private final ApplicationTest applicationTest;

	public AboutDialog(final ApplicationTest applicationTest) {
		this.applicationTest = applicationTest;
	}

	public Button getOkayButton() {
		return new Button(this.applicationTest, "#okay");
	}

	public Label getDescriptionLabel() {
		return new Label(this.applicationTest, "#description");
	}

}
