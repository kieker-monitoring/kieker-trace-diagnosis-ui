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

package kieker.diagnosis.mainview.subview.calls.util;

import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.mainview.subview.util.AbstractCallTableColumnSortListener;

/**
 * @author Nils Christian Ehmke
 */
public final class DurationSortListener extends AbstractCallTableColumnSortListener<OperationCall> {

	private static final long serialVersionUID = 1L;

	@Override
	protected int compare(final OperationCall fstCall, final OperationCall sndCall) {
		return Long.compare(fstCall.getDuration(), sndCall.getDuration());
	}

}