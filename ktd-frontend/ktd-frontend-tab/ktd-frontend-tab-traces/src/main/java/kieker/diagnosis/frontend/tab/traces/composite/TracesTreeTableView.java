package kieker.diagnosis.frontend.tab.traces.composite;

import java.util.List;
import java.util.ResourceBundle;
import java.util.Stack;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import kieker.diagnosis.backend.base.service.ServiceFactory;
import kieker.diagnosis.backend.data.MethodCall;
import kieker.diagnosis.backend.properties.PropertiesService;
import kieker.diagnosis.backend.settings.MethodCallAggregation;
import kieker.diagnosis.backend.settings.properties.MaxNumberOfMethodCallsProperty;
import kieker.diagnosis.backend.settings.properties.MethodCallAggregationProperty;
import kieker.diagnosis.backend.settings.properties.MethodCallThresholdProperty;
import kieker.diagnosis.backend.settings.properties.ShowUnmonitoredTimeProperty;
import kieker.diagnosis.frontend.base.mixin.StylesheetMixin;
import kieker.diagnosis.frontend.tab.traces.aggregator.Aggregator;
import kieker.diagnosis.frontend.tab.traces.aggregator.DurationAggregator;
import kieker.diagnosis.frontend.tab.traces.aggregator.IdentityAggregator;
import kieker.diagnosis.frontend.tab.traces.aggregator.ThresholdAggregator;
import kieker.diagnosis.frontend.tab.traces.aggregator.TraceDepthAggregator;
import kieker.diagnosis.frontend.tab.traces.aggregator.TraceSizeAggregator;
import kieker.diagnosis.frontend.tab.traces.atom.ClassCellValueFactory;
import kieker.diagnosis.frontend.tab.traces.atom.DurationCellValueFactory;
import kieker.diagnosis.frontend.tab.traces.atom.MethodCallTreeItem;
import kieker.diagnosis.frontend.tab.traces.atom.MethodCellValueFactory;
import kieker.diagnosis.frontend.tab.traces.atom.StyledRow;
import kieker.diagnosis.frontend.tab.traces.atom.TimestampCellValueFactory;

public final class TracesTreeTableView extends TreeTableView<MethodCall> implements StylesheetMixin {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( TracesTreeTableView.class.getName( ) );

	private TreeTableColumn<MethodCall, Long> durationColumn;

