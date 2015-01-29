package kieker.gui.common.domain;

public final class AggregatedExecutionTest extends AbstractExecutionTest<AggregatedExecution> {

	@Override
	protected AggregatedExecution createEmptyExecution() {
		return new AggregatedExecution(new Execution(0, "", "", ""));
	}

}
