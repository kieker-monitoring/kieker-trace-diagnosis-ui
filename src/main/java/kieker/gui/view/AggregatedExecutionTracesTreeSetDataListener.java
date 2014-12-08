package kieker.gui.view;

import java.util.List;

import kieker.gui.model.AggregatedExecutionEntry;
import kieker.gui.model.Properties;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

class AggregatedExecutionTracesTreeSetDataListener implements Listener {

	@Override
	public void handleEvent(final Event event) {
		// Get the necessary information from the event
		final Tree tree = (Tree) event.widget;
		final TreeItem item = (TreeItem) event.item;
		final int tableIndex = event.index;
		final TreeItem parent = item.getParentItem();

		// Decide whether the current item is a root or not
		final AggregatedExecutionEntry executionEntry;
		if (parent == null) {
			executionEntry = ((List<AggregatedExecutionEntry>) tree.getData()).get(tableIndex);
		} else {
			// executionEntry = ((AggregatedExecutionEntry) parent.getData()).getChildren().get(tableIndex);
			executionEntry = null;
		}

		String componentName = executionEntry.getComponent();
		if (Properties.getInstance().isShortComponentNames()) {
			final int lastPointPos = componentName.lastIndexOf('.');
			componentName = componentName.substring(lastPointPos + 1);
		}
		String operationString = executionEntry.getOperation();
		if (Properties.getInstance().isShortOperationParameters()) {
			operationString = operationString.replaceAll("\\(..*\\)", "(...)");

			final int lastPointPos = operationString.lastIndexOf('.', operationString.length() - 5);
			operationString = operationString.substring(lastPointPos + 1);
		}
		item.setText(new String[] { executionEntry.getContainer(), componentName, operationString, Integer.toString(executionEntry.getCalls()) });

		// if (executionEntry.isFailed()) {
		// final Color colorRed = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
		// item.setForeground(colorRed);
		// }

		item.setData(executionEntry);
		item.setItemCount(0);
		// item.setItemCount(executionEntry.getChildren().size());
	}
}