package kieker.diagnosis.architecture;

import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Nils Christian Ehmke
 */
@Configuration
@ComponentScan
@PropertySource ( "classpath:config.properties" )
public class ArchitectureTestConfiguration {

	@Bean
	public CacheManager cacheManager( ) {
		return new NoOpCacheManager( );
	}

}
