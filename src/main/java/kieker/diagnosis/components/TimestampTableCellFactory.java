package kieker.diagnosis.components;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.util.Callback;
import kieker.diagnosis.domain.AbstractOperationCall;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.util.NameConverter;

public class TimestampTableCellFactory<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {

	@Override
	public TableCell<S, T> call(final TableColumn<S, T> p) {
		return new FailedTableCell();
	}

	private final class FailedTableCell extends TableCell<S, T> {

		@Override
		protected void updateItem(final T item, final boolean empty) {
			setFailedStyle();

			super.updateItem(item, empty);

			if (empty || item == null) {
				setText(null);
				setGraphic(null);
			} else {
				setText(NameConverter.toTimestampString((Long) item, DataModel.getInstance().getTimeUnit()));
			}
		}

		private void setFailedStyle() {
			final TableRow<?> currentRow = super.getTableRow();

			if (currentRow != null) {
				final Object rowItem = currentRow.getItem();

				super.getStyleClass().remove("failed");
				if (rowItem instanceof AbstractOperationCall) {
					if (((AbstractOperationCall<?>) rowItem).isFailed()) {
						super.getStyleClass().add("failed");
					}
				}
			}
		}

	}
}