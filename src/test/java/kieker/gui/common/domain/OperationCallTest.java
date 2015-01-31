package kieker.gui.common.domain;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

public class OperationCallTest {

	@Test
	public void addingChildrenShouldUpdateTheParent() {
		final OperationCall execution = new OperationCall("", "", "", 42);
		final OperationCall child = new OperationCall("", "", "", 42);

		execution.addChild(child);

		Assert.assertThat(child.getParent(), Matchers.is(execution));
	}

}
