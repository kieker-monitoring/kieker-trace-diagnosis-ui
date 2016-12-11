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

package kieker.diagnosis.domain;

import java.util.List;

/**
 * This class represents an aggregated trace (also called a trace equivalence class) within this application.
 *
 * @author Nils Christian Ehmke
 */
public final class AggregatedTrace extends AbstractTrace<AggregatedOperationCall> {

	private final List<Trace> ivTraces;

	public AggregatedTrace(final List<Trace> aTraces) {
		super(new AggregatedOperationCall(aTraces.get(0).getRootOperationCall()));

		this.ivTraces = aTraces;
	}

	public List<Trace> getTraces() {
		return this.ivTraces;
	}

}
