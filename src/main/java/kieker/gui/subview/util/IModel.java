package kieker.gui.subview.util;

import java.util.List;
import java.util.Observer;

public interface IModel<T> {

	public List<T> getContent();

	public String getShortTimeUnit();

	public void addObserver(Observer observer);

}
