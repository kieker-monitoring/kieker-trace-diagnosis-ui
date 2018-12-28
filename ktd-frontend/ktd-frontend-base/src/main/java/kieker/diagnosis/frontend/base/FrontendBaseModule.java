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

package kieker.diagnosis.frontend.base;

import java.lang.reflect.Method;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;

import kieker.diagnosis.backend.monitoring.MonitoringInterceptor;
import kieker.diagnosis.frontend.base.ui.ControllerBase;
import kieker.diagnosis.frontend.base.ui.ErrorHandlingInterceptor;
import kieker.diagnosis.frontend.base.ui.ViewModelBase;

/**
 * This is the Guice module for the frontend base.
 *
 * @author Nils Christian Ehmke
 */
public final class FrontendBaseModule extends AbstractModule {

	@Override
	protected void configure( ) {
		final ErrorHandlingInterceptor errorHandlingInterceptor = new ErrorHandlingInterceptor( );
		final MonitoringInterceptor monitoringInterceptor = new MonitoringInterceptor( );

		bindInterceptor( Matchers.subclassesOf( ControllerBase.class ), Matchers.any( ), errorHandlingInterceptor );
		bindInterceptor( Matchers.subclassesOf( ControllerBase.class ), Matchers.not( new SyntheticMethodMatcher( ) ), monitoringInterceptor );
		bindInterceptor( Matchers.subclassesOf( ViewModelBase.class ), Matchers.not( new SyntheticMethodMatcher( ) ), monitoringInterceptor );
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
