package kieker.diagnosis.architecture.exception;

/**
 * This is an exception which marks that something from a business point of view failed.
 *
 * @author Nils Christian Ehmke
 */
public final class BusinessException extends Exception {

	private static final long serialVersionUID = 1L;

	public BusinessException( ) {
		super( );
	}

	public BusinessException( final String aMessage, final Throwable aCause ) {
		super( aMessage, aCause );
	}

	public BusinessException( final String aFormatMessage, final Object... aArgs ) {
		super( String.format( aFormatMessage, aArgs ) );
	}

	public BusinessException( final String aMessage ) {
		super( aMessage );
	}

	public BusinessException( final Throwable aCause ) {
		super( aCause );
	}

}
