/***************************************************************************
 * Copyright 2015-2016 Kieker Project (http://kieker-monitoring.net)
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

	private static final TimeUnit[] TIME_UNITS = { TimeUnit.NANOSECONDS, TimeUnit.MICROSECONDS, TimeUnit.MILLISECONDS, TimeUnit.SECONDS, TimeUnit.MINUTES,
			TimeUnit.HOURS };

	private final PropertiesModel ivPropertiesModel = PropertiesModel.getInstance( );

	@FXML
	private ComboBox<OperationNames> ivOperationNames;
	@FXML
	private ComboBox<ComponentNames> ivComponentNames;
	@FXML
	private ComboBox<Threshold> ivThresholds;
	@FXML
	private ComboBox<TimeUnit> ivTimeunits;
	@FXML
	private ComboBox<TimestampTypes> ivTimestamps;
	@FXML
	private CheckBox ivAdditionalLogChecks;
	@FXML
	private CheckBox ivActivateRegularExpressions;
	@FXML
	private CheckBox ivAggregateMethodCalls;
	@FXML
	private CheckBox ivCaseSensitive;
	@FXML
	private CheckBox ivPercentageCalculation;
	@FXML
	private CheckBox ivCacheViews;
	@FXML
	private CheckBox ivSearchInEntireTrace;

	public SettingsDialogViewController( final Context aContext ) {
		super( aContext );
	}

	public void initialize( ) {
		ivTimeunits.setItems( FXCollections.observableArrayList( SettingsDialogViewController.TIME_UNITS ) );
		ivComponentNames.setItems( FXCollections.observableArrayList( ComponentNames.values( ) ) );
		ivOperationNames.setItems( FXCollections.observableArrayList( OperationNames.values( ) ) );
		ivTimestamps.setItems( FXCollections.observableArrayList( TimestampTypes.values( ) ) );
		ivThresholds.setItems( FXCollections.observableArrayList( Threshold.values( ) ) );

		ivThresholds.disableProperty( ).bind( ivAggregateMethodCalls.selectedProperty( ).not( ) );

		loadSettings( );
	}

	public void saveAndCloseDialog( ) {
		saveSettings( );
		closeDialog( );
	}

	private void loadSettings( ) {
		ivOperationNames.getSelectionModel( ).select( ivPropertiesModel.getOperationNames( ) );
		ivComponentNames.getSelectionModel( ).select( ivPropertiesModel.getComponentNames( ) );
		ivTimeunits.getSelectionModel( ).select( ivPropertiesModel.getTimeUnit( ) );
		ivAdditionalLogChecks.setSelected( ivPropertiesModel.isAdditionalLogChecksActive( ) );
		ivActivateRegularExpressions.setSelected( ivPropertiesModel.isRegularExpressionsActive( ) );
		ivAggregateMethodCalls.setSelected( ivPropertiesModel.isMethodCallAggregationActive( ) );
		ivThresholds.getSelectionModel( ).select( ivPropertiesModel.getThreshold( ) );
		ivCaseSensitive.setSelected( ivPropertiesModel.isCaseSensitivityActive( ) );
		ivPercentageCalculation.setSelected( ivPropertiesModel.isPercentageCalculationActive( ) );
		ivTimestamps.getSelectionModel( ).select( ivPropertiesModel.getTimestampType( ) );
		ivCacheViews.setSelected( ivPropertiesModel.isCacheViews( ) );
		ivSearchInEntireTrace.setSelected( ivPropertiesModel.isSearchInEntireTrace( ) );
	}

	private void saveSettings( ) {
		ivPropertiesModel.setOperationNames( ivOperationNames.getSelectionModel( ).getSelectedItem( ) );
		ivPropertiesModel.setComponentNames( ivComponentNames.getSelectionModel( ).getSelectedItem( ) );
		ivPropertiesModel.setTimeUnit( ivTimeunits.getSelectionModel( ).getSelectedItem( ) );
		ivPropertiesModel.setAdditionalLogChecksActive( ivAdditionalLogChecks.isSelected( ) );
		ivPropertiesModel.setRegularExpressionsActive( ivActivateRegularExpressions.isSelected( ) );
		ivPropertiesModel.setMethodCallAggregationActive( ivAggregateMethodCalls.isSelected( ) );
		ivPropertiesModel.setThreshold( ivThresholds.getSelectionModel( ).getSelectedItem( ) );
		ivPropertiesModel.setCaseSensitivityActive( ivCaseSensitive.isSelected( ) );
		ivPropertiesModel.setPercentageCalculationActive( ivPercentageCalculation.isSelected( ) );
		ivPropertiesModel.setTimestampType( ivTimestamps.getSelectionModel( ).getSelectedItem( ) );
		ivPropertiesModel.setCacheViews( ivCacheViews.isSelected( ) );
		ivPropertiesModel.setSearchInEntireTrace( ivSearchInEntireTrace.isSelected( ) );
	}

}
