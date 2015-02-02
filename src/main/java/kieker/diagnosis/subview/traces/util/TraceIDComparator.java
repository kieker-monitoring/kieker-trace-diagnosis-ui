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

package kieker.diagnosis.subview.traces.util;

import kieker.diagnosis.common.domain.Trace;
import kieker.diagnosis.subview.util.AbstractDirectedComparator;

import org.eclipse.swt.SWT;

public final class TraceIDComparator extends AbstractDirectedComparator<Trace> {

	private static final long serialVersionUID = 1L;

	@Override
	public int compare(final Trace fstTrace, final Trace sndTrace) {
		int result;

		if (this.getDirection() == SWT.UP) {
			result = Long.compare(sndTrace.getTraceID(), fstTrace.getTraceID());
		} else {
			result = Long.compare(fstTrace.getTraceID(), sndTrace.getTraceID());
		}

		return result;
	}

}
