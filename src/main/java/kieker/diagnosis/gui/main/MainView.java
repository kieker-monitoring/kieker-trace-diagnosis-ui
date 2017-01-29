package kieker.diagnosis.gui.main;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import kieker.diagnosis.gui.AbstractView;

public class MainView extends AbstractView {

	private Node ivView;
	private AnchorPane ivContent;
	private VBox ivLeftButtonBox;

	private Button ivTraces;
	private Button ivAggregatedtraces;
	private Button ivCalls;
	private Button ivAggregatedcalls;
	private Button ivStatistics;

	public Node getView( ) {
		return ivView;
	}

	public AnchorPane getContent( ) {
		return ivContent;
	}

	public VBox getLeftButtonBox( ) {
		return ivLeftButtonBox;
	}

	public Button getTraces( ) {
		return ivTraces;
	}

	public Button getAggregatedtraces( ) {
		return ivAggregatedtraces;
	}

	public Button getCalls( ) {
		return ivCalls;
	}

	public Button getAggregatedcalls( ) {
		return ivAggregatedcalls;
	}

	public Button getStatistics( ) {
		return ivStatistics;
	}

	public Window getWindow( ) {
		final Scene scene = ivContent.getScene( );
		return scene.getWindow( );
	}

}
