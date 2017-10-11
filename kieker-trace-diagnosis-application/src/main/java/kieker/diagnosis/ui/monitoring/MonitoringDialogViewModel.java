package kieker.diagnosis.ui.monitoring;

import com.google.inject.Singleton;

import kieker.diagnosis.architecture.monitoring.MonitoringConfiguration;
import kieker.diagnosis.architecture.monitoring.Status;
import kieker.diagnosis.architecture.ui.ViewModelBase;

@Singleton
public class MonitoringDialogViewModel extends ViewModelBase<MonitoringDialogView> {

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

	public MonitoringConfiguration savePresentationConfiguration( ) {
		final MonitoringConfiguration configuration = new MonitoringConfiguration( );

		configuration.setActive( getView( ).getActive( ).isSelected( ) );
		configuration.setOutputDirectory( getView( ).getOutputDirectory( ).getText( ) );
		configuration.setTimer( getView( ).getTimer( ).getValue( ) );
		configuration.setWriter( getView( ).getWriter( ).getValue( ) );
		configuration.setMaxEntriesPerFile( Integer.parseInt( getView( ).getMaxEntriesPerFile( ).getText( ) ) );
		configuration.setQueueSize( Integer.parseInt( getView( ).getQueueSize( ).getText( ) ) );
		configuration.setBuffer( Integer.parseInt( getView( ).getBuffer( ).getText( ) ) );

		return configuration;
	}

}