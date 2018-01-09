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

package kieker.diagnosis.architecture.ui;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import kieker.diagnosis.architecture.common.ClassUtil;
import kieker.diagnosis.architecture.common.ExceptionUtil;
import kieker.diagnosis.architecture.exception.BusinessRuntimeException;

/**
 * This is an interceptor which handles all kind of {@link Throwable Throwables}. It is usually used around controller actions. If an exception occurs, the
 * interceptor logs the exception, shows an error dialog, and returns {@code null}. If the exception is a {@link BusinessRuntimeException}, the error is not
 * logged and the error dialog indicates that the error is a business error.
 *
 * @author Nils Christian Ehmke
 */
public final class ErrorHandlingInterceptor implements MethodInterceptor {

	@Override
	public Object invoke( final MethodInvocation aMethodInvocation ) throws Throwable {
		try {
			return aMethodInvocation.proceed( );
		} catch ( final Throwable ex ) {
			// Get the logger name
			final Object controller = aMethodInvocation.getThis( );
			final Class<?> controllerClass = ClassUtil.getRealClass( controller.getClass( ) );

			// Now handle the exception
			ExceptionUtil.handleException( ex, controllerClass.getName( ) );

			return null;
		}
	}

}
