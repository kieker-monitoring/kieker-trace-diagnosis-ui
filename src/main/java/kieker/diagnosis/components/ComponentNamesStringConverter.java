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
import kieker.diagnosis.util.Mapper;

/**
 * @author Nils Christian Ehmke
 */
public class ComponentNamesStringConverter extends StringConverter<ComponentNames> {
	
	private static Mapper<ComponentNames, String> componentMapper;

	static {
		ComponentNamesStringConverter.componentMapper = new Mapper<>();
		ComponentNamesStringConverter.componentMapper.map(ComponentNames.SHORT).to("Catalog");
		ComponentNamesStringConverter.componentMapper.map(ComponentNames.LONG).to("kieker.examples.bookstore.Catalog");
	}
	
	@Override
	public ComponentNames fromString(final String string) {
		return ComponentNamesStringConverter.componentMapper.invertedResolve(string);
	}

	@Override
	public String toString(final ComponentNames object) {
		return ComponentNamesStringConverter.componentMapper.resolve(object);
	}

}
