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

	public String convert( final String aClass ) {
		String clazz = aClass;

		if ( this == SHORT ) {
			final Matcher matcher = cvMethodPattern.matcher( clazz );
			if ( matcher.find( ) ) {
				// Replace the parenthesis
				clazz = matcher.group( 1 );
				clazz = clazz + "(...)";

				// Remove the class part
				final int lastPointPos = clazz.lastIndexOf( '.', clazz.length( ) - 5 );
				if ( lastPointPos != -1 ) {
					clazz = clazz.substring( lastPointPos + 1 );
				}
			}
		}

		return clazz.intern( );
	}

}
