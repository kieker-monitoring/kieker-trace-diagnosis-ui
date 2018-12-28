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

package kieker.diagnosis.ui.composite.monitoring;

import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import kieker.diagnosis.architecture.ui.EnumStringConverter;
import kieker.diagnosis.backend.monitoring.MonitoringConfiguration;
import kieker.diagnosis.backend.monitoring.Status;
import kieker.diagnosis.backend.monitoring.Timer;
import kieker.diagnosis.backend.monitoring.Writer;
import kieker.diagnosis.ui.atom.IntegerTextField;
import kieker.diagnosis.ui.mixin.DialogMixin;
import kieker.diagnosis.ui.mixin.ImageMixin;

public final class MonitoringDialog extends Dialog<MonitoringConfiguration> implements DialogMixin, ImageMixin {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( MonitoringDialog.class.getCanonicalName( ) );

	private final StringProperty ivOutputDirectory = new SimpleStringProperty( );
	private final BooleanProperty ivActive = new SimpleBooleanProperty( );
	private final ObjectProperty<Timer> ivTimer = new SimpleObjectProperty<>( );
	private final ObjectProperty<Writer> ivWriter = new SimpleObjectProperty<>( );
	private final ObjectProperty<Integer> ivMaxEntriesPerFile = new SimpleObjectProperty<>( );
	private final ObjectProperty<Integer> ivQueueSize = new SimpleObjectProperty<>( );
	private final ObjectProperty<Integer> ivBuffer = new SimpleObjectProperty<>( );
	private final ObjectProperty<Status> ivStatus = new SimpleObjectProperty<>( );

