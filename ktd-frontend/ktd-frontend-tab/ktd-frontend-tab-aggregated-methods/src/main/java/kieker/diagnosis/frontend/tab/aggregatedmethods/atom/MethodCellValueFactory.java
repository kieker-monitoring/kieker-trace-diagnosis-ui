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

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import kieker.diagnosis.backend.data.AggregatedMethodCall;
import kieker.diagnosis.backend.properties.PropertiesService;
import kieker.diagnosis.backend.settings.MethodAppearance;
import kieker.diagnosis.backend.settings.properties.MethodAppearanceProperty;
import lombok.RequiredArgsConstructor;

/**
 * This is a cell factory for a table which shows the method of an {@link AggregatedMethodCall} in the configured
 * manner.
 *
 * @author Nils Christian Ehmke
 */
@RequiredArgsConstructor
public final class MethodCellValueFactory implements Callback<CellDataFeatures<AggregatedMethodCall, String>, ObservableValue<String>> {

	private final PropertiesService propertiesService;

	@Override
	public ObservableValue<String> call( final CellDataFeatures<AggregatedMethodCall, String> cellDataFeatures ) {
		final MethodAppearance methodAppearance = propertiesService.loadApplicationProperty( MethodAppearanceProperty.class );
		final String method = cellDataFeatures.getValue( ).getMethod( );
		final String convertedMethodString = methodAppearance.convert( method );

		return new ReadOnlyObjectWrapper<>( convertedMethodString );
	}

}
