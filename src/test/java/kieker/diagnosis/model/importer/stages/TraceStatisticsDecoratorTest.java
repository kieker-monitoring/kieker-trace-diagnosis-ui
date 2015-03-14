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
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertThat;
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.domain.Trace;
import kieker.diagnosis.model.importer.stages.TraceStatisticsDecorator;

import org.junit.Test;

public final class TraceStatisticsDecoratorTest {

	@Test
	public void percentCalculationShouldWork() throws Exception {
		final OperationCall rootCall = new OperationCall("", "", "", 42);
		final OperationCall child1 = new OperationCall("", "", "", 42);
		final OperationCall child2 = new OperationCall("", "", "", 42);
		final OperationCall child3 = new OperationCall("", "", "", 42);
		final OperationCall child4 = new OperationCall("", "", "", 42);

		rootCall.setDuration(100);
		child1.setDuration(70);
		child2.setDuration(15);
		child3.setDuration(36);
		child4.setDuration(18);

		rootCall.addChild(child1);
		rootCall.addChild(child2);
		rootCall.addChild(child3);

		child3.addChild(child4);

		final Trace trace = new Trace(rootCall, 42);

		final TraceStatisticsDecorator decorator = new TraceStatisticsDecorator();

		decorator.onStarting();
		decorator.execute(trace);

		assertThat((double) rootCall.getPercent(), is(closeTo(100.0, 1e-3)));
		assertThat((double) child1.getPercent(), is(closeTo(70.0, 1e-3)));
		assertThat((double) child2.getPercent(), is(closeTo(15.0, 1e-3)));
		assertThat((double) child3.getPercent(), is(closeTo(36.0, 1e-3)));
		assertThat((double) child4.getPercent(), is(closeTo(50.0, 1e-3)));
	}

	@Test
	public void traceDepthCalculationInCommonCaseShouldWork() throws Exception {
		final OperationCall rootCall = new OperationCall("", "", "", 1);

		rootCall.addChild(new OperationCall("", "", "", 1));
		rootCall.addChild(new OperationCall("", "", "", 1));

		rootCall.getChildren().get(0).addChild(new OperationCall("", "", "", 1));

		final Trace trace = new Trace(rootCall, 1);

		final TraceStatisticsDecorator decorator = new TraceStatisticsDecorator();

		decorator.onStarting();
		decorator.execute(trace);

		assertThat(rootCall.getStackDepth(), is(2));
	}

	@Test
	public void traceDepthCalculationForNoChildrenShouldWork() throws Exception {
		final OperationCall rootCall = new OperationCall("", "", "", 1);

		final Trace trace = new Trace(rootCall, 1);

		final TraceStatisticsDecorator decorator = new TraceStatisticsDecorator();

		decorator.onStarting();
		decorator.execute(trace);

		assertThat(rootCall.getStackDepth(), is(0));
	}

	@Test
	public void traceSizeCalculationInCommonCaseShouldWork() throws Exception {
		final OperationCall rootCall = new OperationCall("", "", "", 42);

		final OperationCall child1 = new OperationCall("", "", "", 42);
		final OperationCall child2 = new OperationCall("", "", "", 42);
		final OperationCall child3 = new OperationCall("", "", "", 42);
		final OperationCall child4 = new OperationCall("", "", "", 42);

		rootCall.addChild(child1);
		rootCall.addChild(child2);
		rootCall.addChild(child3);

		child3.addChild(child4);

		final Trace trace = new Trace(rootCall, 1);

		final TraceStatisticsDecorator decorator = new TraceStatisticsDecorator();

		decorator.onStarting();
		decorator.execute(trace);

		assertThat(rootCall.getStackSize(), is(5));
	}

	@Test
	public void traceSizeCalculationForNoChildrenShouldWork() throws Exception {
		final OperationCall rootCall = new OperationCall("", "", "", 1);

		final Trace trace = new Trace(rootCall, 1);

		final TraceStatisticsDecorator decorator = new TraceStatisticsDecorator();

		decorator.onStarting();
		decorator.execute(trace);

		assertThat(rootCall.getStackSize(), is(1));
	}

}
