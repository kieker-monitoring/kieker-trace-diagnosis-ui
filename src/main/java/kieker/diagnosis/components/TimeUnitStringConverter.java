/***************************************************************************
 * Copyright 2015 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.components;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javafx.util.StringConverter;
import kieker.diagnosis.util.Mapper;

/**
 * @author Nils Christian Ehmke
 */
public class TimeUnitStringConverter extends StringConverter<TimeUnit> {

	private static Mapper<TimeUnit, String> timeUnitMapper;

	static {
		final String bundleBaseName = "locale.kieker.diagnosis.components.components";
		final ResourceBundle resourceBundle = ResourceBundle.getBundle(bundleBaseName, Locale.getDefault());

		final String nanoseconds = resourceBundle.getString("nanoseconds");
		final String microseconds = resourceBundle.getString("microseconds");
		final String milliseconds = resourceBundle.getString("milliseconds");
		final String seconds = resourceBundle.getString("seconds");
		final String minutes = resourceBundle.getString("minutes");
		final String hours = resourceBundle.getString("hours");

		TimeUnitStringConverter.timeUnitMapper = new Mapper<>();
		TimeUnitStringConverter.timeUnitMapper.map(TimeUnit.NANOSECONDS).to(nanoseconds);
		TimeUnitStringConverter.timeUnitMapper.map(TimeUnit.MICROSECONDS).to(microseconds);
		TimeUnitStringConverter.timeUnitMapper.map(TimeUnit.MILLISECONDS).to(milliseconds);
		TimeUnitStringConverter.timeUnitMapper.map(TimeUnit.SECONDS).to(seconds);
		TimeUnitStringConverter.timeUnitMapper.map(TimeUnit.MINUTES).to(minutes);
		TimeUnitStringConverter.timeUnitMapper.map(TimeUnit.HOURS).to(hours);
	}

	@Override
	public TimeUnit fromString(final String string) {
		return TimeUnitStringConverter.timeUnitMapper.invertedResolve(string);
	}

	@Override
	public String toString(final TimeUnit object) {
		return TimeUnitStringConverter.timeUnitMapper.resolve(object);
	}

}
