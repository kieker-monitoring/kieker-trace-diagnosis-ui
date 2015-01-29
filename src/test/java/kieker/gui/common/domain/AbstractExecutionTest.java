package kieker.gui.common.domain;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public abstract class AbstractExecutionTest<T extends AbstractExecution<T>> {

	@Test
	public void traceDepthCalculationInCommonCaseShouldWork() {
		final AbstractExecution<T> execution = this.createEmptyExecution();

		execution.addExecutionEntry(this.createEmptyExecution());
		execution.addExecutionEntry(this.createEmptyExecution());
		execution.addExecutionEntry(this.createEmptyExecution());

		execution.children.get(0).addExecutionEntry(this.createEmptyExecution());

		assertThat(execution.getTraceDepth(), is(2));
	}

	@Test
	public void traceDepthCalculationForNoChildrenShouldWork() {
		final AbstractExecution<T> execution = this.createEmptyExecution();

		assertThat(execution.getTraceDepth(), is(0));
	}

	@Test
	public void traceSizeCalculationInCommonCaseShouldWork() {
		final AbstractExecution<T> execution = this.createEmptyExecution();

		execution.addExecutionEntry(this.createEmptyExecution());
		execution.addExecutionEntry(this.createEmptyExecution());
		execution.addExecutionEntry(this.createEmptyExecution());

		execution.children.get(0).addExecutionEntry(this.createEmptyExecution());

		assertThat(execution.getTraceSize(), is(5));
	}

	@Test
	public void traceSizeCalculationForNoChildrenShouldWork() {
		final AbstractExecution<T> execution = this.createEmptyExecution();

		assertThat(execution.getTraceSize(), is(1));
	}

	protected abstract T createEmptyExecution();

}
