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

package kieker.diagnosis.controller.settings;

import java.util.concurrent.TimeUnit;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import kieker.diagnosis.controller.AbstractDialogController;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.model.PropertiesModel.ComponentNames;
import kieker.diagnosis.model.PropertiesModel.OperationNames;
import kieker.diagnosis.model.PropertiesModel.Threshold;
import kieker.diagnosis.model.PropertiesModel.TimestampTypes;
import kieker.diagnosis.util.Context;

/**
 * @author Nils Christian Ehmke
 */
public final class SettingsDialogViewController extends AbstractDialogController {

	private static final TimeUnit[] TIME_UNITS = { TimeUnit.NANOSECONDS, TimeUnit.MICROSECONDS, TimeUnit.MILLISECONDS, TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS };

	private final PropertiesModel ivPropertiesModel = PropertiesModel.getInstance();

	@FXML private ComboBox<OperationNames> ivOperationNames;
	@FXML private ComboBox<ComponentNames> ivComponentNames;
	@FXML private ComboBox<Threshold> ivThresholds;
	@FXML private ComboBox<TimeUnit> ivTimeunits;
	@FXML private ComboBox<TimestampTypes> ivTimestamps;
	@FXML private CheckBox ivAdditionalLogChecks;
	@FXML private CheckBox ivActivateRegularExpressions;
	@FXML private CheckBox ivAggregateMethodCalls;
	@FXML private CheckBox ivCaseSensitive;
	@FXML private CheckBox ivPercentageCalculation;

	public SettingsDialogViewController(final Context aContext) {
		super(aContext);
	}

	public void initialize() {
		this.ivTimeunits.setItems(FXCollections.observableArrayList(SettingsDialogViewController.TIME_UNITS));
		this.ivComponentNames.setItems(FXCollections.observableArrayList(ComponentNames.values()));
		this.ivOperationNames.setItems(FXCollections.observableArrayList(OperationNames.values()));
		this.ivTimestamps.setItems(FXCollections.observableArrayList(TimestampTypes.values()));
		this.ivThresholds.setItems(FXCollections.observableArrayList(Threshold.values()));

		this.ivThresholds.disableProperty().bind(this.ivAggregateMethodCalls.selectedProperty().not());

		this.loadSettings();
	}

	public void saveAndCloseDialog() {
		this.saveSettings();
		this.closeDialog();
	}

	private void loadSettings() {
		this.ivOperationNames.getSelectionModel().select(this.ivPropertiesModel.getOperationNames());
		this.ivComponentNames.getSelectionModel().select(this.ivPropertiesModel.getComponentNames());
		this.ivTimeunits.getSelectionModel().select(this.ivPropertiesModel.getTimeUnit());
		this.ivAdditionalLogChecks.setSelected(this.ivPropertiesModel.isAdditionalLogChecksActive());
		this.ivActivateRegularExpressions.setSelected(this.ivPropertiesModel.isRegularExpressionsActive());
		this.ivAggregateMethodCalls.setSelected(this.ivPropertiesModel.isMethodCallAggregationActive());
		this.ivThresholds.getSelectionModel().select(this.ivPropertiesModel.getThreshold());
		this.ivCaseSensitive.setSelected(this.ivPropertiesModel.isCaseSensitivityActive());
		this.ivPercentageCalculation.setSelected(this.ivPropertiesModel.isPercentageCalculationActive());
		this.ivTimestamps.getSelectionModel().select(this.ivPropertiesModel.getTimestampType());
	}

	private void saveSettings() {
		this.ivPropertiesModel.setOperationNames(this.ivOperationNames.getSelectionModel().getSelectedItem());
		this.ivPropertiesModel.setComponentNames(this.ivComponentNames.getSelectionModel().getSelectedItem());
		this.ivPropertiesModel.setTimeUnit(this.ivTimeunits.getSelectionModel().getSelectedItem());
		this.ivPropertiesModel.setAdditionalLogChecksActive(this.ivAdditionalLogChecks.isSelected());
		this.ivPropertiesModel.setRegularExpressionsActive(this.ivActivateRegularExpressions.isSelected());
		this.ivPropertiesModel.setMethodCallAggregationActive(this.ivAggregateMethodCalls.isSelected());
		this.ivPropertiesModel.setThreshold(this.ivThresholds.getSelectionModel().getSelectedItem());
		this.ivPropertiesModel.setCaseSensitivityActive(this.ivCaseSensitive.isSelected());
		this.ivPropertiesModel.setPercentageCalculationActive(this.ivPercentageCalculation.isSelected());
		this.ivPropertiesModel.setTimestampType(this.ivTimestamps.getSelectionModel().getSelectedItem());
	}

}
