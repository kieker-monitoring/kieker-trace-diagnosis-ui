package kieker.gui.common.domain;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;

public final class TraceTest {

	@Test
	public void equalsWithNullShouldNotLeadToException() {
		final Trace fstTrace = new Trace(new OperationCall("", "", "", 0), 0);
		final Trace sndTrace = null;

		Assert.assertThat(fstTrace, Is.is(IsNot.not(IsEqual.equalTo(sndTrace))));
	}

	@Test
	public void equalsForSameInstanceShouldWork() {
		final Trace fstTrace = new Trace(new OperationCall("", "", "", 0), 0);
		final Trace sndTrace = fstTrace;

		Assert.assertThat(fstTrace, Is.is(IsEqual.equalTo(sndTrace)));
	}

	@Test
	public void equalsForSameValuesShouldWork() {
		final Trace fstTrace = new Trace(new OperationCall("container", "component", "operation", 42), 42);
		final Trace sndTrace = new Trace(new OperationCall("container", "component", "operation", 42), 42);

		Assert.assertThat(fstTrace, Is.is(IsEqual.equalTo(sndTrace)));
	}

	@Test
	public void equalsForDifferentTraceIDsShouldWork() {
		final Trace fstTrace = new Trace(new OperationCall("container", "component", "operation", 42), 42);
		final Trace sndTrace = new Trace(new OperationCall("container", "component", "operation", 43), 43);

		Assert.assertThat(fstTrace, Is.is(IsEqual.equalTo(sndTrace)));
	}

	@Test
	public void equalsForDifferentDurationsShouldWork() {
		final Trace fstTrace = new Trace(new OperationCall("container", "component", "operation", 42), 42);
		final Trace sndTrace = new Trace(new OperationCall("container", "component", "operation", 42), 42);

		fstTrace.getRootOperationCall().setDuration(100);
		sndTrace.getRootOperationCall().setDuration(200);

		Assert.assertThat(fstTrace, Is.is(IsEqual.equalTo(sndTrace)));
	}

	@Test
	public void equalsForDifferentContainerShouldReturnFalse() {
		final Trace fstTrace = new Trace(new OperationCall("container1", "component", "operation", 42), 42);
		final Trace sndTrace = new Trace(new OperationCall("container2", "component", "operation", 42), 42);

		Assert.assertThat(fstTrace, Is.is(IsNot.not(IsEqual.equalTo(sndTrace))));
	}

	@Test
	public void equalsForDifferentComponentsShouldReturnFalse() {
		final Trace fstTrace = new Trace(new OperationCall("container", "component1", "operation", 42), 42);
		final Trace sndTrace = new Trace(new OperationCall("container", "component2", "operation", 42), 42);

		Assert.assertThat(fstTrace, Is.is(IsNot.not(IsEqual.equalTo(sndTrace))));
	}

	@Test
	public void equalsForDifferentOperationsShouldReturnFalse() {
		final Trace fstTrace = new Trace(new OperationCall("container", "component", "operation1", 42), 42);
		final Trace sndTrace = new Trace(new OperationCall("container", "component", "operation2", 42), 42);

		Assert.assertThat(fstTrace, Is.is(IsNot.not(IsEqual.equalTo(sndTrace))));
	}

	@Test
	public void equalsForSameValuesAndNestedTracesShouldWork() {
		final Trace fstTrace = new Trace(new OperationCall("container", "component", "operation", 42), 42);
		final Trace sndTrace = new Trace(new OperationCall("container", "component", "operation", 43), 43);

		fstTrace.getRootOperationCall().addChild(new OperationCall("container1", "component1", "operation1", 42));
		sndTrace.getRootOperationCall().addChild(new OperationCall("container1", "component1", "operation1", 43));

		Assert.assertThat(fstTrace, Is.is(IsEqual.equalTo(sndTrace)));
	}

	@Test
	public void equalsForDifferentValuesInNestedTracesShouldReturnFalse() {
		final Trace fstTrace = new Trace(new OperationCall("container", "component", "operation", 42), 42);
		final Trace sndTrace = new Trace(new OperationCall("container", "component", "operation", 43), 42);

		fstTrace.getRootOperationCall().addChild(new OperationCall("container1", "component1", "operation1", 42));
		sndTrace.getRootOperationCall().addChild(new OperationCall("container2", "component1", "operation1", 43));

		Assert.assertThat(fstTrace, Is.is(IsNot.not(IsEqual.equalTo(sndTrace))));
	}

}
