/***************************************************************************
 * Copyright 2015-2017 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.application.gui.components.treetable;

import kieker.diagnosis.application.service.data.DataService;
import kieker.diagnosis.application.service.nameconverter.NameConverterService;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Nils Christian Ehmke
 *
 * @param <S>
 *            The type of the table.
 * @param <T>
 *            The type of the content in all cells in the table columns.
 */
public final class TimestampTreeTableCellFactory<S, T> extends AbstractTreeTableCellFactory<S, T> {

	@Autowired
	private NameConverterService ivNameConverterService;

	@Autowired
	private DataService ivDataService;

	@Override
	protected String getItemLabel( final T aItem ) {
		return ivNameConverterService.toTimestampString( (Long) aItem, ivDataService.getTimeUnit( ) );
	}

}
