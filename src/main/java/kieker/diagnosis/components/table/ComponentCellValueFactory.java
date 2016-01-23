/***************************************************************************
 * Copyright 2015 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.components.table;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import javafx.beans.NamedArg;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.model.PropertiesModel.ComponentNames;
import kieker.diagnosis.util.NameConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ComponentCellValueFactory implements Callback<CellDataFeatures<?, String>, ObservableValue<String>> {

	private static final Logger LOGGER = LogManager.getLogger(ComponentCellValueFactory.class);

	private final String ivProperty;

	public ComponentCellValueFactory(@NamedArg(value = "property") final String aProperty) {
		this.ivProperty = aProperty.substring(0, 1).toUpperCase(Locale.ROOT) + aProperty.substring(1);
	}

	@Override
	public ObservableValue<String> call(final CellDataFeatures<?, String> aCall) {
		try {
			final Method getter = aCall.getValue().getClass().getMethod("get" + this.ivProperty, new Class<?>[0]);
			String componentName = (String) getter.invoke(aCall.getValue(), new Object[0]);

			if (PropertiesModel.getInstance().getComponentNames() == ComponentNames.SHORT) {
				componentName = NameConverter.toShortComponentName(componentName);
			}

			return new ReadOnlyObjectWrapper<String>(componentName);
		} catch (final NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			ComponentCellValueFactory.LOGGER.warn(ex);
			return null;
		}
	}

}
