package kieker.diagnosis.test.architecture.ui;

import org.testfx.api.FxRobotInterface;

public class MenuItem extends Component {

	public MenuItem(final FxRobotInterface aRobot, final String aSelectorId) {
		super(aRobot, aSelectorId);
	}

	public void click() {
		getRobot().clickOn(getNode());
	}

}
