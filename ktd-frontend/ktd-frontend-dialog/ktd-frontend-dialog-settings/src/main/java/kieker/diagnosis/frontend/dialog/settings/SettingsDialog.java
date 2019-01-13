/***************************************************************************
 * Copyright 2015-2019 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.frontend.dialog.settings;

import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import kieker.diagnosis.backend.settings.ClassAppearance;
import kieker.diagnosis.backend.settings.MethodAppearance;
import kieker.diagnosis.backend.settings.MethodCallAggregation;
import kieker.diagnosis.backend.settings.Settings;
import kieker.diagnosis.backend.settings.TimestampAppearance;
import kieker.diagnosis.frontend.base.atom.FloatTextField;
import kieker.diagnosis.frontend.base.atom.IntegerTextField;
import kieker.diagnosis.frontend.base.mixin.DialogMixin;
import kieker.diagnosis.frontend.base.mixin.ImageMixin;
import kieker.diagnosis.frontend.base.ui.EnumStringConverter;
import kieker.diagnosis.frontend.dialog.alert.Alert;

public final class SettingsDialog extends Dialog<Settings> implements DialogMixin, ImageMixin {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( SettingsDialog.class.getCanonicalName( ) );

	private final ObjectProperty<TimestampAppearance> timestampAppearance = new SimpleObjectProperty<>( );
	private final ObjectProperty<TimeUnit> timeUnit = new SimpleObjectProperty<>( );
	private final ObjectProperty<ClassAppearance> classAppearance = new SimpleObjectProperty<>( );
	private final ObjectProperty<MethodAppearance> methodAppearance = new SimpleObjectProperty<>( );
	private final BooleanProperty showUnmonitoredTimeProperty = new SimpleBooleanProperty( );
	private final ObjectProperty<MethodCallAggregation> methodCallAggregation = new SimpleObjectProperty<>( );
	private final ObjectProperty<Integer> maxNumberOfMethodCalls = new SimpleObjectProperty<>( );
	private final ObjectProperty<Float> methodCallThreshold = new SimpleObjectProperty<>( );

	private final Label timestampAppearanceLabel = new Label( );
	private final ComboBox<TimestampAppearance> timestampAppearanceField = new ComboBox<>( );
	private final Label timeUnitLabel = new Label( );
	private final ComboBox<TimeUnit> timeUnitField = new ComboBox<>( );
	private final Label classesLabel = new Label( );
	private final ComboBox<ClassAppearance> classesField = new ComboBox<>( );
	private final Label methodsLabel = new Label( );
	private final ComboBox<MethodAppearance> methodsField = new ComboBox<>( );
	private final Label showUnmonitoredTimeLabel = new Label( );
	private final CheckBox showUnmonitoredTimeField = new CheckBox( );
	private final Label methodCallAggregationLabel = new Label( );
	private final ComboBox<MethodCallAggregation> methodCallAggregationField = new ComboBox<>( );
	private final Label maxNumberOfCallsLabel = new Label( );
	private final IntegerTextField maxNumberOfCallsField = new IntegerTextField( );
	private final Label methodCallThresholdLabel = new Label( );
	private final FloatTextField methodCallThresholdField = new FloatTextField( );

	public SettingsDialog( ) {
		configureDialog( );
		addComponents( );
		configureResultConverter( );
		addButtons( );
	}

	private void configureDialog( ) {
		setTitle( RESOURCE_BUNDLE.getString( "title" ) );
		getStage( ).getIcons( ).add( createIcon( ) );
		addDefaultStylesheet( );
	}

	private Image createIcon( ) {
		final String iconPath = RESOURCE_BUNDLE.getString( "icon" );
		return loadImage( iconPath );
	}

	private void addComponents( ) {
		final VBox vBox = new VBox( );
		vBox.setSpacing( 10 );
		getDialogPane( ).setContent( vBox );

		addAppearancePane( vBox );
		addAggregateMethodCallsPane( vBox );
	}

	private void addAppearancePane( final VBox vBox ) {
		final TitledPane appearanceTitledPane = new TitledPane( );
		appearanceTitledPane.setCollapsible( false );
		appearanceTitledPane.setText( RESOURCE_BUNDLE.getString( "appearance" ) );

		final GridPane appearanceGridPane = new GridPane( );
		appearanceGridPane.setVgap( 5 );
		appearanceGridPane.setHgap( 5 );

		int rowIndex = 0;

		configureTimestampAppearanceLabelAndField( );
		appearanceGridPane.add( timestampAppearanceLabel, 1, rowIndex );
		appearanceGridPane.add( timestampAppearanceField, 2, rowIndex++ );

		configureTimeUnitLabelAndField( );
		appearanceGridPane.add( timeUnitLabel, 1, rowIndex );
		appearanceGridPane.add( timeUnitField, 2, rowIndex++ );

		configureClassesLabelAndField( );
		appearanceGridPane.add( classesLabel, 1, rowIndex );
		appearanceGridPane.add( classesField, 2, rowIndex++ );

		configureMethodsLabelAndField( );
		appearanceGridPane.add( methodsLabel, 1, rowIndex );
		appearanceGridPane.add( methodsField, 2, rowIndex++ );

		configureShowUnmonitoredTimeLabelAndField( );
		appearanceGridPane.add( showUnmonitoredTimeLabel, 1, rowIndex );
		appearanceGridPane.add( showUnmonitoredTimeField, 2, rowIndex++ );

		appearanceTitledPane.setContent( appearanceGridPane );

		vBox.getChildren( ).add( appearanceTitledPane );
	}

	private void addAggregateMethodCallsPane( final VBox vBox ) {
		final TitledPane aggregateMethodCallsTitledPane = new TitledPane( );
		aggregateMethodCallsTitledPane.setCollapsible( false );
		aggregateMethodCallsTitledPane.setText( RESOURCE_BUNDLE.getString( "aggregateMethodCalls" ) );

		final GridPane aggregateMethodCallsGridPane = new GridPane( );
		aggregateMethodCallsGridPane.setVgap( 5 );
		aggregateMethodCallsGridPane.setHgap( 5 );

		int rowIndex = 0;

		configureMethodCallAggregationLabelAndField( );
		aggregateMethodCallsGridPane.add( methodCallAggregationLabel, 1, rowIndex );
		aggregateMethodCallsGridPane.add( methodCallAggregationField, 2, rowIndex++ );

		configureMaxNumberOfCallsLabelAndField( );
		aggregateMethodCallsGridPane.add( maxNumberOfCallsLabel, 1, rowIndex );
		aggregateMethodCallsGridPane.add( maxNumberOfCallsField, 2, rowIndex++ );

		configureMethodCallThresholdLabelAndField( );
		aggregateMethodCallsGridPane.add( methodCallThresholdLabel, 1, rowIndex );
		aggregateMethodCallsGridPane.add( methodCallThresholdField, 2, rowIndex++ );

		aggregateMethodCallsTitledPane.setContent( aggregateMethodCallsGridPane );

		vBox.getChildren( ).add( aggregateMethodCallsTitledPane );
	}

	private void configureMaxNumberOfCallsLabelAndField( ) {
		maxNumberOfCallsLabel.setText( RESOURCE_BUNDLE.getString( "maxNumberOfMethodCalls" ) );

		maxNumberOfCallsField.setId( "settingsDialogMaxMethodCalls" );

		final ReadOnlyObjectProperty<MethodCallAggregation> property = methodCallAggregationField.getSelectionModel( ).selectedItemProperty( );
		maxNumberOfCallsField.disableProperty( ).bind( property.isEqualTo( MethodCallAggregation.NONE ).or( property.isEqualTo( MethodCallAggregation.BY_THRESHOLD ) ) );
		maxNumberOfCallsField.valueProperty( ).bindBidirectional( maxNumberOfMethodCalls );
	}

	private void configureMethodCallAggregationLabelAndField( ) {
		methodCallAggregationLabel.setText( RESOURCE_BUNDLE.getString( "methodCallAggregation" ) );

		methodCallAggregationField.setItems( FXCollections.observableArrayList( MethodCallAggregation.values( ) ) );
		methodCallAggregationField.setConverter( new EnumStringConverter<>( MethodCallAggregation.class ) );
		methodCallAggregationField.setMaxWidth( Double.POSITIVE_INFINITY );
		methodCallAggregationField.valueProperty( ).bindBidirectional( methodCallAggregation );

		GridPane.setHgrow( methodCallAggregationField, Priority.ALWAYS );
	}

	private void configureMethodCallThresholdLabelAndField( ) {
		methodCallThresholdLabel.setText( RESOURCE_BUNDLE.getString( "methodCallThreshold" ) );

		methodCallThresholdField.setId( "settingsDialogMethodCallThreshold" );
		methodCallThresholdField.valueProperty( ).bindBidirectional( methodCallThreshold );
		final ReadOnlyObjectProperty<MethodCallAggregation> selectedItemProperty = methodCallAggregationField.getSelectionModel( ).selectedItemProperty( );
		methodCallThresholdField.disableProperty( ).bind( selectedItemProperty.isEqualTo( MethodCallAggregation.BY_THRESHOLD ).not( ) );
	}

	private void configureShowUnmonitoredTimeLabelAndField( ) {
		showUnmonitoredTimeLabel.setText( RESOURCE_BUNDLE.getString( "showUnmonitoredTime" ) );

		showUnmonitoredTimeField.selectedProperty( ).bindBidirectional( showUnmonitoredTimeProperty );
	}

	private void configureMethodsLabelAndField( ) {
		methodsLabel.setText( RESOURCE_BUNDLE.getString( "methods" ) );

		methodsField.setItems( FXCollections.observableArrayList( MethodAppearance.values( ) ) );
		methodsField.setConverter( new EnumStringConverter<>( MethodAppearance.class ) );
		methodsField.setMaxWidth( Double.POSITIVE_INFINITY );
		methodsField.valueProperty( ).bindBidirectional( methodAppearance );

		GridPane.setHgrow( methodsField, Priority.ALWAYS );
	}

	private void configureClassesLabelAndField( ) {
		classesLabel.setText( RESOURCE_BUNDLE.getString( "classes" ) );

		classesField.setItems( FXCollections.observableArrayList( ClassAppearance.values( ) ) );
		classesField.setConverter( new EnumStringConverter<>( ClassAppearance.class ) );
		classesField.setMaxWidth( Double.POSITIVE_INFINITY );
		classesField.valueProperty( ).bindBidirectional( classAppearance );
	}

	private void configureTimeUnitLabelAndField( ) {
		timeUnitLabel.setText( RESOURCE_BUNDLE.getString( "timeunit" ) );

		timeUnitField.setItems( FXCollections.observableArrayList( TimeUnit.values( ) ) );
		timeUnitField.setConverter( new EnumStringConverter<>( TimeUnit.class ) );
		timeUnitField.setMaxWidth( Double.POSITIVE_INFINITY );
		timeUnitField.valueProperty( ).bindBidirectional( timeUnit );
	}

	private void configureTimestampAppearanceLabelAndField( ) {
		timestampAppearanceLabel.setText( RESOURCE_BUNDLE.getString( "timestamp" ) );

		timestampAppearanceField.setItems( FXCollections.observableArrayList( TimestampAppearance.values( ) ) );
		timestampAppearanceField.setValue( TimestampAppearance.DATE_AND_TIME );
		timestampAppearanceField.setConverter( new EnumStringConverter<>( TimestampAppearance.class ) );
		timestampAppearanceField.setMaxWidth( Double.POSITIVE_INFINITY );
		timestampAppearanceField.valueProperty( ).bindBidirectional( timestampAppearance );
	}

	private void configureResultConverter( ) {
		setResultConverter( buttonType -> buttonType == ButtonType.OK ? getValue( ) : null );
	}

	public void setValue( final Settings settings ) {
		timestampAppearance.setValue( settings.getTimestampAppearance( ) );
		timeUnit.setValue( settings.getTimeUnit( ) );
		classAppearance.setValue( settings.getClassAppearance( ) );
		methodAppearance.setValue( settings.getMethodAppearance( ) );
		showUnmonitoredTimeProperty.setValue( settings.isShowUnmonitoredTimeProperty( ) );
		methodCallAggregation.setValue( settings.getMethodCallAggregation( ) );
		maxNumberOfMethodCalls.setValue( settings.getMaxNumberOfMethodCalls( ) );
		methodCallThreshold.setValue( settings.getMethodCallThreshold( ) );
	}

	public Settings getValue( ) {
		return Settings.builder( )
				.timestampAppearance( timestampAppearance.getValue( ) )
				.timeUnit( timeUnit.getValue( ) )
				.classAppearance( classAppearance.getValue( ) )
				.methodAppearance( methodAppearance.getValue( ) )
				.showUnmonitoredTimeProperty( showUnmonitoredTimeProperty.getValue( ) )
				.methodCallAggregation( methodCallAggregation.getValue( ) )
				.maxNumberOfMethodCalls( maxNumberOfMethodCalls.getValue( ) )
				.methodCallThreshold( methodCallThreshold.getValue( ) )
				.build( );
	}

	private void addButtons( ) {
		getDialogPane( ).getButtonTypes( ).addAll( ButtonType.CANCEL, ButtonType.OK );
		getDialogPane( ).lookupButton( ButtonType.OK ).setId( "settingsDialogOk" );
		getDialogPane( ).lookupButton( ButtonType.OK ).addEventFilter( ActionEvent.ACTION, event -> {
			if ( !inputValid( ) ) {
				event.consume( );
			}
		} );
	}

	private boolean inputValid( ) {
		boolean inputValid = true;
		String errorMessage = "";

		final Float methodCallThreshold = this.methodCallThreshold.getValue( );
		if ( methodCallThreshold == null || methodCallThreshold <= 0.0f || methodCallThreshold >= 100.0 ) {
			inputValid = false;
			errorMessage = RESOURCE_BUNDLE.getString( "errorThresholdRange" );
		}

		final Integer maxNumberOfMethodCalls = this.maxNumberOfMethodCalls.getValue( );
		if ( maxNumberOfMethodCalls == null || maxNumberOfMethodCalls <= 0 ) {
			inputValid = false;
			errorMessage = RESOURCE_BUNDLE.getString( "errorMaxNumberOfMethodCallsRange" );
		}

		if ( !inputValid ) {
			final Alert alert = new Alert( AlertType.WARNING );
			alert.getDialogPane( ).lookupButton( ButtonType.OK ).setId( "settingsDialogValidationOk" );
			alert.setContentText( errorMessage );
			alert.show( );
		}

		return inputValid;
	}

}
