package kieker.diagnosis.application.gui.main;

import kieker.diagnosis.architecture.gui.AbstractView;
import kieker.diagnosis.architecture.gui.AutowiredElement;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import org.springframework.stereotype.Component;

@Component
class MainView extends AbstractView {

	@AutowiredElement
	private Node ivView;
	@AutowiredElement
	private AnchorPane ivContent;
	@AutowiredElement
	private VBox ivLeftButtonBox;

	@AutowiredElement
	private Button ivTraces;
	@AutowiredElement
	private Button ivAggregatedtraces;
	@AutowiredElement
	private Button ivCalls;
	@AutowiredElement
	private Button ivAggregatedcalls;
	@AutowiredElement
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
