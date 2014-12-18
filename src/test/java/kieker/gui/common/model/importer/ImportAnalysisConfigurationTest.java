package kieker.gui.common.model.importer;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Test;

import teetime.framework.Analysis;

public class ImportAnalysisConfigurationTest {

	@Test
	public void exampleLogImportShouldWork() {
		final ImportAnalysisConfiguration configuration = new ImportAnalysisConfiguration(new File("example/monitoring log"));
		final Analysis analysis = new Analysis(configuration);
		analysis.init();
		analysis.start();

		assertThat(configuration.getMetadataRecords(), hasSize(1));
		assertThat(configuration.getRecordsList(), is(not(empty())));
		assertThat(configuration.getTracesList(), is(not(empty())));
		assertThat(configuration.getAggregatedTraces(), is(not(empty())));
		assertThat(configuration.getFailedTracesList(), is(not(empty())));
		assertThat(configuration.getFailureContainingTracesList(), is(not(empty())));
		assertThat(configuration.getFailedAggregatedTracesList(), is(not(empty())));
		assertThat(configuration.getFailureContainingAggregatedTracesList(), is(not(empty())));
	}
}
