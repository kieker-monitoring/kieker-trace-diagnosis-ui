package kieker.diagnosis.architecture.gui.testclasses;

import kieker.diagnosis.architecture.gui.AbstractView;
import kieker.diagnosis.architecture.gui.AutowiredElement;

import javafx.scene.Node;

public class View2 extends AbstractView {

	@AutowiredElement
	private Node node3;

	public Node getNode3( ) {
		return node3;
	}

}
