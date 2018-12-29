/***************************************************************************
 * Copyright 2015-2018 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.frontend.complex.methods;

import java.util.List;

import com.google.inject.Singleton;

import kieker.diagnosis.backend.base.exception.BusinessException;
import kieker.diagnosis.backend.base.exception.BusinessRuntimeException;
import kieker.diagnosis.backend.data.AggregatedMethodCall;
import kieker.diagnosis.backend.data.MethodCall;
import kieker.diagnosis.backend.export.CSVData;
import kieker.diagnosis.backend.search.methods.MethodsFilter;
import kieker.diagnosis.backend.search.methods.MethodsService;
import kieker.diagnosis.backend.search.methods.SearchType;
import kieker.diagnosis.backend.settings.SettingsService;
import kieker.diagnosis.frontend.base.ui.ControllerBase;
import kieker.diagnosis.frontend.complex.main.MainController;

/**
 * The controller of the methods tab.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
class MethodsController extends ControllerBase<MethodsViewModel> {

	private List<MethodCall> ivMethods;
	private String ivDurationSuffix;
	private int ivTotalMethods;

	/**
	 * This action is performed once during the application's start.
	 */
	public void performInitialize( ) {
		getViewModel( ).updatePresentationDetails( null );
		getViewModel( ).updatePresentationStatus( 0, 0 );
		getViewModel( ).updatePresentationFilter( new MethodsFilter( ) );
	}

	public void performSearch( ) {
		try {
			// Get the filter input from the user
			final MethodsFilter filter = getViewModel( ).savePresentationFilter( );

			// Find the methods to display
			final MethodsService methodsService = getService( MethodsService.class );
			final List<MethodCall> methods = methodsService.searchMethods( filter );
			final int totalMethods = methodsService.countMethods( );

			// Update the view
			getViewModel( ).updatePresentationMethods( methods );
			getViewModel( ).updatePresentationStatus( methods.size( ), totalMethods );
		} catch ( final BusinessException ex ) {
			throw new BusinessRuntimeException( ex );
		}

	}

	public void performPrepareRefresh( ) {
		final MethodsService methodsService = getService( MethodsService.class );
		ivMethods = methodsService.searchMethods( new MethodsFilter( ) );
		ivTotalMethods = methodsService.countMethods( );

		final SettingsService settingsService = getService( SettingsService.class );
		ivDurationSuffix = settingsService.getCurrentDurationSuffix( );
	}

	/**
	 * This action is performed, when a refresh of the view is required
	 */
	public void performRefresh( ) {
		// Reset the filter
		final MethodsFilter filter = new MethodsFilter( );
		getViewModel( ).updatePresentationFilter( filter );

		// Update the table
		getViewModel( ).updatePresentationMethods( ivMethods );
		getViewModel( ).updatePresentationStatus( ivMethods.size( ), ivTotalMethods );

		// Update the column header of the table
		getViewModel( ).updatePresentationDurationColumnHeader( ivDurationSuffix );
	}

	/**
	 * This action is performed, when the selection of the table changes
	 */
	public void performSelectionChange( ) {
		final MethodCall methodCall = getViewModel( ).getSelected( );
		getViewModel( ).updatePresentationDetails( methodCall );
	}

	public void performJumpToTrace( ) {
		final MethodCall methodCall = getViewModel( ).getSelected( );

		if ( methodCall != null ) {
			getController( MainController.class ).performJumpToTrace( methodCall );
		}
	}

	public void performSaveAsFavorite( ) {
		try {
			final MethodsFilter filter = getViewModel( ).savePresentationFilter( );
			getController( MainController.class ).performSaveAsFavorite( MethodsView.class, filter );
		} catch ( final BusinessException ex ) {
			throw new BusinessRuntimeException( ex );
		}
	}

	public void performSetParameter( final Object aParameter ) {
		if ( aParameter instanceof AggregatedMethodCall ) {
			final AggregatedMethodCall methodCall = (AggregatedMethodCall) aParameter;

			// We have to prepare a filter which maches only the method call
			final MethodsFilter filter = new MethodsFilter( );
			filter.setHost( methodCall.getHost( ) );
			filter.setClazz( methodCall.getClazz( ) );
			filter.setMethod( methodCall.getMethod( ) );
			filter.setException( methodCall.getException( ) );
			filter.setSearchType( methodCall.getException( ) != null ? SearchType.ONLY_FAILED : SearchType.ONLY_SUCCESSFUL );

			getViewModel( ).updatePresentationFilter( filter );

			// Now we can perform the actual search
			performSearch( );
		}

		if ( aParameter instanceof MethodsFilter ) {
			final MethodsFilter filter = (MethodsFilter) aParameter;
			getViewModel( ).updatePresentationFilter( filter );

			performSearch( );
		}
	}

	public void performExportToCSV( ) {
		final CSVData csvData = getViewModel( ).savePresentationAsCSV( );
		getController( MainController.class ).performExportToCSV( csvData );
	}

}
