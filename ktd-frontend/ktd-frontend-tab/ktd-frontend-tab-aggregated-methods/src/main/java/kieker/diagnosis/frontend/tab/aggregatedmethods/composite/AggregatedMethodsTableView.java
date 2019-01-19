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

package kieker.diagnosis.frontend.tab.aggregatedmethods.composite;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import kieker.diagnosis.backend.data.AggregatedMethodCall;
import kieker.diagnosis.backend.export.CSVData;
import kieker.diagnosis.backend.properties.PropertiesService;
import kieker.diagnosis.backend.settings.ClassAppearance;
import kieker.diagnosis.backend.settings.MethodAppearance;
import kieker.diagnosis.backend.settings.properties.ClassAppearanceProperty;
import kieker.diagnosis.backend.settings.properties.MethodAppearanceProperty;
import kieker.diagnosis.frontend.base.mixin.CdiMixin;
import kieker.diagnosis.frontend.base.mixin.StylesheetMixin;
import kieker.diagnosis.frontend.tab.aggregatedmethods.atom.ClassCellValueFactory;
import kieker.diagnosis.frontend.tab.aggregatedmethods.atom.DurationCellValueFactory;
import kieker.diagnosis.frontend.tab.aggregatedmethods.atom.MethodCellValueFactory;
import kieker.diagnosis.frontend.tab.aggregatedmethods.atom.StyledRow;

/**
 * This component is a specific table view that displays a collection of aggregated methods.
 *
 * @author Nils Christian Ehmke
 */
public final class AggregatedMethodsTableView extends TableView<AggregatedMethodCall> implements StylesheetMixin, CdiMixin {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( AggregatedMethodsTableView.class.getName( ) );

	@Inject
	private PropertiesService propertiesService;

	private TableColumn<AggregatedMethodCall, String> count;
	private TableColumn<AggregatedMethodCall, String> host;
	private TableColumn<AggregatedMethodCall, String> clazz;
	private TableColumn<AggregatedMethodCall, String> method;
	private TableColumn<AggregatedMethodCall, String> minDuration;
	private TableColumn<AggregatedMethodCall, String> avgDuration;
	private TableColumn<AggregatedMethodCall, String> medianDuration;
	private TableColumn<AggregatedMethodCall, String> maxDuration;
	private TableColumn<AggregatedMethodCall, String> totalDuration;

	public AggregatedMethodsTableView( ) {
		injectFields( );
		createControl( );
	}

	private void createControl( ) {
		setTableMenuButtonVisible( true );
		setRowFactory( aParam -> new StyledRow( "failed" ) );
		setPlaceholder( createPlaceholder( ) );

		getColumns( ).add( createCountTableColumn( ) );
		getColumns( ).add( createHostTableColumn( ) );
		getColumns( ).add( createClassTableColumn( ) );
		getColumns( ).add( createMethodTableColumn( ) );
		getColumns( ).add( createMinDurationTableColumn( ) );
		getColumns( ).add( createAvgDurationTableColumn( ) );
		getColumns( ).add( createMedianDurationTableColumn( ) );
		getColumns( ).add( createMaxDurationTableColumn( ) );
		getColumns( ).add( createTotalDurationTableColumn( ) );

		// The default sorting is a little bit too slow. We use a custom sort policy which sorts the data directly.
		setSortPolicy( createSortPolicy( ) );

		addDefaultStylesheet( );
	}

	private Node createPlaceholder( ) {
		final Label placeholder = new Label( );

		placeholder.setText( RESOURCE_BUNDLE.getString( "noDataAvailable" ) );

		return placeholder;
	}

	private TableColumn<AggregatedMethodCall, ?> createCountTableColumn( ) {
		count = new TableColumn<>( );

		count.setCellValueFactory( aParam -> new ReadOnlyStringWrapper( Integer.toString( aParam.getValue( ).getCount( ) ).intern( ) ) );
		count.setText( RESOURCE_BUNDLE.getString( "columnCount" ) );
		count.setPrefWidth( 100 );

		return count;
	}

	private TableColumn<AggregatedMethodCall, ?> createHostTableColumn( ) {
		host = new TableColumn<>( );

		host.setCellValueFactory( aParam -> new ReadOnlyObjectWrapper<>( aParam.getValue( ).getHost( ) ) );
		host.setText( RESOURCE_BUNDLE.getString( "columnHost" ) );
		host.setPrefWidth( 100 );

		return host;
	}

