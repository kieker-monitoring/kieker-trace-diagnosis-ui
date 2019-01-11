package kieker.diagnosis.frontend.tab.statistics.complex;

import org.testfx.api.FxRobot;

import kieker.diagnosis.frontend.test.TextField;
import lombok.Getter;

@Getter
public final class StatisticsPage {

	private final TextField processedBytes;
	private final TextField processDuration;
	private final TextField processSpeed;

	public StatisticsPage( final FxRobot fxRobot ) {
		processedBytes = new TextField( fxRobot, "#statisticsProcessedBytes" );
		processDuration = new TextField( fxRobot, "#statisticsProcessDuration" );
		processSpeed = new TextField( fxRobot, "#statisticsProcessSpeed" );
	}

}
