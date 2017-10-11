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

package kieker.diagnosis.architecture.service.properties;

/**
 * Classes implementing this interface can be considered system properties. They are loaded via {@link System#getProperty(String) and can not be modified during
 * runtime. Classes implementing this interface should be annotated with {@link Property}.
 *
 * @author Nils Christian Ehmke
 *
 * @param <T>
 *            The type of the property.
 *
 * @see ApplicationProperty
 * @see PropertiesService
 */
public interface SystemProperty<T> {

	/**
	 * Delivers the key of the property. It is recommended that this is unique among the application.
	 *
	 * @return The key of the property.
	 */
	public String getKey( );

	/**
	 * Deserializes the property from the string representation.
	 *
	 * @param aString
	 *            The string representation loaded from the configuration file.
	 *
	 * @return The actual value deserialized from the string.
	 */
	public T deserialize( String aString );

}
