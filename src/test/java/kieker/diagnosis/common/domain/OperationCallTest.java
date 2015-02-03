package kieker.diagnosis.common.domain;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class OperationCallTest {

	@Test
	public void addingChildrenShouldUpdateTheParent() {
		final OperationCall call = new OperationCall("", "", "", 42);
		final OperationCall child = new OperationCall("", "", "", 42);

		call.addChild(child);

		assertThat(child.getParent(), is(call));
	}

}
