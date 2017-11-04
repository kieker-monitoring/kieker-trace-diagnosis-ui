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

package kieker.diagnosis.ui.dialogs.monitoring;

import com.google.inject.Singleton;

import kieker.diagnosis.architecture.exception.BusinessException;
import kieker.diagnosis.architecture.monitoring.MonitoringConfiguration;
import kieker.diagnosis.architecture.monitoring.Status;
import kieker.diagnosis.architecture.ui.ViewModelBase;

/**
 * The view model of the monitoring dialog.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
class MonitoringDialogViewModel extends ViewModelBase<MonitoringDialogView> {

	public void updatePresentationStatus( final Status aStatus ) {
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

		getView( ).getStatus( ).setText( status );
		getView( ).getStatus( ).getStyleClass( ).remove( "monitoringRunning" );
		getView( ).getStatus( ).getStyleClass( ).remove( "monitoringTerminated" );
		getView( ).getStatus( ).getStyleClass( ).remove( "noMonitoringStarted" );
		getView( ).getStatus( ).getStyleClass( ).add( style );
	}

	public void updatePresentationConfiguration( final MonitoringConfiguration aConfiguration ) {
		getView( ).getActive( ).setSelected( aConfiguration.isActive( ) );
		getView( ).getOutputDirectory( ).setText( aConfiguration.getOutputDirectory( ) );
		getView( ).getTimer( ).setValue( aConfiguration.getTimer( ) );
		getView( ).getWriter( ).setValue( aConfiguration.getWriter( ) );
		getView( ).getMaxEntriesPerFile( ).setText( Integer.toString( aConfiguration.getMaxEntriesPerFile( ) ) );
		getView( ).getQueueSize( ).setText( Integer.toString( aConfiguration.getQueueSize( ) ) );
		getView( ).getBuffer( ).setText( Integer.toString( aConfiguration.getBuffer( ) ) );
	}

	public MonitoringConfiguration savePresentationConfiguration( ) throws BusinessException {
		final MonitoringConfiguration configuration = new MonitoringConfiguration( );

		configuration.setActive( getView( ).getActive( ).isSelected( ) );
		configuration.setOutputDirectory( getView( ).getOutputDirectory( ).getText( ) );
		configuration.setTimer( getView( ).getTimer( ).getValue( ) );
		configuration.setWriter( getView( ).getWriter( ).getValue( ) );

		final Integer maxEntriesPerFile = getView( ).getMaxEntriesPerFile( ).getValue( );
		if ( maxEntriesPerFile == null || maxEntriesPerFile <= 0 ) {
			throw new BusinessException( getLocalizedString( "errorRange" ) );
		}
		configuration.setMaxEntriesPerFile( maxEntriesPerFile );

		final Integer queueSize = getView( ).getQueueSize( ).getValue( );
		if ( queueSize == null || queueSize <= 0 ) {
			throw new BusinessException( getLocalizedString( "errorRange" ) );
		}
		configuration.setQueueSize( queueSize );

		final Integer buffer = getView( ).getBuffer( ).getValue( );
		if ( buffer == null || buffer <= 0 ) {
			throw new BusinessException( getLocalizedString( "errorRange" ) );
		}
		configuration.setBuffer( buffer );

		return configuration;
	}

}
