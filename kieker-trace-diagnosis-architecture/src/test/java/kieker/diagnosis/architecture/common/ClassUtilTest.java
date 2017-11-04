/*************************************************************************** 
 * Copyright 2015-2017 Kieker Project (http://kieker-monitoring.net)         
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

package kieker.diagnosis.architecture.common;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;

import kieker.diagnosis.architecture.exception.TechnicalException;

/**
 * Test class for {@link ClassUtil}.
 *
 * @author Nils Christian Ehmke
 */
public class ClassUtilTest {

	@Rule
	public final ExpectedException ivExpectedException = ExpectedException.none( );

	private final Injector ivInjector = Guice.createInjector( new Module( ) );

	@Test
	public void realClassOfProxyClassShouldReturnCorrectClass( ) {
		assertThat( ClassUtil.getRealClass( ivInjector.getInstance( ProxiedClass.class ).getClass( ) ), is( equalTo( ProxiedClass.class ) ) );
	}

	@Test
	public void realClassOfNonProxyClassShouldReturnCorrectClass( ) {
		assertThat( ClassUtil.getRealClass( ivInjector.getInstance( NonProxiedClass.class ).getClass( ) ), is( equalTo( NonProxiedClass.class ) ) );
	}

	@Test
	public void realNameOfProxyClassShouldReturnCorrectName( ) {
		assertThat( ClassUtil.getRealName( ivInjector.getInstance( ProxiedClass.class ).getClass( ) ), is( equalTo( ProxiedClass.class.getName( ) ) ) );
	}

	@Test
	public void realNameOfNonProxyClassShouldReturnCorrectName( ) {
		assertThat( ClassUtil.getRealName( ivInjector.getInstance( NonProxiedClass.class ).getClass( ) ), is( equalTo( NonProxiedClass.class.getName( ) ) ) );
	}

	@Test
	public void realCanonicalNameOfProxyClassShouldReturnCorrectName( ) {
		assertThat( ClassUtil.getRealCanonicalName( ivInjector.getInstance( ProxiedClass.class ).getClass( ) ), is( equalTo( ProxiedClass.class.getCanonicalName( ) ) ) );
	}

	@Test
	public void realCanonicalNameOfNonProxyClassShouldReturnCorrectName( ) {
		assertThat( ClassUtil.getRealCanonicalName( ivInjector.getInstance( NonProxiedClass.class ).getClass( ) ), is( equalTo( NonProxiedClass.class.getCanonicalName( ) ) ) );
	}

	@Test
	public void realSimpleNameOfProxyClassShouldReturnCorrectName( ) {
		assertThat( ClassUtil.getRealSimpleName( ivInjector.getInstance( ProxiedClass.class ).getClass( ) ), is( equalTo( ProxiedClass.class.getSimpleName( ) ) ) );
	}

	@Test
	public void realSimpleNameOfNonProxyClassShouldReturnCorrectName( ) {
		assertThat( ClassUtil.getRealSimpleName( ivInjector.getInstance( NonProxiedClass.class ).getClass( ) ), is( equalTo( NonProxiedClass.class.getSimpleName( ) ) ) );
	}

	@Test
	public void assertSingletonAnnotationShouldThrowErrorOnNonSingleton( ) {
		ivExpectedException.expect( TechnicalException.class );
		ClassUtil.assertSingletonAnnotation( ProxiedClass.class );
	}

	@Test
	public void assertSingletonAnnotationShouldNotThrowErrorOnSingleton( ) {
		ClassUtil.assertSingletonAnnotation( SingletonClass.class );
	}

}

class NonProxiedClass {
}

class ProxiedClass {
}

@Singleton
class SingletonClass {

}

class Module extends AbstractModule {

	@Override
	protected void configure( ) {
		bindInterceptor( Matchers.subclassesOf( ProxiedClass.class ), Matchers.any( ), aInvocation -> aInvocation.proceed( ) );
	}

}
