package kieker.diagnosis.test.ui.main;

import org.testfx.api.FxRobotInterface;

import kieker.diagnosis.test.architecture.ui.Menu;
import kieker.diagnosis.test.architecture.ui.MenuItem;

public class FileMenu extends Menu {

	private MenuItem ivCloseMenuItem;

	public FileMenu( final FxRobotInterface aRobot ) {
		super( aRobot, "#file" );

		ivCloseMenuItem = new MenuItem( aRobot, "#close" );
	}

	public MenuItem getCloseMenuItem( ) {
		return ivCloseMenuItem;
	}

}
