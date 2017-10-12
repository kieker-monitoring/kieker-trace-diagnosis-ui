package kieker.diagnosis.service.methods;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import kieker.diagnosis.KiekerTraceDiagnosisModule;
import kieker.diagnosis.service.data.MethodCall;
import kieker.diagnosis.service.data.MonitoringLogService;

public class MethodsServiceTest {

	private MethodsService ivMethodsService;
	private MonitoringLogService ivDataService;

	@Before
	public void setUp( ) {
		final Injector injector = Guice.createInjector( new KiekerTraceDiagnosisModule( ) );
		ivMethodsService = injector.getInstance( MethodsService.class );
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
		final MethodsFilter methodsFilter = new MethodsFilter( );
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
	public void testSearchTypeFilter( ) {
		// Prepare some data for the search
		createMethodCall( "host1", "class1", "op1", "cause1" );
		createMethodCall( "host1", "class2", "op1", null );
		createMethodCall( "host1", "class1", "op3", "cause1" );
		createMethodCall( "host1", "class1", "op3", "cause4" );

		assertThat( ivMethodsService.countMethods( ), is( 4 ) );

		// Now search with a filter
		final MethodsFilter methodsFilter = new MethodsFilter( );
		methodsFilter.setSearchType( SearchType.ALL );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 4 ) );

		methodsFilter.setSearchType( SearchType.ONLY_FAILED );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 3 ) );

		methodsFilter.setSearchType( SearchType.ONLY_SUCCESSFUL );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 1 ) );
	}

	private void createMethodCall( final String aHost, final String aClazz, final String aMethod, final String aException ) {
		final MethodCall methodCall = new MethodCall( );
		methodCall.setHost( aHost );
		methodCall.setClazz( aClazz );
		methodCall.setMethod( aMethod );
		methodCall.setException( aException );

		ivDataService.getMethods( ).add( methodCall );
	}

}