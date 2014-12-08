package kieker.gui.view;

import kieker.gui.model.ExecutionEntry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;

class ExecutionTraceTreeSelectionListener implements SelectionListener {

	/**
	 * 
	 */
	private final MainWindow mainWindow;

	/**
	 * @param mainWindow
	 */
	ExecutionTraceTreeSelectionListener(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		final Object data = e.item.getData();
		if (data instanceof ExecutionEntry) {
			this.mainWindow.lblNa_1.setText(Long.toString(((ExecutionEntry) data).getTraceID()));
			this.mainWindow.lblNa_3.setText(Long.toString(((ExecutionEntry) data).getDuration()));

			this.mainWindow.lblNa_4.setText(((ExecutionEntry) data).getContainer());
			this.mainWindow.lblNa_5.setText(((ExecutionEntry) data).getComponent());
			this.mainWindow.lblNa_6.setText(((ExecutionEntry) data).getOperation());
			this.mainWindow.lblNa_7.setText(Integer.toString(((ExecutionEntry) data).getStackDepth()));

			if (((ExecutionEntry) data).isFailed()) {
				this.mainWindow.lblNa_2.setText("Yes (" + ((ExecutionEntry) data).getFailedCause() + ")");
				this.mainWindow.lblNa_2.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
				this.mainWindow.lblFailed.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
			} else {
				this.mainWindow.lblNa_2.setText("No");
				this.mainWindow.lblNa_2.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
				this.mainWindow.lblFailed.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
			}

			this.mainWindow.lblNa_1.pack();
			this.mainWindow.lblNa_2.pack();
			this.mainWindow.lblNa_3.pack();
			this.mainWindow.lblNa_4.pack();
			this.mainWindow.lblNa_5.pack();
			this.mainWindow.lblNa_6.pack();
			this.mainWindow.lblNa_7.pack();
		}
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent e) {

	}

}