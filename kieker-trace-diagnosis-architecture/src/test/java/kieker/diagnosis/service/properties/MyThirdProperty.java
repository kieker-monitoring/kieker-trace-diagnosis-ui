package kieker.diagnosis.service.properties;

public class MyThirdProperty extends AbstractBooleanApplicationProperty {

	public MyThirdProperty( final int aParameter ) {
	}

	@Override
	public String getDefaultValue( ) {
		return serialize( Boolean.TRUE );
	}

	@Override
	public String getKey( ) {
		return "testProperty3";
	}

}
