package kieker.diagnosis.service.settings;

/**
 * This enumeration represents the possible appearances of classes in the ui.
 *
 * @author Nils Christian Ehmke
 */
public enum ClassAppearance {

	/**
	 * A short representation. This means that only the simple name of the class is shown.
	 */
	SHORT,

	/**
	 * A long representation. This means that the full name of the class is shown.
	 */
	LONG;

	public String convert( final String aClass ) {
		String clazz = aClass;

		// This can only happen when the records contains null values. Ugly but possible.
		if ( clazz == null ) {
			return null;
		}

		if ( this == SHORT ) {
			final int lastPoint = clazz.lastIndexOf( '.' );
			if ( lastPoint != -1 ) {
				clazz = clazz.substring( lastPoint + 1 );
			}
		}

		return clazz.intern( );
	}

}
