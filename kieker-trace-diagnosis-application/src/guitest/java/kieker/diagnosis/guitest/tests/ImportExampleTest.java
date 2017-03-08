package kieker.diagnosis.guitest.tests;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import kieker.diagnosis.guitest.GuiTestConfiguration;
import kieker.diagnosis.guitest.JavaFXThreadPerformer;
import kieker.diagnosis.guitest.views.CallsView;
import kieker.diagnosis.guitest.views.MainView;
import kieker.diagnosis.guitest.views.TracesView;
import kieker.diagnosis.service.ServiceUtil;
import kieker.diagnosis.service.data.DataService;

@SpringBootTest
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

	@Test
	public void importExecutionMonitoringLog( ) {
		// Unfortunately TestFX cannot handle the native file dialog of JavaFX. Therefore we have to use direct access as a workaround.
		performer.perform( ( ) -> {
			final DataService dataService = ServiceUtil.getService( DataService.class );
			dataService.loadMonitoringLogFromFS( new File( "../kieker-trace-diagnosis-release-engineering/example/execution monitoring log" ) );
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
			final DataService dataService = ServiceUtil.getService( DataService.class );
			dataService.loadMonitoringLogFromFS( new File( "../kieker-trace-diagnosis-release-engineering/example/event monitoring log" ) );
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
