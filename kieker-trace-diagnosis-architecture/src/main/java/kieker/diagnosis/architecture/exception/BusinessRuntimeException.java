package kieker.diagnosis.architecture.exception;

/**
 * This is an exception which marks that something from a business point of view failed. It is only to be used in controllers, where a {@link BusinessException}
 * is transformed into a runtime exception. This can be done, as controllers are proxied with an exception handling mechanism. This exception should not be used
 * outside of controllers.
 *
 * @author Nils Christian Ehmke
 */
public final class BusinessRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BusinessRuntimeException( final BusinessException aBusinessException ) {
		super( aBusinessException );
	}

}
