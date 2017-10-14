package kieker.diagnosis.ui.traces.components;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.util.Callback;
import kieker.diagnosis.architecture.service.properties.PropertiesService;
import kieker.diagnosis.service.data.MethodCall;
import kieker.diagnosis.service.settings.MethodAppearance;
import kieker.diagnosis.service.settings.properties.MethodAppearanceProperty;

/**
 * This is a cell factory for a tree table which shows the method of a method call in the configured manner. It has to be in the CDI context, as it has to have
 * access to the application properties.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class MethodCellValueFactory implements Callback<CellDataFeatures<MethodCall, String>, ObservableValue<String>> {

	@Inject
	private PropertiesService ivPropertiesService;

	@Override
	public ObservableValue<String> call( final CellDataFeatures<MethodCall, String> aParam ) {
		final MethodAppearance methodAppearance = ivPropertiesService.loadApplicationProperty( MethodAppearanceProperty.class );
		return new ReadOnlyObjectWrapper<>( methodAppearance.convert( aParam.getValue( ).getValue( ).getMethod( ) ) );
	}

}
