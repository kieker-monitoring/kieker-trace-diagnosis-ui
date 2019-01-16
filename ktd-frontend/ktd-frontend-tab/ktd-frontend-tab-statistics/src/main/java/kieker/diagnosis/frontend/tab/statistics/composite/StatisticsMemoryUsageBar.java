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

package kieker.diagnosis.frontend.tab.statistics.composite;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public final class StatisticsMemoryUsageBar extends TitledPane {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( StatisticsMemoryUsageBar.class.getName( ) );

	private ProgressBar progressBar;
	private Text progressText;

	public StatisticsMemoryUsageBar( ) {
		createControl( );
		startUpdateThread( );
	}

	private void createControl( ) {
		setText( RESOURCE_BUNDLE.getString( "memoryUsage" ) );
		setCollapsible( false );

		setContent( createStackPane( ) );
	}

	private StackPane createStackPane( ) {
		final StackPane stackPane = new StackPane( );

		stackPane.getChildren( ).add( createProgressBar( ) );
		stackPane.getChildren( ).add( createProgressText( ) );

		return stackPane;
	}

	private Node createProgressBar( ) {
		progressBar = new ProgressBar( );

		progressBar.setMaxWidth( Double.POSITIVE_INFINITY );
		progressBar.setPrefHeight( 30 );

		return progressBar;
	}

	private Node createProgressText( ) {
		progressText = new Text( );

		return progressText;
	}

	private void startUpdateThread( ) {
		final Thread thread = new Thread( ( ) -> {
			while ( !Thread.interrupted( ) ) {
				final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean( );
				final long usedHeap = memoryMXBean.getHeapMemoryUsage( ).getUsed( ) / 1024 / 1024;
				final long committedHeap = memoryMXBean.getHeapMemoryUsage( ).getCommitted( ) / 1024 / 1024;

				Platform.runLater( ( ) -> {
					setValue( usedHeap, committedHeap );
				} );

				try {
					Thread.sleep( 2500 );
				} catch ( final InterruptedException ex ) {
					Thread.currentThread( ).interrupt( );
				}
			}
		} );
		thread.setDaemon( true );
		thread.setName( "Statistics Memory Refresh Thread" );
		thread.start( );
	}

	private void setValue( final long aCurrentMegaByte, final long aTotalMegaByte ) {
		progressBar.setProgress( 1.0 * aCurrentMegaByte / aTotalMegaByte );
		progressText.setText( String.format( "%d / %d [MB]", aCurrentMegaByte, aTotalMegaByte ) );
	}

}
