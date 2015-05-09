package kieker.diagnosis.mainview.subview.util;

import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.util.Callback;
import kieker.diagnosis.domain.AbstractOperationCall;
import kieker.diagnosis.model.PropertiesModel;

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
