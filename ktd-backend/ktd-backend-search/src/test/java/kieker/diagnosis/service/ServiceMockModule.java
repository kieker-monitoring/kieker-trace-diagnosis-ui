package kieker.diagnosis.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.inject.AbstractModule;

public final class ServiceMockModule extends AbstractModule {

	private final Map<Class<?>, Object> mockMap;
	
	public <T> ServiceMockModule( final Class<T> mockClass, final T mockObject ) {
		this( createMockMap( mockClass, mockObject ) );
	}
	
	private static <T> Map<Class<?>, Object> createMockMap(Class<T> mockClass, T mockObject) {
		final Map<Class<?>, Object> mockMap = new HashMap<>();
		mockMap.put( mockClass, mockObject );
		return mockMap;
	}

	public ServiceMockModule( final Map<Class<?>, Object> mockMap ) {
		this.mockMap = mockMap;
	}

	@Override
	protected void configure() {
		mockMap.entrySet()
		       .stream()
		       .forEach( this::bind );
	}

	@SuppressWarnings("unchecked")
	private <T> void bind( final Entry<Class<?>, Object> entry ) {
		bind( ( Class<T> ) entry.getKey( ) ).toInstance( (T) entry.getValue( ) );
	}
	
}
