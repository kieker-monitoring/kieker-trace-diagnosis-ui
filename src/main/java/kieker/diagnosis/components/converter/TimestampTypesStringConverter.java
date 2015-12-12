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

package kieker.diagnosis.components.converter;

import java.util.ResourceBundle;

import kieker.diagnosis.model.PropertiesModel.TimestampTypes;
import kieker.diagnosis.util.Mapper;

public final class TimestampTypesStringConverter extends AbstractStringConverter<TimestampTypes> {

	@Override
	protected void fillMapper(final Mapper<TimestampTypes, String> aMapper, final ResourceBundle aResourceBundle) {
		aMapper.map(TimestampTypes.TIMESTAMP).to("1418993603113900610");
		aMapper.map(TimestampTypes.DATE).to("19.12.14");
		aMapper.map(TimestampTypes.SHORT_TIME).to("13:53");
		aMapper.map(TimestampTypes.LONG_TIME).to("13:53:23");
		aMapper.map(TimestampTypes.DATE_AND_TIME).to("19.12.2014 13:53:23");
	}

}
