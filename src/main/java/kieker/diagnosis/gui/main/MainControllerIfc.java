package kieker.diagnosis.gui.main;

public interface MainControllerIfc {

	public void doInitialize( );

	public void showTraces( );

	public void showAggregatedTraces( );

	public void showCalls( );

	public void showAggregatedCalls( );

	public void showStatistics( );

	public void showImportDialog( );

	public void showSettings( );

	public void showAbout( );

	public void showBugReporting( );

	public void close( );

}
