package kieker.diagnosis.service.statistics;

import com.google.inject.Singleton;

import kieker.diagnosis.architecture.service.ServiceBase;
import kieker.diagnosis.service.data.MethodCall;
import kieker.diagnosis.service.data.MonitoringLogService;
import kieker.diagnosis.service.settings.TimestampAppearance;

@Singleton
public class StatisticsService extends ServiceBase {

	public Statistics getStatistics( ) {
		final MonitoringLogService monitoringLogService = getService( MonitoringLogService.class );

		Statistics statistics = null;

		// We create the DTO only, if we have any data available
		if ( monitoringLogService.isDataAvailable( ) ) {
			statistics = new Statistics( );

			statistics.setProcessDuration( monitoringLogService.getProcessDuration( ) );
			statistics.setProcessedBytes( monitoringLogService.getProcessedBytes( ) );
			statistics.setProcessSpeed( statistics.getProcessedBytes( ) / statistics.getProcessDuration( ) );
			statistics.setIgnoredRecords( monitoringLogService.getIgnoredRecords( ) );
			statistics.setDanglingRecords( monitoringLogService.getDanglingRecords( ) );
			statistics.setIncompleteTraces( monitoringLogService.getIncompleteTraces( ) );
			statistics.setMethods( monitoringLogService.getMethods( ).size( ) );
			statistics.setAggregatedMethods( monitoringLogService.getAggreatedMethods( ).size( ) );
			statistics.setTraces( monitoringLogService.getTraceRoots( ).size( ) );
			statistics.setDirectory( monitoringLogService.getDirectory( ) );

			final long minTimestamp = monitoringLogService.getMethods( ).parallelStream( ).map( MethodCall::getTimestamp ).min( Long::compareTo ).orElse( 0L );
			final long maxTimestamp = monitoringLogService.getMethods( ).parallelStream( ).map( MethodCall::getTimestamp ).max( Long::compareTo ).orElse( 0L );
			statistics.setBeginnOfMonitoring( TimestampAppearance.DATE_AND_TIME.convert( minTimestamp ) );
			statistics.setEndOfMonitoring( TimestampAppearance.DATE_AND_TIME.convert( maxTimestamp ) );
		}

		return statistics;
	}

}
