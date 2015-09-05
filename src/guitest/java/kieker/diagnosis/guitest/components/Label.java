package kieker.diagnosis.guitest.components;

import javafx.scene.Node;

import org.testfx.framework.junit.ApplicationTest;

public final class Label {

	private final String id;
	private final ApplicationTest applicationTest;

	public Label(final ApplicationTest applicationTest, final String id) {
		this.applicationTest = applicationTest;
		this.id = id;
	}

	public String getText() {
		final Node node = this.applicationTest.lookup(this.id).queryFirst();
		return ((javafx.scene.control.Label) node).getText();
	}
}
