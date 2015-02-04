package kieker.diagnosis.dialog;

import java.util.concurrent.TimeUnit;

import kieker.diagnosis.common.model.PropertiesModel;
import kieker.diagnosis.common.model.PropertiesModel.ComponentNames;
import kieker.diagnosis.common.model.PropertiesModel.GraphvizGenerator;
import kieker.diagnosis.common.model.PropertiesModel.OperationNames;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public final class SettingsDialog extends Dialog {

	protected int result;
	protected Shell shlSettings;
	private Text text;
	private Label lblTimeUnit;
	private Combo combo;
	private Label lblOperationNames;
	private Combo combo_1;
	private Label lblComponentNames;
	private Combo combo_2;
	private Group grpAppearance;
	private Button btnOkay;
	private Button btnCancel;
	private Label lblGenerator;
	private Combo combo_3;
	private final PropertiesModel model;

	public SettingsDialog(final Shell parent, final int style, final PropertiesModel model) {
		super(parent, style);

		this.setText("SWT Dialog");
		this.model = model;
	}

	public int open() {
		this.createContents();

		this.loadSettings();

		this.shlSettings.open();
		this.shlSettings.layout();
		final Display display = this.getParent().getDisplay();

		final Rectangle screenSize = display.getPrimaryMonitor().getBounds();
		this.shlSettings.setLocation((screenSize.width - this.shlSettings.getBounds().width) / 2, (screenSize.height - this.shlSettings.getBounds().height) / 2);

		while (!this.shlSettings.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return this.result;
	}

	public String getGraphvizPath() {
		return this.text.getText();
	}

	private void loadSettings() {
		this.text.setText(this.model.getGraphvizPath());
		this.combo_3.select(this.model.getGraphvizGenerator() == GraphvizGenerator.DOT ? 0 : 1);
		this.combo.select(this.timeUnitToIndex(this.model.getTimeunit()));
		this.combo_1.select(this.model.getOperationNames() == OperationNames.SHORT ? 0 : 1);
		this.combo_2.select(this.model.getComponentNames() == ComponentNames.SHORT ? 0 : 1);
	}

	private void saveSettings() {
		this.model.startModification();

		this.model.setGraphvizPath(this.text.getText());
		this.model.setGraphvizGenerator(this.combo_3.getSelectionIndex() == 0 ? GraphvizGenerator.DOT : GraphvizGenerator.NEATO);
		this.model.setTimeunit(this.indexToTimeUnit());
		this.model.setOperationNames(this.combo_1.getSelectionIndex() == 0 ? OperationNames.SHORT : OperationNames.LONG);
		this.model.setComponentNames(this.combo_2.getSelectionIndex() == 0 ? ComponentNames.SHORT : ComponentNames.LONG);

		this.model.commitModification();
	}

	private int timeUnitToIndex(final TimeUnit timeunit) {
		final int result;

		switch (timeunit) {
		case DAYS:
			result = 6;
			break;
		case HOURS:
			result = 5;
			break;
		case MICROSECONDS:
			result = 1;
			break;
		case MILLISECONDS:
			result = 2;
			break;
		case MINUTES:
			result = 4;
			break;
		case NANOSECONDS:
			result = 0;
			break;
		case SECONDS:
			result = 3;
			break;
		default:
			result = 0;
			break;
		}

		return result;
	}

	private TimeUnit indexToTimeUnit() {
		final TimeUnit result;

		switch (this.combo.getSelectionIndex()) {
		case 0:
			result = TimeUnit.NANOSECONDS;
			break;
		case 1:
			result = TimeUnit.MICROSECONDS;
			break;
		case 2:
			result = TimeUnit.MILLISECONDS;
			break;
		case 3:
			result = TimeUnit.SECONDS;
			break;
		case 4:
			result = TimeUnit.MINUTES;
			break;
		case 5:
			result = TimeUnit.HOURS;
			break;
		case 6:
			result = TimeUnit.DAYS;
			break;
		default:
			result = TimeUnit.NANOSECONDS;
			break;
		}

		return result;
	}

	private void createContents() {
		this.shlSettings = new Shell(this.getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		this.shlSettings.setSize(440, 300);
		this.shlSettings.setText("Settings");
		this.shlSettings.setLayout(new FormLayout());

		this.grpAppearance = new Group(this.shlSettings, SWT.NONE);
		final FormData fd_grpAppearance = new FormData();
		fd_grpAppearance.top = new FormAttachment(0, 10);
		fd_grpAppearance.left = new FormAttachment(0, 10);
		fd_grpAppearance.bottom = new FormAttachment(0, 117);
		fd_grpAppearance.right = new FormAttachment(0, 424);
		this.grpAppearance.setLayoutData(fd_grpAppearance);
		this.grpAppearance.setText("Appearance");
		this.grpAppearance.setLayout(new GridLayout(2, false));

		this.lblTimeUnit = new Label(this.grpAppearance, SWT.NONE);
		this.lblTimeUnit.setText("Time Unit:");

		this.combo = new Combo(this.grpAppearance, SWT.READ_ONLY);
		this.combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		this.combo.setItems(new String[] { "Nanoseconds (ns)", "Microseconds (\u00B5s)", "Milliseconds (ms)", "Seconds (s)", "Minutes (m)", "Hours (h)", "Days (d)" });
		this.combo.select(0);

		this.lblOperationNames = new Label(this.grpAppearance, SWT.NONE);
		this.lblOperationNames.setText("Operations:");

		this.combo_1 = new Combo(this.grpAppearance, SWT.READ_ONLY);
		this.combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		this.combo_1.setItems(new String[] { "getBook(...)", "public void kieker.examples.bookstore.Catalog.getBook(boolean)" });
		this.combo_1.select(0);

		this.lblComponentNames = new Label(this.grpAppearance, SWT.NONE);
		this.lblComponentNames.setText("Components:");

		this.combo_2 = new Combo(this.grpAppearance, SWT.READ_ONLY);
		this.combo_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		this.combo_2.setItems(new String[] { "Catalog", "kieker.examples.bookstore.Catalog" });
		this.combo_2.select(0);

		final Group grpDependencyAndCall = new Group(this.shlSettings, SWT.NONE);
		grpDependencyAndCall.setLayout(new GridLayout(3, false));
		final FormData fd_grpDependencyAndCall = new FormData();
		fd_grpDependencyAndCall.top = new FormAttachment(this.grpAppearance, 18);
		fd_grpDependencyAndCall.left = new FormAttachment(0, 10);
		fd_grpDependencyAndCall.right = new FormAttachment(100, -10);
		grpDependencyAndCall.setLayoutData(fd_grpDependencyAndCall);
		grpDependencyAndCall.setText("Dependency and Call Graph Generation");

		final Label lblGraphviz = new Label(grpDependencyAndCall, SWT.NONE);
		lblGraphviz.setText("Graphviz Directory:");

		this.text = new Text(grpDependencyAndCall, SWT.BORDER);
		this.text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Button btnBrowse = new Button(grpDependencyAndCall, SWT.NONE);
		btnBrowse.setText("Browse");

		this.lblGenerator = new Label(grpDependencyAndCall, SWT.NONE);
		this.lblGenerator.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		this.lblGenerator.setText("Generator:");

		this.combo_3 = new Combo(grpDependencyAndCall, SWT.READ_ONLY);
		this.combo_3.setItems(new String[] { "Dot", "Neato" });
		this.combo_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		this.combo_3.select(0);
		new Label(grpDependencyAndCall, SWT.NONE);

		this.btnOkay = new Button(this.shlSettings, SWT.NONE);
		fd_grpDependencyAndCall.bottom = new FormAttachment(this.btnOkay, -20);
		this.btnOkay.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				SettingsDialog.this.result = SWT.OK;

				SettingsDialog.this.saveSettings();

				SettingsDialog.this.shlSettings.close();
			}
		});
		final FormData fd_btnOkay = new FormData();
		fd_btnOkay.bottom = new FormAttachment(100, -10);
		fd_btnOkay.left = new FormAttachment(0, 290);
		this.btnOkay.setLayoutData(fd_btnOkay);
		this.btnOkay.setText("OK");

		this.btnCancel = new Button(this.shlSettings, SWT.NONE);
		this.btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				SettingsDialog.this.result = SWT.CANCEL;
				SettingsDialog.this.shlSettings.close();
			}
		});
		fd_btnOkay.right = new FormAttachment(100, -80);
		final FormData fd_btnCancel = new FormData();
		fd_btnCancel.top = new FormAttachment(this.btnOkay, 0, SWT.TOP);
		fd_btnCancel.left = new FormAttachment(this.btnOkay, 6);
		fd_btnCancel.right = new FormAttachment(100, -10);
		this.btnCancel.setLayoutData(fd_btnCancel);
		this.btnCancel.setText("Cancel");
	}

}
