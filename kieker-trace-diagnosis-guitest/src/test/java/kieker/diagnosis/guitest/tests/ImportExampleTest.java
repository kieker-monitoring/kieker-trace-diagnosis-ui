package kieker.diagnosis.guitest.tests;

import static org.junit.Assert.assertTrue;

import kieker.diagnosis.application.Main;
import kieker.diagnosis.guitest.GuiTestConfiguration;
import kieker.diagnosis.guitest.views.CallsView;
import kieker.diagnosis.guitest.views.MainView;
import kieker.diagnosis.guitest.views.TracesView;
import kieker.diagnosis.guitestarchitecture.JavaFXThreadPerformer;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith ( SpringRunner.class )
@ContextConfiguration ( classes = GuiTestConfiguration.class )
public class ImportExampleTest {

	@Autowired
	private JavaFXThreadPerformer performer;

	@Autowired
	private MainView mainView;

	@Autowired
	private CallsView callsView;

	@Autowired
	private TracesView tracesView;

	@Autowired
	private Main ivMain;;

	@Test
	public void importExecutionMonitoringLog( ) {
		// Unfortunately TestFX cannot handle the native file dialog of JavaFX. Therefore we have to use direct access as a workaround.
		performer.perform( ( ) -> {
			ivMain.getDataService( ).loadMonitoringLogFromFS( new File( "../kieker-trace-diagnosis-release-engineering/example/execution monitoring log" ) );
		} );

		mainView.getCallsButton( ).click( );

		callsView.getFilterContainerTextField( ).setText( "" );
		callsView.getFilterContainerTextField( ).pushEnter( );
		callsView.getFilterComponentTextField( ).setText( "" );
		callsView.getFilterComponentTextField( ).pushEnter( );
		callsView.getFilterOperationTextField( ).setText( "" );
		callsView.getFilterOperationTextField( ).pushEnter( );
		callsView.getFilterTraceIDTextField( ).setText( "" );
		callsView.getFilterTraceIDTextField( ).pushEnter( );

		assertTrue( callsView.getCounterTextField( ).getText( ).startsWith( "6540 " ) );

		mainView.getTracesButton( ).click( );

		tracesView.getFilterContainerTextField( ).setText( "" );
		tracesView.getFilterContainerTextField( ).pushEnter( );
		tracesView.getFilterComponentTextField( ).setText( "" );
		tracesView.getFilterComponentTextField( ).pushEnter( );
		tracesView.getFilterOperationTextField( ).setText( "" );
		tracesView.getFilterOperationTextField( ).pushEnter( );
		tracesView.getFilterTraceIDTextField( ).setText( "" );
		tracesView.getFilterTraceIDTextField( ).pushEnter( );

		assertTrue( tracesView.getCounterTextField( ).getText( ).startsWith( "1635 " ) );
	}

	@Test
	public void importEventMonitoringLog( ) {
		// Unfortunately TestFX cannot handle the native file dialog of JavaFX. Therefore we have to use direct access as a workaround.
		performer.perform( ( ) -> {
			ivMain.getDataService( ).loadMonitoringLogFromFS( new File( "../kieker-trace-diagnosis-release-engineering/example/event monitoring log" ) );
		} );

		mainView.getCallsButton( ).click( );
		callsView.getFilterContainerTextField( ).setText( "" );
		callsView.getFilterContainerTextField( ).pushEnter( );
		callsView.getFilterComponentTextField( ).setText( "" );
		callsView.getFilterComponentTextField( ).pushEnter( );
		callsView.getFilterOperationTextField( ).setText( "" );
		callsView.getFilterOperationTextField( ).pushEnter( );
		callsView.getFilterTraceIDTextField( ).setText( "" );
		callsView.getFilterTraceIDTextField( ).pushEnter( );

		assertTrue( callsView.getCounterTextField( ).getText( ).startsWith( "396 " ) );

		mainView.getTracesButton( ).click( );
		tracesView.getFilterContainerTextField( ).setText( "" );
		tracesView.getFilterContainerTextField( ).pushEnter( );
		tracesView.getFilterComponentTextField( ).setText( "" );
		tracesView.getFilterComponentTextField( ).pushEnter( );
		tracesView.getFilterOperationTextField( ).setText( "" );
		tracesView.getFilterOperationTextField( ).pushEnter( );
		tracesView.getFilterTraceIDTextField( ).setText( "" );
		tracesView.getFilterTraceIDTextField( ).pushEnter( );

		assertTrue( tracesView.getCounterTextField( ).getText( ).startsWith( "100 " ) );
	}

}