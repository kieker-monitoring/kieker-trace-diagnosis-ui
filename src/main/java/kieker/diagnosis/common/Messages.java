package kieker.diagnosis.common;

import java.beans.Beans;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class Messages {

	private static final String BUNDLE_NAME = "kieker.diagnosis.common.messages"; //$NON-NLS-1$
	private static final ResourceBundle RESOURCE_BUNDLE = loadBundle();

	// //////////////////////////////////////////////////////////////////////////
	//
	// Constructor
	//
	// //////////////////////////////////////////////////////////////////////////
	private Messages() {
		// do not instantiate
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// Bundle access
	//
	// //////////////////////////////////////////////////////////////////////////

	private static ResourceBundle loadBundle() {
		return ResourceBundle.getBundle(BUNDLE_NAME);
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// Strings access
	//
	// //////////////////////////////////////////////////////////////////////////
	public static String getString(final String key) {
		try {
			final ResourceBundle bundle = Beans.isDesignTime() ? loadBundle() : RESOURCE_BUNDLE;
			return bundle.getString(key);
		} catch (final MissingResourceException e) {
			return "!" + key + "!";
		}
	}
}
