package kieker.diagnosis.gui.bugreporting;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Window;
import kieker.diagnosis.gui.AbstractView;

public class BugReportingDialogView extends AbstractView {

	private Node ivView;

	public Node getView( ) {
		return ivView;
	}

	public Window getStage( ) {
		final Scene scene = ivView.getScene( );
		return scene.getWindow( );
	}

}
