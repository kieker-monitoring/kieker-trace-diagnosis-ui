package kieker.diagnosis.architecture.common;

import com.google.inject.Singleton;

import kieker.diagnosis.architecture.exception.TechnicalException;

/**
 * A util class to get the real classes of proxied objects.
 *
 * @author Nils Christian Ehmke
 */
public final class ClassUtil {

	private ClassUtil( ) {
		// Avoid instantiation
	}

	/**
	 * Delivers the real class of the given class. This means that for a proxy class the super class will be returned and the class itself otherwise.
	 *
	 * @param aClass
	 *            The (potentially proxy) class.
	 *
	 * @return The real class.
	 */
	public static Class<?> getRealClass( final Class<?> aClass ) {
		if ( aClass.getName( ).contains( "$$EnhancerByGuice$$" ) ) {
			return aClass.getSuperclass( );
		} else {
			return aClass;
		}
	}

	/**
	 * Delivers the real name of the given class. This means that for a proxy class the name of the super class will be returned and the name of the class
	 * itself otherwise.
	 *
	 * @param aClass
	 *            The (potentially proxy) class.
	 *
	 * @return The real class name.
	 */
	public static String getRealName( final Class<?> aClass ) {
		return getRealClass( aClass ).getName( );
	}

	/**
	 * Delivers the real canonical name of the given class. This means that for a proxy class the name of the super class will be returned and the name of the
	 * class itself otherwise.
	 *
	 * @param aClass
	 *            The (potentially proxy) class.
	 *
	 * @return The real canonical class name.
	 */
	public static String getRealCanonicalName( final Class<?> aClass ) {
		return getRealClass( aClass ).getCanonicalName( );
	}

	/**
	 * Delivers the real simple name of the given class. This means that for a proxy class the name of the super class will be returned and the name of the
	 * class itself otherwise.
	 *
	 * @param aClass
	 *            The (potentially proxy) class.
	 *
	 * @return The real simple class name.
	 */
	public static Object getRealSimpleName( final Class<?> aClass ) {
		return getRealClass( aClass ).getSimpleName( );
	}

	/**
	 * Checks whether the given class is annotated with the {@link Singleton} annotation. For the check the real class will be used, which means that the method
	 * can handle proxy classes.
	 *
	 * @param aClass
	 *            The (potentially proxy) class.
	 *
	 * @throws TechnicalException
	 *             If the given class is not annotated.
	 */
	public static void assertSingletonAnnotation( final Class<?> aClass ) throws TechnicalException {
		final boolean isAnnotated = getRealClass( aClass ).isAnnotationPresent( Singleton.class );
		if ( !isAnnotated ) {
			throw new TechnicalException( "The class '%s' must be annotated with '%s'.", getRealSimpleName( aClass ), Singleton.class.getSimpleName( ) );
		}
	}
}
