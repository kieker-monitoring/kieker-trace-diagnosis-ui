package kieker.diagnosis.backend.base.monitoring;

import java.lang.reflect.Method;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;

import kieker.diagnosis.backend.base.service.ServiceBase;

public final class KiekerTraceDiagnosisMonitoringModule extends AbstractModule {

	@Override
	protected void configure( ) {
		final MonitoringInterceptor monitoringInterceptor = new MonitoringInterceptor( );

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
