package kieker.diagnosis.frontend.test;

import org.testfx.api.FxRobot;
import org.testfx.service.query.NodeQuery;

import javafx.scene.control.TextInputControl;

public final class TextField {

	private final FxRobot fxRobot;
	private final String locator;

	public TextField( final FxRobot fxRobot, final String locator ) {
		this.fxRobot = fxRobot;
		this.locator = locator;
	}

	public String getText( ) {
		final NodeQuery query = fxRobot.lookup( locator );
		final TextInputControl textInputControl = query.queryTextInputControl( );
		return textInputControl.getText( );
	}

}
