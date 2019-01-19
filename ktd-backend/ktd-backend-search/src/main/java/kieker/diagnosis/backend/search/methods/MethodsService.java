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

package kieker.diagnosis.backend.search.methods;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import kieker.diagnosis.backend.base.service.Service;
import kieker.diagnosis.backend.data.MethodCall;
import kieker.diagnosis.backend.data.reader.Repository;
import kieker.diagnosis.backend.filter.FilterService;

/**
 * This is the service responsible for searching method calls within the imported monitoring log.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class MethodsService implements Service {

	@Inject
	private Repository repository;

	@Inject
	private FilterService filterService;

	/**
	 * This method searches, based on the given filter, for method calls within the imported monitoring log.
	 *
	 * @param aFilter
	 *            The filter to apply to the method calls.
	 *
	 * @return A new list containing all available method calls matching the filter.
	 */
	public List<MethodCall> searchMethods( final MethodsFilter aFilter ) {
		// Get the methods
		final List<MethodCall> methods = repository.getMethods( );

		// Filter the methods
		return methods
				.parallelStream( )
				.filter( filterService.createStringPredicate( MethodCall::getHost, aFilter.getHost( ), aFilter.isUseRegExpr( ) ) )
				.filter( filterService.createStringPredicate( MethodCall::getClazz, aFilter.getClazz( ), aFilter.isUseRegExpr( ) ) )
				.filter( filterService.createStringPredicate( MethodCall::getMethod, aFilter.getMethod( ), aFilter.isUseRegExpr( ) ) )
				.filter( filterService.createStringPredicate( MethodCall::getException, aFilter.getException( ), aFilter.isUseRegExpr( ) ) )
				.filter( filterService.createLongPredicate( MethodCall::getTraceId, aFilter.getTraceId( ) ) )
				.filter( getSearchTypePredicate( aFilter.getSearchType( ) ) )
				.filter( filterService.createAfterTimePredicate( MethodCall::getTimestamp, aFilter.getLowerDate( ), aFilter.getLowerTime( ) ) )
				.filter( filterService.createBeforeTimePredicate( MethodCall::getTimestamp, aFilter.getUpperDate( ), aFilter.getUpperTime( ) ) )
				.collect( Collectors.toList( ) );
	}

	private Predicate<MethodCall> getSearchTypePredicate( final SearchType aSearchType ) {
		return method -> {
			final boolean failedCall = method.getException( ) != null;
			return aSearchType == SearchType.ALL || aSearchType == SearchType.ONLY_FAILED && failedCall || aSearchType == SearchType.ONLY_SUCCESSFUL && !failedCall;
		};
	}

	/**
	 * This method counts the number of all method calls within the imported monitoring log.
	 *
	 * @return The number of all method calls.
	 */
	public int countMethods( ) {
		final List<MethodCall> methods = repository.getMethods( );
		return methods.size( );
	}

}
