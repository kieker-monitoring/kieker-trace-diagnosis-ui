package kieker.diagnosis.test.ui.main;

import org.testfx.api.FxRobotInterface;

public class MainView {

	private final FileMenu ivFileMenu;

	public MainView( final FxRobotInterface aRobot ) {
		ivFileMenu = new FileMenu( aRobot );
	}

	public FileMenu getFileMenu( ) {
		return ivFileMenu;
	}

}
