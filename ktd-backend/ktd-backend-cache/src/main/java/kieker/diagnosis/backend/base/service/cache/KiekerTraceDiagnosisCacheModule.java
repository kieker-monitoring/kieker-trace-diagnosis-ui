package kieker.diagnosis.backend.base.service.cache;

import java.lang.reflect.AnnotatedElement;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;

import kieker.diagnosis.backend.base.service.ServiceBase;

public final class KiekerTraceDiagnosisCacheModule extends AbstractModule {

	@Override
	protected void configure( ) {
		final CacheInterceptor cacheInterceptor = new CacheInterceptor( );

		final Matcher<AnnotatedElement> cacheMatcher = Matchers.annotatedWith( UseCache.class ).or( Matchers.annotatedWith( InvalidateCache.class ) );
		bindInterceptor( Matchers.subclassesOf( ServiceBase.class ), cacheMatcher, cacheInterceptor );
	}
	
}
