package kieker.diagnosis.gui.about;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Window;
import kieker.diagnosis.gui.AbstractView;
import kieker.diagnosis.gui.InjectComponent;

/**
 * The view for the about dialog. The about dialog shows some information about the application.
 *
 * @author Nils Christian Ehmke
 */
public class AboutDialogView extends AbstractView {

	@InjectComponent
	private Node ivView;

	public Node getView( ) {
		return ivView;
	}

	public Window getStage( ) {
		final Scene scene = ivView.getScene( );
		return scene.getWindow( );
	}

}
