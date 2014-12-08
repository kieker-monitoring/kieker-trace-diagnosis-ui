package kieker.gui.view;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.widgets.Tree;

/**
 * An observer clearing an instance of {@link Tree}.
 *
 * @author Nils Christian Ehmke
 */
public final class ClearTreeObserver implements Observer {

	private final Tree tree;

	public ClearTreeObserver(final Tree tree) {
		this.tree = tree;
	}

	@Override
	public void update(final Observable observable, final Object obj) {
		this.tree.clearAll(true);
	}

}
