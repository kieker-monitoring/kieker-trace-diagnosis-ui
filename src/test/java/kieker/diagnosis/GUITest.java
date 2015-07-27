package kieker.diagnosis;

import javafx.stage.Stage;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

public final class GUITest extends ApplicationTest {

	@Override
	public void start(final Stage stage) throws Exception {
		final Main main = new Main();
		main.start(stage);
	}

	@Test
	public void allButtonsShouldBeAvailable() {
		this.clickOn("#traces");
		this.clickOn("#aggregatedtraces");
		this.clickOn("#calls");
		this.clickOn("#aggregatedcalls");
		this.clickOn("#statistics");
	}

}
