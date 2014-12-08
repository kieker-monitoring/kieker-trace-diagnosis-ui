package kieker.gui.view;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Widget;

public final class ExplorerTreeSelectionAdapter extends SelectionAdapter {

	private final MainWindow mainWindow;

	public ExplorerTreeSelectionAdapter(final MainWindow mainWindow) {
		this.mainWindow = mainWindow;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		final Widget selectedWidget = e.item;

		if (this.mainWindow.recordsTreeItem == selectedWidget) {
			this.mainWindow.showRecords();
		} else if (this.mainWindow.executionTracesTreeItem == selectedWidget) {
			this.mainWindow.showExecutionTraces();
		} else if (this.mainWindow.trtmAggregatedExecutionTraces == selectedWidget) {
			this.mainWindow.showAggregatedExecutionTraces();
		} else {
			this.mainWindow.setVisibleMainComponent(null);
			this.mainWindow.lblNa.setText("");
		}
	}

}
