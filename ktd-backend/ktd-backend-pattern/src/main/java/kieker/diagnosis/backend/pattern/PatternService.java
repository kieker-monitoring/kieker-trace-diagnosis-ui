/***************************************************************************
 * Copyright 2015-2018 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.backend.pattern;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.google.inject.Singleton;

import kieker.diagnosis.backend.base.service.ServiceBase;
import kieker.diagnosis.backend.cache.UseCache;

/**
 * The {@link PatternService} is responsible for validating and compiling regular expressions. Some of the methods are cached.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class PatternService extends ServiceBase {

	/**
	 * Checks whether the given pattern is valid. In other words: Is the pattern compilable by {@link #compilePattern(String)}. This method is cached.
	 *
	 * @param aPattern
	 *            The pattern to check.
	 *
	 * @return true if and only if the pattern is valid.
	 */
	@UseCache ( cacheName = "validPattern" )
	public boolean isValidPattern( final String aPattern ) {
		try {
			Pattern.compile( aPattern );
			return true;
		} catch ( final PatternSyntaxException | NullPointerException ex ) {
			getLogger( ).debug( ( ) -> String.format( "Pattern \"%s\" is invalid", aPattern ), ex );

			return false;
		}
	}

	/**
	 * Compiles the given pattern. This method is cached.
	 *
	 * @param aPattern
	 *            The pattern to compile.
	 *
	 * @return The compiled pattern.
	 */
	@UseCache ( cacheName = "pattern" )
	public Pattern compilePattern( final String aPattern ) {
		return Pattern.compile( aPattern );
	}

}
