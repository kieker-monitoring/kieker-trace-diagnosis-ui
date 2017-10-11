package kieker.diagnosis.architecture;

import java.lang.reflect.Method;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;

import kieker.diagnosis.architecture.monitoring.MonitoringInterceptor;
import kieker.diagnosis.architecture.service.ServiceBase;
import kieker.diagnosis.architecture.service.cache.CacheInterceptor;
import kieker.diagnosis.architecture.service.cache.InvalidateCache;
import kieker.diagnosis.architecture.service.cache.UseCache;
import kieker.diagnosis.architecture.ui.ControllerBase;
import kieker.diagnosis.architecture.ui.ErrorHandlingInterceptor;

/**
 * This is the Guice module for the architecture.
 *
 * @author Nils Christian Ehmke
 */
public class KiekerTraceDiagnosisArchitectureModule extends AbstractModule {

	@Override
	protected void configure( ) {
		// Create the interceptors
		final ErrorHandlingInterceptor errorHandlingInterceptor = new ErrorHandlingInterceptor( );
		final MonitoringInterceptor monitoringInterceptor = new MonitoringInterceptor( );
		final CacheInterceptor cacheInterceptor = new CacheInterceptor( );

		// UI
		bindInterceptor( Matchers.subclassesOf( ControllerBase.class ), Matchers.any( ), errorHandlingInterceptor );
		bindInterceptor( Matchers.subclassesOf( ControllerBase.class ), Matchers.not( new SyntheticMethodMatcher( ) ), monitoringInterceptor );

		// Service
		bindInterceptor( Matchers.subclassesOf( ServiceBase.class ), Matchers.annotatedWith( UseCache.class ).or( Matchers.annotatedWith( InvalidateCache.class ) ),
				cacheInterceptor );
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
