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

package kieker.diagnosis.util;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import kieker.diagnosis.service.nameconverter.NameConverterService;

public class NameConverterTest {

	@Test
	public void toShortComponentNameForCommonCaseShouldWork( ) {
		final String result = NameConverterService.toShortComponentName( "A.B.C" );

		assertThat( result, is( "C" ) );
	}

	@Test
	public void toShortComponentNameForSingleComponentShouldWork( ) {
		final String result = NameConverterService.toShortComponentName( "A" );

		assertThat( result, is( "A" ) );
	}

	@Test
	public void toShortOperationNameForCommonCaseShouldWork( ) {
		final String result = NameConverterService.toShortOperationName( "public void kieker.examples.bookstore.Catalog.getBook(boolean)" );

		assertThat( result, is( "getBook(...)" ) );
	}

	@Test
	public void toShortOperationNameForMultipleFullQualifiedClassesShouldWork( ) {
		final String result = NameConverterService
				.toShortOperationName( "public final kieker.examples.bookstore.CRM kieker.examples.bookstore.Catalog.getBook(boolean)" );

		assertThat( result, is( "getBook(...)" ) );
	}

	@Test
	public void toShortTimeUnitForNanosecondsShouldWork( ) {
		final String result = NameConverterService.toShortTimeUnit( TimeUnit.NANOSECONDS );

		assertThat( result, is( "ns" ) );
	}

	@Test
	public void toShortTimeUnitForMicrosecondsShouldWork( ) {
		final String result = NameConverterService.toShortTimeUnit( TimeUnit.MICROSECONDS );

		assertThat( result, is( "us" ) );
	}

	@Test
	public void toShortTimeUnitForMillisecondsShouldWork( ) {
		final String result = NameConverterService.toShortTimeUnit( TimeUnit.MILLISECONDS );

		assertThat( result, is( "ms" ) );
	}

	@Test
	public void toShortTimeUnitForSecondsShouldWork( ) {
		final String result = NameConverterService.toShortTimeUnit( TimeUnit.SECONDS );

		assertThat( result, is( "s" ) );
	}

	@Test
	public void toShortTimeUnitForMinutesShouldWork( ) {
		final String result = NameConverterService.toShortTimeUnit( TimeUnit.MINUTES );

		assertThat( result, is( "m" ) );
	}

	@Test
	public void toShortTimeUnitForHoursShouldWork( ) {
		final String result = NameConverterService.toShortTimeUnit( TimeUnit.HOURS );

		assertThat( result, is( "h" ) );
	}

	@Test
	public void toShortTimeUnitForDaysShouldWork( ) {
		final String result = NameConverterService.toShortTimeUnit( TimeUnit.DAYS );

		assertThat( result, is( "d" ) );
	}

	@Test
	public void toShortTimeUnitForNullShouldReturnEmptyString( ) {
		final String result = NameConverterService.toShortTimeUnit( null );

		assertThat( result, is( "" ) );
	}

}
