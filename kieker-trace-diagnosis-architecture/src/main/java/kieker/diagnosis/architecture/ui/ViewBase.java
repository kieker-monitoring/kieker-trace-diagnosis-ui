package kieker.diagnosis.architecture.ui;

import java.util.ResourceBundle;

import com.google.inject.Inject;

import javafx.scene.layout.VBox;
import kieker.diagnosis.architecture.common.ClassUtil;

/**
 * This is the abstract base for a view.
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

	protected final C getController( ) {
		return ivController;
	}

	private String getStylsheetUrl( ) {
		return ClassUtil.getRealName( getClass( ) ).replace( ".", "/" ) + ".css";
	}

	public abstract void setParameter( Object aParameter );

}
