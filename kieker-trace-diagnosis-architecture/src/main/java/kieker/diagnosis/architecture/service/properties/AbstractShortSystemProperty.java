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
 * A convenient base class for {@link SystemProperty system properties} of the type {@link Short}.
 *
 * @author Nils Christian Ehmke
 */
public abstract class AbstractShortSystemProperty implements SystemProperty<Short> {

	@Override
	public final Short deserialize( final String aString ) {
		return Short.valueOf( aString );
	}

}
