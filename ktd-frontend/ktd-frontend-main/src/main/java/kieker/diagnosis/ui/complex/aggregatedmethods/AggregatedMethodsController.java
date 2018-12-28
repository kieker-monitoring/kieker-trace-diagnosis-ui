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

package kieker.diagnosis.ui.complex.aggregatedmethods;

import java.util.List;

import com.google.inject.Singleton;

import kieker.diagnosis.backend.base.exception.BusinessException;
import kieker.diagnosis.backend.base.exception.BusinessRuntimeException;
import kieker.diagnosis.backend.data.AggregatedMethodCall;
import kieker.diagnosis.backend.export.CSVData;
import kieker.diagnosis.backend.search.aggregatedmethods.AggregatedMethodsFilter;
import kieker.diagnosis.backend.search.aggregatedmethods.AggregatedMethodsService;
import kieker.diagnosis.backend.settings.SettingsService;
import kieker.diagnosis.frontend.base.ui.ControllerBase;
import kieker.diagnosis.ui.complex.main.MainController;

/**
 * The controller of the aggregated methods tab.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
class AggregatedMethodsController extends ControllerBase<AggregatedMethodsViewModel> {

	private List<AggregatedMethodCall> ivMethods;
	private String ivDurationSuffix;
	private int ivTotalMethods;

	/**
	 * This action is performed once during the application's start.
	 */
	public void performInitialize( ) {
		getViewModel( ).updatePresentationStatus( 0, 0 );
		getViewModel( ).updatePresentationFilter( new AggregatedMethodsFilter( ) );
		getViewModel( ).updatePresentationDetails( null );
	}

	public void performSearch( ) {
		try {
			// Get the filter input from the user
			final AggregatedMethodsFilter filter = getViewModel( ).savePresentationFilter( );

			// Find the methods to display
			final AggregatedMethodsService methodsService = getService( AggregatedMethodsService.class );
			final List<AggregatedMethodCall> methods = methodsService.searchMethods( filter );
			final int totalMethods = methodsService.countMethods( );

			// Update the view
			getViewModel( ).updatePresentationMethods( methods );
			getViewModel( ).updatePresentationStatus( methods.size( ), totalMethods );
		} catch ( final BusinessException ex ) {
			throw new BusinessRuntimeException( ex );
		}

	}

	public void performPrepareRefresh( ) {
		// Get the data
		final AggregatedMethodsService methodsService = getService( AggregatedMethodsService.class );
		ivMethods = methodsService.searchMethods( new AggregatedMethodsFilter( ) );
		ivTotalMethods = methodsService.countMethods( );

		final SettingsService settingsService = getService( SettingsService.class );
		ivDurationSuffix = settingsService.getCurrentDurationSuffix( );
	}

	/**
	 * This action is performed, when a refresh of the view is required
	 */
	public void performRefresh( ) {
		// Reset the filter
		final AggregatedMethodsFilter filter = new AggregatedMethodsFilter( );
		getViewModel( ).updatePresentationFilter( filter );

		// Update the table
		getViewModel( ).updatePresentationMethods( ivMethods );
		getViewModel( ).updatePresentationStatus( ivMethods.size( ), ivTotalMethods );

		// Update the column header of the table
		getViewModel( ).updatePresentationDurationColumnHeader( ivDurationSuffix );
	}

	public void performSetParameter( final Object aParameter ) {
		if ( aParameter instanceof AggregatedMethodsFilter ) {
			final AggregatedMethodsFilter filter = (AggregatedMethodsFilter) aParameter;
			getViewModel( ).updatePresentationFilter( filter );

			performSearch( );
		}
	}

	public void performSaveAsFavorite( ) {
		try {
			final AggregatedMethodsFilter filter = getViewModel( ).savePresentationFilter( );
			getController( MainController.class ).performSaveAsFavorite( AggregatedMethodsView.class, filter );
		} catch ( final BusinessException ex ) {
			throw new BusinessRuntimeException( ex );
		}
	}

	public void performSelectionChange( ) {
		final AggregatedMethodCall methodCall = getViewModel( ).getSelected( );
		getViewModel( ).updatePresentationDetails( methodCall );
	}

	public void performJumpToMethods( ) {
		final AggregatedMethodCall methodCall = getViewModel( ).getSelected( );

		if ( methodCall != null ) {
			getController( MainController.class ).performJumpToMethods( methodCall );
		}
	}

	public void performExportToCSV( ) {
		final CSVData csvData = getViewModel( ).savePresentationAsCSV( );
		getController( MainController.class ).performExportToCSV( csvData );
	}

}