	public MonitoringDialog( ) {
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
		final GridPane gridPane = new GridPane( );
		VBox.setMargin( gridPane, new Insets( 10, 10, 0, 10 ) );
		gridPane.setVgap( 5 );
		gridPane.setHgap( 5 );

		int rowIndex = 0;

		{
			final Label label = new Label( );
			label.setText( RESOURCE_BUNDLE.getString( "status" ) );

			GridPane.setRowIndex( label, rowIndex );
			GridPane.setColumnIndex( label, 1 );

			gridPane.getChildren( ).add( label );
		}

		{
			final Label statusField = new Label( );
			statusField.setId( "monitoringDialogStatus" );
			statusField.setMaxWidth( Double.POSITIVE_INFINITY );
			statusField.textProperty( ).bindBidirectional( ivStatus, new EnumStringConverter<>( Status.class ) );
			ivStatus.addListener( ( ChangeListener<Status> ) ( observable, oldStatus, newStatus ) -> {
				final List<String> style = getStatusStyle( newStatus );
				statusField.getStyleClass( ).setAll( style );
			} );

			GridPane.setRowIndex( statusField, rowIndex++ );
			GridPane.setColumnIndex( statusField, 2 );
			GridPane.setHgrow( statusField, Priority.ALWAYS );

			gridPane.getChildren( ).add( statusField );
		}

		{
			final Label label = new Label( );
			label.setText( RESOURCE_BUNDLE.getString( "active" ) );

			GridPane.setRowIndex( label, rowIndex );
			GridPane.setColumnIndex( label, 1 );

			gridPane.getChildren( ).add( label );
		}

		{
			final CheckBox activeField = new CheckBox( );
			activeField.setId( "monitoringDialogActive" );
			activeField.setMaxWidth( Double.POSITIVE_INFINITY );
			activeField.selectedProperty( ).bindBidirectional( ivActive );

			GridPane.setRowIndex( activeField, rowIndex++ );
			GridPane.setColumnIndex( activeField, 2 );

			gridPane.getChildren( ).add( activeField );
		}

		{
			final Label label = new Label( );
			label.setText( RESOURCE_BUNDLE.getString( "outputDirectory" ) );

			GridPane.setRowIndex( label, rowIndex );
			GridPane.setColumnIndex( label, 1 );

			gridPane.getChildren( ).add( label );
		}

		{
			final TextField outputDirectoryField = new TextField( );
			outputDirectoryField.setMaxWidth( Double.POSITIVE_INFINITY );
			outputDirectoryField.textProperty( ).bindBidirectional( ivOutputDirectory );

			GridPane.setRowIndex( outputDirectoryField, rowIndex++ );
			GridPane.setColumnIndex( outputDirectoryField, 2 );

			gridPane.getChildren( ).add( outputDirectoryField );
		}

		{
			final Label label = new Label( );
			label.setText( RESOURCE_BUNDLE.getString( "timer" ) );

			GridPane.setRowIndex( label, rowIndex );
			GridPane.setColumnIndex( label, 1 );

			gridPane.getChildren( ).add( label );
		}

		{
			final ComboBox<Timer> timerField = new ComboBox<>( );
			timerField.setMaxWidth( Double.POSITIVE_INFINITY );
			timerField.setConverter( new EnumStringConverter<>( Timer.class ) );
			timerField.valueProperty( ).bindBidirectional( ivTimer );
			timerField.setItems( FXCollections.observableArrayList( Timer.values( ) ) );

			GridPane.setRowIndex( timerField, rowIndex++ );
			GridPane.setColumnIndex( timerField, 2 );

			gridPane.getChildren( ).add( timerField );
		}

		{
			final Label label = new Label( );
			label.setText( RESOURCE_BUNDLE.getString( "writer" ) );

			GridPane.setRowIndex( label, rowIndex );
			GridPane.setColumnIndex( label, 1 );

			gridPane.getChildren( ).add( label );
		}

		{
			final ComboBox<Writer> writerField = new ComboBox<>( );
			writerField.setMaxWidth( Double.POSITIVE_INFINITY );
			writerField.setConverter( new EnumStringConverter<>( Writer.class ) );
			writerField.valueProperty( ).bindBidirectional( ivWriter );
			writerField.setItems( FXCollections.observableArrayList( Writer.values( ) ) );

			GridPane.setRowIndex( writerField, rowIndex++ );
			GridPane.setColumnIndex( writerField, 2 );

			gridPane.getChildren( ).add( writerField );
		}

		{
			final Label label = new Label( );
			label.setText( RESOURCE_BUNDLE.getString( "maxEntriesPerFile" ) );

			GridPane.setRowIndex( label, rowIndex );
			GridPane.setColumnIndex( label, 1 );

			gridPane.getChildren( ).add( label );
		}

		{
			final IntegerTextField maxEntriesPerFileField = new IntegerTextField( );
			maxEntriesPerFileField.setMaxWidth( Double.POSITIVE_INFINITY );
			maxEntriesPerFileField.valueProperty( ).bindBidirectional( ivMaxEntriesPerFile );

			GridPane.setRowIndex( maxEntriesPerFileField, rowIndex++ );
			GridPane.setColumnIndex( maxEntriesPerFileField, 2 );

			gridPane.getChildren( ).add( maxEntriesPerFileField );
		}

		{
			final Label label = new Label( );
			label.setText( RESOURCE_BUNDLE.getString( "queueSize" ) );

			GridPane.setRowIndex( label, rowIndex );
			GridPane.setColumnIndex( label, 1 );

			gridPane.getChildren( ).add( label );
		}

		{
			final IntegerTextField queueSizeField = new IntegerTextField( );
			queueSizeField.setMaxWidth( Double.POSITIVE_INFINITY );
			queueSizeField.valueProperty( ).bindBidirectional( ivQueueSize );

			GridPane.setRowIndex( queueSizeField, rowIndex++ );
			GridPane.setColumnIndex( queueSizeField, 2 );

			gridPane.getChildren( ).add( queueSizeField );
		}

		{
			final Label label = new Label( );
			label.setText( RESOURCE_BUNDLE.getString( "buffer" ) );

			GridPane.setRowIndex( label, rowIndex );
			GridPane.setColumnIndex( label, 1 );

			gridPane.getChildren( ).add( label );
		}

		{
			final IntegerTextField bufferField = new IntegerTextField( );
			bufferField.setMaxWidth( Double.POSITIVE_INFINITY );
			bufferField.valueProperty( ).bindBidirectional( ivBuffer );

			GridPane.setRowIndex( bufferField, rowIndex++ );
			GridPane.setColumnIndex( bufferField, 2 );

			gridPane.getChildren( ).add( bufferField );
		}

		getDialogPane( ).setContent( gridPane );
	}

