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

package kieker.diagnosis.components;

import javafx.util.StringConverter;
import kieker.diagnosis.model.PropertiesModel.ComponentNames;

/**
 * @author Nils Christian Ehmke
 */
public class ComponentNamesStringConverter extends StringConverter<ComponentNames> {

	@Override
	public String toString(final ComponentNames object) {
		return (object == ComponentNames.SHORT) ? "Catalog" : "kieker.examples.bookstore.Catalog";
	}

	@Override
	public ComponentNames fromString(final String string) {
		return ("Catalog".equals(string)) ? ComponentNames.SHORT : ComponentNames.LONG;
	}

}
