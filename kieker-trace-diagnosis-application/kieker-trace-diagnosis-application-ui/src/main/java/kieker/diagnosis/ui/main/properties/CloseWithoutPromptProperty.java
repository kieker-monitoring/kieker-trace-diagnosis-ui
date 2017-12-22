package kieker.diagnosis.ui.main.properties;

import com.google.inject.Singleton;

import kieker.diagnosis.architecture.service.properties.BooleanApplicationProperty;

/**
 * @author Nils Christian Ehmke
 */
@Singleton
public class CloseWithoutPromptProperty extends BooleanApplicationProperty {

	@Override
	public Boolean getDefaultValue( ) {
		return Boolean.FALSE;
	}

	@Override
	public String getKey( ) {
		return "closeWithoutPrompt";
	}

}
