/***************************************************************************
 * Copyright 2014 Kieker Project (http://kieker-monitoring.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/

package kieker.diagnosis.common.model.importer;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Test;

import teetime.framework.Analysis;

public final class ImportAnalysisConfigurationTest {

	@Test
	public void exampleLogImportShouldWork() {
		final ImportAnalysisConfiguration configuration = new ImportAnalysisConfiguration(new File("example/event monitoring log"));
		final Analysis analysis = new Analysis(configuration);
		analysis.start();

		assertThat(configuration.getMetadataRecords(), hasSize(1));
		assertThat(configuration.getTracesList(), hasSize(100));
		assertThat(configuration.getAggregatedTraces(), hasSize(4));
		assertThat(configuration.getFailedTracesList(), hasSize(3));
		assertThat(configuration.getFailureContainingTracesList(), hasSize(4));
		assertThat(configuration.getFailedAggregatedTracesList(), hasSize(2));
		assertThat(configuration.getFailureContainingAggregatedTracesList(), hasSize(3));
	}

	@Test
	public void exampleLegacyLogImportShouldWork() {
		final ImportAnalysisConfiguration configuration = new ImportAnalysisConfiguration(new File("example/execution monitoring log"));
		final Analysis analysis = new Analysis(configuration);
		analysis.start();

		assertThat(configuration.getMetadataRecords(), hasSize(1));
		assertThat(configuration.getTracesList(), hasSize(1635));
		assertThat(configuration.getAggregatedTraces(), hasSize(4));
		assertThat(configuration.getFailedTracesList(), is(empty()));
		assertThat(configuration.getFailureContainingTracesList(), is(empty()));
		assertThat(configuration.getFailedAggregatedTracesList(), is(empty()));
		assertThat(configuration.getFailureContainingAggregatedTracesList(), is(empty()));
	}

	@Test
	public void nonExistingLogShouldNotLeadToCrash() {
		final ImportAnalysisConfiguration configuration = new ImportAnalysisConfiguration(new File("nonExistingLog"));
		final Analysis analysis = new Analysis(configuration);
		analysis.start();

		assertThat(configuration.getMetadataRecords(), is(empty()));
		assertThat(configuration.getTracesList(), is(empty()));
		assertThat(configuration.getAggregatedTraces(), is(empty()));
		assertThat(configuration.getFailedTracesList(), is(empty()));
		assertThat(configuration.getFailureContainingTracesList(), is(empty()));
		assertThat(configuration.getFailedAggregatedTracesList(), is(empty()));
		assertThat(configuration.getFailureContainingAggregatedTracesList(), is(empty()));
	}

}
