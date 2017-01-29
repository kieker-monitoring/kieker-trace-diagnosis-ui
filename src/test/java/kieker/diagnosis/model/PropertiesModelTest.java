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

package kieker.diagnosis.model;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import kieker.diagnosis.service.properties.PropertiesService;

public final class PropertiesModelTest {

	@Test
	public void settingShouldBePersisted( ) {
		final PropertiesService fstModel = new PropertiesService( );
		fstModel.setTimeUnit( TimeUnit.NANOSECONDS );
		fstModel.setTimeUnit( TimeUnit.MICROSECONDS );

		final PropertiesService sndModel = new PropertiesService( );
		assertThat( sndModel.getTimeUnit( ), is( TimeUnit.MICROSECONDS ) );
	}

}
