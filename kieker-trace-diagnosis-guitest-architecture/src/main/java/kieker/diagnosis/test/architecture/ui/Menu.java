package kieker.diagnosis.test.architecture.ui;

import org.testfx.api.FxRobotInterface;

public class Menu extends Component {

	public Menu(final FxRobotInterface aRobot, final String aSelectorId) {
		super(aRobot, aSelectorId);
	}

	public void click() {
		getRobot().clickOn(getNode());
	}

}
