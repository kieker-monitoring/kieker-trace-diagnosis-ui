package kieker.diagnosis.ui.aggregatedmethods.components;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import com.google.inject.Inject;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import kieker.diagnosis.architecture.service.properties.PropertiesService;
import kieker.diagnosis.service.data.AggregatedMethodCall;
import kieker.diagnosis.service.settings.properties.TimeUnitProperty;

public class DurationCellValueFactory implements Callback<CellDataFeatures<AggregatedMethodCall, Long>, ObservableValue<Long>> {

	@Inject
	private PropertiesService ivPropertiesService;

	private Function<AggregatedMethodCall, Long> ivGetter;

	public void setGetter( final Function<AggregatedMethodCall, Long> aGetter ) {
		ivGetter = aGetter;
	}

	@Override
	public ObservableValue<Long> call( final CellDataFeatures<AggregatedMethodCall, Long> aParam ) {
		final TimeUnit timeUnit = ivPropertiesService.loadApplicationProperty( TimeUnitProperty.class );
		return new ReadOnlyObjectWrapper<>( timeUnit.convert( ivGetter.apply( aParam.getValue( ) ), TimeUnit.NANOSECONDS ) );
	}

}
