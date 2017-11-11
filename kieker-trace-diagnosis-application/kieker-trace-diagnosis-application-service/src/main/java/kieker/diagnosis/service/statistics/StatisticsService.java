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

package kieker.diagnosis.service.statistics;

import com.google.inject.Singleton;

import kieker.diagnosis.architecture.service.ServiceBase;
import kieker.diagnosis.service.data.MethodCall;
import kieker.diagnosis.service.data.MonitoringLogService;
import kieker.diagnosis.service.settings.TimestampAppearance;

/**
 * This service is responsible for loading statistics for the imported log.
 *
 * @author Nils Christian Ehmke
 */
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

			// TRACEUI-10 [Occasional division by zero]
			final long processDuration = statistics.getProcessDuration( );
			if ( processDuration != 0L ) {
				statistics.setProcessSpeed( statistics.getProcessedBytes( ) / processDuration );
			}

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
