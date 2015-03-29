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

package kieker.diagnosis.mainview.subview.util;

import kieker.diagnosis.domain.AbstractTrace;

/**
 * @author Nils Christian Ehmke
 */
public final class ContainerSortListener extends AbstractTraceTreeColumnSortListener<AbstractTrace<?>> {

	private static final long serialVersionUID = 1L;

	@Override
	protected int compare(final AbstractTrace<?> fstTrace, final AbstractTrace<?> sndTrace) {
		final String fstContainer = fstTrace.getRootOperationCall().getContainer();
		final String sndContainer = sndTrace.getRootOperationCall().getContainer();

		return fstContainer.compareTo(sndContainer);
	}

}