	private TableColumn<AggregatedMethodCall, ?> createClassTableColumn( ) {
		clazz = new TableColumn<>( );

		clazz.setCellValueFactory( new ClassCellValueFactory( ) );
		clazz.setText( RESOURCE_BUNDLE.getString( "columnClass" ) );
		clazz.setPrefWidth( 200 );

		return clazz;
	}

	private TableColumn<AggregatedMethodCall, ?> createMethodTableColumn( ) {
		method = new TableColumn<>( );

		method.setCellValueFactory( new MethodCellValueFactory( ) );
		method.setText( RESOURCE_BUNDLE.getString( "columnMethod" ) );
		method.setPrefWidth( 400 );

		return method;
	}

	private TableColumn<AggregatedMethodCall, ?> createMinDurationTableColumn( ) {
		final DurationCellValueFactory cellValueFactory = new DurationCellValueFactory( AggregatedMethodCall::getMinDuration );

		minDuration = new TableColumn<>( );
		minDuration.setCellValueFactory( cellValueFactory );
		minDuration.setText( RESOURCE_BUNDLE.getString( "columnMinDuration" ) );
		minDuration.setPrefWidth( 150 );

		return minDuration;
	}

	private TableColumn<AggregatedMethodCall, ?> createAvgDurationTableColumn( ) {
		final DurationCellValueFactory cellValueFactory = new DurationCellValueFactory( AggregatedMethodCall::getAvgDuration );

		avgDuration = new TableColumn<>( );
		avgDuration.setCellValueFactory( cellValueFactory );
		avgDuration.setText( RESOURCE_BUNDLE.getString( "columnAvgDuration" ) );
		avgDuration.setPrefWidth( 200 );

		return avgDuration;
	}

	private TableColumn<AggregatedMethodCall, ?> createMedianDurationTableColumn( ) {
		final DurationCellValueFactory cellValueFactory = new DurationCellValueFactory( AggregatedMethodCall::getMedianDuration );

		medianDuration = new TableColumn<>( );
		medianDuration.setCellValueFactory( cellValueFactory );
		medianDuration.setText( RESOURCE_BUNDLE.getString( "columnMedianDuration" ) );
		medianDuration.setPrefWidth( 150 );

		return medianDuration;
	}

	private TableColumn<AggregatedMethodCall, ?> createMaxDurationTableColumn( ) {
		final DurationCellValueFactory cellValueFactory = new DurationCellValueFactory( AggregatedMethodCall::getMaxDuration );

		maxDuration = new TableColumn<>( );
		maxDuration.setCellValueFactory( cellValueFactory );
		maxDuration.setText( RESOURCE_BUNDLE.getString( "columnMaxDuration" ) );
		maxDuration.setPrefWidth( 150 );

		return maxDuration;
	}

	private TableColumn<AggregatedMethodCall, ?> createTotalDurationTableColumn( ) {
		final DurationCellValueFactory cellValueFactory = new DurationCellValueFactory( AggregatedMethodCall::getTotalDuration );

		totalDuration = new TableColumn<>( );
		totalDuration.setCellValueFactory( cellValueFactory );
		totalDuration.setText( RESOURCE_BUNDLE.getString( "columnTotalDuration" ) );
		totalDuration.setPrefWidth( 150 );

		return totalDuration;
	}

	private Callback<TableView<AggregatedMethodCall>, Boolean> createSortPolicy( ) {
		return param -> {
			final ObservableList<TableColumn<AggregatedMethodCall, ?>> sortOrder = param.getSortOrder( );

			if ( sortOrder.size( ) == 1 ) {
				final TableColumn<AggregatedMethodCall, ?> tableColumn = sortOrder.get( 0 );

				if ( tableColumn == count ) {
					final ObservableList<AggregatedMethodCall> items = param.getItems( );
					if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
						items.sort( ( o1, o2 ) -> Long.compare( o1.getCount( ), o2.getCount( ) ) );
					} else {
						items.sort( ( o1, o2 ) -> Long.compare( o2.getCount( ), o1.getCount( ) ) );
					}
				}

				if ( tableColumn == host ) {
					final ObservableList<AggregatedMethodCall> items = param.getItems( );
					if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
						items.sort( ( o1, o2 ) -> o1.getHost( ).compareTo( o2.getHost( ) ) );
					} else {
						items.sort( ( o1, o2 ) -> o2.getHost( ).compareTo( o1.getHost( ) ) );
					}
				}

				if ( tableColumn == clazz ) {
					final ClassAppearance classAppearance = propertiesService.loadApplicationProperty( ClassAppearanceProperty.class );

					final ObservableList<AggregatedMethodCall> items = param.getItems( );
					if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
						items.sort( ( o1, o2 ) -> classAppearance.convert( o1.getClazz( ) ).compareTo( classAppearance.convert( o2.getClazz( ) ) ) );
					} else {
						items.sort( ( o1, o2 ) -> classAppearance.convert( o2.getClazz( ) ).compareTo( classAppearance.convert( o1.getClazz( ) ) ) );
					}
				}

