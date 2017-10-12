package kieker.diagnosis.architecture.ui;

import java.util.ResourceBundle;

import javafx.util.StringConverter;

/**
 * This is an implementation of a {@link StringConverter} to convert an enum value to a string representation and vice versa. A resource bundle with the name of
 * the enum has to be available in the classpath. It is also recommended, that each enum value has a different locale string. {@code null} values are mapped to
 * the empty string and vice versa.
 *
 * @param <E>
 *            The type of the enum.
 *
 * @author Nils Christian Ehmke
 */
public final class EnumStringConverter<E extends Enum<?>> extends StringConverter<E> {

	private final ResourceBundle ivResourceBundle;
	private final Class<E> ivEnumClass;

	public EnumStringConverter( final Class<E> aEnumClass ) {
		ivResourceBundle = ResourceBundle.getBundle( aEnumClass.getName( ) );
		ivEnumClass = aEnumClass;
	}

	@Override
	public String toString( final E aObject ) {
		if ( aObject == null ) {
			return "";
		}

		return ivResourceBundle.getString( aObject.name( ) );
	}

	@Override
	public E fromString( final String aString ) {
		if ( aString.isEmpty( ) ) {
			return null;
		}

		final E[] enumConstants = ivEnumClass.getEnumConstants( );

		// Run through all values of the enum and check which one maps to the given string.
		for ( final E enumConstant : enumConstants ) {
			final String value = toString( enumConstant );
			if ( value.equals( aString ) ) {
				return enumConstant;
			}
		}

		// We did not found a value. This should not happen. We have to return something though.
		return null;
	}

}
