package kieker.diagnosis.architecture.ui;

import java.util.ResourceBundle;

import com.google.inject.Inject;

import javafx.scene.layout.VBox;
import kieker.diagnosis.architecture.common.ClassUtil;

/**
 * This is the abstract base for a view. It provides a convenient method to localize a string. Also a corresponding stylesheet file is applied. For each class
 * extending this base, a resource bundle has to be available in the classpath with the name of the implementing class.
 *
 * @param <C>
 *            The type of the controller.
 *
 * @author Nils Christian Ehmke
 */
public abstract class ViewBase<C extends ControllerBase<?>> extends VBox {

	private final ResourceBundle ivResourceBundle = ResourceBundle.getBundle( ClassUtil.getRealName( getClass( ) ) );

	@Inject
	private C ivController;

	public ViewBase( ) {
		// Make sure that the singleton annotation is present
		ClassUtil.assertSingletonAnnotation( getClass( ) );

		getStylesheets( ).add( getStylsheetUrl( ) );
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
	 * Gets the controller for this view.
	 *
	 * @return The controller.
	 */
	protected final C getController( ) {
		return ivController;
	}

	private String getStylsheetUrl( ) {
		return ClassUtil.getRealName( getClass( ) ).replace( ".", "/" ) + ".css";
	}

	/**
	 * This method can be used to send a parameter to the current view. The precise nature of the parameter (and all actions necessary to use it) depends on the
	 * view.
	 *
	 * @param aParameter
	 *            The parameter.
	 */
	public abstract void setParameter( Object aParameter );

}
