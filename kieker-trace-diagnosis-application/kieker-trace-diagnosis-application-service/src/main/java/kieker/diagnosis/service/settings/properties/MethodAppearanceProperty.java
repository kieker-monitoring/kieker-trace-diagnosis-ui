package kieker.diagnosis.service.settings.properties;

import com.google.inject.Singleton;

import kieker.diagnosis.architecture.service.properties.EnumApplicationProperty;
import kieker.diagnosis.service.settings.MethodAppearance;

@Singleton
public class MethodAppearanceProperty extends EnumApplicationProperty<MethodAppearance> {

	public MethodAppearanceProperty( ) {
		super( MethodAppearance.class );
	}

	@Override
	public MethodAppearance getDefaultValue( ) {
		return MethodAppearance.SHORT;
	}

	@Override
	public String getKey( ) {
		// If someone used an older version of the application
		return "operations";
	}

}