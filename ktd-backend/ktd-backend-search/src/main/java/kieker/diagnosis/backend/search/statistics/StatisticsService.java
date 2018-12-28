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

package kieker.diagnosis.backend.search.statistics;

import java.util.Optional;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import kieker.diagnosis.backend.base.service.Service;
import kieker.diagnosis.backend.data.MethodCall;
import kieker.diagnosis.backend.data.MonitoringLogService;
import kieker.diagnosis.backend.search.statistics.Statistics.StatisticsBuilder;
import kieker.diagnosis.backend.settings.TimestampAppearance;

/**
 * This service is responsible for loading statistics for the imported log.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class StatisticsService implements Service {

	@Inject
	private MonitoringLogService monitoringLogService;

	public Optional<Statistics> getStatistics( ) {
		// We create the DTO only, if we have any data available
		if ( monitoringLogService.isDataAvailable( ) ) {
			final StatisticsBuilder statisticsBuilder = Statistics.builder( );

			// TRACEUI-10 [Occasional division by zero]
			final long processDuration = monitoringLogService.getProcessDuration( );
			statisticsBuilder.processDuration( processDuration );
			final long processedBytes = monitoringLogService.getProcessedBytes( );
			statisticsBuilder.processedBytes( processedBytes );

			if ( processDuration != 0L ) {
				statisticsBuilder.processSpeed( processedBytes / processDuration );
			}

			statisticsBuilder.ignoredRecords( monitoringLogService.getIgnoredRecords( ) );
			statisticsBuilder.danglingRecords( monitoringLogService.getDanglingRecords( ) );
			statisticsBuilder.incompleteTraces( monitoringLogService.getIncompleteTraces( ) );
			statisticsBuilder.methods( monitoringLogService.getMethods( ).size( ) );
			statisticsBuilder.aggregatedMethods( monitoringLogService.getAggreatedMethods( ).size( ) );
			statisticsBuilder.traces( monitoringLogService.getTraceRoots( ).size( ) );
			statisticsBuilder.directory( monitoringLogService.getDirectory( ) );

			final long minTimestamp = monitoringLogService.getMethods( )
					.parallelStream( )
					.map( MethodCall::getTimestamp )
					.min( Long::compareTo )
					.orElse( 0L );
			final long maxTimestamp = monitoringLogService.getMethods( )
					.parallelStream( )
					.map( MethodCall::getTimestamp )
					.max( Long::compareTo )
					.orElse( 0L );
			statisticsBuilder.beginnOfMonitoring( TimestampAppearance.DATE_AND_TIME.convert( minTimestamp ) );
			statisticsBuilder.endOfMonitoring( TimestampAppearance.DATE_AND_TIME.convert( maxTimestamp ) );

			final Statistics statistics = statisticsBuilder.build( );
			return Optional.of( statistics );
		} else {
			return Optional.empty( );
		}
	}

}
