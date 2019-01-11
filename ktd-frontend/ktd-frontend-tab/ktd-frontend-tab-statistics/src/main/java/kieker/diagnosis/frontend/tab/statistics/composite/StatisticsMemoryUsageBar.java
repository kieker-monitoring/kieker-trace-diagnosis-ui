package kieker.diagnosis.frontend.tab.statistics.composite;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public final class StatisticsMemoryUsageBar extends TitledPane {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( StatisticsMemoryUsageBar.class.getName( ) );
	private final ProgressBar ivProgressBar;
	private final Text ivProgressText;

	public StatisticsMemoryUsageBar( ) {
		setText( RESOURCE_BUNDLE.getString( "memoryUsage" ) );
		setCollapsible( false );

		final StackPane stackPane = new StackPane( );
		VBox.setMargin( stackPane, new Insets( 2 ) );

		{
			ivProgressBar = new ProgressBar( );
			ivProgressBar.setMaxWidth( Double.POSITIVE_INFINITY );
			ivProgressBar.setPrefHeight( 30 );

			stackPane.getChildren( ).add( ivProgressBar );
		}

		{
			ivProgressText = new Text( );

			stackPane.getChildren( ).add( ivProgressText );
		}

		setContent( stackPane );
		startUpdateThread( );
	}

	private void startUpdateThread( ) {
		final Thread thread = new Thread( ( ) -> {
			while ( true ) {
				final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean( );
				final long usedHeap = memoryMXBean.getHeapMemoryUsage( ).getUsed( ) / 1024 / 1024;
				final long committedHeap = memoryMXBean.getHeapMemoryUsage( ).getCommitted( ) / 1024 / 1024;

				Platform.runLater( ( ) -> {
					setValue( usedHeap, committedHeap );
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

	public void setValue( final long aCurrentMegaByte, final long aTotalMegaByte ) {
		ivProgressBar.setProgress( 1.0 * aCurrentMegaByte / aTotalMegaByte );
		ivProgressText.setText( String.format( "%d / %d [MB]", aCurrentMegaByte, aTotalMegaByte ) );
	}

}
