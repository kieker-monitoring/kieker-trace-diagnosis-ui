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
 * A convenient base class for {@link SystemProperty system properties} of enum types.
 *
 * @param <T>
 *            The enum type.
 * 
 * @author Nils Christian Ehmke
 */
public abstract class AbstractEnumSystemProperty<T extends Enum<T>> implements SystemProperty<T> {

	private final Class<T> ivEnumClass;

	public AbstractEnumSystemProperty( final Class<T> aEnumClass ) {
		ivEnumClass = aEnumClass;
	}

	@Override
	public final T deserialize( final String aString ) {
		return Enum.valueOf( ivEnumClass, aString );
	}

}
