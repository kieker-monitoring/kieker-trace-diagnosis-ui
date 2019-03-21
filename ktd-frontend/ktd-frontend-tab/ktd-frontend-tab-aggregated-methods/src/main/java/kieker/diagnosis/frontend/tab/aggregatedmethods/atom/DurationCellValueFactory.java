/***************************************************************************
 * Copyright 2015-2019 Kieker Project (http://kieker-monitoring.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/

package kieker.diagnosis.frontend.tab.aggregatedmethods.atom;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import kieker.diagnosis.backend.data.AggregatedMethodCall;
import kieker.diagnosis.backend.properties.PropertiesService;
import kieker.diagnosis.backend.settings.properties.TimeUnitProperty;
import lombok.RequiredArgsConstructor;

/**
 * This is a cell factory for a table which shows the duration of an {@link AggregatedMethodCall} in the configured
 * manner.
 *
 * @author Nils Christian Ehmke
 */
@RequiredArgsConstructor
public final class DurationCellValueFactory implements Callback<CellDataFeatures<AggregatedMethodCall, String>, ObservableValue<String>> {

	private final PropertiesService propertiesService;
	private final Function<AggregatedMethodCall, Long> getter;

	@Override
	public ObservableValue<String> call( final CellDataFeatures<AggregatedMethodCall, String> cellDataFeatures ) {
		final TimeUnit timeUnit = propertiesService.loadApplicationProperty( TimeUnitProperty.class );
		final Long duration = getter.apply( cellDataFeatures.getValue( ) );
		final String convertedDurationString = Long.toString( timeUnit.convert( duration, TimeUnit.NANOSECONDS ) ).intern( );

		return new ReadOnlyStringWrapper( convertedDurationString );
	}

}
