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

package kieker.gui.common.model.importer;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Test;

import teetime.framework.Analysis;

public final class ImportAnalysisConfigurationTest {

	@Test
	public void exampleLogImportShouldWork() {
		final ImportAnalysisConfiguration configuration = new ImportAnalysisConfiguration(new File("example/monitoring log"));
		final Analysis analysis = new Analysis(configuration);
		analysis.init();
		analysis.start();

		assertThat(configuration.getMetadataRecords(), hasSize(1));
		assertThat(configuration.getTracesList(), is(not(empty())));
		assertThat(configuration.getAggregatedTraces(), is(not(empty())));
		assertThat(configuration.getFailedTracesList(), is(not(empty())));
		assertThat(configuration.getFailureContainingTracesList(), is(not(empty())));
		assertThat(configuration.getFailedAggregatedTracesList(), is(not(empty())));
		assertThat(configuration.getFailureContainingAggregatedTracesList(), is(not(empty())));
	}
}
