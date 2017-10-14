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

		// Try to convert
		final String methodCallThresholdText = getView( ).getMethodCallThreshold( ).getText( );
		try {
			final float methodCallThreshold = Float.parseFloat( methodCallThresholdText );

			// Check the range
			if ( methodCallThreshold <= 0.0f || methodCallThreshold >= 100.0 ) {
				throw new BusinessException( getLocalizedString( "errorThresholdRange" ) );
			}

			settings.setMethodCallThreshold( methodCallThreshold );
		} catch ( final NumberFormatException ex ) {
			throw new BusinessException( getLocalizedString( "errorNotAValidFloatingNumber" ), methodCallThresholdText );
		}

		// Try to convert
		final String maxNumberOfMethodCallsText = getView( ).getMaxNumberOfMethodCalls( ).getText( );
		try {
			final int maxNumberOfMethodCalls = Integer.parseInt( maxNumberOfMethodCallsText );

			// Check the range
			if ( maxNumberOfMethodCalls <= 0 ) {
				throw new BusinessException( getLocalizedString( "errorMaxNumberOfMethodCallsRange" ) );
			}

			settings.setMaxNumberOfMethodCalls( maxNumberOfMethodCalls );
		} catch ( final NumberFormatException ex ) {
			throw new BusinessException( getLocalizedString( "errorNotAValidInteger" ), maxNumberOfMethodCallsText );
		}

		return settings;
	}

}
