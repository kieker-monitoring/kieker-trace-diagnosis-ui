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

package kieker.diagnosis.application.service.nameconverter;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import kieker.diagnosis.application.service.ServiceTestConfiguration;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith ( SpringRunner.class )
@ContextConfiguration ( classes = ServiceTestConfiguration.class )
public class NameServiceTest {

	@Autowired
	private NameConverterService ivNameConverterService;

	@Test
	public void toShortComponentNameForCommonCaseShouldWork( ) {
		final String result = ivNameConverterService.toShortComponentName( "A.B.C" );

		assertThat( result, is( "C" ) );
	}

	@Test
	public void toShortComponentNameForSingleComponentShouldWork( ) {
		final String result = ivNameConverterService.toShortComponentName( "A" );

		assertThat( result, is( "A" ) );
	}

	@Test
	public void toShortOperationNameForCommonCaseShouldWork( ) {
		final String result = ivNameConverterService.toShortOperationName( "public void kieker.examples.bookstore.Catalog.getBook(boolean)" );

		assertThat( result, is( "getBook(...)" ) );
	}

	@Test
	public void toShortOperationNameForMultipleFullQualifiedClassesShouldWork( ) {
		final String result = ivNameConverterService
				.toShortOperationName( "public final kieker.examples.bookstore.CRM kieker.examples.bookstore.Catalog.getBook(boolean)" );

		assertThat( result, is( "getBook(...)" ) );
	}

	@Test
	public void toShortTimeUnitForNanosecondsShouldWork( ) {
		final String result = ivNameConverterService.toShortTimeUnit( TimeUnit.NANOSECONDS );

		assertThat( result, is( "ns" ) );
	}

	@Test
	public void toShortTimeUnitForMicrosecondsShouldWork( ) {
		final String result = ivNameConverterService.toShortTimeUnit( TimeUnit.MICROSECONDS );

		assertThat( result, is( "us" ) );
	}

	@Test
	public void toShortTimeUnitForMillisecondsShouldWork( ) {
		final String result = ivNameConverterService.toShortTimeUnit( TimeUnit.MILLISECONDS );

		assertThat( result, is( "ms" ) );
	}

	@Test
	public void toShortTimeUnitForSecondsShouldWork( ) {
		final String result = ivNameConverterService.toShortTimeUnit( TimeUnit.SECONDS );

		assertThat( result, is( "s" ) );
	}

	@Test
	public void toShortTimeUnitForMinutesShouldWork( ) {
		final String result = ivNameConverterService.toShortTimeUnit( TimeUnit.MINUTES );

		assertThat( result, is( "m" ) );
	}

	@Test
	public void toShortTimeUnitForHoursShouldWork( ) {
		final String result = ivNameConverterService.toShortTimeUnit( TimeUnit.HOURS );

		assertThat( result, is( "h" ) );
	}

	@Test
	public void toShortTimeUnitForDaysShouldWork( ) {
		final String result = ivNameConverterService.toShortTimeUnit( TimeUnit.DAYS );

		assertThat( result, is( "d" ) );
	}

	@Test
	public void toShortTimeUnitForNullShouldReturnEmptyString( ) {
		final String result = ivNameConverterService.toShortTimeUnit( null );

		assertThat( result, is( "" ) );
	}

}
