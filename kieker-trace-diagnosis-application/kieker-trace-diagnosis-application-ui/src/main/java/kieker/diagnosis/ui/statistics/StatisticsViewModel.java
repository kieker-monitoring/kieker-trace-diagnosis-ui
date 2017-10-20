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

package kieker.diagnosis.ui.statistics;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.google.inject.Singleton;

import kieker.diagnosis.architecture.ui.ViewModelBase;
import kieker.diagnosis.service.statistics.Statistics;

/**
 * The view model of the statistics tab.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
class StatisticsViewModel extends ViewModelBase<StatisticsView> {

	public void updatePresentation( final Statistics aStatistics ) {
		if ( aStatistics != null ) {
			final NumberFormat decimalFormat = DecimalFormat.getInstance( );

			getView( ).getProcessedBytes( ).setText( convertToByteString( aStatistics.getProcessedBytes( ) ) );
			getView( ).getProcessDuration( ).setText( convertToDurationString( aStatistics.getProcessDuration( ) ) );
			getView( ).getProcessSpeed( ).setText( convertToSpeedString( aStatistics.getProcessSpeed( ) ) );
			getView( ).getMethods( ).setText( decimalFormat.format( aStatistics.getMethods( ) ) );
			getView( ).getAggregatedMethods( ).setText( decimalFormat.format( aStatistics.getAggregatedMethods( ) ) );
			getView( ).getTraces( ).setText( decimalFormat.format( aStatistics.getTraces( ) ) );
			getView( ).getIgnoredRecords( ).setText( decimalFormat.format( aStatistics.getIgnoredRecords( ) ) );
			getView( ).getDanglingRecords( ).setText( decimalFormat.format( aStatistics.getDanglingRecords( ) ) );
			getView( ).getIncompleteTraces( ).setText( decimalFormat.format( aStatistics.getIncompleteTraces( ) ) );
			getView( ).getBeginnOfMonitoring( ).setText( aStatistics.getBeginnOfMonitoring( ) );
			getView( ).getEndOfMonitoring( ).setText( aStatistics.getEndOfMonitoring( ) );
			getView( ).getDirectory( ).setText( aStatistics.getDirectory( ) );
		} else {
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
		}
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
