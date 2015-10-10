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

package kieker.diagnosis.mainview.dialog.settings;

import java.util.concurrent.TimeUnit;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import kieker.diagnosis.mainview.dialog.AbstractDialogController;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.model.PropertiesModel.ComponentNames;
import kieker.diagnosis.model.PropertiesModel.OperationNames;

/**
 * @author Nils Christian Ehmke
 */
public final class SettingsDialogViewController extends AbstractDialogController {

	private static final TimeUnit[] TIME_UNITS = { TimeUnit.NANOSECONDS, TimeUnit.MICROSECONDS, TimeUnit.MILLISECONDS, TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS };

	private final PropertiesModel propertiesModel = PropertiesModel.getInstance();

	@FXML private ComboBox<OperationNames> operationNames;
	@FXML private ComboBox<ComponentNames> componentNames;
	@FXML private ComboBox<TimeUnit> timeunits;
	@FXML private ComboBox<Boolean> additionalLogChecks;
	@FXML private ComboBox<Boolean> activateRegularExpressions;

	public void initialize() {
		this.timeunits.setItems(FXCollections.observableArrayList(SettingsDialogViewController.TIME_UNITS));
		this.componentNames.setItems(FXCollections.observableArrayList(ComponentNames.values()));
		this.operationNames.setItems(FXCollections.observableArrayList(OperationNames.values()));
		this.additionalLogChecks.setItems(FXCollections.observableArrayList(Boolean.TRUE, Boolean.FALSE));
		this.activateRegularExpressions.setItems(FXCollections.observableArrayList(Boolean.TRUE, Boolean.FALSE));

		this.loadSettings();
	}

	public void saveAndCloseDialog() {
		this.saveSettings();
		this.closeDialog();
	}

	private void loadSettings() {
		this.operationNames.getSelectionModel().select(this.propertiesModel.getOperationNames());
		this.componentNames.getSelectionModel().select(this.propertiesModel.getComponentNames());
		this.timeunits.getSelectionModel().select(this.propertiesModel.getTimeUnit());
		this.additionalLogChecks.getSelectionModel().select(this.propertiesModel.isAdditionalLogChecks());
		this.activateRegularExpressions.getSelectionModel().select(this.propertiesModel.isActivateRegularExpressions());
	}
 
	private void saveSettings() {
		this.propertiesModel.setOperationNames(this.operationNames.getSelectionModel().getSelectedItem());
		this.propertiesModel.setComponentNames(this.componentNames.getSelectionModel().getSelectedItem());
		this.propertiesModel.setTimeUnit(this.timeunits.getSelectionModel().getSelectedItem());
		this.propertiesModel.setAdditionalLogChecks(this.additionalLogChecks.getSelectionModel().getSelectedItem());
		this.propertiesModel.setActivateRegularExpressions(this.activateRegularExpressions.getSelectionModel().getSelectedItem());
	}

}
