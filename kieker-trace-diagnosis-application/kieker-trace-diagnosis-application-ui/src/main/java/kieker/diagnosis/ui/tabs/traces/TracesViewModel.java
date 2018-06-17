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
import java.util.List;
import java.util.Stack;

import com.google.inject.Singleton;

import javafx.scene.control.TreeItem;
import kieker.diagnosis.architecture.exception.BusinessException;
import kieker.diagnosis.architecture.service.properties.PropertiesService;
import kieker.diagnosis.architecture.ui.ViewModelBase;
import kieker.diagnosis.service.data.MethodCall;
import kieker.diagnosis.service.pattern.PatternService;
import kieker.diagnosis.service.settings.MethodCallAggregation;
import kieker.diagnosis.service.settings.properties.MaxNumberOfMethodCallsProperty;
import kieker.diagnosis.service.settings.properties.MethodCallAggregationProperty;
import kieker.diagnosis.service.settings.properties.MethodCallThresholdProperty;
import kieker.diagnosis.service.settings.properties.ShowUnmonitoredTimeProperty;
import kieker.diagnosis.service.traces.TracesFilter;
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
class TracesViewModel extends ViewModelBase<TracesView> {

	public void updatePresentationTraces( final List<MethodCall> aTraceRoots ) {
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

	public void updatePresentationDurationColumnHeader( final String aSuffix ) {
		getView( ).getDurationColumn( ).setText( getLocalizedString( "columnDuration" ) + " " + aSuffix );
	}

	public void updatePresentationDetails( final MethodCall aMethodCall ) {
		final String noDataAvailable = getLocalizedString( "noDataAvailable" );

		if ( aMethodCall != null ) {
			getView( ).getDetailsHost( ).setText( aMethodCall.getHost( ) );
			getView( ).getDetailsClass( ).setText( aMethodCall.getClazz( ) );
			getView( ).getDetailsMethod( ).setText( aMethodCall.getMethod( ) );
			getView( ).getDetailsException( ).setText( aMethodCall.getException( ) != null ? aMethodCall.getException( ) : noDataAvailable );
			getView( ).getDetailsTraceDepth( ).setText( Integer.toString( aMethodCall.getTraceDepth( ) ) );
			getView( ).getDetailsTraceSize( ).setText( Integer.toString( aMethodCall.getTraceSize( ) ) );
			getView( ).getDetailsDuration( ).setText( String.format( "%d [ns]", aMethodCall.getDuration( ) ) );
			getView( ).getDetailsPercent( ).setText( String.format( "%f %%", aMethodCall.getPercent( ) ) );
			getView( ).getDetailsTimestamp( ).setText( Long.toString( aMethodCall.getTimestamp( ) ) );
			getView( ).getDetailsTraceId( ).setText( Long.toString( aMethodCall.getTraceId( ) ) );
		} else {
			getView( ).getDetailsHost( ).setText( noDataAvailable );
			getView( ).getDetailsClass( ).setText( noDataAvailable );
			getView( ).getDetailsMethod( ).setText( noDataAvailable );
			getView( ).getDetailsException( ).setText( noDataAvailable );
			getView( ).getDetailsTraceDepth( ).setText( noDataAvailable );
			getView( ).getDetailsTraceSize( ).setText( noDataAvailable );
			getView( ).getDetailsDuration( ).setText( noDataAvailable );
			getView( ).getDetailsPercent( ).setText( noDataAvailable );
			getView( ).getDetailsTimestamp( ).setText( noDataAvailable );
			getView( ).getDetailsTraceId( ).setText( noDataAvailable );
		}
	}

	public void updatePresentationStatus( final int aTraces, final int aTotalTraces ) {
		final NumberFormat decimalFormat = DecimalFormat.getInstance( );
		getView( ).getStatusLabel( ).setText( String.format( getLocalizedString( "statusLabel" ), decimalFormat.format( aTraces ), decimalFormat.format( aTotalTraces ) ) );
	}

	public MethodCall getSelected( ) {
		final TreeItem<MethodCall> selectedItem = getView( ).getTreeTableView( ).getSelectionModel( ).getSelectedItem( );
		return selectedItem != null ? selectedItem.getValue( ) : null;
	}

	public void updatePresentationFilter( final TracesFilter aFilter ) {
		getView( ).getFilterHost( ).setText( aFilter.getHost( ) );
		getView( ).getFilterClass( ).setText( aFilter.getClazz( ) );
		getView( ).getFilterMethod( ).setText( aFilter.getMethod( ) );
		getView( ).getFilterException( ).setText( aFilter.getException( ) );
		getView( ).getFilterTraceId( ).setText( aFilter.getTraceId( ) != null ? Long.toString( aFilter.getTraceId( ) ) : null );
		getView( ).getFilterUseRegExpr( ).setSelected( aFilter.isUseRegExpr( ) );
		getView( ).getFilterSearchWholeTrace( ).setSelected( aFilter.isSearchWholeTrace( ) );
		getView( ).getFilterLowerDate( ).setValue( aFilter.getLowerDate( ) );
		getView( ).getFilterLowerTime( ).setLocalTime( aFilter.getLowerTime( ) );
		getView( ).getFilterUpperDate( ).setValue( aFilter.getUpperDate( ) );
		getView( ).getFilterUpperTime( ).setLocalTime( aFilter.getUpperTime( ) );
		getView( ).getFilterSearchType( ).setValue( aFilter.getSearchType( ) );
	}

	public TracesFilter savePresentationFilter( ) throws BusinessException {
		final TracesFilter filter = new TracesFilter( );

		filter.setHost( trimToNull( getView( ).getFilterHost( ).getText( ) ) );
		filter.setClazz( trimToNull( getView( ).getFilterClass( ).getText( ) ) );
		filter.setMethod( trimToNull( getView( ).getFilterMethod( ).getText( ) ) );
		filter.setException( trimToNull( getView( ).getFilterException( ).getText( ) ) );
		filter.setUseRegExpr( getView( ).getFilterUseRegExpr( ).isSelected( ) );
		filter.setSearchWholeTrace( getView( ).getFilterSearchWholeTrace( ).isSelected( ) );
		filter.setLowerDate( getView( ).getFilterLowerDate( ).getValue( ) );
		filter.setLowerTime( getView( ).getFilterLowerTime( ).getLocalTime( ) );
		filter.setUpperDate( getView( ).getFilterUpperDate( ).getValue( ) );
		filter.setUpperTime( getView( ).getFilterUpperTime( ).getLocalTime( ) );
		filter.setSearchType( getView( ).getFilterSearchType( ).getValue( ) );
		filter.setTraceId( getView( ).getFilterTraceId( ).getValue( ) );

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

	public void select( final MethodCall aMethodCall ) {
		final TreeItem<MethodCall> root = getView( ).getTreeTableView( ).getRoot( );

		final Stack<TreeItem<MethodCall>> stack = new Stack<>( );
		stack.push( root );

		while ( !stack.isEmpty( ) ) {
			final TreeItem<MethodCall> treeItem = stack.pop( );

			if ( treeItem.getValue( ) == aMethodCall ) {
				// We found the item. Select it - and expand all parents
				expand( treeItem );
				getView( ).getTreeTableView( ).getSelectionModel( ).select( treeItem );

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

}
