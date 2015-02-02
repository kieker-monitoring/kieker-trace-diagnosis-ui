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

package kieker.diagnosis.subview.aggregatedtraces.util;

import kieker.diagnosis.common.domain.AggregatedTrace;
import kieker.diagnosis.common.domain.StatisticType;
import kieker.diagnosis.subview.util.AbstractDirectedComparator;

import org.eclipse.swt.SWT;

public final class AggregatedTraceComparator<T extends Comparable<T>> extends AbstractDirectedComparator<AggregatedTrace> {

	private static final long serialVersionUID = 1L;

	private final StatisticType statisticType;

	public AggregatedTraceComparator(final StatisticType statisticType) {
		this.statisticType = statisticType;
	}

	@Override
	@SuppressWarnings("unchecked")
	public int compare(final AggregatedTrace fstTrace, final AggregatedTrace sndTrace) {
		final T fstValue = (T) fstTrace.getStatistic(this.statisticType);
		final T sndValue = (T) sndTrace.getStatistic(this.statisticType);

		int result;

		if (this.getDirection() == SWT.UP) {
			result = fstValue.compareTo(sndValue);
		} else {
			result = sndValue.compareTo(fstValue);
		}

		return result;
	}

}
