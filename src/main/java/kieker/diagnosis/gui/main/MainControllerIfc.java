package kieker.diagnosis.gui.main;

public interface MainControllerIfc {

	public void doInitialize( );

	public void showTraces( ) throws Exception;

	public void showAggregatedTraces( ) throws Exception;

	public void showCalls( ) throws Exception;

	public void showAggregatedCalls( ) throws Exception;

	public void showStatistics( ) throws Exception;

	public void showImportDialog( );

	public void showSettings( ) throws Exception;

	public void showAbout( ) throws Exception;

	public void showBugReporting( ) throws Exception;

	public void close( );

}
