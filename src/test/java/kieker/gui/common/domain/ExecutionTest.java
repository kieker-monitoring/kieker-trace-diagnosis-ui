package kieker.gui.common.domain;

public final class ExecutionTest extends AbstractExecutionTest<Execution> {

	@Override
	protected Execution createEmptyExecution() {
		return new Execution(0, "", "", "");
	}

}
