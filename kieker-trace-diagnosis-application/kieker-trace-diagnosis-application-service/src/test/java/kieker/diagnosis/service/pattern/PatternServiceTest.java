package kieker.diagnosis.service.pattern;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import kieker.diagnosis.service.KiekerTraceDiagnosisServiceModule;

/**
 * Test class for the {@link PatternService}.
 *
 * @author Nils Christian Ehmke
 */
public class PatternServiceTest {

	private PatternService ivPatternService;

	@Before
	public void setUp( ) {
		final Injector injector = Guice.createInjector( new KiekerTraceDiagnosisServiceModule( ) );
		ivPatternService = injector.getInstance( PatternService.class );
	}

	@Test
	public void testVariousValidPattern( ) {
		assertThat( ivPatternService.isValidPattern( ".*" ), is( true ) );
		assertThat( ivPatternService.isValidPattern( "Test" ), is( true ) );
		assertThat( ivPatternService.isValidPattern( ".*Test.*" ), is( true ) );
		assertThat( ivPatternService.isValidPattern( "(\\d)" ), is( true ) );
		assertThat( ivPatternService.isValidPattern( "[.*]" ), is( true ) );
	}

	@Test
	public void testVariousInvalidPattern( ) {
		assertThat( ivPatternService.isValidPattern( "(" ), is( false ) );
		assertThat( ivPatternService.isValidPattern( ")" ), is( false ) );
		assertThat( ivPatternService.isValidPattern( "*" ), is( false ) );
		assertThat( ivPatternService.isValidPattern( "[" ), is( false ) );
		assertThat( ivPatternService.isValidPattern( null ), is( false ) );
	}

	@Test
	public void testCompilePatternWithVariousPattern( ) {
		assertThat( ivPatternService.compilePattern( ".*" ).matcher( "Test" ).matches( ), is( true ) );
		assertThat( ivPatternService.compilePattern( "Test" ).matcher( "Test" ).matches( ), is( true ) );
		assertThat( ivPatternService.compilePattern( ".*Test.*" ).matcher( "Test" ).matches( ), is( true ) );
		assertThat( ivPatternService.compilePattern( "(\\\\d)" ).matcher( "Test" ).matches( ), is( false ) );
		assertThat( ivPatternService.compilePattern( "\\d*" ).matcher( "123" ).matches( ), is( true ) );
	}

}
