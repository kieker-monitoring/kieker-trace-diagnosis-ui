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

package kieker.diagnosis.backend.search;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.inject.AbstractModule;

public final class ServiceMockModule extends AbstractModule {

	private final Map<Class<?>, Object> mockMap;

	public <T> ServiceMockModule( final Class<T> mockClass, final T mockObject ) {
		this( createMockMap( mockClass, mockObject ) );
	}

	private static <T> Map<Class<?>, Object> createMockMap( final Class<T> mockClass, final T mockObject ) {
		final Map<Class<?>, Object> mockMap = new HashMap<>( );
		mockMap.put( mockClass, mockObject );
		return mockMap;
	}

	public ServiceMockModule( final Map<Class<?>, Object> mockMap ) {
		this.mockMap = mockMap;
	}

	@Override
	protected void configure( ) {
		mockMap.entrySet( )
				.stream( )
				.forEach( this::bind );
	}

	@SuppressWarnings ( "unchecked" )
	private <T> void bind( final Entry<Class<?>, Object> entry ) {
		bind( (Class<T>) entry.getKey( ) ).toInstance( (T) entry.getValue( ) );
	}

}
