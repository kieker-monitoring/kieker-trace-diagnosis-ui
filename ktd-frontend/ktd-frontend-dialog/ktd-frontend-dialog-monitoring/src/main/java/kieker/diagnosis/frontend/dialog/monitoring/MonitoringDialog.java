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

package kieker.diagnosis.frontend.dialog.monitoring;

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
import kieker.diagnosis.backend.monitoring.MonitoringConfiguration;
import kieker.diagnosis.backend.monitoring.Status;
import kieker.diagnosis.backend.monitoring.Timer;
import kieker.diagnosis.backend.monitoring.Writer;
import kieker.diagnosis.frontend.base.atom.IntegerTextField;
import kieker.diagnosis.frontend.base.mixin.DialogMixin;
import kieker.diagnosis.frontend.base.mixin.ImageMixin;
import kieker.diagnosis.frontend.base.ui.EnumStringConverter;
import kieker.diagnosis.frontend.dialog.alert.Alert;

public final class MonitoringDialog extends Dialog<MonitoringConfiguration> implements DialogMixin, ImageMixin {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( MonitoringDialog.class.getCanonicalName( ) );

	private final StringProperty outputDirectory = new SimpleStringProperty( );
	private final BooleanProperty active = new SimpleBooleanProperty( );
	private final ObjectProperty<Timer> timer = new SimpleObjectProperty<>( );
	private final ObjectProperty<Writer> writer = new SimpleObjectProperty<>( );
	private final ObjectProperty<Integer> maxEntriesPerFile = new SimpleObjectProperty<>( );
	private final ObjectProperty<Integer> queueSize = new SimpleObjectProperty<>( );
	private final ObjectProperty<Integer> buffer = new SimpleObjectProperty<>( );
	private final ObjectProperty<Status> status = new SimpleObjectProperty<>( );

	private final GridPane gridPane = new GridPane( );
	private final Label statusLabel = new Label( );
	private final Label statusField = new Label( );
	private final Label activeLabel = new Label( );
	private final CheckBox activeField = new CheckBox( );
	private final Label outputDirectoryLabel = new Label( );
	private final TextField outputDirectoryField = new TextField( );
	private final Label timerLabel = new Label( );
	private final ComboBox<Timer> timerField = new ComboBox<>( );
	private final Label writerLabel = new Label( );
	private final ComboBox<Writer> writerField = new ComboBox<>( );
	private final Label maxEntriesPerFileLabel = new Label( );
	private final IntegerTextField maxEntriesPerFileField = new IntegerTextField( );
	private final Label queueSizeLabel = new Label( );
	private final IntegerTextField queueSizeField = new IntegerTextField( );
	private final Label bufferLabel = new Label( );
	private final IntegerTextField bufferField = new IntegerTextField( );

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
		configureGridPane( );
		getDialogPane( ).setContent( gridPane );

		int rowIndex = 0;

		configureStatusLabelAndField( );
		gridPane.add( statusLabel, 1, rowIndex );
		gridPane.add( statusField, 2, rowIndex++ );

		configureActiveLabelAndField( );
		gridPane.add( activeLabel, 1, rowIndex );
		gridPane.add( activeField, 2, rowIndex++ );

		configureOutputDirectoryLabelAndField( );
		gridPane.add( outputDirectoryLabel, 1, rowIndex );
		gridPane.add( outputDirectoryField, 2, rowIndex++ );

		configureTimerLabelAndField( );
		gridPane.add( timerLabel, 1, rowIndex );
		gridPane.add( timerField, 2, rowIndex++ );

		configureWriterLabelAndField( );
		gridPane.add( writerLabel, 1, rowIndex );
		gridPane.add( writerField, 2, rowIndex++ );

		configureMaxEntriesPerFileLabelAndField( );
		gridPane.add( maxEntriesPerFileLabel, 1, rowIndex );
		gridPane.add( maxEntriesPerFileField, 2, rowIndex++ );

		configureQueueSizeLabelAndField( );
		gridPane.add( queueSizeLabel, 1, rowIndex );
		gridPane.add( queueSizeField, 2, rowIndex++ );

