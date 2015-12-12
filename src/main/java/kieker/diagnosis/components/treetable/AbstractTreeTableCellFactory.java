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

package kieker.diagnosis.components.treetable;

import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.util.Callback;
import kieker.diagnosis.domain.AbstractOperationCall;

public abstract class AbstractTreeTableCellFactory<S, T> implements Callback<TreeTableColumn<S, T>, TreeTableCell<S, T>> {

	@Override
	public final TreeTableCell<S, T> call(final TreeTableColumn<S, T> aTreeTableColumn) {
		return new FailedTableCell();
	}

	protected abstract String getItemLabel(T aItem);

	private final class FailedTableCell extends TreeTableCell<S, T> {

		@Override
		protected void updateItem(final T aItem, final boolean aEmpty) {
			this.setFailedStyle();

			super.updateItem(aItem, aEmpty);

			if (aEmpty || (aItem == null)) {
				this.setText(null);
				this.setGraphic(null);
			} else {
				this.setText(AbstractTreeTableCellFactory.this.getItemLabel(aItem));
			}
		}

		private void setFailedStyle() {
			final TreeTableRow<?> currentRow = super.getTreeTableRow();

			if (currentRow != null) {
				final Object rowItem = currentRow.getItem();

				super.getStyleClass().remove("failed");
				if ((rowItem instanceof AbstractOperationCall) && ((AbstractOperationCall<?>) rowItem).isFailed()) {
					super.getStyleClass().add("failed");
				}
			}
		}

	}
}
