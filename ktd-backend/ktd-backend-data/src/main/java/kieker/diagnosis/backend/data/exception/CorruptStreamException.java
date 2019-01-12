package kieker.diagnosis.backend.data.exception;

public final class CorruptStreamException extends Exception {

	private static final long serialVersionUID = 1L;

	public CorruptStreamException( final String msg, final Exception cause ) {
		super( msg, cause );
	}

}
