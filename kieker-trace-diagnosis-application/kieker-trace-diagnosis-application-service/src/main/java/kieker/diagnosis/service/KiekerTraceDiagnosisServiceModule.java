package kieker.diagnosis.service;

import com.google.inject.AbstractModule;

import kieker.diagnosis.architecture.KiekerTraceDiagnosisArchitectureModule;

/**
 * This is the Guice module for the service part of the application.
 *
 * @author Nils Christian Ehmke
 */
public class KiekerTraceDiagnosisServiceModule extends AbstractModule {

	@Override
	protected void configure( ) {
		// We need to make sure that the Guice module from the architecture sub-project is installed
		install( new KiekerTraceDiagnosisArchitectureModule( ) );
	}

}
