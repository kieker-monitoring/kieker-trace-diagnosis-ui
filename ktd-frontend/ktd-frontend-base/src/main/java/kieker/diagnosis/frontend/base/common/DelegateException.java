package kieker.diagnosis.frontend.base.common;

/**
 * This is an exception that allows to "convert" a checked exception into a runtime exception. This is only to be used
 * in the frontend. Furthermore, it is only to be used if the developer is aware that a checked exception can occur, but
 * wants to let the general error handling mechanism handle the exception. If it occurs, the error handler will unwrap
 * the exception and show a general error dialog to the user.
 *
 * @author Nils Christian Ehmke
 */
public final class DelegateException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DelegateException( final Exception exception ) {
		super( exception );
	}

}
