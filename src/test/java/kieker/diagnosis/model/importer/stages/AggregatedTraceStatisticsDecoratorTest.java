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

package kieker.diagnosis.model.importer.stages;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import kieker.diagnosis.domain.AggregatedTrace;
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.domain.Trace;

import org.junit.Test;

public final class AggregatedTraceStatisticsDecoratorTest {

	@Test
	public void minMaxMeanAndAvgCalculationForSingleCallShouldWork() throws Exception {
		final OperationCall call1 = new OperationCall("", "", "", 43, 0);
		final OperationCall call2 = new OperationCall("", "", "", 44, 0);
		final OperationCall call3 = new OperationCall("", "", "", 45, 0);

		call1.setDuration(15);
		call2.setDuration(7);
		call3.setDuration(44);

		final Trace trace1 = new Trace(call1, 43);
		final Trace trace2 = new Trace(call2, 44);
		final Trace trace3 = new Trace(call3, 45);

		final AggregatedTrace trace = new AggregatedTrace(Arrays.asList(trace1, trace2, trace3));

		final AggregatedTraceStatisticsDecorator decorator = new AggregatedTraceStatisticsDecorator();

		decorator.onStarting();
		decorator.execute(trace);

		assertThat(trace.getRootOperationCall().getMinDuration(), is(7L));
		assertThat(trace.getRootOperationCall().getMaxDuration(), is(44L));
		assertThat(trace.getRootOperationCall().getMeanDuration(), is(22L));
		assertThat(trace.getRootOperationCall().getMedianDuration(), is(15L));
	}

}
