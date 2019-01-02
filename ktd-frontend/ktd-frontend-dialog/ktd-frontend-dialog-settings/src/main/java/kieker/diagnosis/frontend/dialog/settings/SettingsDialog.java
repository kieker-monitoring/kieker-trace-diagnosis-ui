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
import javafx.scene.control.Alert;
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

public final class SettingsDialog extends Dialog<Settings> implements DialogMixin, ImageMixin {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( SettingsDialog.class.getCanonicalName( ) );

	private final ObjectProperty<TimestampAppearance> ivTimestampAppearance = new SimpleObjectProperty<>( );
	private final ObjectProperty<TimeUnit> ivTimeUnit = new SimpleObjectProperty<>( );
	private final ObjectProperty<ClassAppearance> ivClassAppearance = new SimpleObjectProperty<>( );
	private final ObjectProperty<MethodAppearance> ivMethodAppearance = new SimpleObjectProperty<>( );
	private final BooleanProperty ivShowUnmonitoredTimeProperty = new SimpleBooleanProperty( );
	private final ObjectProperty<MethodCallAggregation> ivMethodCallAggregation = new SimpleObjectProperty<>( );
	private final ObjectProperty<Integer> ivMaxNumberOfMethodCalls = new SimpleObjectProperty<>( );
	private final ObjectProperty<Float> ivMethodCallThreshold = new SimpleObjectProperty<>( );

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

		{
			final TitledPane titledPane = new TitledPane( );
			titledPane.setCollapsible( false );
			titledPane.setText( RESOURCE_BUNDLE.getString( "appearance" ) );

			{
				final GridPane gridPane = new GridPane( );
				gridPane.setVgap( 5 );
				gridPane.setHgap( 5 );

				int rowIndex = 0;

				{
					final Label label = new Label( );
					label.setText( RESOURCE_BUNDLE.getString( "timestamp" ) );

					GridPane.setRowIndex( label, rowIndex );
					GridPane.setColumnIndex( label, 1 );

					gridPane.getChildren( ).add( label );
				}

				{
					final ComboBox<TimestampAppearance> timestampAppearanceField = new ComboBox<>( );
					timestampAppearanceField.setItems( FXCollections.observableArrayList( TimestampAppearance.values( ) ) );
					timestampAppearanceField.setValue( TimestampAppearance.DATE_AND_TIME );
					timestampAppearanceField.setConverter( new EnumStringConverter<>( TimestampAppearance.class ) );
					timestampAppearanceField.setMaxWidth( Double.POSITIVE_INFINITY );
					timestampAppearanceField.valueProperty( ).bindBidirectional( ivTimestampAppearance );

					GridPane.setRowIndex( timestampAppearanceField, rowIndex++ );
					GridPane.setColumnIndex( timestampAppearanceField, 2 );

					gridPane.getChildren( ).add( timestampAppearanceField );
				}

				{
					final Label label = new Label( );
					label.setText( RESOURCE_BUNDLE.getString( "timeunit" ) );

					GridPane.setRowIndex( label, rowIndex );
					GridPane.setColumnIndex( label, 1 );

					gridPane.getChildren( ).add( label );
				}

				{
					final ComboBox<TimeUnit> timeUnitField = new ComboBox<>( );
					timeUnitField.setItems( FXCollections.observableArrayList( TimeUnit.values( ) ) );
					timeUnitField.setConverter( new EnumStringConverter<>( TimeUnit.class ) );
					timeUnitField.setMaxWidth( Double.POSITIVE_INFINITY );
					timeUnitField.valueProperty( ).bindBidirectional( ivTimeUnit );

					GridPane.setRowIndex( timeUnitField, rowIndex++ );
					GridPane.setColumnIndex( timeUnitField, 2 );

					gridPane.getChildren( ).add( timeUnitField );
				}

				{
					final Label label = new Label( );
					label.setText( RESOURCE_BUNDLE.getString( "classes" ) );

					GridPane.setRowIndex( label, rowIndex );
					GridPane.setColumnIndex( label, 1 );

					gridPane.getChildren( ).add( label );
				}

				{
					final ComboBox<ClassAppearance> classesField = new ComboBox<>( );
					classesField.setItems( FXCollections.observableArrayList( ClassAppearance.values( ) ) );
					classesField.setConverter( new EnumStringConverter<>( ClassAppearance.class ) );
					classesField.setMaxWidth( Double.POSITIVE_INFINITY );
					classesField.valueProperty( ).bindBidirectional( ivClassAppearance );

					GridPane.setRowIndex( classesField, rowIndex++ );
					GridPane.setColumnIndex( classesField, 2 );

					gridPane.getChildren( ).add( classesField );
				}

				{
					final Label label = new Label( );
					label.setText( RESOURCE_BUNDLE.getString( "methods" ) );

					GridPane.setRowIndex( label, rowIndex );
					GridPane.setColumnIndex( label, 1 );

					gridPane.getChildren( ).add( label );
				}

				{
					final ComboBox<MethodAppearance> methodsField = new ComboBox<>( );
					methodsField.setItems( FXCollections.observableArrayList( MethodAppearance.values( ) ) );
					methodsField.setConverter( new EnumStringConverter<>( MethodAppearance.class ) );
					methodsField.setMaxWidth( Double.POSITIVE_INFINITY );
					methodsField.valueProperty( ).bindBidirectional( ivMethodAppearance );

					GridPane.setRowIndex( methodsField, rowIndex++ );
					GridPane.setColumnIndex( methodsField, 2 );
					GridPane.setHgrow( methodsField, Priority.ALWAYS );

					gridPane.getChildren( ).add( methodsField );
				}

				{
					final Label label = new Label( );
					label.setText( RESOURCE_BUNDLE.getString( "showUnmonitoredTime" ) );

					GridPane.setRowIndex( label, rowIndex );
					GridPane.setColumnIndex( label, 1 );

					gridPane.getChildren( ).add( label );
				}

				{
					final CheckBox showUnmonitoredTimeField = new CheckBox( );

					GridPane.setRowIndex( showUnmonitoredTimeField, rowIndex++ );
					GridPane.setColumnIndex( showUnmonitoredTimeField, 2 );

					gridPane.getChildren( ).add( showUnmonitoredTimeField );

					showUnmonitoredTimeField.selectedProperty( ).bindBidirectional( ivShowUnmonitoredTimeProperty );
				}

				titledPane.setContent( gridPane );
			}

			vBox.getChildren( ).add( titledPane );
		}

