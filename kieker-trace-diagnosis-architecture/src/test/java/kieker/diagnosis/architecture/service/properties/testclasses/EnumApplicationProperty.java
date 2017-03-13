package kieker.diagnosis.architecture.service.properties.testclasses;

import kieker.diagnosis.architecture.service.properties.AbstractEnumApplicationProperty;

import java.util.concurrent.TimeUnit;

public final class EnumApplicationProperty extends AbstractEnumApplicationProperty<TimeUnit> {

	public EnumApplicationProperty( ) {
		super( TimeUnit.class );
	}

	@Override
	public String getKey( ) {
		return null;
	}

	@Override
	public TimeUnit getDefaultValue( ) {
		return null;
	}

}
