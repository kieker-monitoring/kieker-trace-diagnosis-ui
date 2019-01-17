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
import java.util.ResourceBundle;

import com.sun.management.OperatingSystemMXBean;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public final class StatisticsCPUUsageBar extends TitledPane {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( StatisticsCPUUsageBar.class.getName( ) );

	private ProgressBar progressBar;
	private Text progressText;

	public StatisticsCPUUsageBar( ) {
		createControl( );
		startUpdateThread( );
	}

	private void createControl( ) {
		setText( RESOURCE_BUNDLE.getString( "cpuUsage" ) );
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
				final OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getPlatformMXBean( OperatingSystemMXBean.class );
				final double cpuLoad = operatingSystemMXBean.getProcessCpuLoad( );

				Platform.runLater( ( ) -> {
					setValue( cpuLoad );
				} );

				try {
					Thread.sleep( 2500 );
				} catch ( final InterruptedException ex ) {
					Thread.currentThread( ).interrupt( );
				}
			}
		} );
		thread.setDaemon( true );
		thread.setName( "Statistics CPU Refresh Thread" );
		thread.start( );
	}

	private void setValue( final double cpuLoad ) {
		progressBar.setProgress( cpuLoad );
		progressText.setText( String.format( "%d %%", (int) ( cpuLoad * 100 ) ) );
	}

}
