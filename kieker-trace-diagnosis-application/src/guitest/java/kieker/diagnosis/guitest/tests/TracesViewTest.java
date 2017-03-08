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
import kieker.diagnosis.guitest.views.MainView;
import kieker.diagnosis.guitest.views.TracesView;
import kieker.diagnosis.service.ServiceUtil;
import kieker.diagnosis.service.data.DataService;

@SpringBootTest
@RunWith ( SpringRunner.class )
@ContextConfiguration ( classes = GuiTestConfiguration.class )
public class TracesViewTest {

	@Autowired
	private JavaFXThreadPerformer performer;

	@Autowired
	private MainView mainView;

	@Autowired
	private TracesView tracesView;

	@Test
	public void testFilterForTracesView( ) throws InterruptedException {
		// Unfortunately TestFX cannot handle the native file dialog of JavaFX. Therefore we have to use direct access as a workaround.
		performer.perform( ( ) -> {
			final DataService dataService = ServiceUtil.getService( DataService.class );
			dataService.loadMonitoringLogFromFS( new File( "../kieker-trace-diagnosis-release-engineering/example/event monitoring log" ) );
		} );

		mainView.getTracesButton( ).click( );

		tracesView.getFilterContainerTextField( ).setText( "SE-Nils-Ehmke" );
		tracesView.getFilterContainerTextField( ).pushEnter( );
		tracesView.getFilterComponentTextField( ).setText( "kieker.examples.bookstore.Bookstore" );
		tracesView.getFilterComponentTextField( ).pushEnter( );
		tracesView.getFilterOperationTextField( ).setText( "public void kieker.examples.bookstore.Bookstore.searchBook()" );
		tracesView.getFilterOperationTextField( ).pushEnter( );
		tracesView.getFilterTraceIDTextField( ).setText( "4658150164341456896" );
		tracesView.getFilterTraceIDTextField( ).pushEnter( );

		assertTrue( tracesView.getCounterTextField( ).getText( ).startsWith( "1 " ) );
	}

}
