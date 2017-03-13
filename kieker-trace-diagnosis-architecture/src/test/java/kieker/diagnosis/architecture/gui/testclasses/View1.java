package kieker.diagnosis.architecture.gui.testclasses;

import kieker.diagnosis.architecture.gui.AbstractView;
import kieker.diagnosis.architecture.gui.AutowiredElement;

import javafx.scene.Node;

public class View1 extends AbstractView {

	@AutowiredElement
	private Node node1;

	private Node node2;

	public Node getNode1( ) {
		return node1;
	}

	public Node getNode2( ) {
		return node2;
	}

}
