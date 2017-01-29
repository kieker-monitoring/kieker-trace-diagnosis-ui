package kieker.diagnosis.gui.aggregatedcalls;

import java.io.IOException;

import javafx.scene.input.InputEvent;

public interface AggregatedCallsControllerIfc {

	void selectCall( InputEvent aEvent ) throws Exception;

	void useFilter( );

	void exportToCSV( ) throws IOException;

	void saveAsFavorite( );

}