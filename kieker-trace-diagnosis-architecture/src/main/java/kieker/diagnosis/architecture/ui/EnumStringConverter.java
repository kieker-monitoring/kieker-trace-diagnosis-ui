/*************************************************************************** 
 * Copyright 2015-2017 Kieker Project (http://kieker-monitoring.net)         
 *                                                                           
 * Licensed under the Apache License, Version 2.0 (the "License");           
 * you may not use this file except in compliance with the License.          
 * You may obtain a copy of the License at                                   
 *                                                                           
 *     http://www.apache.org/licenses/LICENSE-2.0                            
 *                                                                           
 * Unless required by applicable law or agreed to in writing, software       
 * distributed under the License is distributed on an "AS IS" BASIS,         
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  
 * See the License for the specific language governing permissions and       
 * limitations under the License.                                            
 ***************************************************************************/

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
