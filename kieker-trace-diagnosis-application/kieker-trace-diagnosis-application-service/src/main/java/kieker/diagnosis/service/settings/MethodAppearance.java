package kieker.diagnosis.service.settings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This enumeration represents the possible appearances of methods in the ui.
 *
 * @author Nils Christian Ehmke
 */
public enum MethodAppearance {

	SHORT, LONG;

	private static final Pattern cvMethodPattern = Pattern.compile( "(.*)\\(.*\\)" );

	public String convert( final String aMethod ) {
		String method = aMethod;

		// This can only happen when the records contains null values. Ugly but possible.
		if ( method == null ) {
			return null;
		}

		if ( this == SHORT ) {
			final Matcher matcher = cvMethodPattern.matcher( method );
			if ( matcher.find( ) ) {
				// Replace the parenthesis
				method = matcher.group( 1 );
				method = method + "(...)";

				// Remove the class part
				final int lastPointPos = method.lastIndexOf( '.', method.length( ) - 5 );
				if ( lastPointPos != -1 ) {
					method = method.substring( lastPointPos + 1 );
				}
			}
		}

		return method.intern( );
	}

}
