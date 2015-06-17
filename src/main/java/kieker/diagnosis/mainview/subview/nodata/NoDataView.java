package kieker.diagnosis.mainview.subview.nodata;

import java.util.Observable;
import java.util.Observer;

import javax.annotation.PostConstruct;

import kieker.diagnosis.mainview.subview.ISubView;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * View serves as a placeholder for non-existing data
 * 
 * @author Christian Zirkelbach
 */
@Component
public final class NoDataView implements ISubView, Observer {

	@Autowired
	private PropertiesModel propertiesModel;
	@Autowired
	private DataModel dataModel;

	private Composite composite;

	@PostConstruct
	public void initialize() {
		this.dataModel.addObserver(this);
		this.propertiesModel.addObserver(this);
	}

	@Override
	public void update(final Observable observable, final Object o) {
		this.composite.layout();
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void createComposite(final Composite parent) {
		if (this.composite != null) {
			this.composite.dispose();
		}

		this.composite = new Composite(parent, SWT.NONE);
		this.composite.setLayout(new GridLayout(1, true));

		final Label lblInfoText = new Label(composite, SWT.CENTER);
		lblInfoText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		lblInfoText
				.setText("\nNo available data found! Please load another monitoring log or use another view!");
	}

	@Override
	public Composite getComposite() {
		return this.composite;
	}
}