		{
			final TitledPane titledPane = new TitledPane( );
			titledPane.setCollapsible( false );
			titledPane.setText( RESOURCE_BUNDLE.getString( "aggregateMethodCalls" ) );

			{
				final GridPane gridPane = new GridPane( );
				gridPane.setVgap( 5 );
				gridPane.setHgap( 5 );

				int rowIndex = 0;

				{
					final Label label = new Label( );
					label.setText( RESOURCE_BUNDLE.getString( "methodCallAggregation" ) );

					GridPane.setRowIndex( label, rowIndex );
					GridPane.setColumnIndex( label, 1 );

					gridPane.getChildren( ).add( label );
				}

				final ComboBox<MethodCallAggregation> methodCallAggregationField = new ComboBox<>( );
				{
					methodCallAggregationField.setItems( FXCollections.observableArrayList( MethodCallAggregation.values( ) ) );
					methodCallAggregationField.setConverter( new EnumStringConverter<>( MethodCallAggregation.class ) );
					methodCallAggregationField.setMaxWidth( Double.POSITIVE_INFINITY );
					methodCallAggregationField.valueProperty( ).bindBidirectional( ivMethodCallAggregation );

					GridPane.setRowIndex( methodCallAggregationField, rowIndex++ );
					GridPane.setColumnIndex( methodCallAggregationField, 2 );
					GridPane.setHgrow( methodCallAggregationField, Priority.ALWAYS );

					gridPane.getChildren( ).add( methodCallAggregationField );
				}

				{
					final Label label = new Label( );
					label.setText( RESOURCE_BUNDLE.getString( "maxNumberOfMethodCalls" ) );

					GridPane.setRowIndex( label, rowIndex );
					GridPane.setColumnIndex( label, 1 );

					gridPane.getChildren( ).add( label );
				}

				{
					final IntegerTextField maxNumberOfCallsField = new IntegerTextField( );
					maxNumberOfCallsField.setId( "settingsDialogMaxMethodCalls" );

					final ReadOnlyObjectProperty<MethodCallAggregation> property = methodCallAggregationField.getSelectionModel( ).selectedItemProperty( );
					maxNumberOfCallsField.disableProperty( )
							.bind( property.isEqualTo( MethodCallAggregation.NONE ).or( property.isEqualTo( MethodCallAggregation.BY_THRESHOLD ) ) );
					maxNumberOfCallsField.valueProperty( ).bindBidirectional( ivMaxNumberOfMethodCalls );

					GridPane.setRowIndex( maxNumberOfCallsField, rowIndex++ );
					GridPane.setColumnIndex( maxNumberOfCallsField, 2 );

					gridPane.getChildren( ).add( maxNumberOfCallsField );
				}

				{
					final Label label = new Label( );
					label.setText( RESOURCE_BUNDLE.getString( "methodCallThreshold" ) );

					GridPane.setRowIndex( label, rowIndex );
					GridPane.setColumnIndex( label, 1 );

					gridPane.getChildren( ).add( label );
				}

				{
					final FloatTextField methodCallThresholdField = new FloatTextField( );
					methodCallThresholdField.setId( "settingsDialogMethodCallThreshold" );
					methodCallThresholdField.valueProperty( ).bindBidirectional( ivMethodCallThreshold );
					final ReadOnlyObjectProperty<MethodCallAggregation> selectedItemProperty = methodCallAggregationField.getSelectionModel( ).selectedItemProperty( );
					methodCallThresholdField.disableProperty( ).bind( selectedItemProperty.isEqualTo( MethodCallAggregation.BY_THRESHOLD ).not( ) );

					GridPane.setRowIndex( methodCallThresholdField, rowIndex++ );
					GridPane.setColumnIndex( methodCallThresholdField, 2 );

					gridPane.getChildren( ).add( methodCallThresholdField );
				}

				titledPane.setContent( gridPane );
			}

			vBox.getChildren( ).add( titledPane );
		}

	}

	private void configureResultConverter( ) {
		setResultConverter( buttonType -> buttonType == ButtonType.OK ? getValue( ) : null );
	}

	public void setValue( final Settings settings ) {
		ivTimestampAppearance.setValue( settings.getTimestampAppearance( ) );
		ivTimeUnit.setValue( settings.getTimeUnit( ) );
		ivClassAppearance.setValue( settings.getClassAppearance( ) );
		ivMethodAppearance.setValue( settings.getMethodAppearance( ) );
		ivShowUnmonitoredTimeProperty.setValue( settings.isShowUnmonitoredTimeProperty( ) );
		ivMethodCallAggregation.setValue( settings.getMethodCallAggregation( ) );
		ivMaxNumberOfMethodCalls.setValue( settings.getMaxNumberOfMethodCalls( ) );
		ivMethodCallThreshold.setValue( settings.getMethodCallThreshold( ) );
	}

	public Settings getValue( ) {
		return Settings.builder( )
				.timestampAppearance( ivTimestampAppearance.getValue( ) )
				.timeUnit( ivTimeUnit.getValue( ) )
				.classAppearance( ivClassAppearance.getValue( ) )
				.methodAppearance( ivMethodAppearance.getValue( ) )
				.showUnmonitoredTimeProperty( ivShowUnmonitoredTimeProperty.getValue( ) )
				.methodCallAggregation( ivMethodCallAggregation.getValue( ) )
				.maxNumberOfMethodCalls( ivMaxNumberOfMethodCalls.getValue( ) )
				.methodCallThreshold( ivMethodCallThreshold.getValue( ) )
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

		final Float methodCallThreshold = ivMethodCallThreshold.getValue( );
		if ( methodCallThreshold == null || methodCallThreshold <= 0.0f || methodCallThreshold >= 100.0 ) {
			inputValid = false;
			errorMessage = RESOURCE_BUNDLE.getString( "errorThresholdRange" );
		}

		final Integer maxNumberOfMethodCalls = ivMaxNumberOfMethodCalls.getValue( );
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
