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

package kieker.diagnosis.ui.dialogs.monitoring;

import com.google.inject.Singleton;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Command;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import kieker.diagnosis.architecture.exception.BusinessException;
import kieker.diagnosis.architecture.exception.BusinessRuntimeException;
import kieker.diagnosis.architecture.monitoring.MonitoringConfiguration;
import kieker.diagnosis.architecture.monitoring.Status;
import kieker.diagnosis.architecture.monitoring.Timer;
import kieker.diagnosis.architecture.monitoring.Writer;
import kieker.diagnosis.architecture.service.monitoring.MonitoringService;
import kieker.diagnosis.architecture.ui.ViewModelBase;

/**
 * The view model of the monitoring dialog.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class MonitoringDialogViewModel extends ViewModelBase<MonitoringDialogView> implements ViewModel {

	public static final String EVENT_CLOSE_DIALOG = "EVENT_CLOSE_DIALOG";

	private final Command ivSaveAndCloseCommand = createCommand( this::performSaveAndClose );
	private final Command ivCloseCommand = createCommand( this::performClose );

	private final StringProperty ivStatusProperty = new SimpleStringProperty( );
	private final StringProperty ivStatusStyleClassProperty = new SimpleStringProperty( );
	private final BooleanProperty ivActiveProperty = new SimpleBooleanProperty( );
	private final StringProperty ivOutputDirectoryProperty = new SimpleStringProperty( );
	private final ObjectProperty<Timer> ivTimerProperty = new SimpleObjectProperty<>( );
	private final ObjectProperty<Writer> ivWriterProperty = new SimpleObjectProperty<>( );
	private final ObjectProperty<Integer> ivMaxEntriesPerFileProperty = new SimpleObjectProperty<>( );
	private final ObjectProperty<Integer> ivQueueSizeProperty = new SimpleObjectProperty<>( );
	private final ObjectProperty<Integer> ivBufferProperty = new SimpleObjectProperty<>( );

	public void initialize( ) {
		// Get the current configuration and status...
		final MonitoringService monitoringService = getService( MonitoringService.class );
		final MonitoringConfiguration configuration = monitoringService.getCurrentConfiguration( );
		final Status status = monitoringService.getCurrentStatus( );

		// ...and display them.
		updatePresentationConfiguration( configuration );
		updatePresentationStatus( status );
	}

	Command getSaveAndCloseCommand( ) {
		return ivSaveAndCloseCommand;
	}

	/**
	 * This action is performed, when the user wants to save and close the dialog.
	 *
	 * @return
	 */
	private void performSaveAndClose( ) {
		try {
			// Get the configuration...
			final MonitoringConfiguration configuration = savePresentationConfiguration( );

			// ...and apply it
			final MonitoringService monitoringService = getService( MonitoringService.class );
			monitoringService.configureMonitoring( configuration );

			performClose( );
		} catch ( final BusinessException ex ) {
			throw new BusinessRuntimeException( ex );
		}
	}

	Command getCloseCommand( ) {
		return ivCloseCommand;
	}

	private void performClose( ) {
		publish( EVENT_CLOSE_DIALOG );
	}

	StringProperty getStatusProperty( ) {
		return ivStatusProperty;
	}

	StringProperty getStatusStyleClassProperty( ) {
		return ivStatusStyleClassProperty;
	}

	BooleanProperty getActiveProperty( ) {
		return ivActiveProperty;
	}

	StringProperty getOutputDirectoryProperty( ) {
		return ivOutputDirectoryProperty;
	}

	ObjectProperty<Timer> getTimerProperty( ) {
		return ivTimerProperty;
	}

	ObjectProperty<Writer> getWriterProperty( ) {
		return ivWriterProperty;
	}

	ObjectProperty<Integer> getMaxEntriesPerFileProperty( ) {
		return ivMaxEntriesPerFileProperty;
	}

	ObjectProperty<Integer> getQueueSizeProperty( ) {
		return ivQueueSizeProperty;
	}

	ObjectProperty<Integer> getBufferProperty( ) {
		return ivBufferProperty;
	}

	private void updatePresentationStatus( final Status aStatus ) {
		String status;
		String style;

		switch ( aStatus ) {
			case RUNNING:
				status = getLocalizedString( "monitoringRunning" );
				style = "monitoringRunning";
			break;
			case TERMINATED:
				status = getLocalizedString( "monitoringTerminated" );
				style = "monitoringTerminated";
			break;
			case NO_MONITORING:
			default:
				status = getLocalizedString( "noMonitoringStarted" );
				style = "noMonitoringStarted";
			break;

		}

		ivStatusProperty.set( status );
		ivStatusStyleClassProperty.set( style );
	}

	private void updatePresentationConfiguration( final MonitoringConfiguration aConfiguration ) {
		ivActiveProperty.set( aConfiguration.isActive( ) );
		ivOutputDirectoryProperty.set( aConfiguration.getOutputDirectory( ) );
		ivTimerProperty.setValue( aConfiguration.getTimer( ) );
		ivWriterProperty.setValue( aConfiguration.getWriter( ) );
		ivMaxEntriesPerFileProperty.set( Integer.valueOf( aConfiguration.getMaxEntriesPerFile( ) ) );
		ivQueueSizeProperty.set( Integer.valueOf( aConfiguration.getQueueSize( ) ) );
		ivBufferProperty.set( Integer.valueOf( aConfiguration.getBuffer( ) ) );
	}

	private MonitoringConfiguration savePresentationConfiguration( ) throws BusinessException {
		final MonitoringConfiguration configuration = new MonitoringConfiguration( );

		configuration.setActive( ivActiveProperty.get( ) );
		configuration.setOutputDirectory( ivOutputDirectoryProperty.get( ) );
		configuration.setTimer( ivTimerProperty.getValue( ) );
		configuration.setWriter( ivWriterProperty.getValue( ) );

		final Integer maxEntriesPerFile = ivMaxEntriesPerFileProperty.getValue( );
		if ( maxEntriesPerFile == null || maxEntriesPerFile <= 0 ) {
			throw new BusinessException( getLocalizedString( "errorRange" ) );
		}
		configuration.setMaxEntriesPerFile( maxEntriesPerFile );

		final Integer queueSize = ivQueueSizeProperty.getValue( );
		if ( queueSize == null || queueSize <= 0 ) {
			throw new BusinessException( getLocalizedString( "errorRange" ) );
		}
		configuration.setQueueSize( queueSize );

		final Integer buffer = ivBufferProperty.getValue( );
		if ( buffer == null || buffer <= 0 ) {
			throw new BusinessException( getLocalizedString( "errorRange" ) );
		}
		configuration.setBuffer( buffer );

		return configuration;
	}

}
