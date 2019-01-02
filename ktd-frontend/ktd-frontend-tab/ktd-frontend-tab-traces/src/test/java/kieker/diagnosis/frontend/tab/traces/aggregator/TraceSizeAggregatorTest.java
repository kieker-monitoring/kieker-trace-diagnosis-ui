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

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import kieker.diagnosis.backend.data.MethodCall;

/**
 * This is a unit test for {@link TraceSizeAggregator}.
 *
 * @author Nils Christian Ehmke
 */
public final class TraceSizeAggregatorTest {

	@Test
	public void testAggregationOnEmptyList( ) {
		final Aggregator aggregator = new TraceSizeAggregator( 1 );

		assertThat( aggregator.aggregate( Collections.emptyList( ) ), is( empty( ) ) );
	}

	@Test
	public void testAggregationOnSingletonList( ) {
		final Aggregator aggregator = new TraceSizeAggregator( 1 );

		final MethodCall call = new MethodCall( );
		assertThat( aggregator.aggregate( Collections.singletonList( call ) ), contains( call ) );
	}

	@Test
	public void testRealAggregation( ) {
		final Aggregator aggregator = new TraceSizeAggregator( 1 );

		final MethodCall call1 = new MethodCall( );
		call1.setTraceSize( 1 );

		final MethodCall call2 = new MethodCall( );
		call2.setTraceSize( 2 );

		final MethodCall call3 = new MethodCall( );
		call3.setTraceSize( 42 );

		final List<MethodCall> aggregatedList = aggregator.aggregate( Arrays.asList( call1, call2, call3 ) );
		assertThat( aggregatedList, hasSize( 2 ) );
		assertTrue( aggregatedList.contains( call3 ) );

		final MethodCall aggregationMethodCall = aggregatedList.get( 1 );
		assertThat( aggregationMethodCall.getMethod( ), is( "2 Methodenaufrufe zusammengefasst " ) );
		assertThat( aggregationMethodCall.getTraceSize( ), is( 3 ) );
	}

}
