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

package kieker.diagnosis.backend.settings.properties;

import java.util.concurrent.TimeUnit;

import com.google.inject.Singleton;

import kieker.diagnosis.backend.properties.EnumApplicationProperty;

/**
 * The application property for time unit.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public final class TimeUnitProperty extends EnumApplicationProperty<TimeUnit> {

	public TimeUnitProperty( ) {
		super( TimeUnit.class );
	}

	@Override
	public TimeUnit getDefaultValue( ) {
		return TimeUnit.NANOSECONDS;
	}

	@Override
	public String getKey( ) {
		return "timeunit";
	}

}
