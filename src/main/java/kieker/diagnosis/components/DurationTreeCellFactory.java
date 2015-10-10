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

import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.util.Callback;
import kieker.diagnosis.domain.AbstractOperationCall;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.util.NameConverter;

/**
 * @author Nils Christian Ehmke
 */
public final class DurationTreeCellFactory<S, T> implements Callback<TreeTableColumn<S, T>, TreeTableCell<S, T>> {

	@Override
	public TreeTableCell<S, T> call(final TreeTableColumn<S, T> p) {
		final TreeTableCell<S, T> cell = new TreeTableCell<S, T>() {
			@SuppressWarnings("unchecked")
			@Override
			protected void updateItem(final Object item, final boolean empty) {
				final TreeTableRow<?> currentRow = this.getTreeTableRow();
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
					this.setText(item.toString() + " " + NameConverter.toShortTimeUnit(PropertiesModel.getInstance().getTimeUnit()));
				} else {
					this.setText("");
				}
			}
		};

		return cell;

	}
}
