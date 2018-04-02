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

package kieker.diagnosis.ui.tabs.statistics;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.google.inject.Singleton;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Command;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import kieker.diagnosis.architecture.ui.ViewModelBase;
import kieker.diagnosis.service.statistics.Statistics;
import kieker.diagnosis.service.statistics.StatisticsService;
import kieker.diagnosis.ui.scopes.MainScope;

/**
 * The view model of the statistics tab.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class StatisticsViewModel extends ViewModelBase<StatisticsView> implements ViewModel {

	@InjectScope
	private MainScope ivMainScope;

	private final Command ivRefreshCommand = createCommand( this::performRefresh );
	private final Command ivPrepareRefreshCommand = createCommand( this::performPrepareRefresh );

	private final StringProperty ivProcessedBytesProperty = new SimpleStringProperty( );
	private final StringProperty ivProcessDurationProperty = new SimpleStringProperty( );
	private final StringProperty ivProcessSpeedProperty = new SimpleStringProperty( );
	private final StringProperty ivMethodsProperty = new SimpleStringProperty( );
	private final StringProperty ivAggregatedMethodsProperty = new SimpleStringProperty( );
	private final StringProperty ivTracesProperty = new SimpleStringProperty( );
	private final StringProperty ivIgnoredRecordsProperty = new SimpleStringProperty( );
	private final StringProperty ivDanglingRecordsProperty = new SimpleStringProperty( );
	private final StringProperty ivIncompleteTracesProperty = new SimpleStringProperty( );
	private final StringProperty ivBeginnOfMonitoringProperty = new SimpleStringProperty( );
	private final StringProperty ivEndOfMonitoringProperty = new SimpleStringProperty( );
	private final StringProperty ivDirectoryProperty = new SimpleStringProperty( );

	private final DoubleProperty ivProgressProperty = new SimpleDoubleProperty( );
	private final StringProperty ivProgressTextProperty = new SimpleStringProperty( );

	private Statistics ivStatistics;

	public StatisticsViewModel( ) {
		// Start a thread to update the memory usage regularly
		final Thread thread = new Thread( ( ) -> {
			while ( true ) {
				final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean( );
				final long usedHeap = memoryMXBean.getHeapMemoryUsage( ).getUsed( ) / 1024 / 1024;
				final long committedHeap = memoryMXBean.getHeapMemoryUsage( ).getCommitted( ) / 1024 / 1024;

				Platform.runLater( ( ) -> {
					updatePresentationMemoryUsage( usedHeap, committedHeap );
				} );

				try {
					Thread.sleep( 2500 );
				} catch ( final InterruptedException ex ) {
					// Can be ignored
				}
			}
		} );
		thread.setDaemon( true );
		thread.setName( "Statistics Memory Refresh Thread" );
		thread.start( );
	}

	/**
	 * This action is performed once during the application's start.
	 */
	public void initialize( ) {
		updatePresentation( null );

		ivMainScope.subscribe( MainScope.EVENT_REFRESH, ( aKey, aPayload ) -> ivRefreshCommand.execute( ) );
		ivMainScope.subscribe( MainScope.EVENT_PREPARE_REFRESH, ( aKey, aPayload ) -> ivPrepareRefreshCommand.execute( ) );
	}

	/**
	 * This action is performed, when a refresh of the view is required
	 */
	public void performRefresh( ) {
		// Show the data on the view
		updatePresentation( ivStatistics );
	}

	public void performPrepareRefresh( ) {
		// Get the current data
		final StatisticsService statisticsService = getService( StatisticsService.class );
		ivStatistics = statisticsService.getStatistics( );
	}

	private void updatePresentation( final Statistics aStatistics ) {
		if ( aStatistics != null ) {
			final NumberFormat decimalFormat = DecimalFormat.getInstance( );

			ivProcessedBytesProperty.set( convertToByteString( aStatistics.getProcessedBytes( ) ) );
			ivProcessDurationProperty.set( convertToDurationString( aStatistics.getProcessDuration( ) ) );
			ivProcessSpeedProperty.set( convertToSpeedString( aStatistics.getProcessSpeed( ) ) );
			ivMethodsProperty.set( decimalFormat.format( aStatistics.getMethods( ) ) );
			ivAggregatedMethodsProperty.set( decimalFormat.format( aStatistics.getAggregatedMethods( ) ) );
			ivTracesProperty.set( decimalFormat.format( aStatistics.getTraces( ) ) );
			ivIgnoredRecordsProperty.set( decimalFormat.format( aStatistics.getIgnoredRecords( ) ) );
			ivDanglingRecordsProperty.set( decimalFormat.format( aStatistics.getDanglingRecords( ) ) );
			ivIncompleteTracesProperty.set( decimalFormat.format( aStatistics.getIncompleteTraces( ) ) );
			ivBeginnOfMonitoringProperty.set( aStatistics.getBeginnOfMonitoring( ) );
			ivEndOfMonitoringProperty.set( aStatistics.getEndOfMonitoring( ) );
			ivDirectoryProperty.set( aStatistics.getDirectory( ) );
		} else {
			ivProcessedBytesProperty.set( getLocalizedString( "noDataAvailable" ) );
			ivProcessDurationProperty.set( getLocalizedString( "noDataAvailable" ) );
			ivProcessSpeedProperty.set( getLocalizedString( "noDataAvailable" ) );
			ivMethodsProperty.set( getLocalizedString( "noDataAvailable" ) );
			ivAggregatedMethodsProperty.set( getLocalizedString( "noDataAvailable" ) );
			ivTracesProperty.set( getLocalizedString( "noDataAvailable" ) );
			ivIgnoredRecordsProperty.set( getLocalizedString( "noDataAvailable" ) );
			ivDanglingRecordsProperty.set( getLocalizedString( "noDataAvailable" ) );
			ivIncompleteTracesProperty.set( getLocalizedString( "noDataAvailable" ) );
			ivBeginnOfMonitoringProperty.set( getLocalizedString( "noDataAvailable" ) );
			ivEndOfMonitoringProperty.set( getLocalizedString( "noDataAvailable" ) );
			ivDirectoryProperty.set( getLocalizedString( "noDataAvailable" ) );
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

	private void updatePresentationMemoryUsage( final long aCurrentMegaByte, final long aTotalMegaByte ) {
		ivProgressProperty.set( 1.0 * aCurrentMegaByte / aTotalMegaByte );
		ivProgressTextProperty.set( String.format( "%d / %d [MB]", aCurrentMegaByte, aTotalMegaByte ) );
	}

	StringProperty getProcessedBytesProperty( ) {
		return ivProcessedBytesProperty;
	}

	StringProperty getProcessDurationProperty( ) {
		return ivProcessDurationProperty;
	}

	StringProperty getProcessSpeedProperty( ) {
		return ivProcessSpeedProperty;
	}

	StringProperty getMethodsProperty( ) {
		return ivMethodsProperty;
	}

	StringProperty getAggregatedMethodsProperty( ) {
		return ivAggregatedMethodsProperty;
	}

	StringProperty getTracesProperty( ) {
		return ivTracesProperty;
	}

	StringProperty getIgnoredRecordsProperty( ) {
		return ivIgnoredRecordsProperty;
	}

	StringProperty getDanglingRecordsProperty( ) {
		return ivDanglingRecordsProperty;
	}

	StringProperty getIncompleteTracesProperty( ) {
		return ivIncompleteTracesProperty;
	}

	StringProperty getBeginnOfMonitoringProperty( ) {
		return ivBeginnOfMonitoringProperty;
	}

	StringProperty getEndOfMonitoringProperty( ) {
		return ivEndOfMonitoringProperty;
	}

	StringProperty getDirectoryProperty( ) {
		return ivDirectoryProperty;
	}

	DoubleProperty getProgressProperty( ) {
		return ivProgressProperty;
	}

	StringProperty getProgressTextProperty( ) {
		return ivProgressTextProperty;
	}

}
