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
