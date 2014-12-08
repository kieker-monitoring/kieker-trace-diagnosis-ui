package kieker.gui.view;

import kieker.gui.model.domain.AggregatedExecutionEntry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

public final class AggregatedTraceDetailComposite extends Composite {

	private final Label lblExecutionContainerDisplay;
	private final Label lblComponentDisplay;
	private final Label lblOperationDisplay;
	private final Label lblFailed;
	private final Label lblFailedDisplay;

	private final Label lblCalledDisplay;

	public AggregatedTraceDetailComposite(final Composite parent, final int style) {
		super(parent, style);
		this.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.setLayout(new GridLayout(2, false));

		final Label lblExecutionContainer = new Label(this, SWT.NONE);
		lblExecutionContainer.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblExecutionContainer.setText("Execution Container:");

		this.lblExecutionContainerDisplay = new Label(this, SWT.NONE);
		this.lblExecutionContainerDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblExecutionContainerDisplay.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		this.lblExecutionContainerDisplay.setText("N/A");

		final Label lblComponent = new Label(this, SWT.NONE);
		lblComponent.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblComponent.setText("Component:");

		this.lblComponentDisplay = new Label(this, SWT.NONE);
		this.lblComponentDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblComponentDisplay.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		this.lblComponentDisplay.setText("N/A");

		final Label lblOperation = new Label(this, SWT.NONE);
		lblOperation.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblOperation.setText("Operation:");

		this.lblOperationDisplay = new Label(this, SWT.NONE);
		this.lblOperationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblOperationDisplay.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		this.lblOperationDisplay.setText("N/A");

		this.lblFailed = new Label(this, SWT.NONE);
		this.lblFailed.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblFailed.setText("Failed:");

		this.lblFailedDisplay = new Label(this, SWT.NONE);
		this.lblFailedDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblFailedDisplay.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		this.lblFailedDisplay.setText("N/A");

		final Label lblCalled = new Label(this, SWT.NONE);
		lblCalled.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblCalled.setText("# Calls:");

		this.lblCalledDisplay = new Label(this, SWT.NONE);
		this.lblCalledDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblCalledDisplay.setText("N/A");
	}

	public void setTraceToDisplay(final AggregatedExecutionEntry trace) {
		this.lblExecutionContainerDisplay.setText(trace.getContainer());
		this.lblComponentDisplay.setText(trace.getComponent());
		this.lblOperationDisplay.setText(trace.getOperation());
		this.lblCalledDisplay.setText(Integer.toString(trace.getCalls()));

		if (trace.isFailed()) {
			this.lblFailedDisplay.setText("Yes (" + trace.getFailedCause() + ")");
			this.lblFailedDisplay.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
			this.lblFailed.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		} else {
			this.lblFailedDisplay.setText("No");
			this.lblFailedDisplay.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
			this.lblFailed.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
		}

		super.layout();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
