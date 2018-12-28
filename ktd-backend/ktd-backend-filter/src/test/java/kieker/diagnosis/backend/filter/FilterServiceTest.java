/***************************************************************************
 * Copyright 2015-2018 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.backend.filter;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import kieker.diagnosis.backend.base.ServiceBaseModule;
import kieker.diagnosis.backend.filter.FilterService;

/**
 * Test class for the {@link FilterService}.
 *
 * @author Nils Christian Ehmke
 */
public final class FilterServiceTest {

	private FilterService filterService;

	@Before
	public void setUp( ) {
		final Injector injector = Guice.createInjector( new ServiceBaseModule( ) );
		filterService = injector.getInstance( FilterService.class );
	}

	@Test
	public void testGetStringPredicateWithNull( ) {
		Predicate<Object> predicate = filterService.getStringPredicate( e -> null, "A", false );
		assertThat( predicate.test( null ), is( false ) );
	}

	@Test
	public void testGetStringPredicateWithNullAndRegExpr( ) {
		Predicate<Object> predicate = filterService.getStringPredicate( e -> null, ".*", true );
		assertThat( predicate.test( null ), is( false ) );
	}

}
