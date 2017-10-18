package kieker.diagnosis.service.aggregatedmethods;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.inject.Singleton;

import kieker.diagnosis.architecture.service.ServiceBase;
import kieker.diagnosis.service.data.AggregatedMethodCall;
import kieker.diagnosis.service.data.MonitoringLogService;
import kieker.diagnosis.service.filter.FilterService;

@Singleton
public class AggregatedMethodsService extends ServiceBase {

	/**
	 * This method searches, based on the given filter, for aggregated method calls within the imported monitoring log.
	 *
	 * @param aFilter
	 *            The filter to apply to the method calls.
	 *
	 * @return A new list containing all available aggregated method calls matching the filter.
	 */
	public List<AggregatedMethodCall> searchMethods( final AggregatedMethodsFilter aFilter ) {
		// Prepare the predicates
		final FilterService filterService = getService( FilterService.class );

		final List<Predicate<AggregatedMethodCall>> predicates = new ArrayList<>( );
		predicates.add( filterService.getStringPredicate( AggregatedMethodCall::getHost, aFilter.getHost( ), aFilter.isUseRegExpr( ) ) );
		predicates.add( filterService.getStringPredicate( AggregatedMethodCall::getClazz, aFilter.getClazz( ), aFilter.isUseRegExpr( ) ) );
		predicates.add( filterService.getStringPredicate( AggregatedMethodCall::getMethod, aFilter.getMethod( ), aFilter.isUseRegExpr( ) ) );
		predicates.add( filterService.getStringPredicate( AggregatedMethodCall::getException, aFilter.getException( ), aFilter.isUseRegExpr( ) ) );
		predicates.add( getSearchTypePredicate( aFilter.getSearchType( ) ) );

		final Predicate<AggregatedMethodCall> predicate = filterService.conjunct( predicates );

		// Get the methods
		final MonitoringLogService monitoringLogService = getService( MonitoringLogService.class );
		final List<AggregatedMethodCall> methods = monitoringLogService.getAggreatedMethods( );

		// Filter the methods
		final List<AggregatedMethodCall> filteredMethods = methods.parallelStream( ).filter( predicate ).collect( Collectors.toList( ) );
		return filteredMethods;
	}

	private Predicate<AggregatedMethodCall> getSearchTypePredicate( final SearchType aSearchType ) {
		return method -> {
			final boolean failedCall = method.getException( ) != null;
			return aSearchType == SearchType.ALL || aSearchType == SearchType.ONLY_FAILED && failedCall || aSearchType == SearchType.ONLY_SUCCESSFUL && !failedCall;
		};
	}

	public int countMethods( ) {
		final MonitoringLogService monitoringLogService = getService( MonitoringLogService.class );
		final List<AggregatedMethodCall> methods = monitoringLogService.getAggreatedMethods( );
		return methods.size( );
	}

}
