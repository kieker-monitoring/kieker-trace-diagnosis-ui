package kieker.diagnosis.gui;

import java.util.ResourceBundle;

public abstract class AbstractView {

	private ResourceBundle ivResourceBundle;

	public ResourceBundle getResourceBundle( ) {
		return ivResourceBundle;
	}

	public void setResourceBundle( final ResourceBundle aResourceBundle ) {
		ivResourceBundle = aResourceBundle;
	}

}
