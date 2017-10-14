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

/**
 * This is a cell factory for a tree table which shows the duration of a method call in the configured manner. It has to be in the CDI context, as it has to
 * have access to the application properties.
 *
 * @author Nils Christian Ehmke
 */
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
