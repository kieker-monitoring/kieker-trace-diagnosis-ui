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
import kieker.diagnosis.gui.Context;
import kieker.diagnosis.service.InjectService;
import kieker.diagnosis.service.properties.PropertiesService;
import kieker.diagnosis.service.properties.PropertiesService.ComponentNames;
import kieker.diagnosis.service.properties.PropertiesService.OperationNames;
import kieker.diagnosis.service.properties.PropertiesService.Threshold;
import kieker.diagnosis.service.properties.PropertiesService.TimestampTypes;

/**
 * @author Nils Christian Ehmke
 */
public final class SettingsDialogController extends AbstractController<SettingsDialogView> implements SettingsDialogControllerIfc {

	private static final TimeUnit[] TIME_UNITS = { TimeUnit.NANOSECONDS, TimeUnit.MICROSECONDS, TimeUnit.MILLISECONDS, TimeUnit.SECONDS, TimeUnit.MINUTES,
			TimeUnit.HOURS };

	@InjectService
	private PropertiesService ivPropertiesService;

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
		getView( ).getOperationNames( ).getSelectionModel( ).select( ivPropertiesService.getOperationNames( ) );
		getView( ).getComponentNames( ).getSelectionModel( ).select( ivPropertiesService.getComponentNames( ) );
		getView( ).getTimeunits( ).getSelectionModel( ).select( ivPropertiesService.getTimeUnit( ) );
		getView( ).getAdditionalLogChecks( ).setSelected( ivPropertiesService.isAdditionalLogChecksActive( ) );
		getView( ).getActivateRegularExpressions( ).setSelected( ivPropertiesService.isRegularExpressionsActive( ) );
		getView( ).getAggregateMethodCalls( ).setSelected( ivPropertiesService.isMethodCallAggregationActive( ) );
		getView( ).getThresholds( ).getSelectionModel( ).select( ivPropertiesService.getThreshold( ) );
		getView( ).getCaseSensitive( ).setSelected( ivPropertiesService.isCaseSensitivityActive( ) );
		getView( ).getPercentageCalculation( ).setSelected( ivPropertiesService.isPercentageCalculationActive( ) );
		getView( ).getTimestamps( ).getSelectionModel( ).select( ivPropertiesService.getTimestampType( ) );
		getView( ).getCacheViews( ).setSelected( ivPropertiesService.isCacheViews( ) );
		getView( ).getSearchInEntireTrace( ).setSelected( ivPropertiesService.isSearchInEntireTrace( ) );
		getView( ).getShowUnmonitoredTime( ).setSelected( ivPropertiesService.isShowUnmonitoredTime( ) );
	}

	private void saveSettings( ) {
		ivPropertiesService.setOperationNames( getView( ).getOperationNames( ).getSelectionModel( ).getSelectedItem( ) );
		ivPropertiesService.setComponentNames( getView( ).getComponentNames( ).getSelectionModel( ).getSelectedItem( ) );
		ivPropertiesService.setTimeUnit( getView( ).getTimeunits( ).getSelectionModel( ).getSelectedItem( ) );
		ivPropertiesService.setAdditionalLogChecksActive( getView( ).getAdditionalLogChecks( ).isSelected( ) );
		ivPropertiesService.setRegularExpressionsActive( getView( ).getActivateRegularExpressions( ).isSelected( ) );
		ivPropertiesService.setMethodCallAggregationActive( getView( ).getAggregateMethodCalls( ).isSelected( ) );
		ivPropertiesService.setThreshold( getView( ).getThresholds( ).getSelectionModel( ).getSelectedItem( ) );
		ivPropertiesService.setCaseSensitivityActive( getView( ).getCaseSensitive( ).isSelected( ) );
		ivPropertiesService.setPercentageCalculationActive( getView( ).getPercentageCalculation( ).isSelected( ) );
		ivPropertiesService.setTimestampType( getView( ).getTimestamps( ).getSelectionModel( ).getSelectedItem( ) );
		ivPropertiesService.setCacheViews( getView( ).getCacheViews( ).isSelected( ) );
		ivPropertiesService.setSearchInEntireTrace( getView( ).getSearchInEntireTrace( ).isSelected( ) );
		ivPropertiesService.setShowUnmonitoredTime( getView( ).getShowUnmonitoredTime( ).isSelected( ) );
	}

}
