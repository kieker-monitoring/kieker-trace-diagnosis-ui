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

package kieker.diagnosis.ui.settings;

import com.google.inject.Singleton;

import kieker.diagnosis.architecture.exception.BusinessException;
import kieker.diagnosis.architecture.ui.ViewModelBase;
import kieker.diagnosis.service.settings.Settings;

@Singleton
class SettingsDialogViewModel extends ViewModelBase<SettingsDialogView> {

	public void updatePresentation( final Settings aSettings ) {
		getView( ).getTimestampAppearanceComboBox( ).setValue( aSettings.getTimestampAppearance( ) );
		getView( ).getTimeUnitComboBox( ).setValue( aSettings.getTimeUnit( ) );
		getView( ).getClassesComboBox( ).setValue( aSettings.getClassAppearance( ) );
		getView( ).getMethodsComboBox( ).setValue( aSettings.getMethodAppearance( ) );
		getView( ).getShowUnmonitoredTime( ).setSelected( aSettings.isShowUnmonitoredTimeProperty( ) );
		getView( ).getMethodCallAggregation( ).setValue( aSettings.getMethodCallAggregation( ) );
		getView( ).getMethodCallThreshold( ).setText( Float.toString( aSettings.getMethodCallThreshold( ) ) );
		getView( ).getMaxNumberOfMethodCalls( ).setText( Integer.toString( aSettings.getMaxNumberOfMethodCalls( ) ) );
	}

	public Settings savePresentation( ) throws BusinessException {
		final Settings settings = new Settings( );

		settings.setTimestampAppearance( getView( ).getTimestampAppearanceComboBox( ).getValue( ) );
		settings.setTimeUnit( getView( ).getTimeUnitComboBox( ).getValue( ) );
		settings.setClassAppearance( getView( ).getClassesComboBox( ).getValue( ) );
		settings.setMethodAppearance( getView( ).getMethodsComboBox( ).getValue( ) );
		settings.setShowUnmonitoredTimeProperty( getView( ).getShowUnmonitoredTime( ).isSelected( ) );
		settings.setMethodCallAggregation( getView( ).getMethodCallAggregation( ).getValue( ) );

		// Check the range
		final Float methodCallThreshold = getView( ).getMethodCallThreshold( ).getValue( );
		if ( methodCallThreshold == null || methodCallThreshold <= 0.0f || methodCallThreshold >= 100.0 ) {
			throw new BusinessException( getLocalizedString( "errorThresholdRange" ) );
		}
		settings.setMethodCallThreshold( methodCallThreshold );

		// Check the range
		final Integer maxNumberOfMethodCalls = getView( ).getMaxNumberOfMethodCalls( ).getValue( );
		if ( maxNumberOfMethodCalls == null || maxNumberOfMethodCalls <= 0 ) {
			throw new BusinessException( getLocalizedString( "errorMaxNumberOfMethodCallsRange" ) );
		}
		settings.setMaxNumberOfMethodCalls( maxNumberOfMethodCalls );

		return settings;
	}

}
