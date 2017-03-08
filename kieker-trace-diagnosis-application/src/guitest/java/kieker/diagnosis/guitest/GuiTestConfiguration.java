package kieker.diagnosis.guitest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import kieker.diagnosis.Main;
import kieker.diagnosis.guitest.GuiTestCoreConfiguration;

@Configuration
@ComponentScan
@Import ( GuiTestCoreConfiguration.class )
public class GuiTestConfiguration {

	@Bean
	public Main application( ) {
		return new Main( );
	}

}
