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

package kieker.diagnosis.frontend.tab.methods.composite;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import kieker.diagnosis.backend.data.MethodCall;
import kieker.diagnosis.backend.export.CSVData;
import kieker.diagnosis.backend.properties.PropertiesService;
import kieker.diagnosis.backend.settings.ClassAppearance;
import kieker.diagnosis.backend.settings.MethodAppearance;
import kieker.diagnosis.backend.settings.properties.ClassAppearanceProperty;
import kieker.diagnosis.backend.settings.properties.MethodAppearanceProperty;
import kieker.diagnosis.frontend.base.mixin.CdiMixin;
import kieker.diagnosis.frontend.base.mixin.StylesheetMixin;
import kieker.diagnosis.frontend.tab.methods.atom.ClassCellValueFactory;
import kieker.diagnosis.frontend.tab.methods.atom.DurationCellValueFactory;
import kieker.diagnosis.frontend.tab.methods.atom.MethodCellValueFactory;
import kieker.diagnosis.frontend.tab.methods.atom.StyledRow;
import kieker.diagnosis.frontend.tab.methods.atom.TimestampCellValueFactory;

/**
 * This component is a specific table view that displays a collection of methods.
 *
 * @author Nils Christian Ehmke
 */
public final class MethodsTableView extends TableView<MethodCall> implements StylesheetMixin, CdiMixin {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( MethodsTableView.class.getName( ) );

	@Inject
	private PropertiesService propertiesService;

	private TableColumn<MethodCall, String> host;
	private TableColumn<MethodCall, String> clazz;
	private TableColumn<MethodCall, String> method;
	private TableColumn<MethodCall, String> duration;
	private TableColumn<MethodCall, String> timestamp;
	private TableColumn<MethodCall, String> traceId;

	public MethodsTableView( ) {
		injectFields( );
		createControl( );
	}

	private void createControl( ) {
		setTableMenuButtonVisible( true );
		setRowFactory( aParam -> new StyledRow( ) );
		setPlaceholder( createPlaceholder( ) );

		getColumns( ).add( createHostTableColumn( ) );
		getColumns( ).add( createClassTableColumn( ) );
		getColumns( ).add( createMethodTableColumn( ) );
		getColumns( ).add( createDurationTableColumn( ) );
		getColumns( ).add( createTimestampTableColumn( ) );
		getColumns( ).add( createTraceIdTableColumn( ) );

		// The default sorting is a little bit too slow.We use a custom sort policy which sorts the data directly.
		setSortPolicy( createSortPolicy( ) );

		addDefaultStylesheet( );
	}

	private TableColumn<MethodCall, ?> createHostTableColumn( ) {
		host = new TableColumn<>( );

		host.setCellValueFactory( aParam -> new ReadOnlyObjectWrapper<>( aParam.getValue( ).getHost( ) ) );
		host.setText( RESOURCE_BUNDLE.getString( "columnHost" ) );
		host.setPrefWidth( 100 );

		return host;
	}

	private TableColumn<MethodCall, ?> createClassTableColumn( ) {
		clazz = new TableColumn<>( );

		clazz.setCellValueFactory( new ClassCellValueFactory( ) );
		clazz.setText( RESOURCE_BUNDLE.getString( "columnClass" ) );
		clazz.setPrefWidth( 200 );

		return clazz;
	}

	private TableColumn<MethodCall, ?> createMethodTableColumn( ) {
		method = new TableColumn<>( );

		method.setCellValueFactory( new MethodCellValueFactory( ) );
		method.setText( RESOURCE_BUNDLE.getString( "columnMethod" ) );
		method.setPrefWidth( 400 );

		return method;
	}

	private TableColumn<MethodCall, ?> createDurationTableColumn( ) {
		duration = new TableColumn<>( );

		duration.setCellValueFactory( new DurationCellValueFactory( ) );
		duration.setText( RESOURCE_BUNDLE.getString( "columnDuration" ) );
		duration.setPrefWidth( 150 );

		return duration;
	}

	private TableColumn<MethodCall, ?> createTimestampTableColumn( ) {
		timestamp = new TableColumn<>( );

		timestamp.setCellValueFactory( new TimestampCellValueFactory( ) );
		timestamp.setText( RESOURCE_BUNDLE.getString( "columnTimestamp" ) );
		timestamp.setPrefWidth( 150 );

		return timestamp;
	}

	private TableColumn<MethodCall, ?> createTraceIdTableColumn( ) {
		traceId = new TableColumn<>( );

		traceId.setCellValueFactory( aParam -> new ReadOnlyStringWrapper( Long.toString( aParam.getValue( ).getTraceId( ) ).intern( ) ) );
		traceId.setText( RESOURCE_BUNDLE.getString( "columnTraceId" ) );
		traceId.setPrefWidth( 150 );

		return traceId;
	}

