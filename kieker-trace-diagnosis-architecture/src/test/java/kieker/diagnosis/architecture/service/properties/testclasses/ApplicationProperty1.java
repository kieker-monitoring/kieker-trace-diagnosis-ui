package kieker.diagnosis.architecture.service.properties.testclasses;

import kieker.diagnosis.architecture.service.properties.AbstractBooleanApplicationProperty;

import org.springframework.stereotype.Component;

/**
 * @author Nils Christian Ehmke
 */
@Component
public class ApplicationProperty1 extends AbstractBooleanApplicationProperty {

	@Override
	public String getKey( ) {
		return "applicationProperty1";
	}

	@Override
	public Boolean getDefaultValue( ) {
		return Boolean.TRUE;
	}

}
