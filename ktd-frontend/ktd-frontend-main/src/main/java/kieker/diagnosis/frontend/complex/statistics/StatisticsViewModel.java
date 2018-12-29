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

package kieker.diagnosis.frontend.complex.statistics;

import java.text.NumberFormat;
import java.util.Optional;

import com.google.inject.Singleton;

import kieker.diagnosis.backend.search.statistics.Statistics;
import kieker.diagnosis.frontend.base.ui.ViewModelBase;

/**
 * The view model of the statistics tab.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
class StatisticsViewModel extends ViewModelBase<StatisticsView> {

	public void updatePresentation( final Optional<Statistics> aStatistics ) {
		aStatistics.ifPresentOrElse( statistics -> {
			final NumberFormat decimalFormat = NumberFormat.getInstance( );

			getView( ).getProcessedBytes( ).setText( convertToByteString( statistics.getProcessedBytes( ) ) );
			getView( ).getProcessDuration( ).setText( convertToDurationString( statistics.getProcessDuration( ) ) );
			getView( ).getProcessSpeed( ).setText( convertToSpeedString( statistics.getProcessSpeed( ) ) );
			getView( ).getMethods( ).setText( decimalFormat.format( statistics.getMethods( ) ) );
			getView( ).getAggregatedMethods( ).setText( decimalFormat.format( statistics.getAggregatedMethods( ) ) );
			getView( ).getTraces( ).setText( decimalFormat.format( statistics.getTraces( ) ) );
			getView( ).getIgnoredRecords( ).setText( decimalFormat.format( statistics.getIgnoredRecords( ) ) );
			getView( ).getDanglingRecords( ).setText( decimalFormat.format( statistics.getDanglingRecords( ) ) );
			getView( ).getIncompleteTraces( ).setText( decimalFormat.format( statistics.getIncompleteTraces( ) ) );
			getView( ).getBeginnOfMonitoring( ).setText( statistics.getBeginnOfMonitoring( ) );
			getView( ).getEndOfMonitoring( ).setText( statistics.getEndOfMonitoring( ) );
			getView( ).getDirectory( ).setText( statistics.getDirectory( ) );
		}, ( ) -> {
			getView( ).getProcessedBytes( ).setText( getLocalizedString( "noDataAvailable" ) );
			getView( ).getProcessDuration( ).setText( getLocalizedString( "noDataAvailable" ) );
			getView( ).getProcessSpeed( ).setText( getLocalizedString( "noDataAvailable" ) );
			getView( ).getMethods( ).setText( getLocalizedString( "noDataAvailable" ) );
			getView( ).getAggregatedMethods( ).setText( getLocalizedString( "noDataAvailable" ) );
			getView( ).getTraces( ).setText( getLocalizedString( "noDataAvailable" ) );
			getView( ).getIgnoredRecords( ).setText( getLocalizedString( "noDataAvailable" ) );
			getView( ).getDanglingRecords( ).setText( getLocalizedString( "noDataAvailable" ) );
			getView( ).getIncompleteTraces( ).setText( getLocalizedString( "noDataAvailable" ) );
			getView( ).getBeginnOfMonitoring( ).setText( getLocalizedString( "noDataAvailable" ) );
			getView( ).getEndOfMonitoring( ).setText( getLocalizedString( "noDataAvailable" ) );
			getView( ).getDirectory( ).setText( getLocalizedString( "noDataAvailable" ) );
		} );
	}

	private String convertToByteString( final long aBytes ) {
		long bytes = aBytes;

		if ( bytes <= 1024 ) {
			return String.format( "%d [B]", bytes );
		} else {
			bytes /= 1024;
			if ( bytes <= 1024 ) {
				return String.format( "%d [KB]", bytes );
			} else {
				bytes /= 1024;
				return String.format( "%d [MB]", bytes );
			}
		}
	}

	private String convertToDurationString( final long aDuration ) {
		long duration = aDuration;

		if ( duration <= 1000 ) {
			return String.format( "%d [ms]", duration );
		} else {
			duration /= 1000;
			if ( duration <= 60 ) {
				return String.format( "%d [s]", duration );
			} else {
				duration /= 60;
				return String.format( "%d [m]", duration );
			}
		}
	}

	private String convertToSpeedString( final long aSpeed ) {
		long speed = aSpeed * 1000;

		if ( speed <= 1024 ) {
			return String.format( "%d [B/s]", speed );
		} else {
			speed /= 1024;
			if ( speed <= 1024 ) {
				return String.format( "%d [KB/s]", speed );
			} else {
				speed /= 1024;
				return String.format( "%d [MB/s]", speed );
			}
		}
	}

	public void updatePresentationMemoryUsage( final long aCurrentMegaByte, final long aTotalMegaByte ) {
		getView( ).getProgressBar( ).setProgress( 1.0 * aCurrentMegaByte / aTotalMegaByte );
		getView( ).getProgressText( ).setText( String.format( "%d / %d [MB]", aCurrentMegaByte, aTotalMegaByte ) );
	}

}
