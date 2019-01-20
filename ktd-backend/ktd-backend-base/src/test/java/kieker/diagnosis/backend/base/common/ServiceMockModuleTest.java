/***************************************************************************
 * Copyright 2015-2019 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.backend.base.common;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.inject.Binder;
import com.google.inject.binder.AnnotatedBindingBuilder;

import kieker.diagnosis.backend.base.service.ServiceMockModule;

@DisplayName ( "Unit-Test for ServiceMockModule" )
public class ServiceMockModuleTest {

	@Test
	@DisplayName ( "Module should bind given elements" )
	public void moduleShouldBindGivenElements( ) {
		final Object instance = new Object( );
		final ServiceMockModule<Object> module = new ServiceMockModule<>( Object.class, instance );

		@SuppressWarnings ( "unchecked" )
		final AnnotatedBindingBuilder<Object> builder = mock( AnnotatedBindingBuilder.class );
		final Binder binder = mock( Binder.class );
		when( binder.bind( Object.class ) ).thenReturn( builder );

		module.configure( binder );

		verify( builder ).toInstance( instance );
	}

}
