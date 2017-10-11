package kieker.diagnosis.service.pattern;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.google.inject.Singleton;

import kieker.diagnosis.architecture.service.ServiceBase;
import kieker.diagnosis.architecture.service.cache.UseCache;

@Singleton
public class PatternService extends ServiceBase {

	@UseCache ( cacheName = "validPattern" )
	public boolean isValidPattern( final String aPattern ) {
		if ( aPattern == null ) {
			return true;
		}

		try {
			Pattern.compile( aPattern );
			return true;
		} catch ( final PatternSyntaxException ex ) {
			return false;
		}
	}

	@UseCache ( cacheName = "pattern" )
	public Pattern compilePattern( final String aPattern ) {
		return Pattern.compile( aPattern );
	}

}
