package kieker.diagnosis.application.service;

import kieker.diagnosis.architecture.ArchitectureConfiguration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan
@Import ( ArchitectureConfiguration.class )
public class ServiceTestConfiguration {

	@Bean
	public CacheManager cacheManager( ) {
		return new NoOpCacheManager( );
	}

}
