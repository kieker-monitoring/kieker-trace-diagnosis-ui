package kieker.gui.view;

import java.util.List;

import kieker.gui.model.RecordEntry;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

class RecordsTableSetDataListener implements Listener {

	@Override
	public void handleEvent(final Event event) {
		// Get the necessary information from the event
		final Table table = (Table) event.widget;
		final TableItem item = (TableItem) event.item;
		final int tableIndex = event.index;

		// Get the data for the current row
		final List<RecordEntry> records = (List<RecordEntry>) table.getData();
		final RecordEntry record = records.get(tableIndex);

		// Get the data to display
		final String timestampStr = Long.toString(record.getTimestamp());
		final String type = record.getType();
		final String recordStr = record.getRepresentation();
		item.setText(new String[] { timestampStr, type, recordStr });
	}

}
