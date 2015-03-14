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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public abstract class AbstractOperationCallTest<T extends AbstractOperationCall<T>> {

	protected abstract T createOperationCall(final String container, final String component, final String operation);

	@Test
	public void equalsWithNullShouldNotLeadToException() {
		final T fstCall = this.createOperationCall("", "", "");
		final T sndCall = null;

		assertThat(fstCall, is(not(equalTo(sndCall))));
	}

	@Test
	public void equalsForSameInstanceShouldWork() {
		final T fstCall = this.createOperationCall("", "", "");
		final T sndCall = fstCall;

		assertThat(fstCall, is(equalTo(sndCall)));
	}

	@Test
	public void equalsForSameValuesShouldWork() {
		final T fstCall = this.createOperationCall("container", "component", "operation");
		final T sndCall = this.createOperationCall("container", "component", "operation");

		assertThat(fstCall, is(equalTo(sndCall)));
	}

	@Test
	public void equalsForDifferentContainerShouldReturnFalse() {
		final T fstCall = this.createOperationCall("container1", "component", "operation");
		final T sndCall = this.createOperationCall("container2", "component", "operation");

		assertThat(fstCall, is(not(equalTo(sndCall))));
	}

	@Test
	public void equalsForDifferentComponentsShouldReturnFalse() {
		final T fstCall = this.createOperationCall("container", "component1", "operation");
		final T sndCall = this.createOperationCall("container", "component2", "operation");

		assertThat(fstCall, is(not(equalTo(sndCall))));
	}

	@Test
	public void equalsForDifferentOperationsShouldReturnFalse() {
		final T fstCall = this.createOperationCall("container", "component", "operation1");
		final T sndCall = this.createOperationCall("container", "component", "operation2");

		assertThat(fstCall, is(not(equalTo(sndCall))));
	}

	@Test
	public void equalsForSameValuesAndNestedOperationCallsShouldWork() {
		final T fstCall = this.createOperationCall("container", "component", "operation");
		final T sndCall = this.createOperationCall("container", "component", "operation");

		fstCall.addChild(this.createOperationCall("container1", "component1", "operation1"));
		sndCall.addChild(this.createOperationCall("container1", "component1", "operation1"));

		assertThat(fstCall, is(equalTo(sndCall)));
	}

	@Test
	public void equalsForDifferentValuesInNestedOperationCallsShouldReturnFalse() {
		final T fstCall = this.createOperationCall("container", "component", "operation");
		final T sndCall = this.createOperationCall("container", "component", "operation");

		fstCall.addChild(this.createOperationCall("container1", "component1", "operation1"));
		sndCall.addChild(this.createOperationCall("container2", "component1", "operation1"));

		assertThat(fstCall, is(not(equalTo(sndCall))));
	}

	@Test
	public void toDotForCallTreeShouldWorkForSimpleCase() {
		final T call = this.createOperationCall("container", "component", "operation");

		call.addChild(this.createOperationCall("container1", "component1", "operation1"));
		call.addChild(this.createOperationCall("container2", "component2", "operation2"));

		assertThat(call.toDotForCallTree(), is("digraph G {rankdir = TB;node [shape = none];operation;operation -> operation1;operation -> operation2;operation2;operation1;}"));
	}

}
