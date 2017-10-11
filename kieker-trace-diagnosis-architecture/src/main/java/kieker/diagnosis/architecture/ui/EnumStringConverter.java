package kieker.diagnosis.architecture.ui;

import java.util.ResourceBundle;

import javafx.util.StringConverter;

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

		for ( final E enumConstant : enumConstants ) {
			final String value = toString( enumConstant );
			if ( value.equals( aString ) ) {
				return enumConstant;
			}
		}

		return null;
	}

}
