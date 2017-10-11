package kieker.diagnosis.architecture.exception;

/**
 * This is an exception which marks that something from a technical point of view failed.
 *
 * @author Nils Christian Ehmke
 */
public final class TechnicalException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TechnicalException( ) {
		super( );
	}

	public TechnicalException( final String aMessage, final Throwable aCause ) {
		super( aMessage, aCause );
	}

	public TechnicalException( final String aFormatMessage, final Object... aArgs ) {
		super( String.format( aFormatMessage, aArgs ) );
	}

	public TechnicalException( final String aMessage ) {
		super( aMessage );
	}

	public TechnicalException( final Throwable aCause ) {
		super( aCause );
	}

}
