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

package kieker.diagnosis.backend.base.common;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Constructor;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;

/**
 * This is a unit test for {@link ClassUtil}.
 *
 * @author Nils Christian Ehmke
 */
public final class ClassUtilTest {

	@Rule
	public final ExpectedException expectedException = ExpectedException.none( );

	private final Injector injector = Guice.createInjector( new Module( ) );

	@Test
	public void realNameOfProxyClassShouldReturnCorrectName( ) {
		final String realName = ClassUtil.getRealName( injector.getInstance( ProxiedClass.class ).getClass( ) );
		assertThat( realName, is( equalTo( ProxiedClass.class.getName( ) ) ) );
	}

	@Test
	public void realNameOfNonProxyClassShouldReturnCorrectName( ) {
		final String realName = ClassUtil.getRealName( injector.getInstance( NonProxiedClass.class ).getClass( ) );
		assertThat( realName, is( equalTo( NonProxiedClass.class.getName( ) ) ) );
	}

	@Test
	public void instantiationShouldThrowException( ) throws ReflectiveOperationException {
		final Constructor<ClassUtil> constructor = ClassUtil.class.getDeclaredConstructor( );
		constructor.setAccessible( true );

		expectedException.expectCause( instanceOf( AssertionError.class ) );
		constructor.newInstance( );
	}

}

class NonProxiedClass {
}

class ProxiedClass {
}

class Module extends AbstractModule {

	@Override
	protected void configure( ) {
		bindInterceptor( Matchers.subclassesOf( ProxiedClass.class ), Matchers.any( ), invocation -> invocation.proceed( ) );
	}

}
