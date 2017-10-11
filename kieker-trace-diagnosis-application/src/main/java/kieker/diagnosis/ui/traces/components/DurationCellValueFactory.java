package kieker.diagnosis.ui.traces.components;

import java.util.concurrent.TimeUnit;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.util.Callback;
import kieker.diagnosis.architecture.service.properties.PropertiesService;
import kieker.diagnosis.service.data.MethodCall;
import kieker.diagnosis.service.settings.properties.TimeUnitProperty;

@Singleton
public class DurationCellValueFactory implements Callback<CellDataFeatures<MethodCall, Long>, ObservableValue<Long>> {

	@Inject
	private PropertiesService ivPropertiesService;

	@Override
	public ObservableValue<Long> call( final CellDataFeatures<MethodCall, Long> aParam ) {
		final TimeUnit timeUnit = ivPropertiesService.loadApplicationProperty( TimeUnitProperty.class );
		return new ReadOnlyObjectWrapper<>( timeUnit.convert( aParam.getValue( ).getValue( ).getDuration( ), TimeUnit.NANOSECONDS ) );
	}

}
