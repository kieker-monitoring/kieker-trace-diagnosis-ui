package kieker.diagnosis.common.domain;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class AggregatedOperationCallTest {

	@Test
	public void constructorShouldCopySingleOperationCall() {
		final OperationCall call = new OperationCall("container", "component", "operation", 42);
		final AggregatedOperationCall aggregatedCall = new AggregatedOperationCall(call);

		assertThat(aggregatedCall.getContainer(), is("container"));
		assertThat(aggregatedCall.getComponent(), is("component"));
		assertThat(aggregatedCall.getOperation(), is("operation"));
	}

	@Test
	public void constructorShouldCopyNestedOperationCall() {
		final OperationCall call = new OperationCall("container", "component", "operation", 42);
		call.addChild(new OperationCall("container1", "component1", "operation1", 42));
		call.addChild(new OperationCall("container2", "component2", "operation2", 42));

		final AggregatedOperationCall aggregatedCall = new AggregatedOperationCall(call);

		assertThat(aggregatedCall.getContainer(), is("container"));
		assertThat(aggregatedCall.getComponent(), is("component"));
		assertThat(aggregatedCall.getOperation(), is("operation"));

		assertThat(aggregatedCall.getChildren().get(0).getContainer(), is("container1"));
		assertThat(aggregatedCall.getChildren().get(0).getComponent(), is("component1"));
		assertThat(aggregatedCall.getChildren().get(0).getOperation(), is("operation1"));

		assertThat(aggregatedCall.getChildren().get(1).getContainer(), is("container2"));
		assertThat(aggregatedCall.getChildren().get(1).getComponent(), is("component2"));
		assertThat(aggregatedCall.getChildren().get(1).getOperation(), is("operation2"));
	}

	@Test
	public void constructorShouldCopyStatistics() {
		final OperationCall call = new OperationCall("container", "component", "operation", 42);
		call.setStackSize(1);

		final AggregatedOperationCall aggregatedCall = new AggregatedOperationCall(call);

		assertThat(aggregatedCall.getStackSize(), is((Object) 1));
	}

}
