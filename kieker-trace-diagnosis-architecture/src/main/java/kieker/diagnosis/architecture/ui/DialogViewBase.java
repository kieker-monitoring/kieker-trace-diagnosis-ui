package kieker.diagnosis.architecture.ui;

import java.io.InputStream;
import java.util.function.Consumer;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * This is the abstract base for a dialog view. It has the same methods as {@link ViewBase}, but provides some more methods and a specific constructor for a
 * more convenient dialog view base. It is assumed that the view's resource file has the keys "title" and "icon".
 *
 * @param <C>
 *            The type of the controller.
 * @param R
 *            The return type of the dialog.
 *
 * @author Nils Christian Ehmke
 */
public abstract class DialogViewBase<C extends ControllerBase<?>, R> extends ViewBase<C> {

	private final Modality ivModality;
	private final StageStyle ivStageStyle;
	private R ivResult;
	private final Consumer<C> ivRefreshFunction;
	private final boolean ivShowAndWait;

	public DialogViewBase( final Modality aModality, final StageStyle aStageStyle, final Consumer<C> aRefreshFunction, final boolean aShowAndWait ) {
		ivModality = aModality;
		ivStageStyle = aStageStyle;
		ivRefreshFunction = aRefreshFunction;
		ivShowAndWait = aShowAndWait;
	}

	public DialogViewBase( final Modality aModality, final StageStyle aStageStyle, final boolean aShowAndWait ) {
		this( aModality, aStageStyle, null, aShowAndWait );
	}

	protected final void setResult( final R aResult ) {
		ivResult = aResult;
	}

	public final R open( final Window aParent ) {
		// Create a scene if necessary
		Scene scene = getScene( );
		if ( scene == null ) {
			scene = new Scene( this );
		}

		ivResult = null;

		// Load the icon
		final String iconPath = getLocalizedString( "icon" );
		final InputStream iconStream = getClass( ).getClassLoader( ).getResourceAsStream( iconPath );
		final Image icon = new Image( iconStream );

		// Prepare and show the stage
		final Stage stage = new Stage( );
		stage.setResizable( false );
		stage.initModality( ivModality );
		stage.initStyle( ivStageStyle );
		stage.initOwner( aParent );
		stage.getIcons( ).add( icon );
		stage.setTitle( getLocalizedString( "title" ) );
		stage.setScene( scene );

		if ( ivRefreshFunction != null ) {
			ivRefreshFunction.accept( getController( ) );
		}

		if ( ivShowAndWait ) {
			stage.showAndWait( );
		} else {
			stage.show( );
		}

		return ivResult;
	}

	@FunctionalInterface
	public static interface RefreshFunction {

		void apply( );

	}

}
