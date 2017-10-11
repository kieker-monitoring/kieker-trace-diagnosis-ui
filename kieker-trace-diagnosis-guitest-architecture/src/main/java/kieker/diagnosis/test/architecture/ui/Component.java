package kieker.diagnosis.test.architecture.ui;

import org.testfx.api.FxRobotInterface;

import javafx.scene.Node;

public abstract class Component {

	private final FxRobotInterface ivRobot;
	private final String ivSelectorID;

	public Component(final FxRobotInterface aRobot, final String aSelectorId) {
		ivRobot = aRobot;
		ivSelectorID = aSelectorId;
	}

	protected final Node getNode() {
		return ivRobot.lookup(ivSelectorID).queryFirst();
	}
	
	protected final FxRobotInterface getRobot() {
		return ivRobot;
	}

}
