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

package kieker.diagnosis.ui.tabs.traces;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Stack;

import com.google.inject.Singleton;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Command;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TreeItem;
import kieker.diagnosis.architecture.exception.BusinessException;
import kieker.diagnosis.architecture.service.properties.PropertiesService;
import kieker.diagnosis.architecture.ui.ViewModelBase;
import kieker.diagnosis.service.data.MethodCall;
import kieker.diagnosis.service.pattern.PatternService;
import kieker.diagnosis.service.settings.MethodCallAggregation;
import kieker.diagnosis.service.settings.SettingsService;
import kieker.diagnosis.service.settings.properties.MaxNumberOfMethodCallsProperty;
import kieker.diagnosis.service.settings.properties.MethodCallAggregationProperty;
import kieker.diagnosis.service.settings.properties.MethodCallThresholdProperty;
import kieker.diagnosis.service.settings.properties.ShowUnmonitoredTimeProperty;
import kieker.diagnosis.service.traces.SearchType;
import kieker.diagnosis.service.traces.TracesFilter;
import kieker.diagnosis.service.traces.TracesService;
import kieker.diagnosis.ui.main.MainController;
import kieker.diagnosis.ui.scopes.MainScope;
import kieker.diagnosis.ui.tabs.traces.aggregator.Aggregator;
import kieker.diagnosis.ui.tabs.traces.aggregator.DurationAggregator;
import kieker.diagnosis.ui.tabs.traces.aggregator.IdentityAggregator;
import kieker.diagnosis.ui.tabs.traces.aggregator.ThresholdAggregator;
import kieker.diagnosis.ui.tabs.traces.aggregator.TraceDepthAggregator;
import kieker.diagnosis.ui.tabs.traces.aggregator.TraceSizeAggregator;
import kieker.diagnosis.ui.tabs.traces.components.MethodCallTreeItem;

