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

package kieker.diagnosis.backend.search.statistics;

import java.util.Optional;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import kieker.diagnosis.backend.base.service.Service;
import kieker.diagnosis.backend.data.MethodCall;
import kieker.diagnosis.backend.data.reader.Repository;
import kieker.diagnosis.backend.search.statistics.Statistics.StatisticsBuilder;
import kieker.diagnosis.backend.settings.TimestampAppearance;
import lombok.RequiredArgsConstructor;

/**
 * This service is responsible for loading statistics for the imported log.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
@RequiredArgsConstructor ( onConstructor = @__ ( @Inject ) )
public class StatisticsService implements Service {

	private final Repository repository;

	public Optional<Statistics> getStatistics( ) {
		// We create the DTO only, if we have any data available
		if ( repository.isDataAvailable( ) ) {
			final StatisticsBuilder statisticsBuilder = Statistics.builder( );

			// TRACEUI-10 [Occasional division by zero]
			final long processDuration = repository.getProcessDuration( );
			statisticsBuilder.processDuration( processDuration );
			final long processedBytes = repository.getProcessedBytes( );
			statisticsBuilder.processedBytes( processedBytes );

			if ( processDuration != 0L ) {
				statisticsBuilder.processSpeed( processedBytes / processDuration );
			}

			statisticsBuilder.ignoredRecords( repository.getIgnoredRecords( ) );
			statisticsBuilder.danglingRecords( repository.getDanglingRecords( ) );
			statisticsBuilder.incompleteTraces( repository.getIncompleteTraces( ) );
			statisticsBuilder.methods( repository.getMethods( ).size( ) );
			statisticsBuilder.aggregatedMethods( repository.getAggreatedMethods( ).size( ) );
			statisticsBuilder.traces( repository.getTraceRoots( ).size( ) );
			statisticsBuilder.directory( repository.getDirectory( ) );

			final long minTimestamp = repository.getMethods( )
					.parallelStream( )
					.map( MethodCall::getTimestamp )
					.min( Long::compareTo )
					.orElse( 0L );
			final long maxTimestamp = repository.getMethods( )
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
