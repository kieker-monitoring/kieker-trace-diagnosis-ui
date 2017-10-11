package kieker.diagnosis.architecture.service;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Injector;

import kieker.diagnosis.architecture.common.ClassUtil;
import kieker.diagnosis.architecture.exception.TechnicalException;

/**
 * This is the abstract base for a service.
 *
 * @author Nils Christian Ehmke
 */
public abstract class ServiceBase {

	private final ResourceBundle ivResourceBundle = ResourceBundle.getBundle( ClassUtil.getRealName( getClass( ) ) );

	private static Logger cvLogger;

	@Inject
	private Injector ivInjector;

	protected ServiceBase( final boolean aCheckFields ) {
		// Make sure that the singleton annotation is present
		ClassUtil.assertSingletonAnnotation( getClass( ) );

		if ( aCheckFields ) {
			// Make sure that the service has no fields
			final Field[] declaredFields = ClassUtil.getRealClass( getClass( ) ).getDeclaredFields( );
			for ( final Field field : declaredFields ) {
				if ( !Modifier.isStatic( field.getModifiers( ) ) ) {
					throw new TechnicalException( "The service class '%s' must not have any non-static fields ('%s').", ClassUtil.getRealSimpleName( getClass( ) ),
							field.getName( ) );
				}
			}
		}
	}

	protected ServiceBase( ) {
		this( true );
	}

	/**
	 * Gets a service of the given type.
	 *
	 * @param aServiceClass
	 *            The type of the service.
	 *
	 * @return The service.
	 */
	protected final <S extends ServiceBase> S getService( final Class<S> aServiceClass ) {
		return ivInjector.getInstance( aServiceClass );
	}

	/**
	 * Returns the logger for the current class.
	 *
	 * @return The logger.
	 */
	protected final Logger getLogger( ) {
		if ( cvLogger == null ) {
			cvLogger = LogManager.getLogger( getClass( ) );
		}

		return cvLogger;
	}

	/**
	 * Delivers the localized string for the given key for the current class.
	 *
	 * @param aKey
	 *            The resource key.
	 *
	 * @return The localized string.
	 */
	protected final String getLocalizedString( final String aKey ) {
		return ivResourceBundle.getString( aKey );
	}

}
