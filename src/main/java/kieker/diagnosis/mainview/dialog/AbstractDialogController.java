package kieker.diagnosis.mainview.dialog;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

public abstract class AbstractDialogController {

	@FXML private Node view;

	public final void closeDialog() {
		final Scene scene = this.getView().getScene();
		final Window window = scene.getWindow();
		if (window instanceof Stage) {
			((Stage) window).close();
		}
	}

	protected final Node getView() {
		return this.view;
	}

}
