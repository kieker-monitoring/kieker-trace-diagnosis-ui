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
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Pair;

public final class StatisticsMemoryUsageBar extends TitledPane {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( StatisticsMemoryUsageBar.class.getName( ) );

	private final ObjectProperty<Pair<Long, Long>> memoryUsage = new SimpleObjectProperty<>( new Pair<>( 0L, 0L ) );

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
		final ProgressBar progressBar = new ProgressBar( );

		progressBar.setMaxWidth( Double.POSITIVE_INFINITY );
		progressBar.setPrefHeight( 30 );

		final ObservableDoubleValue observable = Bindings.createDoubleBinding( ( ) -> 1.0 * memoryUsage.get( ).getKey( ) / memoryUsage.get( ).getValue( ), memoryUsage );
		progressBar.progressProperty( ).bind( observable );

		return progressBar;
	}

	private Node createProgressText( ) {
		final Text progressText = new Text( );

		final ObservableValue<String> observable = Bindings.createStringBinding( ( ) -> String.format( "%d / %d [MB]", memoryUsage.get( ).getKey( ), memoryUsage.get( ).getValue( ) ), memoryUsage );
		progressText.textProperty( ).bind( observable );

		return progressText;
	}

	private void startUpdateThread( ) {
		final Thread thread = new Thread( ( ) -> {
			while ( !Thread.interrupted( ) ) {
				final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean( );
				final long usedHeap = memoryMXBean.getHeapMemoryUsage( ).getUsed( ) / 1024 / 1024;
				final long committedHeap = memoryMXBean.getHeapMemoryUsage( ).getCommitted( ) / 1024 / 1024;

				Platform.runLater( ( ) -> memoryUsage.set( new Pair<>( usedHeap, committedHeap ) ) );

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

}
