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

import kieker.diagnosis.model.PropertiesModel.Threshold;
import kieker.diagnosis.util.Mapper;

public final class ThresholdStringConverter extends AbstractStringConverter<Threshold> {

	@Override
	protected void fillMapper(final Mapper<Threshold, String> mapper, final ResourceBundle resourceBundle) {
		final String threshold = resourceBundle.getString("threshold");

		mapper.map(Threshold.THRESHOLD_0_5).to(threshold + " < 0.5 %");
		mapper.map(Threshold.THRESHOLD_1).to(threshold + " < 1 %");
		mapper.map(Threshold.THRESHOLD_10).to(threshold + " < 10 %");
		mapper.map(Threshold.THRESHOLD_20).to(threshold + " < 20 %");
		mapper.map(Threshold.THRESHOLD_30).to(threshold + " < 30 %");
		mapper.map(Threshold.THRESHOLD_40).to(threshold + " < 40 %");
		mapper.map(Threshold.THRESHOLD_50).to(threshold + " < 50 %");
	}

}
