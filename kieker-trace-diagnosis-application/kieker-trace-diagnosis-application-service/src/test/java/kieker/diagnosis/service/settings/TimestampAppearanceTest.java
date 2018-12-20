/***************************************************************************
 * Copyright 2015-2018 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.service.settings;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Test class for the {@link TimestampAppearance}.
 *
 * @author Nils Christian Ehmke
 */
public final class TimestampAppearanceTest {

	@Test
	public void testConvertWithTimestampAppearance( ) {
		assertThat( TimestampAppearance.TIMESTAMP.convert( 1418993603113L ), is( "1418993603113" ) );
	}

	@Test
	public void testConvertWithDateAppearance( ) {
		assertThat( TimestampAppearance.DATE.convert( 1418993603113L ), is( "19.12.14" ) );
	}

	@Test
	public void testConvertWithDateAndTimeAppearance( ) {
		assertThat( TimestampAppearance.DATE_AND_TIME.convert( 1418993603113L ), is( "19.12.2014, 13:53:23" ) );
	}

	@Test
	public void testConvertWithLongTimeAppearance( ) {
		assertThat( TimestampAppearance.LONG_TIME.convert( 1418993603113L ), is( "13:53:23 MEZ" ) );
	}

	@Test
	public void testConvertWithShortTimeAppearance( ) {
		assertThat( TimestampAppearance.SHORT_TIME.convert( 1418993603113L ), is( "13:53" ) );
	}

}
