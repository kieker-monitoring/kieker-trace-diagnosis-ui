/***************************************************************************
 * Copyright 2015-2016 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.gui.components.table;

import kieker.diagnosis.service.ServiceUtil;
import kieker.diagnosis.service.data.DataService;
import kieker.diagnosis.service.nameconverter.NameConverterService;

/**
 * @author Nils Christian Ehmke
 *
 * @param <S>
 *            The type of the table view generic type.
 * @param <T>
 *            The type of the content in all cells in the table colums created by this factory.
 */
public final class TimestampTableCellFactory<S, T> extends AbstractTableCellFactory<S, T> {

	private final NameConverterService ivNameConverterService = ServiceUtil.getService( NameConverterService.class );
	private final DataService ivDataService = ServiceUtil.getService( DataService.class );

	@Override
	protected String getItemLabel( final T aItem ) {
		return ivNameConverterService.toTimestampString( (Long) aItem, ivDataService.getTimeUnit( ) );
	}

}
