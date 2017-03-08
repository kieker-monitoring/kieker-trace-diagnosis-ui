package kieker.diagnosis.guitest.tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import kieker.diagnosis.guitest.GuiTestConfiguration;
import kieker.diagnosis.guitest.views.MainView;

@SpringBootTest
@RunWith ( SpringRunner.class )
@ContextConfiguration ( classes = GuiTestConfiguration.class )
public class VisitEachButtonTest {

	@Autowired
	private MainView mainView;

	@Test
	public void visitCalls( ) {
		mainView.getCallsButton( ).click( );
	}

	@Test
	public void visitTraces( ) {
		mainView.getTracesButton( ).click( );
	}

	@Test
	public void visitAggregatedCalls( ) {
		mainView.getAggregatedCallsButton( ).click( );
	}

	@Test
	public void visitAggregatedTraces( ) {
		mainView.getAggregatedTracesButton( ).click( );
	}

	@Test
	public void visitStatisics( ) {
		mainView.getStatisticsButton( ).click( );
	}

}
