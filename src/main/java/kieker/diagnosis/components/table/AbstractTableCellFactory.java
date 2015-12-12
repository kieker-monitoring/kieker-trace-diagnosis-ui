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

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.util.Callback;
import kieker.diagnosis.domain.AbstractOperationCall;

public abstract class AbstractTableCellFactory<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {

	@Override
	public final TableCell<S, T> call(final TableColumn<S, T> aTableColumn) {
		return new FailedTableCell();
	}

	protected abstract String getItemLabel(T aItem);

	private final class FailedTableCell extends TableCell<S, T> {

		@Override
		protected void updateItem(final T aItem, final boolean aEmpty) {
			this.setFailedStyle();

			super.updateItem(aItem, aEmpty);

			if (aEmpty || (aItem == null)) {
				this.setText(null);
				this.setGraphic(null);
			} else {
				this.setText(AbstractTableCellFactory.this.getItemLabel(aItem));
			}
		}

		private void setFailedStyle() {
			final TableRow<?> currentRow = super.getTableRow();

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
