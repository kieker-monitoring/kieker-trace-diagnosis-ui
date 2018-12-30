package kieker.diagnosis.frontend.base.mixin;

public interface StringMixin {

	/**
	 * A convenient helper method to trim a user input.
	 *
	 * @param originalString
	 *            The string to trim. Can be {@code null}.
	 *
	 * @return The trimmed string. If the string becomes empty, {@code null} will be returned.
	 */
	default String trimToNull( final String originalString ) {
		if ( originalString == null ) {
			return originalString;
		} else {
			final String string = originalString.trim( );
			if ( string.isEmpty( ) ) {
				return null;
			} else {
				return string;
			}
		}
	}

}
