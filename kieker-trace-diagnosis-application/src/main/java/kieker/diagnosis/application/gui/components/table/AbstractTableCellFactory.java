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

package kieker.diagnosis.application.gui.components.table;

import kieker.diagnosis.architecture.gui.components.AutowireCandidate;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * @author Nils Christian Ehmke
 *
 * @param <S>
 *            The type of the table view generic type.
 * @param <T>
 *            The type of the content in all cells in the table colums created by this factory.
 */
public abstract class AbstractTableCellFactory<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>>, AutowireCandidate {

	@Override
	public final TableCell<S, T> call( final TableColumn<S, T> aTableColumn ) {
		return new FailedTableCell( );
	}

	protected abstract String getItemLabel( T aItem );

	/**
	 * @author Nils Christian Ehmke
	 */
	private final class FailedTableCell extends TableCell<S, T> {

		@Override
		protected void updateItem( final T aItem, final boolean aEmpty ) {
			super.updateItem( aItem, aEmpty );

			if ( aEmpty || ( aItem == null ) ) {
				setText( null );
				setGraphic( null );
			} else {
				setText( getItemLabel( aItem ) );
			}
		}
	}

}
