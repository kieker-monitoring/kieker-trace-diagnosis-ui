package kieker.gui.view;

import kieker.gui.model.domain.ExecutionEntry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

public final class TraceDetailComposite extends Composite {

	private final Label lblExecutionContainerDisplay;
	private final Label lblComponentDisplay;
	private final Label lblOperationDisplay;
	private final Label lblTraceIdDisplay;
	private final Label lblDurationDisplay;
	private final Label lblStackDepthDisplay;
	private final Label lblFailed;
	private final Label lblFailedDisplay;

	public TraceDetailComposite(final Composite parent, final int style) {
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

		final Label lblTraceId = new Label(this, SWT.NONE);
		lblTraceId.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblTraceId.setText("Trace ID:");

		this.lblTraceIdDisplay = new Label(this, SWT.NONE);
		this.lblTraceIdDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblTraceIdDisplay.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		this.lblTraceIdDisplay.setText("N/A");

		final Label lblDuration = new Label(this, SWT.NONE);
		lblDuration.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblDuration.setText("Duration:");

		this.lblDurationDisplay = new Label(this, SWT.NONE);
		this.lblDurationDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblDurationDisplay.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		this.lblDurationDisplay.setText("N/A");

		final Label lblStackDepth = new Label(this, SWT.NONE);
		lblStackDepth.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblStackDepth.setText("Stack Depth:");

		this.lblStackDepthDisplay = new Label(this, SWT.NONE);
		this.lblStackDepthDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblStackDepthDisplay.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		this.lblStackDepthDisplay.setText("N/A");

		this.lblFailed = new Label(this, SWT.NONE);
		this.lblFailed.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblFailed.setText("Failed:");

		this.lblFailedDisplay = new Label(this, SWT.NONE);
		this.lblFailedDisplay.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblFailedDisplay.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		this.lblFailedDisplay.setText("N/A");
	}

	public void setTraceToDisplay(final ExecutionEntry trace) {
		this.lblTraceIdDisplay.setText(Long.toString(trace.getTraceID()));
		this.lblDurationDisplay.setText(Long.toString(trace.getDuration()));

		this.lblExecutionContainerDisplay.setText(trace.getContainer());
		this.lblComponentDisplay.setText(trace.getComponent());
		this.lblOperationDisplay.setText(trace.getOperation());
		this.lblStackDepthDisplay.setText(Integer.toString(trace.getStackDepth()));

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
