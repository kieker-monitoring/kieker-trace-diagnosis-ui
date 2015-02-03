package kieker.diagnosis.common.domain;

import static org.junit.Assert.assertThat;

import org.hamcrest.Matchers;
import org.junit.Test;

public class OperationCallTest {

	@Test
	public void addingChildrenShouldUpdateTheParent() {
		final OperationCall execution = new OperationCall("", "", "", 42);
		final OperationCall child = new OperationCall("", "", "", 42);

		execution.addChild(child);

		assertThat(child.getParent(), Matchers.is(execution));
	}

}
