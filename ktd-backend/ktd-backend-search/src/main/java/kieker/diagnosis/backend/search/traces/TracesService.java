/***************************************************************************
 * Copyright 2015-2019 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.backend.search.traces;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import kieker.diagnosis.backend.base.service.Service;
import kieker.diagnosis.backend.data.MethodCall;
import kieker.diagnosis.backend.data.reader.Repository;
import kieker.diagnosis.backend.filter.FilterService;
import lombok.RequiredArgsConstructor;

/**
 * This is the service responsible for searching traces (or root method calls) within the imported monitoring log.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
@RequiredArgsConstructor ( onConstructor = @__ ( @Inject ) )
public class TracesService implements Service {

	private final Repository repository;
	private final FilterService filterService;

	/**
	 * This method searches, based on the given filter, for traces within the imported monitoring log.
	 *
	 * @param filter
	 *            The filter to apply to the traces.
	 *
	 * @return A new list containing all available traces matching the filter. Only the root method calls of the traces
	 *         are returned.
	 */
	public List<MethodCall> searchTraces( final TracesFilter filter ) {
		// Prepare the predicates
		final List<Predicate<MethodCall>> predicates = new ArrayList<>( );
		predicates.add( filterService.createStringPredicate( MethodCall::getHost, filter.getHost( ), filter.isUseRegExpr( ) ) );
		predicates.add( filterService.createStringPredicate( MethodCall::getClazz, filter.getClazz( ), filter.isUseRegExpr( ) ) );
		predicates.add( filterService.createStringPredicate( MethodCall::getMethod, filter.getMethod( ), filter.isUseRegExpr( ) ) );
		predicates.add( filterService.createStringPredicate( MethodCall::getException, filter.getException( ), filter.isUseRegExpr( ) ) );
		predicates.add( filterService.createLongPredicate( MethodCall::getTraceId, filter.getTraceId( ) ) );
		predicates.add( getSearchTypePredicate( filter.getSearchType( ) ) );
		predicates.add( filterService.createAfterTimePredicate( MethodCall::getTimestamp, filter.getLowerDate( ), filter.getLowerTime( ) ) );
		predicates.add( filterService.createBeforeTimePredicate( MethodCall::getTimestamp, filter.getUpperDate( ), filter.getUpperTime( ) ) );

		Predicate<MethodCall> predicate = filterService.conjunct( predicates );

		// If we should search the whole trace, we have to apply the predicate recursive
		if ( filter.isSearchWholeTrace( ) ) {
			predicate = recursive( predicate );
		}

		// Get all trace roots...
		final List<MethodCall> traceRoots = repository.getTraceRoots( );

		// ...and apply the filter to each of the traces
		return traceRoots
				.parallelStream( )
				.filter( predicate )
				.collect( Collectors.toList( ) );
	}

	private Predicate<MethodCall> getSearchTypePredicate( final SearchType searchType ) {
		return method -> {
			final boolean failedCall = method.getException( ) != null;
			return searchType == SearchType.ALL || searchType == SearchType.ONLY_FAILED && failedCall || searchType == SearchType.ONLY_SUCCESSFUL && !failedCall;
		};
	}

	private Predicate<MethodCall> recursive( final Predicate<MethodCall> predicate ) {
		return t -> {
			// We start with the trace root
			final Stack<MethodCall> methods = new Stack<>( );
			methods.push( t );

			while ( !methods.isEmpty( ) ) {
				final MethodCall methodCall = methods.pop( );

				// If the current method matches, we are finished. Otherwise we search in the children.
				if ( predicate.test( methodCall ) ) {
					return true;
				} else {
					methods.addAll( methodCall.getChildren( ) );
				}
			}

			// If we are here, we did not find a match in the whole trace
			return false;
		};
	}

	/**
	 * This method counts the number of all traces within the imported monitoring log.
	 *
	 * @return The number of all traces.
	 */
	public int countTraces( ) {
		return repository.getTraceRoots( ).size( );
	}

}
