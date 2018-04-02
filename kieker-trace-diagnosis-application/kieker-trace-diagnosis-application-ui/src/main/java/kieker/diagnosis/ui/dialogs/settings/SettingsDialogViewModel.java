/***************************************************************************
 * Copyright 2015-2018 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.ui.dialogs.settings;

import java.util.concurrent.TimeUnit;

import com.google.inject.Singleton;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Command;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import kieker.diagnosis.architecture.exception.BusinessException;
import kieker.diagnosis.architecture.ui.ViewModelBase;
import kieker.diagnosis.service.settings.ClassAppearance;
import kieker.diagnosis.service.settings.MethodAppearance;
import kieker.diagnosis.service.settings.MethodCallAggregation;
import kieker.diagnosis.service.settings.Settings;
import kieker.diagnosis.service.settings.SettingsService;
import kieker.diagnosis.service.settings.TimestampAppearance;

/**
 * The view model of the settings dialog.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class SettingsDialogViewModel extends ViewModelBase<SettingsDialogView> implements ViewModel {

	public static final String EVENT_CLOSE_DIALOG = "EVENT_CLOSE_DIALOG";

	private final Command ivSaveAndCloseCommand = createCommand( this::performSaveAndClose );
	private final Command ivCloseCommand = createCommand( this::performClose );

	private final ObjectProperty<TimestampAppearance> ivTimestampAppearanceProperty = new SimpleObjectProperty<>( );
	private final ObjectProperty<TimeUnit> ivTimeUnitProperty = new SimpleObjectProperty<>( );
	private final ObjectProperty<ClassAppearance> ivClassesProperty = new SimpleObjectProperty<>( );
	private final ObjectProperty<MethodAppearance> ivMethodsProperty = new SimpleObjectProperty<>( );
	private final BooleanProperty ivShowUnmonitoredTimeProperty = new SimpleBooleanProperty( );
	private final ObjectProperty<MethodCallAggregation> ivMethodCallAggregationProperty = new SimpleObjectProperty<>( );
	private final ObjectProperty<Integer> ivMaxNumberOfCallsProperty = new SimpleObjectProperty<>( );
	private final ObjectProperty<Float> ivMethodCallThresholdProperty = new SimpleObjectProperty<>( );
	private final ObjectProperty<Integer> ivMaxNumberOfMethodCallsProperty = new SimpleObjectProperty<>( );

	public void initialize( ) {
		// Get the current settings...
		final SettingsService settingsService = getService( SettingsService.class );
		final Settings settings = settingsService.loadSettings( );

		// ...and display them.
		updatePresentation( settings );
	}

	Command getSaveAndCloseCommand( ) {
		return ivSaveAndCloseCommand;
	}

	/**
	 * This action is performed, when the user wants to save and close the dialog.
	 */
	private void performSaveAndClose( ) throws BusinessException {
		// Get the settings...
		final Settings settings = savePresentation( );

		// ...and save them
		final SettingsService settingsService = getService( SettingsService.class );
		settingsService.saveSettings( settings );

		performClose( );
	}

	Command getCloseCommand( ) {
		return ivCloseCommand;
	}

	public void performClose( ) {
		publish( EVENT_CLOSE_DIALOG );
	}

	private void updatePresentation( final Settings aSettings ) {
		ivTimestampAppearanceProperty.setValue( aSettings.getTimestampAppearance( ) );
		ivTimeUnitProperty.setValue( aSettings.getTimeUnit( ) );
		ivClassesProperty.setValue( aSettings.getClassAppearance( ) );
		ivMethodsProperty.setValue( aSettings.getMethodAppearance( ) );
		ivShowUnmonitoredTimeProperty.set( aSettings.isShowUnmonitoredTimeProperty( ) );
		ivMethodCallAggregationProperty.setValue( aSettings.getMethodCallAggregation( ) );
		ivMethodCallThresholdProperty.setValue( Float.valueOf( aSettings.getMethodCallThreshold( ) ) );
		ivMaxNumberOfMethodCallsProperty.setValue( Integer.valueOf( aSettings.getMaxNumberOfMethodCalls( ) ) );
	}

	private Settings savePresentation( ) throws BusinessException {
		final Settings settings = new Settings( );

		settings.setTimestampAppearance( ivTimestampAppearanceProperty.getValue( ) );
		settings.setTimeUnit( ivTimeUnitProperty.getValue( ) );
		settings.setClassAppearance( ivClassesProperty.getValue( ) );
		settings.setMethodAppearance( ivMethodsProperty.getValue( ) );
		settings.setShowUnmonitoredTimeProperty( ivShowUnmonitoredTimeProperty.get( ) );
		settings.setMethodCallAggregation( ivMethodCallAggregationProperty.getValue( ) );

		// Check the range
		final Float methodCallThreshold = ivMethodCallThresholdProperty.getValue( );
		if ( methodCallThreshold == null || methodCallThreshold <= 0.0f || methodCallThreshold >= 100.0 ) {
			throw new BusinessException( getLocalizedString( "errorThresholdRange" ) );
		}
		settings.setMethodCallThreshold( methodCallThreshold );

		// Check the range
		final Integer maxNumberOfMethodCalls = ivMaxNumberOfMethodCallsProperty.getValue( );
		if ( maxNumberOfMethodCalls == null || maxNumberOfMethodCalls <= 0 ) {
			throw new BusinessException( getLocalizedString( "errorMaxNumberOfMethodCallsRange" ) );
		}
		settings.setMaxNumberOfMethodCalls( maxNumberOfMethodCalls );

		return settings;
	}

	ObjectProperty<TimestampAppearance> getTimestampAppearanceProperty( ) {
		return ivTimestampAppearanceProperty;
	}

	ObjectProperty<TimeUnit> getTimeUnitProperty( ) {
		return ivTimeUnitProperty;
	}

	ObjectProperty<ClassAppearance> getClassesProperty( ) {
		return ivClassesProperty;
	}

	ObjectProperty<MethodAppearance> getMethodsProperty( ) {
		return ivMethodsProperty;
	}

	BooleanProperty getShowUnmonitoredTimeProperty( ) {
		return ivShowUnmonitoredTimeProperty;
	}

	ObjectProperty<MethodCallAggregation> getMethodCallAggregationProperty( ) {
		return ivMethodCallAggregationProperty;
	}

	ObjectProperty<Integer> getMaxNumberOfCallsProperty( ) {
		return ivMaxNumberOfCallsProperty;
	}

	ObjectProperty<Float> getMethodCallThresholdProperty( ) {
		return ivMethodCallThresholdProperty;
	}

	ObjectProperty<Integer> getMaxNumberOfMethodCallsProperty( ) {
		return ivMaxNumberOfMethodCallsProperty;
	}

}