				if ( tableColumn == method ) {
					final MethodAppearance methodAppearance = propertiesService.loadApplicationProperty( MethodAppearanceProperty.class );

					final ObservableList<AggregatedMethodCall> items = param.getItems( );
					if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
						items.sort( ( o1, o2 ) -> methodAppearance.convert( o1.getMethod( ) ).compareTo( methodAppearance.convert( o2.getMethod( ) ) ) );
					} else {
						items.sort( ( o1, o2 ) -> methodAppearance.convert( o2.getMethod( ) ).compareTo( methodAppearance.convert( o1.getMethod( ) ) ) );
					}
				}

				if ( tableColumn == minDuration ) {
					final ObservableList<AggregatedMethodCall> items = param.getItems( );
					if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
						items.sort( ( o1, o2 ) -> Long.compare( o1.getMinDuration( ), o2.getMinDuration( ) ) );
					} else {
						items.sort( ( o1, o2 ) -> Long.compare( o2.getMinDuration( ), o1.getMinDuration( ) ) );
					}
				}

				if ( tableColumn == avgDuration ) {
					final ObservableList<AggregatedMethodCall> items = param.getItems( );
					if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
						items.sort( ( o1, o2 ) -> Long.compare( o1.getAvgDuration( ), o2.getAvgDuration( ) ) );
					} else {
						items.sort( ( o1, o2 ) -> Long.compare( o2.getAvgDuration( ), o1.getAvgDuration( ) ) );
					}
				}

				if ( tableColumn == medianDuration ) {
					final ObservableList<AggregatedMethodCall> items = param.getItems( );
					if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
						items.sort( ( o1, o2 ) -> Long.compare( o1.getMedianDuration( ), o2.getMedianDuration( ) ) );
					} else {
						items.sort( ( o1, o2 ) -> Long.compare( o2.getMedianDuration( ), o1.getMedianDuration( ) ) );
					}
				}

				if ( tableColumn == maxDuration ) {
					final ObservableList<AggregatedMethodCall> items = param.getItems( );
					if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
						items.sort( ( o1, o2 ) -> Long.compare( o1.getMaxDuration( ), o2.getMaxDuration( ) ) );
					} else {
						items.sort( ( o1, o2 ) -> Long.compare( o2.getMaxDuration( ), o1.getMaxDuration( ) ) );
					}
				}

				if ( tableColumn == totalDuration ) {
					final ObservableList<AggregatedMethodCall> items = param.getItems( );
					if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
						items.sort( ( o1, o2 ) -> Long.compare( o1.getTotalDuration( ), o2.getTotalDuration( ) ) );
					} else {
						items.sort( ( o1, o2 ) -> Long.compare( o2.getTotalDuration( ), o1.getTotalDuration( ) ) );
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
	public void addSelectionChangeListener( final ChangeListener<AggregatedMethodCall> listener ) {
		getSelectionModel( ).selectedItemProperty( ).addListener( listener );
	}

	public void setDurationSuffix( final String suffix ) {
		minDuration.setText( RESOURCE_BUNDLE.getString( "columnMinDuration" ) + " " + suffix );
		avgDuration.setText( RESOURCE_BUNDLE.getString( "columnAvgDuration" ) + " " + suffix );
		medianDuration.setText( RESOURCE_BUNDLE.getString( "columnMedianDuration" ) + " " + suffix );
		maxDuration.setText( RESOURCE_BUNDLE.getString( "columnMaxDuration" ) + " " + suffix );
		totalDuration.setText( RESOURCE_BUNDLE.getString( "columnTotalDuration" ) + " " + suffix );
	}

	public CSVData getValueAsCsv( ) {
		final ObservableList<TableColumn<AggregatedMethodCall, ?>> visibleColumns = getVisibleLeafColumns( );

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

}
