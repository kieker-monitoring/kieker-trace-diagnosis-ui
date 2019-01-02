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

package kieker.diagnosis.backend.search.statistics;

import lombok.Builder;
import lombok.Getter;

/**
 * This is a data transfer object holding the statistics for the statistics service.
 *
 * @author Nils Christian Ehmke
 */
@Builder
@Getter
public final class Statistics {

	private final long processedBytes;
	private final long processDuration;
	private final long processSpeed;
	private final int ignoredRecords;
	private final int danglingRecords;
	private final int incompleteTraces;
	private final int methods;
	private final int aggregatedMethods;
	private final int traces;
	private final String beginnOfMonitoring;
	private final String endOfMonitoring;
	private final String directory;

}
