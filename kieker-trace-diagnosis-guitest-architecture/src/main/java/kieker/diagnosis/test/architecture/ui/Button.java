package kieker.diagnosis.test.architecture.ui;

import org.testfx.api.FxRobotInterface;

public class Button extends Component {

	public Button( final FxRobotInterface aRobot, final String aSelectorId ) {
		super( aRobot, aSelectorId );
	}

	public void click( ) {
		getRobot( ).clickOn( getNode( ) );
	}

}
