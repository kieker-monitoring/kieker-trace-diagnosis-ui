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

package kieker.diagnosis.backend.monitoring;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * This interceptor is responsible for monitoring a method invocation. {@link MonitoringProbe}s are used to handle the
 * actual monitoring.
 *
 * @author Nils Christian Ehmke
 */
public final class MonitoringInterceptor implements MethodInterceptor {

	@Override
	public Object invoke( final MethodInvocation aMethodInvocation ) throws Throwable {
		final MonitoringProbe probe = MonitoringUtil.createMonitoringProbe( aMethodInvocation.getThis( ).getClass( ), aMethodInvocation.getMethod( ).toString( ) );

		try {
			return aMethodInvocation.proceed( );
		} catch ( final Throwable t ) {
			probe.fail( t );
			throw t;
		} finally {
			probe.stop( );
		}
	}

}
