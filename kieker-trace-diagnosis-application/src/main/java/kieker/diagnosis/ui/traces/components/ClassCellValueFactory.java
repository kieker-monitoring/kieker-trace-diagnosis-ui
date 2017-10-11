package kieker.diagnosis.ui.traces.components;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.util.Callback;
import kieker.diagnosis.architecture.service.properties.PropertiesService;
import kieker.diagnosis.service.data.MethodCall;
import kieker.diagnosis.service.settings.ClassAppearance;
import kieker.diagnosis.service.settings.properties.ClassAppearanceProperty;

@Singleton
public class ClassCellValueFactory implements Callback<CellDataFeatures<MethodCall, String>, ObservableValue<String>> {

	@Inject
	private PropertiesService ivPropertiesService;

	@Override
	public ObservableValue<String> call( final CellDataFeatures<MethodCall, String> aParam ) {
		final ClassAppearance classAppearance = ivPropertiesService.loadApplicationProperty( ClassAppearanceProperty.class );
		return new ReadOnlyObjectWrapper<>( classAppearance.convert( aParam.getValue( ).getValue( ).getClazz( ) ) );
	}

}
