package kieker.diagnosis.architecture;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;

import kieker.diagnosis.architecture.monitoring.MonitoringInterceptor;
import kieker.diagnosis.architecture.service.ServiceBase;
import kieker.diagnosis.architecture.service.cache.CacheInterceptor;
import kieker.diagnosis.architecture.service.cache.InvalidateCache;
import kieker.diagnosis.architecture.service.cache.UseCache;
import kieker.diagnosis.architecture.ui.ControllerBase;
import kieker.diagnosis.architecture.ui.ErrorHandlingInterceptor;
import kieker.diagnosis.architecture.ui.ViewModelBase;

/**
 * This is the Guice module for the architecture.
 *
 * @author Nils Christian Ehmke
 */
public class KiekerTraceDiagnosisArchitectureModule extends AbstractModule {

	@Override
	protected void configure( ) {
		// Create the interceptors once. Some of them should not exist multiple times.
		final ErrorHandlingInterceptor errorHandlingInterceptor = new ErrorHandlingInterceptor( );
		final MonitoringInterceptor monitoringInterceptor = new MonitoringInterceptor( );
		final CacheInterceptor cacheInterceptor = new CacheInterceptor( );

		// Add the interceptors for the UI
		bindInterceptor( Matchers.subclassesOf( ControllerBase.class ), Matchers.any( ), errorHandlingInterceptor );
		bindInterceptor( Matchers.subclassesOf( ControllerBase.class ), Matchers.not( new SyntheticMethodMatcher( ) ), monitoringInterceptor );
		bindInterceptor( Matchers.subclassesOf( ViewModelBase.class ), Matchers.not( new SyntheticMethodMatcher( ) ), monitoringInterceptor );

		// Add the interceptors for the services
		final Matcher<AnnotatedElement> cacheMatcher = Matchers.annotatedWith( UseCache.class ).or( Matchers.annotatedWith( InvalidateCache.class ) );
		bindInterceptor( Matchers.subclassesOf( ServiceBase.class ), cacheMatcher, cacheInterceptor );
		bindInterceptor( Matchers.subclassesOf( ServiceBase.class ), Matchers.not( new SyntheticMethodMatcher( ) ), monitoringInterceptor );
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
