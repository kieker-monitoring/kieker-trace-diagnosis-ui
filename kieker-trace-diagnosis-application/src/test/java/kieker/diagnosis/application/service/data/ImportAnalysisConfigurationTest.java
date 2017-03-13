/***************************************************************************
 * Copyright 2015-2016 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.application.service.data;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Test;

import teetime.framework.Execution;

public final class ImportAnalysisConfigurationTest {

	@Test
	public void exampleLogImportShouldWork( ) {
		final ImportAnalysisConfiguration configuration = new ImportAnalysisConfiguration(
				new File( "../kieker-trace-diagnosis-release-engineering/example/event monitoring log" ), true, false );
		final Execution<ImportAnalysisConfiguration> analysis = new Execution<>( configuration );
		analysis.executeBlocking( );

		assertThat( configuration.getMetadataRecords( ), hasSize( 1 ) );
		assertThat( configuration.getTracesList( ), hasSize( 100 ) );
		assertThat( configuration.getAggregatedTraces( ), hasSize( 4 ) );
		assertThat( configuration.getTracesList( ).stream( ).filter( t -> t.getRootOperationCall( ).isFailed( ) ).count( ), is( 3L ) );
		assertThat( configuration.getTracesList( ).stream( ).filter( t -> t.getRootOperationCall( ).containsFailure( ) ).count( ), is( 4L ) );
		assertThat( configuration.getAggregatedTraces( ).stream( ).filter( t -> t.getRootOperationCall( ).isFailed( ) ).count( ), is( 2L ) );
		assertThat( configuration.getAggregatedTraces( ).stream( ).filter( t -> t.getRootOperationCall( ).containsFailure( ) ).count( ), is( 3L ) );
	}

	@Test
	public void exampleLegacyLogImportShouldWork( ) {
		final ImportAnalysisConfiguration configuration = new ImportAnalysisConfiguration(
				new File( "../kieker-trace-diagnosis-release-engineering/example/execution monitoring log" ), true, false );
		final Execution<ImportAnalysisConfiguration> analysis = new Execution<>( configuration );
		analysis.executeBlocking( );

		assertThat( configuration.getMetadataRecords( ), hasSize( 1 ) );
		assertThat( configuration.getTracesList( ), hasSize( 1635 ) );
		assertThat( configuration.getAggregatedTraces( ), hasSize( 4 ) );
		assertThat( configuration.getTracesList( ).stream( ).filter( t -> t.getRootOperationCall( ).isFailed( ) ).count( ), is( 0L ) );
		assertThat( configuration.getTracesList( ).stream( ).filter( t -> t.getRootOperationCall( ).containsFailure( ) ).count( ), is( 0L ) );
		assertThat( configuration.getAggregatedTraces( ).stream( ).filter( t -> t.getRootOperationCall( ).isFailed( ) ).count( ), is( 0L ) );
		assertThat( configuration.getAggregatedTraces( ).stream( ).filter( t -> t.getRootOperationCall( ).containsFailure( ) ).count( ), is( 0L ) );
	}

	@Test
	public void nonExistingLogShouldNotLeadToCrash( ) {
		final ImportAnalysisConfiguration configuration = new ImportAnalysisConfiguration( new File( "nonExistingLog" ), true, false );
		final Execution<ImportAnalysisConfiguration> analysis = new Execution<>( configuration );
		analysis.executeBlocking( );

		assertThat( configuration.getMetadataRecords( ), is( empty( ) ) );
		assertThat( configuration.getTracesList( ), is( empty( ) ) );
		assertThat( configuration.getAggregatedTraces( ), is( empty( ) ) );
		assertThat( configuration.getTracesList( ).stream( ).filter( t -> t.getRootOperationCall( ).isFailed( ) ).count( ), is( 0L ) );
		assertThat( configuration.getTracesList( ).stream( ).filter( t -> t.getRootOperationCall( ).containsFailure( ) ).count( ), is( 0L ) );
		assertThat( configuration.getAggregatedTraces( ).stream( ).filter( t -> t.getRootOperationCall( ).isFailed( ) ).count( ), is( 0L ) );
		assertThat( configuration.getAggregatedTraces( ).stream( ).filter( t -> t.getRootOperationCall( ).containsFailure( ) ).count( ), is( 0L ) );
	}

}
