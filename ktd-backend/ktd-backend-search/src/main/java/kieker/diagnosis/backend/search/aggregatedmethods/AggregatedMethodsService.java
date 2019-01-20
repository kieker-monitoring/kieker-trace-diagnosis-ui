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

package kieker.diagnosis.backend.search.aggregatedmethods;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import kieker.diagnosis.backend.base.service.Service;
import kieker.diagnosis.backend.data.AggregatedMethodCall;
import kieker.diagnosis.backend.data.reader.Repository;
import kieker.diagnosis.backend.filter.FilterService;

/**
 * This is the service responsible for searching aggregated method calls within the imported monitoring log.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class AggregatedMethodsService implements Service {

	@Inject
	private FilterService filterService;

	@Inject
	private Repository repository;

	/**
	 * This method searches, based on the given filter, for aggregated method calls within the imported monitoring log.
	 *
	 * @param filter
	 *            The filter to apply to the method calls.
	 *
	 * @return A new list containing all available aggregated method calls matching the filter.
	 */
	public List<AggregatedMethodCall> searchMethods( final AggregatedMethodsFilter filter ) {
		// Get the methods
		final List<AggregatedMethodCall> methods = repository.getAggreatedMethods( );

		// Filter the methods
		return methods
				.parallelStream( )
				.filter( filterService.createStringPredicate( AggregatedMethodCall::getHost, filter.getHost( ), filter.isUseRegExpr( ) ) )
				.filter( filterService.createStringPredicate( AggregatedMethodCall::getClazz, filter.getClazz( ), filter.isUseRegExpr( ) ) )
				.filter( filterService.createStringPredicate( AggregatedMethodCall::getMethod, filter.getMethod( ), filter.isUseRegExpr( ) ) )
				.filter( filterService.createStringPredicate( AggregatedMethodCall::getException, filter.getException( ), filter.isUseRegExpr( ) ) )
				.filter( getSearchTypePredicate( filter.getSearchType( ) ) )
				.collect( Collectors.toList( ) );
	}

	private Predicate<AggregatedMethodCall> getSearchTypePredicate( final SearchType searchType ) {
		return method -> {
			final boolean failedCall = method.getException( ) != null;
			return searchType == SearchType.ALL || searchType == SearchType.ONLY_FAILED && failedCall || searchType == SearchType.ONLY_SUCCESSFUL && !failedCall;
		};
	}

	/**
	 * This method counts the number of all aggregated method calls within the imported monitoring log.
	 *
	 * @return The number of all aggregated method calls.
	 */
	public int countMethods( ) {
		final List<AggregatedMethodCall> methods = repository.getAggreatedMethods( );
		return methods.size( );
	}

}
