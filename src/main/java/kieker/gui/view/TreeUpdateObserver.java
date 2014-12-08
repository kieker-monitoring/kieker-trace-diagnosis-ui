package kieker.gui.view;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.widgets.Tree;

class TreeUpdateObserver implements Observer {

	private final Tree tree;

	public TreeUpdateObserver(final Tree tree) {
		this.tree = tree;
	}

	@Override
	public void update(final Observable observable, final Object obj) {
		this.tree.clearAll(true);
	}

}