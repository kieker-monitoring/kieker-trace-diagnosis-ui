package kieker.diagnosis.ui.traces.components;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.util.Callback;
import kieker.diagnosis.architecture.service.properties.PropertiesService;
import kieker.diagnosis.service.data.MethodCall;
import kieker.diagnosis.service.settings.TimestampAppearance;
import kieker.diagnosis.service.settings.properties.TimestampProperty;

@Singleton
public class TimestampCellValueFactory implements Callback<CellDataFeatures<MethodCall, String>, ObservableValue<String>> {

	@Inject
	private PropertiesService ivPropertiesService;

	@Override
	public ObservableValue<String> call( final CellDataFeatures<MethodCall, String> aParam ) {
		final TimestampAppearance timestampAppearance = ivPropertiesService.loadApplicationProperty( TimestampProperty.class );
		return new ReadOnlyObjectWrapper<>( timestampAppearance.convert( aParam.getValue( ).getValue( ).getTimestamp( ) ) );
	}

}
