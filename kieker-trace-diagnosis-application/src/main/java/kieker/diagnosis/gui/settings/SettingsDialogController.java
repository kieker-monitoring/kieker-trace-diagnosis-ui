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
import kieker.diagnosis.service.properties.AdditionalLogChecksProperty;
import kieker.diagnosis.service.properties.CacheViewsProperty;
import kieker.diagnosis.service.properties.CaseSensitiveProperty;
import kieker.diagnosis.service.properties.ComponentNames;
import kieker.diagnosis.service.properties.ComponentNamesProperty;
import kieker.diagnosis.service.properties.MethodCallAggregationProperty;
import kieker.diagnosis.service.properties.OperationNames;
import kieker.diagnosis.service.properties.OperationNamesProperty;
import kieker.diagnosis.service.properties.PercentCalculationProperty;
import kieker.diagnosis.service.properties.PropertiesService;
import kieker.diagnosis.service.properties.RegularExpressionsProperty;
import kieker.diagnosis.service.properties.SearchInEntireTraceProperty;
import kieker.diagnosis.service.properties.ShowUnmonitoredTimeProperty;
import kieker.diagnosis.service.properties.Threshold;
import kieker.diagnosis.service.properties.ThresholdProperty;
import kieker.diagnosis.service.properties.TimeUnitProperty;
import kieker.diagnosis.service.properties.TimestampProperty;
import kieker.diagnosis.service.properties.TimestampTypes;

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
		getView( ).getOperationNames( ).getSelectionModel( ).select( ivPropertiesService.loadProperty( OperationNamesProperty.class ) );
		getView( ).getComponentNames( ).getSelectionModel( ).select( ivPropertiesService.loadProperty( ComponentNamesProperty.class ) );
		getView( ).getTimeunits( ).getSelectionModel( ).select( ivPropertiesService.loadProperty( TimeUnitProperty.class ) );
		getView( ).getAdditionalLogChecks( ).setSelected( ivPropertiesService.loadProperty( AdditionalLogChecksProperty.class ) );
		getView( ).getActivateRegularExpressions( ).setSelected( ivPropertiesService.loadProperty( RegularExpressionsProperty.class ) );
		getView( ).getAggregateMethodCalls( ).setSelected( ivPropertiesService.loadProperty( MethodCallAggregationProperty.class ) );
		getView( ).getThresholds( ).getSelectionModel( ).select( ivPropertiesService.loadProperty( ThresholdProperty.class ) );
		getView( ).getCaseSensitive( ).setSelected( ivPropertiesService.loadProperty( CaseSensitiveProperty.class ) );
		getView( ).getPercentageCalculation( ).setSelected( ivPropertiesService.loadProperty( PercentCalculationProperty.class ) );
		getView( ).getTimestamps( ).getSelectionModel( ).select( ivPropertiesService.loadProperty( TimestampProperty.class ) );
		getView( ).getCacheViews( ).setSelected( ivPropertiesService.loadProperty( CacheViewsProperty.class ) );
		getView( ).getSearchInEntireTrace( ).setSelected( ivPropertiesService.loadProperty( SearchInEntireTraceProperty.class ) );
		getView( ).getShowUnmonitoredTime( ).setSelected( ivPropertiesService.loadProperty( ShowUnmonitoredTimeProperty.class ) );
	}

	private void saveSettings( ) {
		ivPropertiesService.saveProperty( OperationNamesProperty.class, getView( ).getOperationNames( ).getSelectionModel( ).getSelectedItem( ) );
		ivPropertiesService.saveProperty( ComponentNamesProperty.class, getView( ).getComponentNames( ).getSelectionModel( ).getSelectedItem( ) );
		ivPropertiesService.saveProperty( TimeUnitProperty.class, getView( ).getTimeunits( ).getSelectionModel( ).getSelectedItem( ) );
		ivPropertiesService.saveProperty( AdditionalLogChecksProperty.class, getView( ).getAdditionalLogChecks( ).isSelected( ) );
		ivPropertiesService.saveProperty( RegularExpressionsProperty.class, getView( ).getActivateRegularExpressions( ).isSelected( ) );
		ivPropertiesService.saveProperty( MethodCallAggregationProperty.class, getView( ).getAggregateMethodCalls( ).isSelected( ) );
		ivPropertiesService.saveProperty( ThresholdProperty.class, getView( ).getThresholds( ).getSelectionModel( ).getSelectedItem( ) );
		ivPropertiesService.saveProperty( CaseSensitiveProperty.class, getView( ).getCaseSensitive( ).isSelected( ) );
		ivPropertiesService.saveProperty( PercentCalculationProperty.class, getView( ).getPercentageCalculation( ).isSelected( ) );
		ivPropertiesService.saveProperty( TimestampProperty.class, getView( ).getTimestamps( ).getSelectionModel( ).getSelectedItem( ) );
		ivPropertiesService.saveProperty( CacheViewsProperty.class, getView( ).getCacheViews( ).isSelected( ) );
		ivPropertiesService.saveProperty( SearchInEntireTraceProperty.class, getView( ).getSearchInEntireTrace( ).isSelected( ) );
		ivPropertiesService.saveProperty( ShowUnmonitoredTimeProperty.class, getView( ).getShowUnmonitoredTime( ).isSelected( ) );
	}

}
