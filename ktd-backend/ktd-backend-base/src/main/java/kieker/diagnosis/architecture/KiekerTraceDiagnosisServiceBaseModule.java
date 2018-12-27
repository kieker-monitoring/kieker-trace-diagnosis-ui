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

package kieker.diagnosis.architecture;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;

import kieker.diagnosis.architecture.monitoring.MonitoringInterceptor;
import kieker.diagnosis.architecture.service.ServiceBase;
import kieker.diagnosis.architecture.service.ServiceFactory;
import kieker.diagnosis.architecture.service.cache.CacheInterceptor;
import kieker.diagnosis.architecture.service.cache.InvalidateCache;
import kieker.diagnosis.architecture.service.cache.UseCache;

public final class KiekerTraceDiagnosisServiceBaseModule extends AbstractModule {

	@Override
	protected void configure( ) {
		final CacheInterceptor cacheInterceptor = new CacheInterceptor( );
		final MonitoringInterceptor monitoringInterceptor = new MonitoringInterceptor( );

		final Matcher<AnnotatedElement> cacheMatcher = Matchers.annotatedWith( UseCache.class ).or( Matchers.annotatedWith( InvalidateCache.class ) );
		bindInterceptor( Matchers.subclassesOf( ServiceBase.class ), cacheMatcher, cacheInterceptor );
		bindInterceptor( Matchers.subclassesOf( ServiceBase.class ), Matchers.not( new SyntheticMethodMatcher( ) ), monitoringInterceptor );

		requestStaticInjection( ServiceFactory.class );
	}
	

	/**
	 * A helper class to find out whether a method is synthetic or not.
	 *
	 * @author Nils Christian Ehmke
	 */
	private static final class SyntheticMethodMatcher extends AbstractMatcher<Method> {

		@Override
		public boolean matches( final Method aMethod ) {
			return aMethod.isSynthetic( );
		}
	}

}
