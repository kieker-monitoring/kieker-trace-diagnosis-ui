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

package kieker.diagnosis.common;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class MapperTest {

	@Test
	public void mappingWithoutEntriesShouldNotLeadToException( ) {
		final Mapper<Integer, String> mapper = new Mapper<>( );

		assertThat( mapper.resolve( 1 ), is( nullValue( ) ) );
	}

	@Test
	public void mappingWithSingleEntryShouldWork( ) {
		final Mapper<Integer, String> mapper = new Mapper<>( );

		mapper.map( 1 ).to( "One" );

		assertThat( mapper.resolve( 1 ), is( "One" ) );
	}

	@Test
	public void mappingWithMultipleEntriesShouldWork( ) {
		final Mapper<Integer, String> mapper = new Mapper<>( );

		mapper.map( 1 ).to( "One" );
		mapper.map( 2 ).to( "Two" );

		assertThat( mapper.resolve( 1 ), is( "One" ) );
		assertThat( mapper.resolve( 2 ), is( "Two" ) );
	}

	@Test
	public void sameKeyShouldOverwriteMapping( ) {
		final Mapper<Integer, String> mapper = new Mapper<>( );

		mapper.map( 1 ).to( "One" );
		mapper.map( 1 ).to( "1" );

		assertThat( mapper.resolve( 1 ), is( "1" ) );
	}

	@Test
	public void reverseMappingShouldWork( ) {
		final Mapper<Integer, String> mapper = new Mapper<>( );

		mapper.map( 1 ).to( "One" );

		assertThat( mapper.invertedResolve( "One" ), is( 1 ) );
	}

	@Test
	public void defaultMappingShouldWork( ) {
		final Mapper<Integer, String> mapper = new Mapper<>( );

		mapper.mapPerDefault( ).to( "N/A" );
		mapper.map( 1 ).to( "One" );

		assertThat( mapper.resolve( 1 ), is( "One" ) );
		assertThat( mapper.resolve( 2 ), is( "N/A" ) );
	}

	@Test
	public void defaultMappingDoesNotConflictWithNullMapping( ) {
		final Mapper<Integer, String> mapper = new Mapper<>( );

		mapper.mapPerDefault( ).to( "N/A" );
		mapper.map( null ).to( "Zero" );

		assertThat( mapper.resolve( null ), is( "Zero" ) );
	}

}
