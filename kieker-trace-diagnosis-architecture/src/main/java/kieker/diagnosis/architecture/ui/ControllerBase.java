package kieker.diagnosis.architecture.ui;

import java.util.ResourceBundle;

import com.google.inject.Inject;
import com.google.inject.Injector;

import kieker.diagnosis.architecture.common.ClassUtil;
import kieker.diagnosis.architecture.service.ServiceBase;

/**
 * This is the abstract base for a controller. It provides convenient methods, like retrieving a service or localizing a string. For each class extending this
 * base, a resource bundle has to be available in the classpath with the name of the implementing class.
 *
 * @param <VM>
 *            The type of the view model.
 *
 * @author Nils Christian Ehmke
 */
public abstract class ControllerBase<VM extends ViewModelBase<?>> {

	private final ResourceBundle ivResourceBundle = ResourceBundle.getBundle( ClassUtil.getRealName( getClass( ) ) );

	@Inject
	private VM ivViewModel;

	@Inject
	private Injector ivInjector;

	public ControllerBase( ) {
		// Make sure that the singleton annotation is present
		ClassUtil.assertSingletonAnnotation( getClass( ) );
	}

	/**
	 * Gets the view model for this controller.
	 *
	 * @return The view model.
	 */
	protected final VM getViewModel( ) {
		return ivViewModel;
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
	 * Gets a controller of the given type. Use this method with care, as it can lead to incomprehensible execution flow.
	 *
	 * @param aControllerClass
	 *            The type of the controller.
	 *
	 * @return The controller.
	 */
	protected final <C extends ControllerBase<?>> C getController( final Class<C> aControllerClass ) {
		return ivInjector.getInstance( aControllerClass );
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
