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

import javafx.util.StringConverter;
import kieker.diagnosis.model.PropertiesModel.Threshold;
import kieker.diagnosis.util.Mapper;

public class ThresholdStringConverter extends StringConverter<Threshold> {

	private static Mapper<Threshold, String> thresholdMapper;

	static {
		final String bundleBaseName = "locale.kieker.diagnosis.components.components";
		final ResourceBundle resourceBundle = ResourceBundle.getBundle(bundleBaseName, Locale.getDefault());
		final String threshold = resourceBundle.getString("threshold");

		ThresholdStringConverter.thresholdMapper = new Mapper<>();
		ThresholdStringConverter.thresholdMapper.map(Threshold.THRESHOLD_0_5).to(threshold + " < 0.5 %");
		ThresholdStringConverter.thresholdMapper.map(Threshold.THRESHOLD_1).to(threshold + " < 1 %");
		ThresholdStringConverter.thresholdMapper.map(Threshold.THRESHOLD_10).to(threshold + " < 10 %");
		ThresholdStringConverter.thresholdMapper.map(Threshold.THRESHOLD_20).to(threshold + " < 20 %");
		ThresholdStringConverter.thresholdMapper.map(Threshold.THRESHOLD_30).to(threshold + " < 30 %");
		ThresholdStringConverter.thresholdMapper.map(Threshold.THRESHOLD_40).to(threshold + " < 40 %");
		ThresholdStringConverter.thresholdMapper.map(Threshold.THRESHOLD_50).to(threshold + " < 50 %");
	}

	@Override
	public Threshold fromString(final String string) {
		return ThresholdStringConverter.thresholdMapper.invertedResolve(string);
	}

	@Override
	public String toString(final Threshold object) {
		return ThresholdStringConverter.thresholdMapper.resolve(object);
	}

}
