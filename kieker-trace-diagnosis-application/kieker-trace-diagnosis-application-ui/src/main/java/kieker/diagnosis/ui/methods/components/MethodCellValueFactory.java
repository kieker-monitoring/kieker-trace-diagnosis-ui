package kieker.diagnosis.ui.methods.components;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import kieker.diagnosis.architecture.service.properties.PropertiesService;
import kieker.diagnosis.service.data.MethodCall;
import kieker.diagnosis.service.settings.MethodAppearance;
import kieker.diagnosis.service.settings.properties.MethodAppearanceProperty;

@Singleton
public class MethodCellValueFactory implements Callback<CellDataFeatures<MethodCall, String>, ObservableValue<String>> {

	@Inject
	private PropertiesService ivPropertiesService;

	@Override
	public ObservableValue<String> call( final CellDataFeatures<MethodCall, String> aParam ) {
		final MethodAppearance methodAppearance = ivPropertiesService.loadApplicationProperty( MethodAppearanceProperty.class );
		return new ReadOnlyObjectWrapper<>( methodAppearance.convert( aParam.getValue( ).getMethod( ) ) );
	}

}
