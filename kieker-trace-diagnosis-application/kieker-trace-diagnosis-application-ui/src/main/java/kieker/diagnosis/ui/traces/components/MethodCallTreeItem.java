package kieker.diagnosis.ui.traces.components;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import kieker.diagnosis.architecture.common.ClassUtil;
import kieker.diagnosis.service.data.MethodCall;
import kieker.diagnosis.ui.traces.aggregator.Aggregator;

/**
 * This is an item for a tree table, which contains a method call. The children are loaded in a lazy way. The item enriches and aggregates the children based on
 * the current application settings.
 *
 * @author Nils Christian Ehmke
 */
public final class MethodCallTreeItem extends TreeItem<MethodCall> {

	private final ResourceBundle ivResourceBundle = ResourceBundle.getBundle( ClassUtil.getRealName( getClass( ) ) );

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
			methodCall.setMethod( ivResourceBundle.getString( "unmonitoredTime" ) );
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