	public TracesTreeTableView( ) {
		setShowRoot( false );
		setTableMenuButtonVisible( true );
		setRowFactory( aParam -> new StyledRow( ) );

		final Label placeholder = new Label( );
		placeholder.setText( RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
		setPlaceholder( placeholder );

		{
			final TreeTableColumn<MethodCall, String> column = new TreeTableColumn<>( );
			column.setCellValueFactory( aParam -> new ReadOnlyObjectWrapper<>( aParam.getValue( ).getValue( ).getHost( ) ) );
			column.setText( RESOURCE_BUNDLE.getString( "columnHost" ) );
			column.setPrefWidth( 100 );

			getColumns( ).add( column );
		}

		{
			final TreeTableColumn<MethodCall, String> column = new TreeTableColumn<>( );
			column.setCellValueFactory( new ClassCellValueFactory( ) );
			column.setText( RESOURCE_BUNDLE.getString( "columnClass" ) );
			column.setPrefWidth( 200 );

			getColumns( ).add( column );
		}

		{
			final TreeTableColumn<MethodCall, String> column = new TreeTableColumn<>( );
			column.setCellValueFactory( new MethodCellValueFactory( ) );
			column.setText( RESOURCE_BUNDLE.getString( "columnMethod" ) );
			column.setPrefWidth( 400 );

			getColumns( ).add( column );
		}

		{
			final TreeTableColumn<MethodCall, Integer> column = new TreeTableColumn<>( );
			column.setCellValueFactory( aParam -> new ReadOnlyObjectWrapper<>( aParam.getValue( ).getValue( ).getTraceDepth( ) ) );
			column.setText( RESOURCE_BUNDLE.getString( "columnTraceDepth" ) );
			column.setPrefWidth( 100 );

			getColumns( ).add( column );
		}

		{
			final TreeTableColumn<MethodCall, Integer> column = new TreeTableColumn<>( );
			column.setCellValueFactory( aParam -> new ReadOnlyObjectWrapper<>( aParam.getValue( ).getValue( ).getTraceSize( ) ) );
			column.setText( RESOURCE_BUNDLE.getString( "columnTraceSize" ) );
			column.setPrefWidth( 100 );

			getColumns( ).add( column );
		}

		{
			final TreeTableColumn<MethodCall, Float> column = new TreeTableColumn<>( );
			column.setCellValueFactory( aParam -> new ReadOnlyObjectWrapper<>( aParam.getValue( ).getValue( ).getPercent( ) ) );
			column.setText( RESOURCE_BUNDLE.getString( "columnPercent" ) );
			column.setPrefWidth( 100 );

			getColumns( ).add( column );
		}

		{
			durationColumn = new TreeTableColumn<>( );
			durationColumn.setCellValueFactory( new DurationCellValueFactory( ) );
			durationColumn.setText( RESOURCE_BUNDLE.getString( "columnDuration" ) );
			durationColumn.setPrefWidth( 150 );

			getColumns( ).add( durationColumn );
		}

		{
			final TreeTableColumn<MethodCall, String> column = new TreeTableColumn<>( );
			column.setCellValueFactory( new TimestampCellValueFactory( ) );
			column.setText( RESOURCE_BUNDLE.getString( "columnTimestamp" ) );
			column.setPrefWidth( 150 );

			getColumns( ).add( column );
		}

		{
			final TreeTableColumn<MethodCall, Long> column = new TreeTableColumn<>( );
			column.setCellValueFactory( aParam -> new ReadOnlyObjectWrapper<>( aParam.getValue( ).getValue( ).getTraceId( ) ) );
			column.setText( RESOURCE_BUNDLE.getString( "columnTraceId" ) );
			column.setPrefWidth( 150 );

			getColumns( ).add( column );
		}

		addDefaultStylesheet( );
	}

	/**
	 * Adds a listener which is called when the selection of an item changes.
	 *
	 * @param listener
	 *            The listener.
	 */
	public void addSelectionChangeListener( final ChangeListener<TreeItem<MethodCall>> listener ) {
		getSelectionModel( ).selectedItemProperty( ).addListener( listener );
	}

	public void setItems( final List<MethodCall> items ) {
		final PropertiesService propertiesService = ServiceFactory.getService( PropertiesService.class );
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
		for ( final MethodCall methodCall : items ) {
			root.getChildren( ).add( new MethodCallTreeItem( methodCall, showUnmonitoredTime, aggregator ) );
		}

		setRoot( root );
	}

	public void setDurationSuffix( final String suffix ) {
		durationColumn.setText( RESOURCE_BUNDLE.getString( "columnDuration" ) + " " + suffix );
	}

	public void setSelected( final MethodCall value ) {
		final TreeItem<MethodCall> root = getRoot( );

		final Stack<TreeItem<MethodCall>> stack = new Stack<>( );
		stack.push( root );

		while ( !stack.isEmpty( ) ) {
			final TreeItem<MethodCall> treeItem = stack.pop( );

			if ( treeItem.getValue( ) == value ) {
				// We found the item. Select it - and expand all parents
				expand( treeItem );
				getSelectionModel( ).select( treeItem );

				break;
			} else {
				// Search in the children
				stack.addAll( treeItem.getChildren( ) );
			}
		}
	}

	private void expand( final TreeItem<MethodCall> root ) {
		TreeItem<MethodCall> currentRoot = root;
		while ( currentRoot != null ) {
			currentRoot.setExpanded( true );
			currentRoot = currentRoot.getParent( );
		}
	}

}
