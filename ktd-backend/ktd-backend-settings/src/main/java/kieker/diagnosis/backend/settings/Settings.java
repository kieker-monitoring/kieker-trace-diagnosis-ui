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

package kieker.diagnosis.backend.settings;

import java.util.concurrent.TimeUnit;

import lombok.Builder;
import lombok.Getter;

/**
 * This is a data transfer object holding the settings of the application.
 *
 * @author Nils Christian Ehmke
 */
@Builder
@Getter
public final class Settings {

	private final TimestampAppearance timestampAppearance;
	private final TimeUnit timeUnit;
	private final ClassAppearance classAppearance;
	private final MethodAppearance methodAppearance;
	private final boolean showUnmonitoredTimeProperty;
	private final MethodCallAggregation methodCallAggregation;
	private final int maxNumberOfMethodCalls;
	private final float methodCallThreshold;

}
