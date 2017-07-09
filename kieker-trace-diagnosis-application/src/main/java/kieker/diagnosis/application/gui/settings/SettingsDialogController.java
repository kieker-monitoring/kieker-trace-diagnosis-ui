/***************************************************************************
 * Copyright 2015-2017 Kieker Project (http://kieker-monitoring.net)
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
import kieker.diagnosis.application.service.properties.MaxNumberOfMethodCallsProperty;
import kieker.diagnosis.application.service.properties.MethodCallAggregation;
import kieker.diagnosis.application.service.properties.MethodCallAggregationProperty;
import kieker.diagnosis.application.service.properties.MethodCallThresholdProperty;
import kieker.diagnosis.application.service.properties.OperationNames;
import kieker.diagnosis.application.service.properties.OperationNamesProperty;
import kieker.diagnosis.application.service.properties.PercentCalculationProperty;
import kieker.diagnosis.application.service.properties.RegularExpressionsProperty;
import kieker.diagnosis.application.service.properties.SearchInEntireTraceProperty;
import kieker.diagnosis.application.service.properties.ShowUnmonitoredTimeProperty;
import kieker.diagnosis.application.service.properties.TimeUnitProperty;
import kieker.diagnosis.application.service.properties.TimestampProperty;
import kieker.diagnosis.application.service.properties.TimestampTypes;
import kieker.diagnosis.architecture.exception.BusinessException;
import kieker.diagnosis.architecture.gui.AbstractController;
import kieker.diagnosis.architecture.service.properties.PropertiesService;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Nils Christian Ehmke
 */
@Component
public class SettingsDialogController extends AbstractController<SettingsDialogView> {

	private static final TimeUnit [] TIME_UNITS = { TimeUnit.NANOSECONDS, TimeUnit.MICROSECONDS, TimeUnit.MILLISECONDS, TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS };

	@Autowired
	private PropertiesService ivPropertiesService;

	@Override
	protected void doInitialize( final boolean aFirstInitialization, final Optional<?> aParameter ) {
		if ( aFirstInitialization ) {
			getView( ).getTimeunits( ).setItems( FXCollections.observableArrayList( SettingsDialogController.TIME_UNITS ) );
			getView( ).getComponentNames( ).setItems( FXCollections.observableArrayList( ComponentNames.values( ) ) );
			getView( ).getOperationNames( ).setItems( FXCollections.observableArrayList( OperationNames.values( ) ) );
			getView( ).getTimestamps( ).setItems( FXCollections.observableArrayList( TimestampTypes.values( ) ) );
			getView( ).getTypeOfMethodAggregation( ).setItems( FXCollections.observableArrayList( MethodCallAggregation.values( ) ) );

			final ObjectProperty<MethodCallAggregation> methodCallAggregationValueProperty = getView( ).getTypeOfMethodAggregation( ).valueProperty( );
			final BooleanBinding maxNumberOfMethodCallsBinding = methodCallAggregationValueProperty.isNotEqualTo( MethodCallAggregation.BY_THRESHOLD )
					.and( methodCallAggregationValueProperty.isNotEqualTo( MethodCallAggregation.NONE ) );
			getView( ).getMaxNumberOfMethodCallsLabel( ).visibleProperty( ).bind( maxNumberOfMethodCallsBinding );
			getView( ).getMaxNumberOfMethodCallsTextField( ).visibleProperty( ).bind( maxNumberOfMethodCallsBinding );

			final BooleanBinding thresholdBinding = methodCallAggregationValueProperty.isEqualTo( MethodCallAggregation.BY_THRESHOLD );
			getView( ).getThresholdLabel( ).visibleProperty( ).bind( thresholdBinding );
			getView( ).getThresholdTextField( ).visibleProperty( ).bind( thresholdBinding );
		}

		loadSettings( );
	}

	@Override
	public void doRefresh( ) {
		// Nothing to refresh
	}

	public void saveAndCloseDialog( ) throws BusinessException {
		validateSettings( );
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
		getView( ).getTypeOfMethodAggregation( ).getSelectionModel( ).select( ivPropertiesService.loadApplicationProperty( MethodCallAggregationProperty.class ) );
		getView( ).getThresholdTextField( ).setText( Float.toString( ivPropertiesService.loadApplicationProperty( MethodCallThresholdProperty.class ) ) );
		getView( ).getMaxNumberOfMethodCallsTextField( ).setText( Integer.toString( ivPropertiesService.loadApplicationProperty( MaxNumberOfMethodCallsProperty.class ) ) );
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
		ivPropertiesService.saveApplicationProperty( MethodCallAggregationProperty.class, getView( ).getTypeOfMethodAggregation( ).getValue( ) );

		ivPropertiesService.saveApplicationProperty( MethodCallThresholdProperty.class, Float.parseFloat( getView( ).getThresholdTextField( ).getText( ) ) );
		ivPropertiesService.saveApplicationProperty( MaxNumberOfMethodCallsProperty.class, Integer.parseInt( getView( ).getMaxNumberOfMethodCallsTextField( ).getText( ) ) );

		ivPropertiesService.saveApplicationProperty( CaseSensitiveProperty.class, getView( ).getCaseSensitive( ).isSelected( ) );
		ivPropertiesService.saveApplicationProperty( PercentCalculationProperty.class, getView( ).getPercentageCalculation( ).isSelected( ) );
		ivPropertiesService.saveApplicationProperty( TimestampProperty.class, getView( ).getTimestamps( ).getSelectionModel( ).getSelectedItem( ) );
		ivPropertiesService.saveApplicationProperty( SearchInEntireTraceProperty.class, getView( ).getSearchInEntireTrace( ).isSelected( ) );
		ivPropertiesService.saveApplicationProperty( ShowUnmonitoredTimeProperty.class, getView( ).getShowUnmonitoredTime( ).isSelected( ) );
	}

	private void validateSettings( ) throws BusinessException {
		final String thresholdText = getView( ).getThresholdTextField( ).getText( );
		try {
			final float threshold = Float.parseFloat( thresholdText );
			if ( ( threshold <= 0.0 ) || ( threshold >= 100.0 ) ) {

				throw new BusinessException( getResourceBundle( ).getString( "errorThresholdRange" ) );
			}
		} catch ( final NumberFormatException ex ) {
			throw new BusinessException( String.format( getResourceBundle( ).getString( "errorNotAValidFloatingNumber" ), thresholdText ), ex );
		}

		final String maxMethodCallsText = getView( ).getMaxNumberOfMethodCallsTextField( ).getText( );
		try {
			final int maxMethodCalls = Integer.parseInt( maxMethodCallsText );
			if ( maxMethodCalls <= 0 ) {
				throw new BusinessException( getResourceBundle( ).getString( "errorMaxNumberOfMethodCallsRange" ) );
			}
		} catch ( final NumberFormatException ex ) {
			throw new BusinessException( String.format( getResourceBundle( ).getString( "errorNotAValidInteger" ), thresholdText ), ex );
		}
	}

}
