/***************************************************************************
 * Copyright 2015 Kieker Project (http://kieker-monitoring.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/

package kieker.diagnosis.mainview.dialog;


/**
 * @author Nils Christian Ehmke
 */
public final class SettingsDialog {

	// private void initializeMapper() {
	// this.timeUnitMapper = new Mapper<>();
	//
	// this.timeUnitMapper.map(TimeUnit.NANOSECONDS).to(0);
	// this.timeUnitMapper.map(TimeUnit.MICROSECONDS).to(1);
	// this.timeUnitMapper.map(TimeUnit.MILLISECONDS).to(2);
	// this.timeUnitMapper.map(TimeUnit.SECONDS).to(3);
	// this.timeUnitMapper.map(TimeUnit.MINUTES).to(4);
	// this.timeUnitMapper.map(TimeUnit.HOURS).to(5);
	// this.timeUnitMapper.map(TimeUnit.DAYS).to(6);
	// }

	// private void loadSettings() {
	// this.comboBoxTimeUnit.select(this.timeUnitMapper.resolve(this.model.getTimeUnit()));
	// this.comboBoxOperationNames.select(this.model.getOperationNames() == OperationNames.SHORT ? 0 : 1);
	// this.comboBoxComponentNames.select(this.model.getComponentNames() == ComponentNames.SHORT ? 0 : 1);
	// this.spinner.setSelection(this.model.getMaxTracesToShow());
	// }
	//
	// private void saveSettings() {
	// this.model.startModification();
	//
	// this.model.setTimeUnit(this.timeUnitMapper.invertedResolve(this.comboBoxTimeUnit.getSelectionIndex()));
	// this.model.setOperationNames(this.comboBoxOperationNames.getSelectionIndex() == 0 ? OperationNames.SHORT : OperationNames.LONG);
	// this.model.setComponentNames(this.comboBoxComponentNames.getSelectionIndex() == 0 ? ComponentNames.SHORT : ComponentNames.LONG);
	// this.model.setMaxTracesToShow(this.spinner.getSelection());
	//
	// this.model.commitModification();
	// }

	// private void createContents() {
	// this.shlSettings = new Shell(this.getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	//		this.shlSettings.setText(BUNDLE.getString("SettingsDialog.shlSettings.text")); //$NON-NLS-1$ 
	// this.shlSettings.setLayout(new GridLayout(1, false));
	//
	// final Group grpAppearance = new Group(this.shlSettings, SWT.NONE);
	//		grpAppearance.setText(BUNDLE.getString("SettingsDialog.grpAppearance.text")); //$NON-NLS-1$ 
	// grpAppearance.setLayout(new GridLayout(2, false));
	//
	// final Label lblTimeUnit = new Label(grpAppearance, SWT.NONE);
	//		lblTimeUnit.setText(BUNDLE.getString("SettingsDialog.lblTimeUnit.text") + ":"); //$NON-NLS-1$ 
	//
	// final String nanoseconds = BUNDLE.getString("SettingsDialog.nanoseconds");
	// final String microseconds = BUNDLE.getString("SettingsDialog.microseconds");
	// final String milliseconds = BUNDLE.getString("SettingsDialog.milliseconds");
	// final String seconds = BUNDLE.getString("SettingsDialog.seconds");
	// final String minutes = BUNDLE.getString("SettingsDialog.minutes");
	// final String hours = BUNDLE.getString("SettingsDialog.hours");
	// final String days = BUNDLE.getString("SettingsDialog.days");
	//
	// this.comboBoxTimeUnit = new Combo(grpAppearance, SWT.READ_ONLY);
	// this.comboBoxTimeUnit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	// this.comboBoxTimeUnit.setItems(new String[] { nanoseconds, microseconds, milliseconds, seconds, minutes, hours, days });
	// this.comboBoxTimeUnit.select(0);
	//
	// final Label lblOperationNames = new Label(grpAppearance, SWT.NONE);
	//		lblOperationNames.setText(BUNDLE.getString("SettingsDialog.lblOperationNames.text") + ":"); //$NON-NLS-1$ 
	//
	// this.comboBoxOperationNames = new Combo(grpAppearance, SWT.READ_ONLY);
	// this.comboBoxOperationNames.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	// this.comboBoxOperationNames.setItems(new String[] { "getBook(...)", "public void kieker.examples.bookstore.Catalog.getBook(boolean)" });
	// this.comboBoxOperationNames.select(0);
	//
	// final Label lblComponentNames = new Label(grpAppearance, SWT.NONE);
	//		lblComponentNames.setText(BUNDLE.getString("SettingsDialog.lblComponentNames.text") + ":"); //$NON-NLS-1$ 
	//
	// this.comboBoxComponentNames = new Combo(grpAppearance, SWT.READ_ONLY);
	// this.comboBoxComponentNames.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	// this.comboBoxComponentNames.setItems(new String[] { "Catalog", "kieker.examples.bookstore.Catalog" });
	// this.comboBoxComponentNames.select(0);
	//
	// final Label lblLimitNumberOf = new Label(grpAppearance, SWT.NONE);
	//		lblLimitNumberOf.setText(BUNDLE.getString("SettingsDialog.lblLimitNumberOf.text")); //$NON-NLS-1$
	//
	// this.spinner = new Spinner(grpAppearance, SWT.BORDER);
	// this.spinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
	// this.spinner.setMaximum(1000000);
	// this.spinner.setMinimum(1);
	// this.spinner.setSelection(100);
	//
	// final Composite composite = new Composite(this.shlSettings, SWT.NONE);
	// composite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
	// final GridLayout gl_composite = new GridLayout();
	// gl_composite.makeColumnsEqualWidth = true;
	// gl_composite.numColumns = 2;
	// composite.setLayout(gl_composite);
	//
	// final Button btnOkay = new Button(composite, SWT.NONE);
	// btnOkay.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	// btnOkay.addSelectionListener(new SelectionAdapter() {
	// @Override
	// public void widgetSelected(final SelectionEvent e) {
	// SettingsDialog.this.result = SWT.OK;
	//
	// SettingsDialog.this.saveSettings();
	//
	// SettingsDialog.this.shlSettings.close();
	// }
	// });
	//		btnOkay.setText(BUNDLE.getString("SettingsDialog.btnOkay.text")); //$NON-NLS-1$ 
	//
	// final Button btnCancel = new Button(composite, SWT.NONE);
	// btnCancel.addSelectionListener(new SelectionAdapter() {
	// @Override
	// public void widgetSelected(final SelectionEvent e) {
	// SettingsDialog.this.result = SWT.CANCEL;
	// SettingsDialog.this.shlSettings.close();
	// }
	// });
	//		btnCancel.setText(BUNDLE.getString("SettingsDialog.btnCancel.text")); //$NON-NLS-1$ 
	// }
}
