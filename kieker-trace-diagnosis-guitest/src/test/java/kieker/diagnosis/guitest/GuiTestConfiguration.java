package kieker.diagnosis.guitest;

import kieker.diagnosis.application.Main;
import kieker.diagnosis.guitestarchitecture.GuiTestCoreConfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan ( nameGenerator = FullyQualifiedBeanNameGenerator.class )
@Import ( GuiTestCoreConfiguration.class )
public class GuiTestConfiguration {

	@Bean
	public Main application( ) {
		return new Main( );
	}

}
