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

package kieker.diagnosis.frontend.tab.traces;

import java.util.List;
import java.util.function.BiConsumer;

import com.google.inject.Singleton;

import kieker.diagnosis.backend.base.exception.BusinessException;
import kieker.diagnosis.backend.base.exception.BusinessRuntimeException;
import kieker.diagnosis.backend.data.MethodCall;
import kieker.diagnosis.backend.search.traces.TracesFilter;
import kieker.diagnosis.backend.search.traces.TracesService;
import kieker.diagnosis.backend.settings.SettingsService;
import kieker.diagnosis.frontend.base.ui.ControllerBase;
import kieker.diagnosis.frontend.base.ui.ViewBase;

/**
 * The controller of the traces tab.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class TracesController extends ControllerBase<TracesViewModel> {

	private List<MethodCall> ivTraceRoots;
	private int ivTotalTraces;
	private String ivDurationSuffix;
	private BiConsumer<Class<? extends ViewBase<?>>, Object> onPerformSaveAsFavorite;

	/**
	 * This action is performed once during the application's start.
	 */
	public void performInitialize( ) {
		getViewModel( ).updatePresentationDetails( null );
		getViewModel( ).updatePresentationStatus( 0, 0 );
		getViewModel( ).updatePresentationFilter( new TracesFilter( ) );
	}

	/**
	 * This action is performed when settings or data are changed and the view has to be refreshed. The actual refresh is only performed when {@link #performRefresh()} is called. This method prepares only the refresh.
	 */
	public void performPrepareRefresh( ) {
		// Find the trace roots to display
		final TracesService tracesService = getService( TracesService.class );
		ivTraceRoots = tracesService.searchTraces( new TracesFilter( ) );
		ivTotalTraces = tracesService.countTraces( );

		// Get the duration suffix
		final SettingsService settingsService = getService( SettingsService.class );
		ivDurationSuffix = settingsService.getCurrentDurationSuffix( );
	}

	/**
	 * This action is performed, when a refresh of the view is required. The preparation of the refresh is performed in {@link #performPrepareRefresh()}.
	 */
	public void performRefresh( ) {
		// Reset the filter
		final TracesFilter filter = new TracesFilter( );
		getViewModel( ).updatePresentationFilter( filter );

		// Update the view
		getViewModel( ).updatePresentationTraces( ivTraceRoots );
		getViewModel( ).updatePresentationStatus( ivTraceRoots.size( ), ivTotalTraces );

		// Update the column header of the table
		getViewModel( ).updatePresentationDurationColumnHeader( ivDurationSuffix );
	}

	/**
	 * This action is performed, when the selection of the table changes.
	 */
	public void performSelectionChange( ) {
		final MethodCall methodCall = getViewModel( ).getSelected( );
		getViewModel( ).updatePresentationDetails( methodCall );
	}

	/**
	 * This action is performed, when the user wants to perform a search.
	 */
	public void performSearch( ) {
		try {
			// Get the filter input from the user
			final TracesFilter filter = getViewModel( ).savePresentationFilter( );

			// Find the trace roots to display
			final TracesService tracesService = getService( TracesService.class );
			final List<MethodCall> traceRoots = tracesService.searchTraces( filter );
			final int totalTraces = tracesService.countTraces( );

			// Update the view
			getViewModel( ).updatePresentationTraces( traceRoots );
			getViewModel( ).updatePresentationStatus( traceRoots.size( ), totalTraces );
		} catch ( final BusinessException ex ) {
			throw new BusinessRuntimeException( ex );
		}

	}

	/**
	 * This action is performed, when someone requested a jump into the traces tab. The real action is determined based on the type of the parameter. If the parameter is a {@link TracesFilter}, the filter is simply applied. If the parameter is a {@link MethodCall}, the trace for the method call is shown and the view tries to navigate directly to the method call. If the method call is not visible, the trace is still shown.
	 *
	 * @param aParameter The parameter.
	 */
	public void performSetParameter( final Object aParameter ) {
		if ( aParameter instanceof MethodCall ) {
			final MethodCall methodCall = ( MethodCall ) aParameter;

			// We are supposed to jump to the method call
			final TracesFilter filter = new TracesFilter( );
			filter.setTraceId( methodCall.getTraceId( ) );
			getViewModel( ).updatePresentationFilter( filter );
			performSearch( );

			// Now let us see, whether we can find the method call
			getViewModel( ).select( methodCall );
		}

		if ( aParameter instanceof TracesFilter ) {
			final TracesFilter filter = ( TracesFilter ) aParameter;
			getViewModel( ).updatePresentationFilter( filter );

			performSearch( );
		}
	}

	public void setOnPerformSaveAsFavorite( final BiConsumer<Class<? extends ViewBase<?>>, Object> action ) {
		onPerformSaveAsFavorite = action;
	}

	/**
	 * This action is performed, when the user wants to save the current filter as a filter favorite.
	 */
	public void performSaveAsFavorite( ) {
		try {
			// We simply save the filter's content and delegate to the main controller.
			final TracesFilter filter = getViewModel( ).savePresentationFilter( );
			onPerformSaveAsFavorite.accept( TracesView.class, filter );
		} catch ( final BusinessException ex ) {
			throw new BusinessRuntimeException( ex );
		}
	}

}
