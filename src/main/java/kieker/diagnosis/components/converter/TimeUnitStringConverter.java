/***************************************************************************
 * Copyright 2015-2016 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.components.converter;

import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import kieker.diagnosis.util.Mapper;

/**
 * @author Nils Christian Ehmke
 */
public final class TimeUnitStringConverter extends AbstractStringConverter<TimeUnit> {

	@Override
	protected void fillMapper(final Mapper<TimeUnit, String> aMapper, final ResourceBundle aResourceBundle) {
		final String nanoseconds = aResourceBundle.getString("nanoseconds");
		final String microseconds = aResourceBundle.getString("microseconds");
		final String milliseconds = aResourceBundle.getString("milliseconds");
		final String seconds = aResourceBundle.getString("seconds");
		final String minutes = aResourceBundle.getString("minutes");
		final String hours = aResourceBundle.getString("hours");

		aMapper.map(TimeUnit.NANOSECONDS).to(nanoseconds);
		aMapper.map(TimeUnit.MICROSECONDS).to(microseconds);
		aMapper.map(TimeUnit.MILLISECONDS).to(milliseconds);
		aMapper.map(TimeUnit.SECONDS).to(seconds);
		aMapper.map(TimeUnit.MINUTES).to(minutes);
		aMapper.map(TimeUnit.HOURS).to(hours);
	}

}
