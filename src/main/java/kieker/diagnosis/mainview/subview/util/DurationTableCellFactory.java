package kieker.diagnosis.mainview.subview.util;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.util.Callback;
import kieker.diagnosis.domain.AbstractOperationCall;
import kieker.diagnosis.model.PropertiesModel;

public final class DurationTableCellFactory<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {

	@Override
	public TableCell<S, T> call(final TableColumn<S, T> p) {
		final TableCell<S, T> cell = new TableCell<S, T>() {
			@SuppressWarnings("unchecked")
			@Override
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
					this.setText(item.toString() + " " + NameConverter.toShortTimeUnit(PropertiesModel.getInstance().getTimeUnit()));
				} else {
					this.setText("");
				}
			}
		};

		return cell;

	}
}
