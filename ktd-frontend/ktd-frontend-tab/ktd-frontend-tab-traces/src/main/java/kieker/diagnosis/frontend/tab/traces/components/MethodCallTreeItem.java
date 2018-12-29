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

package kieker.diagnosis.frontend.tab.traces.components;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import kieker.diagnosis.backend.data.MethodCall;
import kieker.diagnosis.frontend.tab.traces.aggregator.Aggregator;

/**
 * This is an item for a tree table, which contains a method call. The children are loaded in a lazy way. The item enriches and aggregates the children based on
 * the current application settings.
 *
 * @author Nils Christian Ehmke
 */
public final class MethodCallTreeItem extends TreeItem<MethodCall> {

	private static final ResourceBundle cvResourceBundle = ResourceBundle.getBundle( MethodCallTreeItem.class.getName( ) );

	private boolean ivChildrenInitialized = false;
	private final boolean ivShowUnmonitoredTime;
	private final Aggregator ivAggregator;

	/**
	 * Creates a new tree item.
	 *
	 * @param aMethodCall
	 *            The method call to be contained in the new item.
	 * @param aShowUnmonitoredTime
	 *            Determines whether the node should show the unmonitored time.
	 * @param aAggregator
	 *            The aggregator which is used to aggregate the children of the node.
	 */
	public MethodCallTreeItem( final MethodCall aMethodCall, final boolean aShowUnmonitoredTime, final Aggregator aAggregator ) {
		super( aMethodCall );

		ivShowUnmonitoredTime = aShowUnmonitoredTime;
		ivAggregator = aAggregator;
	}

	@Override
	public final ObservableList<TreeItem<MethodCall>> getChildren( ) {
		// Initialize the children in a lazy way.
		if ( !ivChildrenInitialized ) {
			ivChildrenInitialized = true;
			initializeChildren( );
		}

		return super.getChildren( );
	}

	@Override
	public final boolean isLeaf( ) {
		return getValue( ).getChildren( ).isEmpty( );
	}

	private void initializeChildren( ) {
		final List<TreeItem<MethodCall>> result = new ArrayList<>( );

		// Aggregate the method calls if necessary
		final List<MethodCall> children = ivAggregator.aggregate( getValue( ).getChildren( ) );

		// Show the unmonitored time if necessary
		if ( ivShowUnmonitoredTime ) {
			// Calculate the unmonitored time
			float percent = 0.0f;
			long duration = 0;
			for ( final MethodCall child : children ) {
				percent += child.getPercent( );
				duration += child.getDuration( );
			}

			// We create a dummy entry for the unmonitored time
			final MethodCall methodCall = new MethodCall( );
			methodCall.setHost( "-" );
			methodCall.setClazz( "-" );
			methodCall.setMethod( cvResourceBundle.getString( "unmonitoredTime" ) );
			methodCall.setTraceId( getValue( ).getTraceId( ) );
			methodCall.setTimestamp( getValue( ).getTimestamp( ) );

			methodCall.setPercent( 100.0f - percent );
			methodCall.setDuration( getValue( ).getDuration( ) - duration );

			// Make sure that the new node does not try to create further nodes
			result.add( new MethodCallTreeItem( methodCall, false, ivAggregator ) );
		}

		// Now convert the children into items
		for ( final MethodCall child : children ) {
			result.add( new MethodCallTreeItem( child, ivShowUnmonitoredTime, ivAggregator ) );
		}

		super.getChildren( ).setAll( result );
	}

}
