package kieker.gui.common.domain;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public final class ExecutionTest extends AbstractExecutionTest<Execution> {

	@Override
	protected Execution createEmptyExecution() {
		return new Execution(0, "", "", "");
	}

	@Test
	public void equalsWithNullShouldNotLeadToException() {
		final Execution fstExecution = new Execution(0, "", "", "");
		final Execution sndExecution = null;

		assertThat(fstExecution, is(not(equalTo(sndExecution))));
	}

	@Test
	public void equalsForSameInstanceShouldWork() {
		final Execution fstExecution = new Execution(0, "", "", "");
		final Execution sndExecution = fstExecution;

		assertThat(fstExecution, is(equalTo(sndExecution)));
	}

	@Test
	public void equalsForSameValuesShouldWork() {
		final Execution fstExecution = new Execution(42, "container", "component", "operation");
		final Execution sndExecution = new Execution(42, "container", "component", "operation");

		assertThat(fstExecution, is(equalTo(sndExecution)));
	}

	@Test
	public void equalsForDifferentTraceIDsShouldWork() {
		final Execution fstExecution = new Execution(42, "container", "component", "operation");
		final Execution sndExecution = new Execution(43, "container", "component", "operation");

		assertThat(fstExecution, is(equalTo(sndExecution)));
	}

	@Test
	public void equalsForDifferentContainerShouldReturnFalse() {
		final Execution fstExecution = new Execution(42, "container1", "component", "operation");
		final Execution sndExecution = new Execution(42, "container2", "component", "operation");

		assertThat(fstExecution, is(not(equalTo(sndExecution))));
	}

	@Test
	public void equalsForDifferentComponentsShouldReturnFalse() {
		final Execution fstExecution = new Execution(42, "container", "component1", "operation");
		final Execution sndExecution = new Execution(42, "container", "component2", "operation");

		assertThat(fstExecution, is(not(equalTo(sndExecution))));
	}

	@Test
	public void equalsForDifferentOperationsShouldReturnFalse() {
		final Execution fstExecution = new Execution(42, "container", "component", "operation1");
		final Execution sndExecution = new Execution(42, "container", "component", "operation2");

		assertThat(fstExecution, is(not(equalTo(sndExecution))));
	}

	@Test
	public void percentCalculationShouldWork() {
		final Execution execution = new Execution(42, "", "", "");
		final Execution child1 = new Execution(42, "", "", "");
		final Execution child2 = new Execution(42, "", "", "");
		final Execution child3 = new Execution(42, "", "", "");
		final Execution child4 = new Execution(42, "", "", "");

		execution.setDuration(100);
		child1.setDuration(70);
		child2.setDuration(15);
		child3.setDuration(36);
		child4.setDuration(18);

		execution.addExecutionEntry(child1);
		execution.addExecutionEntry(child2);
		execution.addExecutionEntry(child3);

		child3.addExecutionEntry(child4);

		execution.recalculateValues();

		assertThat((double) execution.getPercent(), is(closeTo(100.0, 1e-3)));
		assertThat((double) child1.getPercent(), is(closeTo(70.0, 1e-3)));
		assertThat((double) child2.getPercent(), is(closeTo(15.0, 1e-3)));
		assertThat((double) child3.getPercent(), is(closeTo(36.0, 1e-3)));
		assertThat((double) child4.getPercent(), is(closeTo(50.0, 1e-3)));
	}

}
