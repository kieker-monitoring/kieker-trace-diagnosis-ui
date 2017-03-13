package kieker.diagnosis.architecture.service.properties.testclasses;

import kieker.diagnosis.architecture.service.properties.AbstractEnumSystemProperty;

import java.util.concurrent.TimeUnit;

public class EnumSystemProperty extends AbstractEnumSystemProperty<TimeUnit> {

	public EnumSystemProperty( ) {
		super( TimeUnit.class );
	}

	@Override
	public String getKey( ) {
		return null;
	}

}
