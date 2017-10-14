package kieker.diagnosis;

import com.google.inject.AbstractModule;

import kieker.diagnosis.architecture.KiekerTraceDiagnosisArchitectureModule;
import kieker.diagnosis.service.KiekerTraceDiagnosisServiceModule;

/**
 * This is the Guice module for the application.
 *
 * @author Nils Christian Ehmke
 */
public class KiekerTraceDiagnosisModule extends AbstractModule {

	@Override
	protected void configure( ) {
		// We need to make sure that the Guice module from the service and the architecture sub-project is installed
		install( new KiekerTraceDiagnosisServiceModule( ) );
		install( new KiekerTraceDiagnosisArchitectureModule( ) );
	}

}