/**
 * The view model of the traces tab.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class TracesViewModel extends ViewModelBase<TracesView> implements ViewModel {

	public static final String EVENT_SELECT_TREE_ITEM = "EVENT_SELECT_TREE_ITEM";

	@InjectScope
	private MainScope ivMainScope;

	private final Command ivSearchCommand = createCommand( this::performSearch );
	private final Command ivSaveAsFavoriteCommand = createCommand( this::performSaveAsFavorite );
	private final Command ivSelectionChangeCommand = createCommand( this::performSelectionChange );
	private final Command ivRefreshCommand = createCommand( this::performRefresh );
	private final Command ivPrepareRefreshCommand = createCommand( this::performPrepareRefresh );

	// Filter
	private final StringProperty ivFilterHostProperty = new SimpleStringProperty( );
	private final StringProperty ivFilterClassProperty = new SimpleStringProperty( );
	private final StringProperty ivFilterMethodProperty = new SimpleStringProperty( );
	private final StringProperty ivFilterExceptionProperty = new SimpleStringProperty( );
	private final ObjectProperty<Long> ivFilterTraceIdProperty = new SimpleObjectProperty<>( );
	private final BooleanProperty ivFilterUseRegExprProperty = new SimpleBooleanProperty( );

	private final ObjectProperty<LocalDate> ivFilterLowerDateProperty = new SimpleObjectProperty<>( );
	private final ObjectProperty<LocalTime> ivFilterLowerTimeProperty = new SimpleObjectProperty<>( );
	private final ObjectProperty<LocalDate> ivFilterUpperDateProperty = new SimpleObjectProperty<>( );
	private final ObjectProperty<LocalTime> ivFilterUpperTimeProperty = new SimpleObjectProperty<>( );
	private final BooleanProperty ivFilterSearchWholeTraceProperty = new SimpleBooleanProperty( );
	private final ObjectProperty<SearchType> ivFilterSearchTypeProperty = new SimpleObjectProperty<>( );

	// Table
	private final ObjectProperty<TreeItem<MethodCall>> ivRootMethodCallProperty = new SimpleObjectProperty<>( );
	private final ObjectProperty<TreeItem<MethodCall>> ivSelectedMethodCallProperty = new SimpleObjectProperty<>( );
	private final StringProperty ivDurationColumnHeaderProperty = new SimpleStringProperty( );

	// Details
	private final StringProperty ivDetailsHostProperty = new SimpleStringProperty( );
	private final StringProperty ivDetailsClassProperty = new SimpleStringProperty( );
	private final StringProperty ivDetailsMethodProperty = new SimpleStringProperty( );
	private final StringProperty ivDetailsExceptionProperty = new SimpleStringProperty( );
	private final StringProperty ivDetailsTraceDepthProperty = new SimpleStringProperty( );
	private final StringProperty ivDetailsTraceSizeProperty = new SimpleStringProperty( );
	private final StringProperty ivDetailsDurationProperty = new SimpleStringProperty( );
	private final StringProperty ivDetailsPercentProperty = new SimpleStringProperty( );
	private final StringProperty ivDetailsTimestampProperty = new SimpleStringProperty( );
	private final StringProperty ivDetailsTraceIdProperty = new SimpleStringProperty( );

	private final StringProperty ivStatusLabelProperty = new SimpleStringProperty( );

	// Temporary variables
	private List<MethodCall> ivTraceRoots;
	private int ivTotalTraces;
	private String ivDurationSuffix;

	/**
	 * This action is performed once during the application's start.
	 */
	public void initialize( ) {
		updatePresentationDetails( null );
		updatePresentationStatus( 0, 0 );
		updatePresentationFilter( new TracesFilter( ) );

		ivMainScope.subscribe( MainScope.EVENT_REFRESH, ( aKey, aPayload ) -> ivRefreshCommand.execute( ) );
		ivMainScope.subscribe( MainScope.EVENT_PREPARE_REFRESH, ( aKey, aPayload ) -> ivPrepareRefreshCommand.execute( ) );
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
		updatePresentationFilter( filter );

		// Update the view
		updatePresentationTraces( ivTraceRoots );
		updatePresentationStatus( ivTraceRoots.size( ), ivTotalTraces );

		// Update the column header of the table
		updatePresentationDurationColumnHeader( ivDurationSuffix );
	}

	Command getSelectionChangeCommand( ) {
		return ivSelectionChangeCommand;
	}

	/**
	 * This action is performed, when the selection of the table changes.
	 */
	public void performSelectionChange( ) {
		final MethodCall methodCall = getSelected( );
		updatePresentationDetails( methodCall );
	}

	Command getSearchCommand( ) {
		return ivSearchCommand;
	}

	/**
	 * This action is performed, when the user wants to perform a search.
	 */
	public void performSearch( ) throws BusinessException {
		// Get the filter input from the user
		final TracesFilter filter = savePresentationFilter( );

		// Find the trace roots to display
		final TracesService tracesService = getService( TracesService.class );
		final List<MethodCall> traceRoots = tracesService.searchTraces( filter );
		final int totalTraces = tracesService.countTraces( );

		// Update the view
		updatePresentationTraces( traceRoots );
		updatePresentationStatus( traceRoots.size( ), totalTraces );
	}

	/**
	 * This action is performed, when someone requested a jump into the traces tab. The real action is determined based on the type of the parameter. If the parameter is a {@link TracesFilter}, the filter is simply applied. If the parameter is a {@link MethodCall}, the trace for the method call is shown and the view tries to navigate directly to the method call. If the method call is not visible, the trace is still shown.
	 *
	 * @param aParameter
	 *            The parameter.
	 */
	public void performSetParameter( final Object aParameter ) throws BusinessException {
		if ( aParameter instanceof MethodCall ) {
			final MethodCall methodCall = ( MethodCall ) aParameter;

			// We are supposed to jump to the method call
			final TracesFilter filter = new TracesFilter( );
			filter.setTraceId( methodCall.getTraceId( ) );
			updatePresentationFilter( filter );
			performSearch( );

			// Now let us see, whether we can find the method call
			select( methodCall );
		}

		if ( aParameter instanceof TracesFilter ) {
			final TracesFilter filter = ( TracesFilter ) aParameter;
			updatePresentationFilter( filter );

			performSearch( );
		}
	}

	Command getSaveAsFavoriteCommand( ) {
		return ivSaveAsFavoriteCommand;
	}

	/**
	 * This action is performed, when the user wants to save the current filter as a filter favorite.
	 */
	public void performSaveAsFavorite( ) throws BusinessException {
		// We simply save the filter's content and delegate to the main controller. This should rather be performed with a scope, but is currently not
		// possible due to a bug.
		final TracesFilter filter = savePresentationFilter( );
		getController( MainController.class ).performSaveAsFavorite( TracesView.class, filter );
	}

	private void updatePresentationTraces( final List<MethodCall> aTraceRoots ) {
		final PropertiesService propertiesService = getService( PropertiesService.class );
		final boolean showUnmonitoredTime = propertiesService.loadApplicationProperty( ShowUnmonitoredTimeProperty.class );

		// Prepare the aggregator based on the properties
		final Aggregator aggregator;

		final MethodCallAggregation aggregation = propertiesService.loadApplicationProperty( MethodCallAggregationProperty.class );
		final float threshold = propertiesService.loadApplicationProperty( MethodCallThresholdProperty.class );
		final int maxCalls = propertiesService.loadApplicationProperty( MaxNumberOfMethodCallsProperty.class );

		switch ( aggregation ) {
			case BY_DURATION:
				aggregator = new DurationAggregator( maxCalls );
			break;
			case BY_THRESHOLD:
				aggregator = new ThresholdAggregator( threshold );
			break;
			case BY_TRACE_DEPTH:
				aggregator = new TraceDepthAggregator( maxCalls );
			break;
			case BY_TRACE_SIZE:
				aggregator = new TraceSizeAggregator( maxCalls );
			break;
			case NONE:
			default:
				aggregator = new IdentityAggregator( );
			break;

		}

		final TreeItem<MethodCall> root = new TreeItem<>( );
		root.setValue( new MethodCall( ) );

		// Convert the trace roots to tree items
		for ( final MethodCall methodCall : aTraceRoots ) {
			root.getChildren( ).add( new MethodCallTreeItem( methodCall, showUnmonitoredTime, aggregator ) );
		}

		getView( ).getTreeTableView( ).setRoot( root );
	}

	private void updatePresentationDurationColumnHeader( final String aSuffix ) {
		getView( ).getDurationColumn( ).setText( getLocalizedString( "columnDuration" ) + " " + aSuffix );
	}

	private void updatePresentationDetails( final MethodCall aMethodCall ) {
		final String noDataAvailable = getLocalizedString( "noDataAvailable" );

		if ( aMethodCall != null ) {
			ivDetailsHostProperty.set( aMethodCall.getHost( ) );
			ivDetailsClassProperty.set( aMethodCall.getClazz( ) );
			ivDetailsMethodProperty.set( aMethodCall.getMethod( ) );
			ivDetailsExceptionProperty.set( aMethodCall.getException( ) != null ? aMethodCall.getException( ) : noDataAvailable );
			ivDetailsTraceDepthProperty.set( Integer.toString( aMethodCall.getTraceDepth( ) ) );
			ivDetailsTraceSizeProperty.set( Integer.toString( aMethodCall.getTraceSize( ) ) );
			ivDetailsDurationProperty.set( String.format( "%d [ns]", aMethodCall.getDuration( ) ) );
			ivDetailsPercentProperty.set( String.format( "%f %%", aMethodCall.getPercent( ) ) );
			ivDetailsTimestampProperty.set( Long.toString( aMethodCall.getTimestamp( ) ) );
			ivDetailsTraceIdProperty.set( Long.toString( aMethodCall.getTraceId( ) ) );
		} else {
			ivDetailsHostProperty.set( noDataAvailable );
			ivDetailsClassProperty.set( noDataAvailable );
			ivDetailsMethodProperty.set( noDataAvailable );
			ivDetailsExceptionProperty.set( noDataAvailable );
			ivDetailsTraceDepthProperty.set( noDataAvailable );
			ivDetailsTraceSizeProperty.set( noDataAvailable );
			ivDetailsDurationProperty.set( noDataAvailable );
			ivDetailsPercentProperty.set( noDataAvailable );
			ivDetailsTimestampProperty.set( noDataAvailable );
			ivDetailsTraceIdProperty.set( noDataAvailable );
		}
	}

	private void updatePresentationStatus( final int aTraces, final int aTotalTraces ) {
		final NumberFormat decimalFormat = DecimalFormat.getInstance( );
		ivStatusLabelProperty.set( String.format( getLocalizedString( "statusLabel" ), decimalFormat.format( aTraces ), decimalFormat.format( aTotalTraces ) ) );
	}

	private MethodCall getSelected( ) {
		final TreeItem<MethodCall> treeItem = ivSelectedMethodCallProperty.get( );
		return treeItem != null ? treeItem.getValue( ) : null;
	}

	private void updatePresentationFilter( final TracesFilter aFilter ) {
		ivFilterHostProperty.set( aFilter.getHost( ) );
		ivFilterClassProperty.set( aFilter.getClazz( ) );
		ivFilterMethodProperty.set( aFilter.getMethod( ) );
		ivFilterExceptionProperty.set( aFilter.getException( ) );
		ivFilterTraceIdProperty.set( aFilter.getTraceId( ) );
		ivFilterUseRegExprProperty.set( aFilter.isUseRegExpr( ) );
		ivFilterSearchWholeTraceProperty.set( aFilter.isSearchWholeTrace( ) );
		ivFilterLowerDateProperty.setValue( aFilter.getLowerDate( ) );
		ivFilterLowerTimeProperty.set( aFilter.getLowerTime( ) );
		ivFilterUpperDateProperty.setValue( aFilter.getUpperDate( ) );
		ivFilterUpperTimeProperty.set( aFilter.getUpperTime( ) );
		ivFilterSearchTypeProperty.setValue( aFilter.getSearchType( ) );
	}

	private TracesFilter savePresentationFilter( ) throws BusinessException {
		final TracesFilter filter = new TracesFilter( );

		filter.setHost( trimToNull( ivFilterHostProperty.get( ) ) );
		filter.setClazz( trimToNull( ivFilterClassProperty.get( ) ) );
		filter.setMethod( trimToNull( ivFilterMethodProperty.get( ) ) );
		filter.setException( trimToNull( ivFilterExceptionProperty.get( ) ) );
		filter.setUseRegExpr( ivFilterUseRegExprProperty.get( ) );
		filter.setSearchWholeTrace( ivFilterSearchWholeTraceProperty.get( ) );
		filter.setLowerDate( ivFilterLowerDateProperty.getValue( ) );
		filter.setLowerTime( ivFilterLowerTimeProperty.getValue( ) );
		filter.setUpperDate( ivFilterUpperDateProperty.getValue( ) );
		filter.setUpperTime( ivFilterUpperTimeProperty.getValue( ) );
		filter.setSearchType( ivFilterSearchTypeProperty.getValue( ) );
		filter.setTraceId( ivFilterTraceIdProperty.getValue( ) );

		// If we are using regular expressions, we should check them
		if ( filter.isUseRegExpr( ) ) {
			final PatternService patternService = getService( PatternService.class );

			if ( !( patternService.isValidPattern( filter.getHost( ) ) || filter.getHost( ) == null ) ) {
				throw new BusinessException( String.format( getLocalizedString( "errorMessageRegExpr" ), filter.getHost( ) ) );
			}

			if ( !( patternService.isValidPattern( filter.getClazz( ) ) || filter.getClazz( ) == null ) ) {
				throw new BusinessException( String.format( getLocalizedString( "errorMessageRegExpr" ), filter.getClazz( ) ) );
			}

			if ( !( patternService.isValidPattern( filter.getMethod( ) ) || filter.getMethod( ) == null ) ) {
				throw new BusinessException( String.format( getLocalizedString( "errorMessageRegExpr" ), filter.getMethod( ) ) );
			}

			if ( !( patternService.isValidPattern( filter.getException( ) ) || filter.getException( ) == null ) ) {
				throw new BusinessException( String.format( getLocalizedString( "errorMessageRegExpr" ), filter.getException( ) ) );
			}
		}

		return filter;
	}

	private void select( final MethodCall aMethodCall ) {
		final TreeItem<MethodCall> root = ivRootMethodCallProperty.get( );

		final Stack<TreeItem<MethodCall>> stack = new Stack<>( );
		stack.push( root );

		while ( !stack.isEmpty( ) ) {
			final TreeItem<MethodCall> treeItem = stack.pop( );

			if ( treeItem.getValue( ) == aMethodCall ) {
				// We found the item. Select it - and expand all parents
				expand( treeItem );

				publish( EVENT_SELECT_TREE_ITEM, treeItem );

				break;
			} else {
				// Search in the children
				stack.addAll( treeItem.getChildren( ) );
			}
		}
	}

	private void expand( final TreeItem<MethodCall> aRoot ) {
		TreeItem<MethodCall> root = aRoot;
		while ( root != null ) {
			root.setExpanded( true );
			root = root.getParent( );
		}
	}

	StringProperty getFilterHostProperty( ) {
		return ivFilterHostProperty;
	}

	StringProperty getFilterClassProperty( ) {
		return ivFilterClassProperty;
	}

	StringProperty getFilterMethodProperty( ) {
		return ivFilterMethodProperty;
	}

	StringProperty getFilterExceptionProperty( ) {
		return ivFilterExceptionProperty;
	}

	ObjectProperty<Long> getFilterTraceIdProperty( ) {
		return ivFilterTraceIdProperty;
	}

	BooleanProperty getFilterUseRegExprProperty( ) {
		return ivFilterUseRegExprProperty;
	}

	ObjectProperty<LocalDate> getFilterLowerDateProperty( ) {
		return ivFilterLowerDateProperty;
	}

	ObjectProperty<LocalTime> getFilterLowerTimeProperty( ) {
		return ivFilterLowerTimeProperty;
	}

	ObjectProperty<LocalDate> getFilterUpperDateProperty( ) {
		return ivFilterUpperDateProperty;
	}

	ObjectProperty<LocalTime> getFilterUpperTimeProperty( ) {
		return ivFilterUpperTimeProperty;
	}

	BooleanProperty getFilterSearchWholeTraceProperty( ) {
		return ivFilterSearchWholeTraceProperty;
	}

	ObjectProperty<SearchType> getFilterSearchTypeProperty( ) {
		return ivFilterSearchTypeProperty;
	}

	ObjectProperty<TreeItem<MethodCall>> getRootMethodCallProperty( ) {
		return ivRootMethodCallProperty;
	}

	ObjectProperty<TreeItem<MethodCall>> getSelectedMethodCallProperty( ) {
		return ivSelectedMethodCallProperty;
	}

	StringProperty getDetailsHostProperty( ) {
		return ivDetailsHostProperty;
	}

	StringProperty getDetailsClassProperty( ) {
		return ivDetailsClassProperty;
	}

	StringProperty getDetailsMethodProperty( ) {
		return ivDetailsMethodProperty;
	}

	StringProperty getDetailsExceptionProperty( ) {
		return ivDetailsExceptionProperty;
	}

	StringProperty getDetailsTraceDepthProperty( ) {
		return ivDetailsTraceDepthProperty;
	}

	StringProperty getDetailsTraceSizeProperty( ) {
		return ivDetailsTraceSizeProperty;
	}

	StringProperty getDetailsDurationProperty( ) {
		return ivDetailsDurationProperty;
	}

	StringProperty getDetailsPercentProperty( ) {
		return ivDetailsPercentProperty;
	}

	StringProperty getDetailsTimestampProperty( ) {
		return ivDetailsTimestampProperty;
	}

	StringProperty getDetailsTraceIdProperty( ) {
		return ivDetailsTraceIdProperty;
	}

	StringProperty getStatusLabelProperty( ) {
		return ivStatusLabelProperty;
	}

	StringProperty getDurationColumnHeaderProperty( ) {
		return ivDurationColumnHeaderProperty;
	}

}
