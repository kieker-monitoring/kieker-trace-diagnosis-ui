package kieker.diagnosis.service.aggregatedmethods;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import kieker.diagnosis.service.KiekerTraceDiagnosisServiceModule;
import kieker.diagnosis.service.data.AggregatedMethodCall;
import kieker.diagnosis.service.data.MonitoringLogService;

/**
 * Test class for the {@link AggregatedMethodsService}.
 *
 * @author Nils Christian Ehmke
 */
public class AggregatedMethodsServiceTest {

	private AggregatedMethodsService ivMethodsService;
	private MonitoringLogService ivDataService;

	@Before
	public void setUp( ) {
		final Injector injector = Guice.createInjector( new KiekerTraceDiagnosisServiceModule( ) );
		ivMethodsService = injector.getInstance( AggregatedMethodsService.class );
		ivDataService = injector.getInstance( MonitoringLogService.class );
	}

	@Test
	public void testSimpleSearch( ) {
		// Prepare some data for the search
		createMethodCall( "host1", "class1", "op1", "cause1" );
		createMethodCall( "host1", "class2", "op1", "cause1" );
		createMethodCall( "host1", "class1", "op3", "cause1" );
		createMethodCall( "host1", "class1", "op3", "cause4" );

		assertThat( ivMethodsService.countMethods( ), is( 4 ) );

		// Now search with a filter
		final AggregatedMethodsFilter methodsFilter = new AggregatedMethodsFilter( );
		methodsFilter.setHost( "host1" );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 4 ) );

		methodsFilter.setClazz( "class1" );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 3 ) );

		methodsFilter.setMethod( "op3" );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 2 ) );

		methodsFilter.setException( "cause4" );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 1 ) );
	}

	@Test
	public void testSearchWithRegularExpressions( ) {
		// Prepare some data for the search
		createMethodCall( "host1", "class1", "op1", "cause1" );
		createMethodCall( "host1", "class2", "op1", "cause1" );
		createMethodCall( "host1", "class1", "op3", "cause1" );
		createMethodCall( "host1", "class1", "op3", "cause4" );

		assertThat( ivMethodsService.countMethods( ), is( 4 ) );

		// Now search with a filter
		final AggregatedMethodsFilter methodsFilter = new AggregatedMethodsFilter( );
		methodsFilter.setUseRegExpr( true );
		methodsFilter.setHost( ".*host1.*" );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 4 ) );

		methodsFilter.setClazz( ".*class1.*" );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 3 ) );

		methodsFilter.setMethod( ".*op3.*" );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 2 ) );

		methodsFilter.setException( ".*cause4.*" );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 1 ) );
	}

	@Test
	public void testSearchTypeFilter( ) {
		// Prepare some data for the search
		createMethodCall( "host1", "class1", "op1", "cause1" );
		createMethodCall( "host1", "class2", "op1", null );
		createMethodCall( "host1", "class1", "op3", "cause1" );
		createMethodCall( "host1", "class1", "op3", "cause4" );

		assertThat( ivMethodsService.countMethods( ), is( 4 ) );

		// Now search with a filter
		final AggregatedMethodsFilter methodsFilter = new AggregatedMethodsFilter( );
		methodsFilter.setSearchType( SearchType.ALL );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 4 ) );

		methodsFilter.setSearchType( SearchType.ONLY_FAILED );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 3 ) );

		methodsFilter.setSearchType( SearchType.ONLY_SUCCESSFUL );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 1 ) );
	}

	private void createMethodCall( final String aHost, final String aClazz, final String aMethod, final String aException ) {
		final AggregatedMethodCall methodCall = new AggregatedMethodCall( );
		methodCall.setHost( aHost );
		methodCall.setClazz( aClazz );
		methodCall.setMethod( aMethod );
		methodCall.setException( aException );

		ivDataService.getAggreatedMethods( ).add( methodCall );
	}

}
