package kieker.diagnosis.ui.aggregatedmethods.components;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import kieker.diagnosis.architecture.service.properties.PropertiesService;
import kieker.diagnosis.service.data.AggregatedMethodCall;
import kieker.diagnosis.service.settings.MethodAppearance;
import kieker.diagnosis.service.settings.properties.MethodAppearanceProperty;

@Singleton
public class MethodCellValueFactory implements Callback<CellDataFeatures<AggregatedMethodCall, String>, ObservableValue<String>> {

	@Inject
	private PropertiesService ivPropertiesService;

	@Override
	public ObservableValue<String> call( final CellDataFeatures<AggregatedMethodCall, String> aParam ) {
		final MethodAppearance methodAppearance = ivPropertiesService.loadApplicationProperty( MethodAppearanceProperty.class );
		return new ReadOnlyObjectWrapper<>( methodAppearance.convert( aParam.getValue( ).getMethod( ) ) );
	}

}
