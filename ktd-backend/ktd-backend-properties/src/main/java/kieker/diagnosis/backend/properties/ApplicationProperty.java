/***************************************************************************
 * Copyright 2015-2019 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.backend.properties;

/**
 * Classes implementing this interface can be considered application properties. They are stored and loaded in a
 * properties storage. Unlike {@link SystemProperty system properties} they can be modified during runtime. Classes
 * implementing this interface should be annotated with {@link Property}.
 *
 * @author Nils Christian Ehmke
 *
 * @param <T>
 *            The type of the property.
 *
 * @see SystemProperty
 * @see PropertiesService
 */
public interface ApplicationProperty<T> {

	/**
	 * Delivers the key of the property. It is recommended that this is unique among the application.
	 *
	 * @return The key of the property.
	 */
	String getKey( );

	/**
	 * Delivers the default value of the property. This should not be null.
	 *
	 * @return The default value of the property.
	 */
	T getDefaultValue( );

	/**
	 * Serializes the property into a string representation.
	 *
	 * @param aValue
	 *            The value to be serialized.
	 *
	 * @return A string representation of the given value.
	 */
	String serialize( T aValue );

	/**
	 * Deserializes the property from the string representation.
	 *
	 * @param aString
	 *            The string representation loaded from the properties storage.
	 *
	 * @return The actual value deserialized from the string.
	 */
	T deserialize( String aString );

}
