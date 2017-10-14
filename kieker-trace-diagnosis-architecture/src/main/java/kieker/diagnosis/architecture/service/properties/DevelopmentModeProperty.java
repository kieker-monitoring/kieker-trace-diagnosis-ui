package kieker.diagnosis.architecture.service.properties;

import com.google.inject.Singleton;

/**
 * This system property determines whether the application is executed in development or not.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class DevelopmentModeProperty extends BooleanSystemProperty {

	@Override
	public String getKey( ) {
		return "developmentMode";
	}

}
