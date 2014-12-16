package kieker.gui.view;

import org.eclipse.swt.widgets.Composite;

public interface ISubView {

	public void createComposite(final Composite parent);

	public Composite getComposite();

}
