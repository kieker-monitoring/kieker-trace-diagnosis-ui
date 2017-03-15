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

import kieker.diagnosis.architecture.gui.components.AutowireCandidate;

import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

/**
 * @author Nils Christian Ehmke
 *
 * @param <S>
 *            The type of the table.
 * @param <T>
 *            The type of the content in all cells in the table columns.
 */
public abstract class AbstractTreeTableCellFactory<S, T> implements Callback<TreeTableColumn<S, T>, TreeTableCell<S, T>>, AutowireCandidate {

	@Override
	public final TreeTableCell<S, T> call( final TreeTableColumn<S, T> aTreeTableColumn ) {
		return new FailedTableCell( );
	}

	protected abstract String getItemLabel( T aItem );

	/**
	 * @author Nils Christian Ehmke
	 */
	private final class FailedTableCell extends TreeTableCell<S, T> {

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
