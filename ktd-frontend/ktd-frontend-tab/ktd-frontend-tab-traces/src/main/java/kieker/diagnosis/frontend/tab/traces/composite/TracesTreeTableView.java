package kieker.diagnosis.frontend.tab.traces.composite;

import java.util.ResourceBundle;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import kieker.diagnosis.backend.data.MethodCall;
import kieker.diagnosis.frontend.base.mixin.StylesheetMixin;
import kieker.diagnosis.frontend.tab.traces.atom.ClassCellValueFactory;
import kieker.diagnosis.frontend.tab.traces.atom.DurationCellValueFactory;
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

	public TreeTableColumn<MethodCall, Long> getDurationColumn( ) {
		return durationColumn;
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

}
