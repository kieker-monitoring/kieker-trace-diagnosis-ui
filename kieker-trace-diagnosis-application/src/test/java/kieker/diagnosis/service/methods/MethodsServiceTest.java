package kieker.diagnosis.service.methods;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;
import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import kieker.diagnosis.KiekerTraceDiagnosisModule;
import kieker.diagnosis.service.data.MethodCall;
import kieker.diagnosis.service.data.MonitoringLogService;

/**
 * Test class for the {@link MethodsService}.
 *
 * @author Nils Christian Ehmke
 */
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
	public void testSearchWithRegularExpressions( ) {
		// Prepare some data for the search
		createMethodCall( "host1", "class1", "op1", "cause1" );
		createMethodCall( "host1", "class2", "op1", "cause1" );
		createMethodCall( "host1", "class1", "op3", "cause1" );
		createMethodCall( "host1", "class1", "op3", "cause4" );

		assertThat( ivMethodsService.countMethods( ), is( 4 ) );

		// Now search with a filter
		final MethodsFilter methodsFilter = new MethodsFilter( );
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
		final MethodsFilter methodsFilter = new MethodsFilter( );
		methodsFilter.setSearchType( SearchType.ALL );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 4 ) );

		methodsFilter.setSearchType( SearchType.ONLY_FAILED );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 3 ) );

		methodsFilter.setSearchType( SearchType.ONLY_SUCCESSFUL );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 1 ) );
	}

	@Test
	public void testTraceIdFilter( ) {
		// Prepare some data for the search
		createMethodCall( 1L );
		createMethodCall( 1L );
		createMethodCall( 2L );
		createMethodCall( 3L );

		assertThat( ivMethodsService.countMethods( ), is( 4 ) );

		// Now search with a filter
		final MethodsFilter methodsFilter = new MethodsFilter( );
		methodsFilter.setTraceId( 1L );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 2 ) );

		methodsFilter.setTraceId( 2L );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 1 ) );

		methodsFilter.setTraceId( 3L );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 1 ) );

		methodsFilter.setTraceId( 4L );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 0 ) );
	}

	@Test
	public void testLowerTimeFilter( ) {
		// Prepare some data for the search
		createMethodCall( 2000, 05, 01, 15, 20 );
		createMethodCall( 2000, 05, 05, 15, 20 );
		createMethodCall( 2015, 01, 04, 15, 25 );
		createMethodCall( 2015, 01, 01, 15, 20 );

		assertThat( ivMethodsService.countMethods( ), is( 4 ) );

		// Now search with a filter
		final MethodsFilter methodsFilter = new MethodsFilter( );
		methodsFilter.setLowerDate( LocalDate.of( 2000, 05, 01 ) );
		methodsFilter.setLowerTime( getCalendarFor( 15, 20 ) );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 4 ) );

		methodsFilter.setLowerDate( LocalDate.of( 2000, 05, 01 ) );
		methodsFilter.setLowerTime( getCalendarFor( 14, 20 ) );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 4 ) );

		methodsFilter.setLowerDate( LocalDate.of( 2000, 05, 01 ) );
		methodsFilter.setLowerTime( getCalendarFor( 16, 20 ) );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 3 ) );

		methodsFilter.setLowerDate( LocalDate.of( 2000, 05, 01 ) );
		methodsFilter.setLowerTime( getCalendarFor( 15, 21 ) );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 3 ) );

		methodsFilter.setLowerDate( LocalDate.of( 2015, 01, 01 ) );
		methodsFilter.setLowerTime( getCalendarFor( 15, 20 ) );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 2 ) );

		methodsFilter.setLowerDate( LocalDate.of( 2015, 01, 04 ) );
		methodsFilter.setLowerTime( getCalendarFor( 15, 20 ) );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 1 ) );

		methodsFilter.setLowerDate( LocalDate.of( 2015, 01, 05 ) );
		methodsFilter.setLowerTime( getCalendarFor( 10, 10 ) );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 0 ) );

		methodsFilter.setLowerDate( LocalDate.of( 2015, 01, 04 ) );
		methodsFilter.setLowerTime( getCalendarFor( 15, 26 ) );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 0 ) );

		methodsFilter.setLowerDate( null );
		methodsFilter.setLowerTime( null );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 4 ) );

		methodsFilter.setLowerDate( LocalDate.of( 2015, 01, 01 ) );
		methodsFilter.setLowerTime( null );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 2 ) );

		methodsFilter.setLowerDate( null );
		methodsFilter.setLowerTime( getCalendarFor( 15, 25 ) );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 1 ) );
	}

	@Test
	public void testUpperTimeFilter( ) {
		// Prepare some data for the search
		createMethodCall( 2000, 05, 01, 15, 20 );
		createMethodCall( 2000, 05, 05, 15, 20 );
		createMethodCall( 2015, 01, 04, 15, 25 );
		createMethodCall( 2015, 01, 01, 15, 20 );

		assertThat( ivMethodsService.countMethods( ), is( 4 ) );

		// Now search with a filter
		final MethodsFilter methodsFilter = new MethodsFilter( );
		methodsFilter.setUpperDate( LocalDate.of( 2000, 05, 01 ) );
		methodsFilter.setUpperTime( getCalendarFor( 15, 20 ) );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 1 ) );

		methodsFilter.setUpperDate( LocalDate.of( 2000, 05, 01 ) );
		methodsFilter.setUpperTime( getCalendarFor( 14, 20 ) );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 0 ) );

		methodsFilter.setUpperDate( LocalDate.of( 2000, 05, 01 ) );
		methodsFilter.setUpperTime( getCalendarFor( 16, 20 ) );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 1 ) );

		methodsFilter.setUpperDate( LocalDate.of( 2000, 05, 05 ) );
		methodsFilter.setUpperTime( getCalendarFor( 15, 21 ) );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 2 ) );

		methodsFilter.setUpperDate( LocalDate.of( 2015, 01, 01 ) );
		methodsFilter.setUpperTime( getCalendarFor( 15, 20 ) );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 3 ) );

		methodsFilter.setUpperDate( LocalDate.of( 2015, 01, 04 ) );
		methodsFilter.setUpperTime( getCalendarFor( 15, 20 ) );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 3 ) );

		methodsFilter.setUpperDate( LocalDate.of( 2015, 01, 05 ) );
		methodsFilter.setUpperTime( getCalendarFor( 10, 10 ) );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 4 ) );

		methodsFilter.setUpperDate( LocalDate.of( 2015, 01, 04 ) );
		methodsFilter.setUpperTime( getCalendarFor( 15, 26 ) );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 4 ) );

		methodsFilter.setUpperDate( null );
		methodsFilter.setUpperTime( null );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 4 ) );

		methodsFilter.setUpperDate( LocalDate.of( 2015, 01, 01 ) );
		methodsFilter.setUpperTime( null );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 3 ) );

		methodsFilter.setUpperDate( null );
		methodsFilter.setUpperTime( getCalendarFor( 15, 25 ) );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 4 ) );

		methodsFilter.setUpperDate( null );
		methodsFilter.setUpperTime( getCalendarFor( 15, 20 ) );
		assertThat( ivMethodsService.searchMethods( methodsFilter ).size( ), is( 3 ) );
	}

	private Calendar getCalendarFor( final int aHour, final int aMinute ) {
		final Calendar calendar = Calendar.getInstance( );
		calendar.set( Calendar.HOUR_OF_DAY, aHour );
		calendar.set( Calendar.MINUTE, aMinute );
		return calendar;
	}

	private void createMethodCall( final String aHost, final String aClazz, final String aMethod, final String aException ) {
		final MethodCall methodCall = new MethodCall( );
		methodCall.setHost( aHost );
		methodCall.setClazz( aClazz );
		methodCall.setMethod( aMethod );
		methodCall.setException( aException );

		ivDataService.getMethods( ).add( methodCall );
	}

	private void createMethodCall( final long aTraceId ) {
		final MethodCall methodCall = new MethodCall( );
		methodCall.setTraceId( aTraceId );

		ivDataService.getMethods( ).add( methodCall );
	}

	private void createMethodCall( final int aYear, final int aMonth, final int aDay, final int aHour, final int aMinute ) {
		final Calendar calendar = Calendar.getInstance( );
		calendar.set( aYear, aMonth - 1, aDay, aHour, aMinute, 0 );

		final MethodCall methodCall = new MethodCall( );
		methodCall.setTimestamp( calendar.getTimeInMillis( ) );

		ivDataService.getMethods( ).add( methodCall );
	}

}
