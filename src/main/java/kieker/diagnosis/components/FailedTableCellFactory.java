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

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.util.Callback;
import kieker.diagnosis.domain.AbstractOperationCall;

/**
 * @author Nils Christian Ehmke
 */
public final class FailedTableCellFactory<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {
	@Override
	public TableCell<S, T> call(final TableColumn<S, T> p) {
		final TableCell<S, T> cell = new TableCell<S, T>() {

			@Override
			@SuppressWarnings("unchecked")
			protected void updateItem(final Object item, final boolean empty) {
				final TableRow<?> currentRow = this.getTableRow();
				if (currentRow != null) {
					final Object rowItem = currentRow.getItem();

					this.getStyleClass().remove("failed");
					if ((rowItem != null) && AbstractOperationCall.class.isAssignableFrom(rowItem.getClass())) {
						if (((AbstractOperationCall<?>) rowItem).isFailed()) {
							this.getStyleClass().add("failed");
						}
					}
				}

				super.updateItem((T) item, empty);

				if (item != null) {
					this.setText(item.toString());
				} else {
					this.setText("");
				}
			}
		};

		return cell;

	}
}
