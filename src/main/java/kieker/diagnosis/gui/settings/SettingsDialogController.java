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

package kieker.diagnosis.gui.settings;

import java.util.concurrent.TimeUnit;

import javafx.collections.FXCollections;
import kieker.diagnosis.gui.AbstractController;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.model.PropertiesModel.ComponentNames;
import kieker.diagnosis.model.PropertiesModel.OperationNames;
import kieker.diagnosis.model.PropertiesModel.Threshold;
import kieker.diagnosis.model.PropertiesModel.TimestampTypes;
import kieker.diagnosis.util.Context;

/**
 * @author Nils Christian Ehmke
 */
public final class SettingsDialogController extends AbstractController<SettingsDialogView> implements SettingsDialogControllerIfc {

	private static final TimeUnit[] TIME_UNITS = { TimeUnit.NANOSECONDS, TimeUnit.MICROSECONDS, TimeUnit.MILLISECONDS, TimeUnit.SECONDS, TimeUnit.MINUTES,
			TimeUnit.HOURS };

	private final PropertiesModel ivPropertiesModel = PropertiesModel.getInstance( );

	public SettingsDialogController( final Context aContext ) {
		super( aContext );
	}

	@Override
	public void doInitialize( ) {
		getView( ).getTimeunits( ).setItems( FXCollections.observableArrayList( SettingsDialogController.TIME_UNITS ) );
		getView( ).getComponentNames( ).setItems( FXCollections.observableArrayList( ComponentNames.values( ) ) );
		getView( ).getOperationNames( ).setItems( FXCollections.observableArrayList( OperationNames.values( ) ) );
		getView( ).getTimestamps( ).setItems( FXCollections.observableArrayList( TimestampTypes.values( ) ) );
		getView( ).getThresholds( ).setItems( FXCollections.observableArrayList( Threshold.values( ) ) );

		getView( ).getThresholds( ).disableProperty( ).bind( getView( ).getAggregateMethodCalls( ).selectedProperty( ).not( ) );

		loadSettings( );
	}

	@Override
	public void saveAndCloseDialog( ) {
		saveSettings( );
		closeDialog( );
	}

	@Override
	public void closeDialog( ) {
		getView( ).getStage( ).hide( );
	}

	private void loadSettings( ) {
		getView( ).getOperationNames( ).getSelectionModel( ).select( ivPropertiesModel.getOperationNames( ) );
		getView( ).getComponentNames( ).getSelectionModel( ).select( ivPropertiesModel.getComponentNames( ) );
		getView( ).getTimeunits( ).getSelectionModel( ).select( ivPropertiesModel.getTimeUnit( ) );
		getView( ).getAdditionalLogChecks( ).setSelected( ivPropertiesModel.isAdditionalLogChecksActive( ) );
		getView( ).getActivateRegularExpressions( ).setSelected( ivPropertiesModel.isRegularExpressionsActive( ) );
		getView( ).getAggregateMethodCalls( ).setSelected( ivPropertiesModel.isMethodCallAggregationActive( ) );
		getView( ).getThresholds( ).getSelectionModel( ).select( ivPropertiesModel.getThreshold( ) );
		getView( ).getCaseSensitive( ).setSelected( ivPropertiesModel.isCaseSensitivityActive( ) );
		getView( ).getPercentageCalculation( ).setSelected( ivPropertiesModel.isPercentageCalculationActive( ) );
		getView( ).getTimestamps( ).getSelectionModel( ).select( ivPropertiesModel.getTimestampType( ) );
		getView( ).getCacheViews( ).setSelected( ivPropertiesModel.isCacheViews( ) );
		getView( ).getSearchInEntireTrace( ).setSelected( ivPropertiesModel.isSearchInEntireTrace( ) );
	}

	private void saveSettings( ) {
		ivPropertiesModel.setOperationNames( getView( ).getOperationNames( ).getSelectionModel( ).getSelectedItem( ) );
		ivPropertiesModel.setComponentNames( getView( ).getComponentNames( ).getSelectionModel( ).getSelectedItem( ) );
		ivPropertiesModel.setTimeUnit( getView( ).getTimeunits( ).getSelectionModel( ).getSelectedItem( ) );
		ivPropertiesModel.setAdditionalLogChecksActive( getView( ).getAdditionalLogChecks( ).isSelected( ) );
		ivPropertiesModel.setRegularExpressionsActive( getView( ).getActivateRegularExpressions( ).isSelected( ) );
		ivPropertiesModel.setMethodCallAggregationActive( getView( ).getAggregateMethodCalls( ).isSelected( ) );
		ivPropertiesModel.setThreshold( getView( ).getThresholds( ).getSelectionModel( ).getSelectedItem( ) );
		ivPropertiesModel.setCaseSensitivityActive( getView( ).getCaseSensitive( ).isSelected( ) );
		ivPropertiesModel.setPercentageCalculationActive( getView( ).getPercentageCalculation( ).isSelected( ) );
		ivPropertiesModel.setTimestampType( getView( ).getTimestamps( ).getSelectionModel( ).getSelectedItem( ) );
		ivPropertiesModel.setCacheViews( getView( ).getCacheViews( ).isSelected( ) );
		ivPropertiesModel.setSearchInEntireTrace( getView( ).getSearchInEntireTrace( ).isSelected( ) );
	}

}
