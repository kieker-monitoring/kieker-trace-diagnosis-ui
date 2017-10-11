package kieker.diagnosis.architecture.service.properties;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import kieker.diagnosis.architecture.service.ServiceBase;
import kieker.diagnosis.architecture.service.cache.InvalidateCache;
import kieker.diagnosis.architecture.service.cache.UseCache;

@Singleton
public class PropertiesService extends ServiceBase {

	@Inject
	private Injector ivInjector;

	public PropertiesService( ) {
		super( false );
	}

	@UseCache ( cacheName = "applicationProperties" )
	public <T> T loadApplicationProperty( final Class<? extends ApplicationProperty<T>> aPropertyClass ) {
		final ApplicationProperty<T> property = getProperty( aPropertyClass );
		final String key = property.getKey( );

		final Preferences preferences = Preferences.userNodeForPackage( PropertiesService.class );
		final String defaultValue = property.serialize( property.getDefaultValue( ) );
		final String serializedValue = preferences.get( key, defaultValue );

		return property.deserialize( serializedValue );
	}

	@InvalidateCache ( cacheName = "applicationProperties", keyParameter = 0 )
	public <T> void saveApplicationProperty( final Class<? extends ApplicationProperty<T>> aPropertyClass, final T aValue ) {
		final ApplicationProperty<T> property = getProperty( aPropertyClass );
		final String key = property.getKey( );
		final String serializedValue = property.serialize( aValue );

		final Preferences preferences = Preferences.userNodeForPackage( PropertiesService.class );

		preferences.put( key, serializedValue );

		try {
			preferences.flush( );
		} catch ( final BackingStoreException ex ) {
			getLogger( ).error( getLocalizedString( "errorMessage" ), ex );
		}
	}

	private <T> T getProperty( final Class<? extends T> aPropertyClass ) {
		return ivInjector.getInstance( aPropertyClass );
	}

	@UseCache ( cacheName = "systemProperties" )
	public <T> T loadSystemProperty( final Class<? extends SystemProperty<T>> aPropertyClass ) {
		final SystemProperty<T> property = getProperty( aPropertyClass );
		final String key = property.getKey( );

		final String serializedValue = System.getProperty( key );
		return property.deserialize( serializedValue );
	}

}
