package kieker.diagnosis.service.properties;

class MyFourthProperty implements SystemProperty<Integer> {

	@Override
	public String getKey( ) {
		return "myFourthProperty";
	}

	@Override
	public Integer deserialize( final String aString ) {
		return Integer.valueOf( aString );
	}

}
