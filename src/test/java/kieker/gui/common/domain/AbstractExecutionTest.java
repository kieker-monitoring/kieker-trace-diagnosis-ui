package kieker.gui.common.domain;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public abstract class AbstractExecutionTest<T extends AbstractExecution<T>> {

	@Test
	public void traceDepthCalculationInCommonCaseShouldWork() {
		final T execution = this.createEmptyExecution();

		execution.addExecutionEntry(this.createEmptyExecution());
		execution.addExecutionEntry(this.createEmptyExecution());
		execution.addExecutionEntry(this.createEmptyExecution());

		execution.getChildren().get(0).addExecutionEntry(this.createEmptyExecution());

		assertThat(execution.getTraceDepth(), is(2));
	}

	@Test
	public void traceDepthCalculationForNoChildrenShouldWork() {
		final T execution = this.createEmptyExecution();

		assertThat(execution.getTraceDepth(), is(0));
	}

	@Test
	public void traceSizeCalculationInCommonCaseShouldWork() {
		final T execution = this.createEmptyExecution();

		execution.addExecutionEntry(this.createEmptyExecution());
		execution.addExecutionEntry(this.createEmptyExecution());
		execution.addExecutionEntry(this.createEmptyExecution());

		execution.getChildren().get(0).addExecutionEntry(this.createEmptyExecution());

		assertThat(execution.getTraceSize(), is(5));
	}

	@Test
	public void traceSizeCalculationForNoChildrenShouldWork() {
		final T execution = this.createEmptyExecution();

		assertThat(execution.getTraceSize(), is(1));
	}

	@Test
	public void addingChildrenShouldUpdateTheParent() {
		final T execution = this.createEmptyExecution();
		final T child = this.createEmptyExecution();

		execution.addExecutionEntry(child);

		assertThat(child.getParent(), is(execution));
	}

	protected abstract T createEmptyExecution();

}
