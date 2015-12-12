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

package kieker.diagnosis.domain;

/**
 * This class represents a concrete trace within this application. It adds some properties that are only required for concrete traces, like the trace ID.
 *
 * @author Nils Christian Ehmke
 */
public final class Trace extends AbstractTrace<OperationCall> {

	private final long ivTraceID;

	public Trace(final OperationCall rootOperationCall, final long traceID) {
		super(rootOperationCall);

		this.ivTraceID = traceID;
	}

	public long getTraceID() {
		return this.ivTraceID;
	}

}
