/***************************************************************************
 * Copyright 2015 Kieker Project (http://kiekercoir-monitoring.net)
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

package kieker.diagnosis.model.importer.stages;

import static kieker.diagnosis.model.importer.stages.StageTester.testStageBySending;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import kieker.common.record.controlflow.OperationExecutionRecord;
import kieker.diagnosis.domain.Trace;

import org.junit.Test;

import teetime.framework.Analysis;
import teetime.framework.AnalysisConfiguration;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.SingleElementPipeFactory;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;
import teetime.stage.InstanceOfFilter;
import teetime.stage.className.ClassNameRegistryRepository;
import teetime.stage.io.filesystem.Dir2RecordsFilter;

public class LegacyTraceReconstructorTest {

	@Test
	public void singleCallReconstructionShouldWork() throws Exception {
		final List<OperationExecutionRecord> records = new ArrayList<>();
		records.add(new OperationExecutionRecord("operation", OperationExecutionRecord.NO_SESSION_ID, 42, 15L, 20L, "localhost", 0, 0));

		final LegacyTraceReconstructor reconstructor = new LegacyTraceReconstructor();
		final List<Trace> result = testStageBySending(records).to(reconstructor.getInputPort()).andReceivingFrom(reconstructor.getOutputPort());

		assertThat(result, hasSize(1));
		assertThat(result.get(0).getRootOperationCall().getOperation(), is("operation"));
		assertThat(result.get(0).getRootOperationCall().getDuration(), is(5L));
		assertThat(result.get(0).getRootOperationCall().getTraceID(), is(42L));
	}

	@Test
	public void separationOfComponentShouldWork() throws Exception {
		final List<OperationExecutionRecord> records = new ArrayList<>();
		records.add(new OperationExecutionRecord("bookstoreTracing.Catalog.getBook(boolean)", "1", 42, 15L, 20L, "SRV1", 0, 0));

		final LegacyTraceReconstructor reconstructor = new LegacyTraceReconstructor();
		final List<Trace> result = testStageBySending(records).to(reconstructor.getInputPort()).andReceivingFrom(reconstructor.getOutputPort());

		assertThat(result, hasSize(1));
		assertThat(result.get(0).getRootOperationCall().getContainer(), is("SRV1"));
		assertThat(result.get(0).getRootOperationCall().getComponent(), is("bookstoreTracing.Catalog"));
		assertThat(result.get(0).getRootOperationCall().getOperation(), is("bookstoreTracing.Catalog.getBook(boolean)"));
	}

	@Test
	public void nestedCallReconstructionShouldWork() throws Exception {
		final List<OperationExecutionRecord> records = new ArrayList<>();
		records.add(new OperationExecutionRecord("B", OperationExecutionRecord.NO_SESSION_ID, 42, 10L, 20L, "localhost", 1, 1));
		records.add(new OperationExecutionRecord("C", OperationExecutionRecord.NO_SESSION_ID, 42, 10L, 20L, "localhost", 2, 1));
		records.add(new OperationExecutionRecord("D", OperationExecutionRecord.NO_SESSION_ID, 42, 10L, 20L, "localhost", 3, 2));
		records.add(new OperationExecutionRecord("A", OperationExecutionRecord.NO_SESSION_ID, 42, 10L, 20L, "localhost", 0, 0));

		final LegacyTraceReconstructor reconstructor = new LegacyTraceReconstructor();
		final List<Trace> result = testStageBySending(records).to(reconstructor.getInputPort()).andReceivingFrom(reconstructor.getOutputPort());

		assertThat(result, hasSize(1));
		assertThat(result.get(0).getRootOperationCall().getOperation(), is("A"));
		assertThat(result.get(0).getRootOperationCall().getDuration(), is(10L));
		assertThat(result.get(0).getRootOperationCall().getChildren().get(0).getOperation(), is("B"));
		assertThat(result.get(0).getRootOperationCall().getChildren().get(1).getOperation(), is("C"));
		assertThat(result.get(0).getRootOperationCall().getChildren().get(1).getChildren().get(0).getOperation(), is("D"));
	}

	@Test
	public void nestedCallWithEssJumpsReconstructionShouldWork() throws Exception {
		final List<OperationExecutionRecord> records = new ArrayList<>();
		records.add(new OperationExecutionRecord("B", OperationExecutionRecord.NO_SESSION_ID, 42, 10L, 20L, "localhost", 1, 1));
		records.add(new OperationExecutionRecord("C", OperationExecutionRecord.NO_SESSION_ID, 42, 10L, 20L, "localhost", 2, 2));
		records.add(new OperationExecutionRecord("D", OperationExecutionRecord.NO_SESSION_ID, 42, 10L, 20L, "localhost", 2, 3));
		records.add(new OperationExecutionRecord("E", OperationExecutionRecord.NO_SESSION_ID, 42, 10L, 20L, "localhost", 3, 1));
		records.add(new OperationExecutionRecord("A", OperationExecutionRecord.NO_SESSION_ID, 42, 10L, 20L, "localhost", 0, 0));

		final LegacyTraceReconstructor reconstructor = new LegacyTraceReconstructor();
		final List<Trace> result = testStageBySending(records).to(reconstructor.getInputPort()).andReceivingFrom(reconstructor.getOutputPort());

		assertThat(result, hasSize(1));
		assertThat(result.get(0).getRootOperationCall().getOperation(), is("A"));
		assertThat(result.get(0).getRootOperationCall().getDuration(), is(10L));
		assertThat(result.get(0).getRootOperationCall().getChildren(), hasSize(2));
		assertThat(result.get(0).getRootOperationCall().getChildren().get(0).getOperation(), is("B"));
		assertThat(result.get(0).getRootOperationCall().getChildren().get(1).getOperation(), is("E"));
	}

	@Test
	public void exampleLogReconstructionShouldWork() throws Exception {
		final ExampleLogReconstructionConfiguration configuration = new ExampleLogReconstructionConfiguration();
		final Analysis analysis = new Analysis(configuration);
		analysis.execute();

		assertThat(configuration.getOutput(), hasSize(1635));
	}

	private static class ExampleLogReconstructionConfiguration extends AnalysisConfiguration {

		private final List<Trace> traceCollectorList = new ArrayList<>();

		public ExampleLogReconstructionConfiguration() {
			final InitialElementProducer<File> producer = new InitialElementProducer<>(new File("example/execution monitoring log"));
			final Dir2RecordsFilter reader = new Dir2RecordsFilter(new ClassNameRegistryRepository());
			final InstanceOfFilter<Object, OperationExecutionRecord> typeFilter = new InstanceOfFilter<>(OperationExecutionRecord.class);
			final LegacyTraceReconstructor reconstructor = new LegacyTraceReconstructor();
			final CollectorSink<Trace> collector = new CollectorSink<>(this.traceCollectorList);

			final IPipeFactory pipeFactory = new SingleElementPipeFactory();
			pipeFactory.create(producer.getOutputPort(), reader.getInputPort());
			pipeFactory.create(reader.getOutputPort(), typeFilter.getInputPort());
			pipeFactory.create(typeFilter.getMatchedOutputPort(), reconstructor.getInputPort());
			pipeFactory.create(reconstructor.getOutputPort(), collector.getInputPort());

			this.addThreadableStage(producer);
		}

		public List<Trace> getOutput() {
			return this.traceCollectorList;
		}

	}

}
