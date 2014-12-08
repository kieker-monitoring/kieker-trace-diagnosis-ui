package kieker.gui.view.util;

import java.util.Comparator;

public abstract class AbstractDirectedComparator<T> implements Comparator<T> {

	private int direction;

	public int getDirection() {
		return this.direction;
	}

	public void setDirection(final int direction) {
		this.direction = direction;
	}

}
