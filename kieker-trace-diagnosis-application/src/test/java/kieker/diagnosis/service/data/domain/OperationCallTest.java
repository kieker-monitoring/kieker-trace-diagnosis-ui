/***************************************************************************
 * Copyright 2015-2016 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.service.data.domain;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import kieker.diagnosis.service.data.domain.OperationCall;

public final class OperationCallTest extends AbstractOperationCallTest<OperationCall> {

	@Test
	public void addingChildrenShouldUpdateTheParent( ) {
		final OperationCall call = new OperationCall( "", "", "", 42, 0 );
		final OperationCall child = new OperationCall( "", "", "", 42, 0 );

		call.addChild( child );

		assertThat( child.getParent( ), is( call ) );
	}

	@Test
	public void equalsForDifferentTIDsShouldWork( ) {
		final OperationCall fstCall = new OperationCall( "container", "component", "operation", 42, 0 );
		final OperationCall sndCall = new OperationCall( "container", "component", "operation", 43, 0 );

		assertTrue( fstCall.isEqualTo( sndCall ) );
	}

	@Test
	public void equalsForDifferentDurationsShouldWork( ) {
		final OperationCall fstCall = new OperationCall( "container", "component", "operation", 42, 0 );
		final OperationCall sndCall = new OperationCall( "container", "component", "operation", 42, 0 );

		fstCall.setDuration( 100 );
		sndCall.setDuration( 200 );

		assertTrue( fstCall.isEqualTo( sndCall ) );
	}

	@Override
	protected OperationCall createOperationCall( final String container, final String component, final String operation, final String failedCause ) {
		return new OperationCall( container, component, operation, failedCause, -1, 0 );
	}

}