	private Label createPlaceholder( ) {
		final Label placeholder = new Label( );

		placeholder.setText( RESOURCE_BUNDLE.getString( "noDataAvailable" ) );

		return placeholder;
	}

	private Callback<TableView<MethodCall>, Boolean> createSortPolicy( ) {
		return param -> {
			final ObservableList<TableColumn<MethodCall, ?>> sortOrder = param.getSortOrder( );

			if ( sortOrder.size( ) == 1 ) {
				final TableColumn<MethodCall, ?> tableColumn = sortOrder.get( 0 );

				if ( tableColumn == host ) {
					final ObservableList<MethodCall> items = param.getItems( );
					if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
						items.sort( ( o1, o2 ) -> o1.getHost( ).compareTo( o2.getHost( ) ) );
					} else {
						items.sort( ( o1, o2 ) -> o2.getHost( ).compareTo( o1.getHost( ) ) );
					}
				}

				if ( tableColumn == clazz ) {
					final ClassAppearance classAppearance = propertiesService.loadApplicationProperty( ClassAppearanceProperty.class );

					final ObservableList<MethodCall> items = param.getItems( );
					if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
						items.sort( ( o1, o2 ) -> classAppearance.convert( o1.getClazz( ) ).compareTo( classAppearance.convert( o2.getClazz( ) ) ) );
					} else {
						items.sort( ( o1, o2 ) -> classAppearance.convert( o2.getClazz( ) ).compareTo( classAppearance.convert( o1.getClazz( ) ) ) );
					}
				}

				if ( tableColumn == method ) {
					final MethodAppearance methodAppearance = propertiesService.loadApplicationProperty( MethodAppearanceProperty.class );

					final ObservableList<MethodCall> items = param.getItems( );
					if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
						items.sort( ( o1, o2 ) -> methodAppearance.convert( o1.getMethod( ) ).compareTo( methodAppearance.convert( o2.getMethod( ) ) ) );
					} else {
						items.sort( ( o1, o2 ) -> methodAppearance.convert( o2.getMethod( ) ).compareTo( methodAppearance.convert( o1.getMethod( ) ) ) );
					}
				}

				if ( tableColumn == duration ) {
					final ObservableList<MethodCall> items = param.getItems( );
					if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
						items.sort( ( o1, o2 ) -> Long.compare( o1.getDuration( ), o2.getDuration( ) ) );
					} else {
						items.sort( ( o1, o2 ) -> Long.compare( o2.getDuration( ), o1.getDuration( ) ) );
					}
				}

				if ( tableColumn == timestamp ) {
					final ObservableList<MethodCall> items = param.getItems( );
					if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
						items.sort( ( o1, o2 ) -> Long.compare( o1.getTimestamp( ), o2.getTimestamp( ) ) );
					} else {
						items.sort( ( o1, o2 ) -> Long.compare( o2.getTimestamp( ), o1.getTimestamp( ) ) );
					}
				}

				if ( tableColumn == traceId ) {
					final ObservableList<MethodCall> items = param.getItems( );
					if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
						items.sort( ( o1, o2 ) -> Long.compare( o1.getTraceId( ), o2.getTraceId( ) ) );
					} else {
						items.sort( ( o1, o2 ) -> Long.compare( o2.getTraceId( ), o1.getTraceId( ) ) );
					}
				}
			}

			return true;
		};
	}

	/**
	 * Adds a listener which is called when the selection of an item changes.
	 *
	 * @param listener
	 *            The listener.
	 */
	public void addSelectionChangeListener( final ChangeListener<MethodCall> listener ) {
		getSelectionModel( ).selectedItemProperty( ).addListener( listener );
	}

	public CSVData getValueAsCsv( ) {
		final ObservableList<TableColumn<MethodCall, ?>> visibleColumns = getVisibleLeafColumns( );

		final int columnSize = visibleColumns.size( );
		final int itemsSize = getItems( ).size( );

		final CSVData csvData = new CSVData( );
		for ( int columnIndex = 0; columnIndex < columnSize; columnIndex++ ) {
			final String header = visibleColumns.get( columnIndex ).getText( );
			csvData.addHeader( header );
		}

		for ( int rowIndex = 0; rowIndex < itemsSize; rowIndex++ ) {
			final List<String> row = new ArrayList<>( );
			for ( int columnIndex = 0; columnIndex < columnSize; columnIndex++ ) {
				final Object cellData = visibleColumns.get( columnIndex ).getCellData( rowIndex );
				row.add( cellData.toString( ) );
			}
			csvData.addRow( row );
		}

		return csvData;
	}

	public void setDurationSuffix( final String durationSuffixForRefresh ) {
		duration.setText( RESOURCE_BUNDLE.getString( "columnDuration" ) + " " + durationSuffixForRefresh );
	}

}
