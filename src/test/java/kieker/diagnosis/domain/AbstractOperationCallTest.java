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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractOperationCallTest<T extends AbstractOperationCall<T>> {

	protected abstract T createOperationCall(final String container, final String component, final String operation);

	@Test
	public void equalsWithNullShouldNotLeadToException() {
		final T fstCall = this.createOperationCall("", "", "");
		final T sndCall = null;

		assertFalse(fstCall.isEqualTo(sndCall));
	}

	@Test
	public void equalsForSameInstanceShouldWork() {
		final T fstCall = this.createOperationCall("", "", "");
		final T sndCall = fstCall;

		assertTrue(fstCall.isEqualTo(sndCall));
	}

	@Test
	public void equalsForSameValuesShouldWork() {
		final T fstCall = this.createOperationCall("container", "component", "operation");
		final T sndCall = this.createOperationCall("container", "component", "operation");

		assertTrue(fstCall.isEqualTo(sndCall));
	}

	@Test
	public void equalsForDifferentContainerShouldReturnFalse() {
		final T fstCall = this.createOperationCall("container1", "component", "operation");
		final T sndCall = this.createOperationCall("container2", "component", "operation");

		Assert.assertThat(fstCall, Is.is(IsNot.not(IsEqual.equalTo(sndCall))));
	}

	@Test
	public void equalsForDifferentComponentsShouldReturnFalse() {
		final T fstCall = this.createOperationCall("container", "component1", "operation");
		final T sndCall = this.createOperationCall("container", "component2", "operation");

		assertFalse(fstCall.isEqualTo(sndCall));
	}

	@Test
	public void equalsForDifferentOperationsShouldReturnFalse() {
		final T fstCall = this.createOperationCall("container", "component", "operation1");
		final T sndCall = this.createOperationCall("container", "component", "operation2");

		assertFalse(fstCall.isEqualTo(sndCall));
	}

	@Test
	public void equalsForSameValuesAndNestedOperationCallsShouldWork() {
		final T fstCall = this.createOperationCall("container", "component", "operation");
		final T sndCall = this.createOperationCall("container", "component", "operation");

		fstCall.addChild(this.createOperationCall("container1", "component1", "operation1"));
		sndCall.addChild(this.createOperationCall("container1", "component1", "operation1"));

		assertTrue(fstCall.isEqualTo(sndCall));
	}

	@Test
	public void equalsForDifferentValuesInNestedOperationCallsShouldReturnFalse() {
		final T fstCall = this.createOperationCall("container", "component", "operation");
		final T sndCall = this.createOperationCall("container", "component", "operation");

		fstCall.addChild(this.createOperationCall("container1", "component1", "operation1"));
		sndCall.addChild(this.createOperationCall("container2", "component1", "operation1"));

		assertFalse(fstCall.isEqualTo(sndCall));
	}

}
