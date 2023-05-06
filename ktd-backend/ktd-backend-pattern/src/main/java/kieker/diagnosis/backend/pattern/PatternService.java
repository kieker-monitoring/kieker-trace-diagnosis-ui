/***************************************************************************
 * Copyright 2015-2023 Kieker Project (http://kieker-monitoring.net)
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Singleton;

import kieker.diagnosis.backend.base.service.Service;
import kieker.diagnosis.backend.cache.UseCache;

/**
 * The {@link PatternService} is responsible for validating and compiling regular expressions. Some of the methods are
 * cached.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class PatternService implements Service {

	private static final Logger LOGGER = LogManager.getLogger( PatternService.class );

	/**
	 * Checks whether the given pattern is valid. In other words: Is the pattern compilable by
	 * {@link #compilePattern(String)}? This method is cached.
	 *
	 * @param pattern
	 *            The pattern to check.
	 *
	 * @return true if and only if the pattern is valid.
	 */
	@UseCache ( cacheName = "validPattern" )
	public boolean isValidPattern( final String pattern ) {
		try {
			Pattern.compile( pattern );
			return true;
		} catch ( final PatternSyntaxException | NullPointerException ex ) {
			LOGGER.debug( ( ) -> String.format( "Pattern \"%s\" is invalid", pattern ), ex );

			return false;
		}
	}

	/**
	 * Compiles the given pattern. This method is cached.
	 *
	 * @param pattern
	 *            The pattern to compile.
	 *
	 * @return The compiled pattern.
	 *
	 * @throws PatternSyntaxException
	 *             If the pattern is invalid.
	 */
	@UseCache ( cacheName = "pattern" )
	public Pattern compilePattern( final String pattern ) {
		return Pattern.compile( pattern );
	}

}
