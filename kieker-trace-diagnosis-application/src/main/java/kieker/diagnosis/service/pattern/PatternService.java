package kieker.diagnosis.service.pattern;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.google.inject.Singleton;

import kieker.diagnosis.architecture.service.ServiceBase;
import kieker.diagnosis.architecture.service.cache.UseCache;

/**
 * The {@link PatternService} is responsible for validating and compiling regular expressions.
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
