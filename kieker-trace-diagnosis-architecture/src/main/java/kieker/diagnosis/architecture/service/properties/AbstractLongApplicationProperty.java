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
 * A convenient base class for {@link ApplicationProperty application properties} of the type {@link Long}.
 *
 * @author Nils Christian Ehmke
 */
public abstract class AbstractLongApplicationProperty implements ApplicationProperty<Long> {

	@Override
	public final Long deserialize( final String aString ) {
		return Long.valueOf( aString );
	}

	@Override
	public final String serialize( final Long aValue ) {
		return aValue.toString( );
	}

}
