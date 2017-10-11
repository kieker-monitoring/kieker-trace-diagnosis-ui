package kieker.diagnosis.architecture.service.properties;

import com.google.inject.Singleton;

@Singleton
public class DevelopmentModeProperty extends BooleanSystemProperty {

	@Override
	public String getKey( ) {
		return "developmentMode";
	}

}
