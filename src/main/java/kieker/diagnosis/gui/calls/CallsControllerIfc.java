package kieker.diagnosis.gui.calls;

import java.io.IOException;

import javafx.scene.input.InputEvent;

public interface CallsControllerIfc {

	void selectCall( InputEvent aEvent );

	void useFilter( );

	void exportToCSV( ) throws IOException;

	void saveAsFavorite( );

}