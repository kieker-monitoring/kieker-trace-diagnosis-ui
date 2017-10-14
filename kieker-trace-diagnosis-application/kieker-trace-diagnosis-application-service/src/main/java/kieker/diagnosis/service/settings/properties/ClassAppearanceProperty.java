package kieker.diagnosis.service.settings.properties;

import com.google.inject.Singleton;

import kieker.diagnosis.architecture.service.properties.EnumApplicationProperty;
import kieker.diagnosis.service.settings.ClassAppearance;

@Singleton
public class ClassAppearanceProperty extends EnumApplicationProperty<ClassAppearance> {

	public ClassAppearanceProperty( ) {
		super( ClassAppearance.class );
	}

	@Override
	public ClassAppearance getDefaultValue( ) {
		return ClassAppearance.LONG;
	}

	@Override
	public String getKey( ) {
		// If someone used an older version of the application
		return "components";
	}

}