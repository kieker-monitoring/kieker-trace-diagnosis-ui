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
import kieker.diagnosis.model.PropertiesModel.OperationNames;
import kieker.diagnosis.util.Mapper;

/**
 * @author Nils Christian Ehmke
 */
public class OperationNamesStringConverter extends StringConverter<OperationNames> {

	private static Mapper<OperationNames, String> operationMapper;

	static {
		OperationNamesStringConverter.operationMapper = new Mapper<>();
		OperationNamesStringConverter.operationMapper.map(OperationNames.SHORT).to("getBook(...)");
		OperationNamesStringConverter.operationMapper.map(OperationNames.LONG).to("public void kieker.examples.bookstore.Catalog.getBook(boolean)");
	}

	@Override
	public OperationNames fromString(final String string) {
		return OperationNamesStringConverter.operationMapper.invertedResolve(string);
	}

	@Override
	public String toString(final OperationNames object) {
		return OperationNamesStringConverter.operationMapper.resolve(object);
	}

}
