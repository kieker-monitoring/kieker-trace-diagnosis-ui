/***************************************************************************
 * Copyright 2015-2019 Kieker Project (http://kieker-monitoring.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/

package kieker.diagnosis.backend.settings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This enumeration represents the possible appearances of methods in the ui.
 *
 * @author Nils Christian Ehmke
 */
public enum MethodAppearance {

	SHORT, LONG;

	private static final Pattern methodPattern = Pattern.compile( "(.*)\\(.*\\)" );

	public String convert( final String methodName ) {
		String method = methodName;

		// This can only happen when the records contains null values. Ugly but possible.
		if ( method == null ) {
			return null;
		}

		if ( this == SHORT ) {
			final Matcher matcher = methodPattern.matcher( method );
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