	private void configureResultConverter( ) {
		setResultConverter( buttonType -> buttonType == ButtonType.OK ? getValue( ) : null );
	}

	private void addButtons( ) {
		getDialogPane( ).getButtonTypes( ).addAll( ButtonType.CANCEL, ButtonType.OK );
		getDialogPane( ).lookupButton( ButtonType.OK ).setId( "monitoringDialogOk" );
		getDialogPane( ).lookupButton( ButtonType.OK ).addEventFilter( ActionEvent.ACTION, event -> {
			if ( !inputValid( ) ) {
				event.consume( );
			}
		} );
	}

	private boolean inputValid( ) {
		boolean inputValid = true;

		final Integer maxEntriesPerFile = ivMaxEntriesPerFile.get( );
		if ( maxEntriesPerFile == null || ivMaxEntriesPerFile.get( ) <= 0 ) {
			inputValid = false;
		}

		final Integer queueSize = ivQueueSize.get( );
		if ( queueSize == null || queueSize <= 0 ) {
			inputValid = false;
		}

		final Integer buffer = ivBuffer.get( );
		if ( buffer == null || buffer <= 0 ) {
			inputValid = false;
		}

		if ( !inputValid ) {
			final Alert alert = new Alert( AlertType.WARNING );
			alert.setContentText( RESOURCE_BUNDLE.getString( "errorRange" ) );
			alert.show( );
		}

		return inputValid;
	}

	private List<String> getStatusStyle( final Status aStatus ) {
		String style;

		switch ( aStatus ) {
		case RUNNING:
			style = "monitoringRunning";
			break;
		case TERMINATED:
			style = "monitoringTerminated";
			break;
		case NO_MONITORING:
		default:
			style = "noMonitoringStarted";
			break;

		}

		return List.of( "label", style );
	}

	public void setValue( final MonitoringConfiguration monitoringConfiguration ) {
		ivActive.setValue( monitoringConfiguration.isActive( ) );
		ivOutputDirectory.setValue( monitoringConfiguration.getOutputDirectory( ) );
		ivTimer.setValue( monitoringConfiguration.getTimer( ) );
		ivWriter.setValue( monitoringConfiguration.getWriter( ) );
		ivMaxEntriesPerFile.setValue( monitoringConfiguration.getMaxEntriesPerFile( ) );
		ivQueueSize.setValue( monitoringConfiguration.getQueueSize( ) );
		ivBuffer.setValue( monitoringConfiguration.getBuffer( ) );
	}

	public void setStatus( final Status status ) {
		ivStatus.setValue( status );
	}

	public MonitoringConfiguration getValue( ) {
		final MonitoringConfiguration monitoringConfiguration = new MonitoringConfiguration( );

		monitoringConfiguration.setActive( ivActive.getValue( ) );
		monitoringConfiguration.setOutputDirectory( ivOutputDirectory.getValue( ) );
		monitoringConfiguration.setTimer( ivTimer.getValue( ) );
		monitoringConfiguration.setWriter( ivWriter.getValue( ) );
		monitoringConfiguration.setMaxEntriesPerFile( ivMaxEntriesPerFile.getValue( ) );
		monitoringConfiguration.setQueueSize( ivQueueSize.getValue( ) );
		monitoringConfiguration.setBuffer( ivBuffer.getValue( ) );

		return monitoringConfiguration;
	}

}