		configureBufferLabelAndField( gridPane, rowIndex );
		gridPane.add( bufferLabel, 1, rowIndex );
		gridPane.add( bufferField, 2, rowIndex++ );
	}

	private void configureGridPane( ) {
		VBox.setMargin( gridPane, new Insets( 10, 10, 0, 10 ) );
		gridPane.setVgap( 5 );
		gridPane.setHgap( 5 );
	}

	private void configureStatusLabelAndField( ) {
		statusLabel.setText( RESOURCE_BUNDLE.getString( "status" ) );

		statusField.setId( "monitoringDialogStatus" );
		statusField.setMaxWidth( Double.POSITIVE_INFINITY );
		statusField.textProperty( ).bindBidirectional( status, new EnumStringConverter<>( Status.class ) );
		status.addListener( (ChangeListener<Status>) ( observable, oldStatus, newStatus ) -> {
			final List<String> style = getStatusStyle( newStatus );
			statusField.getStyleClass( ).setAll( style );
		} );

		GridPane.setHgrow( statusField, Priority.ALWAYS );
	}

	private void configureActiveLabelAndField( ) {
		activeLabel.setText( RESOURCE_BUNDLE.getString( "active" ) );

		activeField.setId( "monitoringDialogActive" );
		activeField.setMaxWidth( Double.POSITIVE_INFINITY );
		activeField.selectedProperty( ).bindBidirectional( active );
	}

	private void configureOutputDirectoryLabelAndField( ) {
		outputDirectoryLabel.setText( RESOURCE_BUNDLE.getString( "outputDirectory" ) );

		outputDirectoryField.setMaxWidth( Double.POSITIVE_INFINITY );
		outputDirectoryField.textProperty( ).bindBidirectional( outputDirectory );
	}

	private void configureTimerLabelAndField( ) {
		timerLabel.setText( RESOURCE_BUNDLE.getString( "timer" ) );

		timerField.setMaxWidth( Double.POSITIVE_INFINITY );
		timerField.setConverter( new EnumStringConverter<>( Timer.class ) );
		timerField.valueProperty( ).bindBidirectional( timer );
		timerField.setItems( FXCollections.observableArrayList( Timer.values( ) ) );
	}

	private void configureWriterLabelAndField( ) {
		writerLabel.setText( RESOURCE_BUNDLE.getString( "writer" ) );

		writerField.setMaxWidth( Double.POSITIVE_INFINITY );
		writerField.setConverter( new EnumStringConverter<>( Writer.class ) );
		writerField.valueProperty( ).bindBidirectional( writer );
		writerField.setItems( FXCollections.observableArrayList( Writer.values( ) ) );
	}

	private void configureMaxEntriesPerFileLabelAndField( ) {
		maxEntriesPerFileLabel.setText( RESOURCE_BUNDLE.getString( "maxEntriesPerFile" ) );

		maxEntriesPerFileField.setId( "monitoringDialogMaxEntriesPerFile" );
		maxEntriesPerFileField.setMaxWidth( Double.POSITIVE_INFINITY );
		maxEntriesPerFileField.valueProperty( ).bindBidirectional( maxEntriesPerFile );
	}

	private void configureQueueSizeLabelAndField( ) {
		queueSizeLabel.setText( RESOURCE_BUNDLE.getString( "queueSize" ) );

		queueSizeField.setId( "monitoringDialogQueueSize" );
		queueSizeField.setMaxWidth( Double.POSITIVE_INFINITY );
		queueSizeField.valueProperty( ).bindBidirectional( queueSize );
	}

	private void configureBufferLabelAndField( final GridPane gridPane, final int rowIndex ) {
		bufferLabel.setText( RESOURCE_BUNDLE.getString( "buffer" ) );

		bufferField.setId( "monitoringDialogBufferSize" );
		bufferField.setMaxWidth( Double.POSITIVE_INFINITY );
		bufferField.valueProperty( ).bindBidirectional( buffer );
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

		final Integer maxEntriesPerFile = this.maxEntriesPerFile.get( );
		if ( maxEntriesPerFile == null || this.maxEntriesPerFile.get( ) <= 0 ) {
			inputValid = false;
		}

		final Integer queueSize = this.queueSize.get( );
		if ( queueSize == null || queueSize <= 0 ) {
			inputValid = false;
		}

		final Integer buffer = this.buffer.get( );
		if ( buffer == null || buffer <= 0 ) {
			inputValid = false;
		}

		if ( !inputValid ) {
			final Alert alert = new Alert( AlertType.WARNING );
			alert.getDialogPane( ).lookupButton( ButtonType.OK ).setId( "monitoringDialogValidationOk" );
			alert.setContentText( RESOURCE_BUNDLE.getString( "errorRange" ) );
			alert.show( );
		}

		return inputValid;
	}

	private List<String> getStatusStyle( final Status aStatus ) {
		final String style;

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
		active.setValue( monitoringConfiguration.isActive( ) );
		outputDirectory.setValue( monitoringConfiguration.getOutputDirectory( ) );
		timer.setValue( monitoringConfiguration.getTimer( ) );
		writer.setValue( monitoringConfiguration.getWriter( ) );
		maxEntriesPerFile.setValue( monitoringConfiguration.getMaxEntriesPerFile( ) );
		queueSize.setValue( monitoringConfiguration.getQueueSize( ) );
		buffer.setValue( monitoringConfiguration.getBuffer( ) );
	}

	public void setStatus( final Status status ) {
		this.status.setValue( status );
	}

	public MonitoringConfiguration getValue( ) {
		final MonitoringConfiguration monitoringConfiguration = new MonitoringConfiguration( );

		monitoringConfiguration.setActive( active.getValue( ) );
		monitoringConfiguration.setOutputDirectory( outputDirectory.getValue( ) );
		monitoringConfiguration.setTimer( timer.getValue( ) );
		monitoringConfiguration.setWriter( writer.getValue( ) );
		monitoringConfiguration.setMaxEntriesPerFile( maxEntriesPerFile.getValue( ) );
		monitoringConfiguration.setQueueSize( queueSize.getValue( ) );
		monitoringConfiguration.setBuffer( buffer.getValue( ) );

		return monitoringConfiguration;
	}

}
