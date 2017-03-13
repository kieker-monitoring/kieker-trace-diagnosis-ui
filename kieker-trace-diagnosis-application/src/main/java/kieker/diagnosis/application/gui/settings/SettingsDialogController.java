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

package kieker.diagnosis.application.gui.settings;

import kieker.diagnosis.application.service.properties.AdditionalLogChecksProperty;
import kieker.diagnosis.application.service.properties.CaseSensitiveProperty;
import kieker.diagnosis.application.service.properties.ComponentNames;
import kieker.diagnosis.application.service.properties.ComponentNamesProperty;
import kieker.diagnosis.application.service.properties.MethodCallAggregationProperty;
import kieker.diagnosis.application.service.properties.OperationNames;
import kieker.diagnosis.application.service.properties.OperationNamesProperty;
import kieker.diagnosis.application.service.properties.PercentCalculationProperty;
import kieker.diagnosis.application.service.properties.RegularExpressionsProperty;
import kieker.diagnosis.application.service.properties.SearchInEntireTraceProperty;
import kieker.diagnosis.application.service.properties.ShowUnmonitoredTimeProperty;
import kieker.diagnosis.application.service.properties.Threshold;
import kieker.diagnosis.application.service.properties.ThresholdProperty;
import kieker.diagnosis.application.service.properties.TimeUnitProperty;
import kieker.diagnosis.application.service.properties.TimestampProperty;
import kieker.diagnosis.application.service.properties.TimestampTypes;
import kieker.diagnosis.architecture.gui.AbstractController;
import kieker.diagnosis.architecture.service.properties.PropertiesService;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javafx.collections.FXCollections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Nils Christian Ehmke
 */
@Component
public class SettingsDialogController extends AbstractController<SettingsDialogView> {

	private static final TimeUnit[] TIME_UNITS = { TimeUnit.NANOSECONDS, TimeUnit.MICROSECONDS, TimeUnit.MILLISECONDS, TimeUnit.SECONDS, TimeUnit.MINUTES,
			TimeUnit.HOURS };

	@Autowired
	private PropertiesService ivPropertiesService;

	@Override
	protected void doInitialize( final boolean aFirstInitialization, final Optional<?> aParameter ) {
		if ( aFirstInitialization ) {
			getView( ).getTimeunits( ).setItems( FXCollections.observableArrayList( SettingsDialogController.TIME_UNITS ) );
			getView( ).getComponentNames( ).setItems( FXCollections.observableArrayList( ComponentNames.values( ) ) );
			getView( ).getOperationNames( ).setItems( FXCollections.observableArrayList( OperationNames.values( ) ) );
			getView( ).getTimestamps( ).setItems( FXCollections.observableArrayList( TimestampTypes.values( ) ) );
			getView( ).getThresholds( ).setItems( FXCollections.observableArrayList( Threshold.values( ) ) );

			getView( ).getThresholds( ).disableProperty( ).bind( getView( ).getAggregateMethodCalls( ).selectedProperty( ).not( ) );
		}

		loadSettings( );
	}

	@Override
	public void doRefresh( ) {
	}

	public void saveAndCloseDialog( ) {
		saveSettings( );
		closeDialog( );
	}

	public void closeDialog( ) {
		getView( ).getStage( ).hide( );
	}

	private void loadSettings( ) {
		getView( ).getOperationNames( ).getSelectionModel( ).select( ivPropertiesService.loadApplicationProperty( OperationNamesProperty.class ) );
		getView( ).getComponentNames( ).getSelectionModel( ).select( ivPropertiesService.loadApplicationProperty( ComponentNamesProperty.class ) );
		getView( ).getTimeunits( ).getSelectionModel( ).select( ivPropertiesService.loadApplicationProperty( TimeUnitProperty.class ) );
		getView( ).getAdditionalLogChecks( ).setSelected( ivPropertiesService.loadApplicationProperty( AdditionalLogChecksProperty.class ) );
		getView( ).getActivateRegularExpressions( ).setSelected( ivPropertiesService.loadApplicationProperty( RegularExpressionsProperty.class ) );
		getView( ).getAggregateMethodCalls( ).setSelected( ivPropertiesService.loadApplicationProperty( MethodCallAggregationProperty.class ) );
		getView( ).getThresholds( ).getSelectionModel( ).select( ivPropertiesService.loadApplicationProperty( ThresholdProperty.class ) );
		getView( ).getCaseSensitive( ).setSelected( ivPropertiesService.loadApplicationProperty( CaseSensitiveProperty.class ) );
		getView( ).getPercentageCalculation( ).setSelected( ivPropertiesService.loadApplicationProperty( PercentCalculationProperty.class ) );
		getView( ).getTimestamps( ).getSelectionModel( ).select( ivPropertiesService.loadApplicationProperty( TimestampProperty.class ) );
		getView( ).getSearchInEntireTrace( ).setSelected( ivPropertiesService.loadApplicationProperty( SearchInEntireTraceProperty.class ) );
		getView( ).getShowUnmonitoredTime( ).setSelected( ivPropertiesService.loadApplicationProperty( ShowUnmonitoredTimeProperty.class ) );
	}

	private void saveSettings( ) {
		ivPropertiesService.saveApplicationProperty( OperationNamesProperty.class, getView( ).getOperationNames( ).getSelectionModel( ).getSelectedItem( ) );
		ivPropertiesService.saveApplicationProperty( ComponentNamesProperty.class, getView( ).getComponentNames( ).getSelectionModel( ).getSelectedItem( ) );
		ivPropertiesService.saveApplicationProperty( TimeUnitProperty.class, getView( ).getTimeunits( ).getSelectionModel( ).getSelectedItem( ) );
		ivPropertiesService.saveApplicationProperty( AdditionalLogChecksProperty.class, getView( ).getAdditionalLogChecks( ).isSelected( ) );
		ivPropertiesService.saveApplicationProperty( RegularExpressionsProperty.class, getView( ).getActivateRegularExpressions( ).isSelected( ) );
		ivPropertiesService.saveApplicationProperty( MethodCallAggregationProperty.class, getView( ).getAggregateMethodCalls( ).isSelected( ) );
		ivPropertiesService.saveApplicationProperty( ThresholdProperty.class, getView( ).getThresholds( ).getSelectionModel( ).getSelectedItem( ) );
		ivPropertiesService.saveApplicationProperty( CaseSensitiveProperty.class, getView( ).getCaseSensitive( ).isSelected( ) );
		ivPropertiesService.saveApplicationProperty( PercentCalculationProperty.class, getView( ).getPercentageCalculation( ).isSelected( ) );
		ivPropertiesService.saveApplicationProperty( TimestampProperty.class, getView( ).getTimestamps( ).getSelectionModel( ).getSelectedItem( ) );
		ivPropertiesService.saveApplicationProperty( SearchInEntireTraceProperty.class, getView( ).getSearchInEntireTrace( ).isSelected( ) );
		ivPropertiesService.saveApplicationProperty( ShowUnmonitoredTimeProperty.class, getView( ).getShowUnmonitoredTime( ).isSelected( ) );
	}

}
