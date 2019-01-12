package kieker.diagnosis.backend.data.exception;

public final class ImportFailedException extends Exception {

	private static final long serialVersionUID = 1L;

	public ImportFailedException( final String msg, final Exception cause ) {
		super( msg, cause );
	}

	public ImportFailedException( final String msg ) {
		super( msg );
	}

}
