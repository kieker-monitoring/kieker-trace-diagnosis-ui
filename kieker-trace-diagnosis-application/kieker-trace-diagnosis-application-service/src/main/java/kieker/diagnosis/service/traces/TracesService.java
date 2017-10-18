package kieker.diagnosis.service.traces;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.inject.Singleton;

import kieker.diagnosis.architecture.service.ServiceBase;
import kieker.diagnosis.service.data.MethodCall;
import kieker.diagnosis.service.data.MonitoringLogService;
import kieker.diagnosis.service.filter.FilterService;

@Singleton
public class TracesService extends ServiceBase {

	/**
	 * This method searches, based on the given filter, for traces within the imported monitoring log.
	 *
	 * @param aFilter
	 *            The filter to apply to the traces.
	 *
	 * @return A new list containing all available traces matching the filter. Only the root method calls of the traces are returned.
	 */
	public List<MethodCall> searchTraces( final TracesFilter aFilter ) {
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

		Predicate<MethodCall> predicate = filterService.conjunct( predicates );

		// If we should search the whole trace, we have to apply the predicate recursive
		if ( aFilter.isSearchWholeTrace( ) ) {
			predicate = recursive( predicate );
		}

		// Get all trace roots...
		final MonitoringLogService monitoringLogService = getService( MonitoringLogService.class );
		final List<MethodCall> traceRoots = monitoringLogService.getTraceRoots( );

		// ...and apply the filter to each of the traces
		final List<MethodCall> filteredTraceRoots = traceRoots.parallelStream( ).filter( predicate ).collect( Collectors.toList( ) );

		return filteredTraceRoots;
	}

	private Predicate<MethodCall> getSearchTypePredicate( final SearchType aSearchType ) {
		return method -> {
			final boolean failedCall = method.getException( ) != null;
			return aSearchType == SearchType.ALL || aSearchType == SearchType.ONLY_FAILED && failedCall || aSearchType == SearchType.ONLY_SUCCESSFUL && !failedCall;
		};
	}

	private Predicate<MethodCall> recursive( final Predicate<MethodCall> aPredicate ) {
		return t -> {
			// We start with the trace root
			final Stack<MethodCall> methods = new Stack<>( );
			methods.push( t );

			while ( !methods.isEmpty( ) ) {
				final MethodCall methodCall = methods.pop( );

				// If the current method matches, we are finished. Otherwise we search in the children.
				if ( aPredicate.test( methodCall ) ) {
					return true;
				} else {
					methods.addAll( methodCall.getChildren( ) );
				}
			}

			// If we are here, we did not find a match in the whole trace
			return false;
		};
	}

	public int countTraces( ) {
		final MonitoringLogService monitoringLogService = getService( MonitoringLogService.class );
		return monitoringLogService.getTraceRoots( ).size( );
	}

}
