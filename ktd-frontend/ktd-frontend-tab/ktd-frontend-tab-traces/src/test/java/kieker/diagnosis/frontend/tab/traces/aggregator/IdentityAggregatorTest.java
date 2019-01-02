/***************************************************************************
 * Copyright 2015-2019 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.frontend.tab.traces.aggregator;

import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Collections;

import org.junit.Test;

import kieker.diagnosis.backend.data.MethodCall;

/**
 * This is a unit test for {@link IdentityAggregator}.
 *
 * @author Nils Christian Ehmke
 */
public final class IdentityAggregatorTest {

	@Test
	public void testAggregationOnEmptyList( ) {
		final Aggregator aggregator = new IdentityAggregator( );

		assertThat( aggregator.aggregate( Collections.emptyList( ) ), is( empty( ) ) );
	}

	@Test
	public void testAggregationOnSingletonList( ) {
		final Aggregator aggregator = new IdentityAggregator( );

		final MethodCall call = new MethodCall( );
		assertThat( aggregator.aggregate( Collections.singletonList( call ) ), contains( call ) );
	}

}
