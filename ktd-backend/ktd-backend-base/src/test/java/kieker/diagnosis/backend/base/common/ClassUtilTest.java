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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;

/**
 * This is a unit test for {@link ClassUtil}.
 *
 * @author Nils Christian Ehmke
 */
@DisplayName ( "Unit-Test for ClassUtil" )
public final class ClassUtilTest {

	private final Injector injector = Guice.createInjector( new Module( ) );

	@Test
	@DisplayName ( "Real name of proxy class should return correct name" )
	public void realNameOfProxyClassShouldReturnCorrectName( ) {
		final String realName = ClassUtil.getRealName( injector.getInstance( ProxiedClass.class ).getClass( ) );
		assertThat( realName ).isEqualTo( ProxiedClass.class.getName( ) );
	}

	@Test
	@DisplayName ( "Real name of non-proxy class should return correct name" )
	public void realNameOfNonProxyClassShouldReturnCorrectName( ) {
		final String realName = ClassUtil.getRealName( injector.getInstance( NonProxiedClass.class ).getClass( ) );
		assertThat( realName ).isEqualTo( NonProxiedClass.class.getName( ) );
	}

	@Test
	@DisplayName ( "Instantiation should throw exception" )
	public void instantiationShouldThrowException( ) throws ReflectiveOperationException {
		final Constructor<ClassUtil> constructor = ClassUtil.class.getDeclaredConstructor( );
		constructor.setAccessible( true );

		assertThrows( InvocationTargetException.class, constructor::newInstance );
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
