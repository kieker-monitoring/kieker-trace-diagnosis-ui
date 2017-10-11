package kieker.diagnosis.architecture.monitoring;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * This interceptor is responsible for monitoring a method invocation. {@link MonitoringProbe}s are used to handle the actual monitoring.
 *
 * @author Nils Christian Ehmke
 */
public final class MonitoringInterceptor implements MethodInterceptor {

	@Override
	public Object invoke( final MethodInvocation aMethodInvocation ) throws Throwable {
		final MonitoringProbe probe = new MonitoringProbe( aMethodInvocation.getThis( ).getClass( ), aMethodInvocation.getMethod( ).toString( ) );

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
