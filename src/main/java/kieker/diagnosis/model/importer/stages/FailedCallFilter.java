/***************************************************************************
 * Copyright 2014 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.model.importer.stages;

import kieker.diagnosis.domain.AbstractOperationCall;

/**
 * This stage filters incoming calls and forwards only those which are failed.
 *
 * @author Nils Christian Ehmke
 *
 * @param <T>
 *            The precise type of the incoming and outgoing calls.
 */
public final class FailedCallFilter<T extends AbstractOperationCall<?>> extends AbstractStage<T, T> {

	@Override
	protected void execute(final T element) {
		if (element.isFailed()) {
			super.send(element);
		}
	}

}
