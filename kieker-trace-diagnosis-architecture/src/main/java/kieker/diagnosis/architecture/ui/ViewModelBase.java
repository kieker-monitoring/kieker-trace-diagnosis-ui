package kieker.diagnosis.architecture.ui;

import java.util.ResourceBundle;

import com.google.inject.Inject;
import com.google.inject.Injector;

import javafx.scene.Scene;
import javafx.stage.Window;
import kieker.diagnosis.architecture.common.ClassUtil;
import kieker.diagnosis.architecture.service.ServiceBase;

/**
 * This is the abstract base for a view model.
 *
 * @param <V>
 *            The type of the view.
 *
 * @author Nils Christian Ehmke
 */
public abstract class ViewModelBase<V extends ViewBase<?>> {

	private final ResourceBundle ivResourceBundle = ResourceBundle.getBundle( ClassUtil.getRealName( getClass( ) ) );

	@Inject
	private V ivView;

	@Inject
	private Injector ivInjector;

	public ViewModelBase( ) {
		// Make sure that the singleton annotation is present
		ClassUtil.assertSingletonAnnotation( getClass( ) );
	}

	protected final V getView( ) {
		return ivView;
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
	 * Closes the corresponding window of the view.
	 */
	public final void close( ) {
		final Scene scene = getView( ).getScene( );
		final Window window = scene.getWindow( );
		window.hide( );
	}

	protected final String trimToNull( final String aString ) {
		if ( aString == null ) {
			return aString;
		} else {
			final String string = aString.trim( );
			if ( string.isEmpty( ) ) {
				return null;
			} else {
				return string;
			}
		}
	}

}
