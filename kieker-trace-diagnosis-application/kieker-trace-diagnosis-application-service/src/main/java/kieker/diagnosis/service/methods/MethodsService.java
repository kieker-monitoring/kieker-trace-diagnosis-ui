package kieker.diagnosis.service.methods;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.inject.Singleton;

import kieker.diagnosis.architecture.service.ServiceBase;
import kieker.diagnosis.service.data.MethodCall;
import kieker.diagnosis.service.data.MonitoringLogService;
import kieker.diagnosis.service.filter.FilterService;

@Singleton
public class MethodsService extends ServiceBase {

	public List<MethodCall> searchMethods( final MethodsFilter aFilter ) {
		// Prepare the predicates
		final FilterService filterService = getService( FilterService.class );

		final List<Predicate<MethodCall>> predicates = new ArrayList<>( );
		predicates.add( filterService.getStringPredicate( MethodCall::getHost, aFilter.getHost( ), aFilter.isUseRegExpr( ) ) );
		predicates.add( filterService.getStringPredicate( MethodCall::getClazz, aFilter.getClazz( ), aFilter.isUseRegExpr( ) ) );
		predicates.add( filterService.getStringPredicate( MethodCall::getMethod, aFilter.getMethod( ), aFilter.isUseRegExpr( ) ) );
		predicates.add( filterService.getStringPredicate( MethodCall::getException, aFilter.getException( ), aFilter.isUseRegExpr( ) ) );
		predicates.add( filterService.getLongPredicate( MethodCall::getTraceId, aFilter.getTraceId( ) ) );
		predicates.add( getSearchTypePredicate( aFilter.getSearchType( ) ) );
		predicates.add( filterService.getAfterTimePredicate( MethodCall::getTimestamp, aFilter.getLowerDate( ), aFilter.getLowerTime( ) ) );
		predicates.add( filterService.getBeforeTimePredicate( MethodCall::getTimestamp, aFilter.getUpperDate( ), aFilter.getUpperTime( ) ) );

		final Predicate<MethodCall> predicate = filterService.conjunct( predicates );

		// Get the methods
		final MonitoringLogService monitoringLogService = getService( MonitoringLogService.class );
		final List<MethodCall> methods = monitoringLogService.getMethods( );

		// Filter the methods
		final List<MethodCall> filteredMethods = methods.parallelStream( ).filter( predicate ).collect( Collectors.toList( ) );
		return filteredMethods;
	}

	private Predicate<MethodCall> getSearchTypePredicate( final SearchType aSearchType ) {
		return method -> {
			final boolean failedCall = method.getException( ) != null;
			return aSearchType == SearchType.ALL || aSearchType == SearchType.ONLY_FAILED && failedCall || aSearchType == SearchType.ONLY_SUCCESSFUL && !failedCall;
		};
	}

	public int countMethods( ) {
		final MonitoringLogService monitoringLogService = getService( MonitoringLogService.class );
		final List<MethodCall> methods = monitoringLogService.getMethods( );
		return methods.size( );
	}

}
