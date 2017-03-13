package kieker.diagnosis.application.service.properties;

import kieker.diagnosis.architecture.service.properties.AbstractStringApplicationProperty;

import org.springframework.stereotype.Component;

@Component
public final class LastImportPathProperty extends AbstractStringApplicationProperty {

	@Override
	public String getKey( ) {
		return "lastImportPath";
	}

	@Override
	public String getDefaultValue( ) {
		return ".";
	}

}
