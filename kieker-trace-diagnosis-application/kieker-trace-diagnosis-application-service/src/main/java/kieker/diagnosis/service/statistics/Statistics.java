/***************************************************************************
 * Copyright 2015-2018 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.service.statistics;

import lombok.Getter;
import lombok.Setter;

/**
 * This is a data transfer object holding the statistics for the statistics service.
 *
 * @author Nils Christian Ehmke
 */
@Getter
@Setter
public final class Statistics {

	private long processedBytes;
	private long processDuration;
	private long processSpeed;
	private int ignoredRecords;
	private int danglingRecords;
	private int incompleteTraces;
	private int methods;
	private int aggregatedMethods;
	private int traces;
	private String beginnOfMonitoring;
	private String endOfMonitoring;
	private String directory;

}
