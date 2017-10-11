package kieker.diagnosis.architecture.ui;

import java.util.ResourceBundle;

import com.google.inject.Inject;
import com.google.inject.Injector;

import kieker.diagnosis.architecture.common.ClassUtil;
import kieker.diagnosis.architecture.service.ServiceBase;

/**
 * This is the abstract base for a controller.
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
