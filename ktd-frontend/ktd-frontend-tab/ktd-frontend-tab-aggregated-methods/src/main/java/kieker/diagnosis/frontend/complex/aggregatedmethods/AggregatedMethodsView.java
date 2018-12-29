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

package kieker.diagnosis.frontend.complex.aggregatedmethods;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import kieker.diagnosis.backend.data.AggregatedMethodCall;
import kieker.diagnosis.backend.properties.PropertiesService;
import kieker.diagnosis.backend.search.aggregatedmethods.SearchType;
import kieker.diagnosis.backend.settings.ClassAppearance;
import kieker.diagnosis.backend.settings.MethodAppearance;
import kieker.diagnosis.backend.settings.properties.ClassAppearanceProperty;
import kieker.diagnosis.backend.settings.properties.MethodAppearanceProperty;
import kieker.diagnosis.frontend.base.ui.EnumStringConverter;
import kieker.diagnosis.frontend.base.ui.ViewBase;
import kieker.diagnosis.frontend.complex.aggregatedmethods.components.ClassCellValueFactory;
import kieker.diagnosis.frontend.complex.aggregatedmethods.components.DurationCellValueFactory;
import kieker.diagnosis.frontend.complex.aggregatedmethods.components.MethodCellValueFactory;
import kieker.diagnosis.frontend.complex.aggregatedmethods.components.StyledRow;

/**
 * The view of the aggregated methods tab.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class AggregatedMethodsView extends ViewBase<AggregatedMethodsController> {

	// Filter
	private final TextField ivFilterHost;
	private final TextField ivFilterClass;
	private final TextField ivFilterMethod;
	private final TextField ivFilterException;
	private final CheckBox ivFilterUseRegExpr;
	private final ComboBox<SearchType> ivFilterSearchType;

	private final Button ivSearchButton;

	// Table
	private final TableView<AggregatedMethodCall> ivTableView;

	private final TableColumn<AggregatedMethodCall, String> ivColumnMinDuration;
	private final TableColumn<AggregatedMethodCall, String> ivColumnAvgDuration;
	private final TableColumn<AggregatedMethodCall, String> ivColumnMedianDuration;
	private final TableColumn<AggregatedMethodCall, String> ivColumnMaxDuration;
	private final TableColumn<AggregatedMethodCall, String> ivColumnTotalDuration;

	// Details
	private final TextField ivDetailsCount;
	private final TextField ivDetailsHost;
	private final TextField ivDetailsClass;
	private final TextField ivDetailsMethod;
	private final TextField ivDetailsException;
	private final TextField ivDetailsMinDuration;
	private final TextField ivDetailsAvgDuration;
	private final TextField ivDetailsMedianDuration;
	private final TextField ivDetailsMaxDuration;
	private final TextField ivDetailsTotalDuration;

	// Status bar
	private final Label ivStatusLabel;
	private TableColumn<AggregatedMethodCall, String> ivHostColumn;
	private TableColumn<AggregatedMethodCall, String> ivClassColumn;
	private TableColumn<AggregatedMethodCall, String> ivMethodColumn;
	private TableColumn<AggregatedMethodCall, String> ivCountColumn;

	@Inject
	private PropertiesService ivPropertiesService;

	public void initialize( ) {
		getController( ).performInitialize( );
	}

	@Inject
	public AggregatedMethodsView( ) {

		// Filter
		{
			final TitledPane titledPane = new TitledPane( );
			titledPane.setText( getLocalizedString( "filterTitle" ) );

			{
				final GridPane outerGridPane = new GridPane( );
				outerGridPane.setHgap( 5 );
				outerGridPane.setVgap( 5 );

				for ( int i = 0; i < 2; i++ ) {
					final RowConstraints constraint = new RowConstraints( );
					constraint.setPercentHeight( 100.0 / 2 );
					outerGridPane.getRowConstraints( ).add( constraint );
				}

				{
					final GridPane gridPane = new GridPane( );
					gridPane.setHgap( 5 );
					gridPane.setVgap( 5 );

					for ( int i = 0; i < 5; i++ ) {
						final ColumnConstraints constraint = new ColumnConstraints( );
						constraint.setPercentWidth( 100.0 / 5 );
						gridPane.getColumnConstraints( ).add( constraint );
					}

					int columnIndex = 0;

					{
						ivFilterHost = new TextField( );
						ivFilterHost.setId( "tabAggregatedMethodsFilterHost" );
						ivFilterHost.setPromptText( getLocalizedString( "filterByHost" ) );
						GridPane.setColumnIndex( ivFilterHost, columnIndex++ );
						GridPane.setRowIndex( ivFilterHost, 0 );
						GridPane.setHgrow( ivFilterHost, Priority.ALWAYS );

						gridPane.getChildren( ).add( ivFilterHost );
					}

					{
						ivFilterClass = new TextField( );
						ivFilterClass.setPromptText( getLocalizedString( "filterByClass" ) );
						GridPane.setColumnIndex( ivFilterClass, columnIndex++ );
						GridPane.setRowIndex( ivFilterClass, 0 );
						GridPane.setHgrow( ivFilterClass, Priority.ALWAYS );

						gridPane.getChildren( ).add( ivFilterClass );
					}

					{
						ivFilterMethod = new TextField( );
						ivFilterMethod.setPromptText( getLocalizedString( "filterByMethod" ) );
						GridPane.setColumnIndex( ivFilterMethod, columnIndex++ );
						GridPane.setRowIndex( ivFilterMethod, 0 );
						GridPane.setHgrow( ivFilterMethod, Priority.ALWAYS );

						gridPane.getChildren( ).add( ivFilterMethod );
					}

					{
						ivFilterException = new TextField( );
						ivFilterException.setPromptText( getLocalizedString( "filterByException" ) );
						GridPane.setColumnIndex( ivFilterException, columnIndex++ );
						GridPane.setRowIndex( ivFilterException, 0 );
						GridPane.setHgrow( ivFilterException, Priority.ALWAYS );

						gridPane.getChildren( ).add( ivFilterException );
					}

					{
						ivFilterSearchType = new ComboBox<>( );
						ivFilterSearchType.setItems( FXCollections.observableArrayList( SearchType.values( ) ) );
						ivFilterSearchType.setConverter( new EnumStringConverter<>( SearchType.class ) );
						ivFilterSearchType.setMaxWidth( Double.POSITIVE_INFINITY );

						GridPane.setColumnIndex( ivFilterSearchType, columnIndex++ );
						GridPane.setRowIndex( ivFilterSearchType, 0 );

						gridPane.getChildren( ).add( ivFilterSearchType );
					}

					GridPane.setColumnIndex( gridPane, 0 );
					GridPane.setRowIndex( gridPane, 0 );
					GridPane.setHgrow( gridPane, Priority.ALWAYS );

					outerGridPane.getChildren( ).add( gridPane );
				}

				{
					final Hyperlink hyperlink = new Hyperlink( );
					hyperlink.setText( getLocalizedString( "saveAsFavorite" ) );
					hyperlink.setOnAction( e -> getController( ).performSaveAsFavorite( ) );

					GridPane.setColumnIndex( hyperlink, 0 );
					GridPane.setRowIndex( hyperlink, 1 );

					outerGridPane.getChildren( ).add( hyperlink );
				}

				{
					ivFilterUseRegExpr = new CheckBox( );
					ivFilterUseRegExpr.setText( getLocalizedString( "filterUseRegExpr" ) );

					GridPane.setColumnIndex( ivFilterUseRegExpr, 1 );
					GridPane.setRowIndex( ivFilterUseRegExpr, 0 );
					GridPane.setValignment( ivFilterUseRegExpr, VPos.CENTER );

					outerGridPane.getChildren( ).add( ivFilterUseRegExpr );
				}

				{
					ivSearchButton = new Button( );
					ivSearchButton.setId( "tabAggregatedMethodsSearch" );
					ivSearchButton.setText( getLocalizedString( "search" ) );
					ivSearchButton.setMinWidth( 140 );
					ivSearchButton.setMaxWidth( Double.POSITIVE_INFINITY );
					ivSearchButton.setOnAction( e -> getController( ).performSearch( ) );
					ivSearchButton.setGraphic( createIcon( Icon.SEARCH ) );

					GridPane.setColumnIndex( ivSearchButton, 1 );
					GridPane.setRowIndex( ivSearchButton, 1 );

					outerGridPane.getChildren( ).add( ivSearchButton );
				}

				titledPane.setContent( outerGridPane );
			}

			getChildren( ).add( titledPane );
		}

		// Table view
		{
			ivTableView = new TableView<>( );
			ivTableView.setId( "tabAggregatedMethodsTable" );
			ivTableView.setTableMenuButtonVisible( true );
			ivTableView.setRowFactory( aParam -> new StyledRow( ) );
			ivTableView.getSelectionModel( ).selectedItemProperty( ).addListener( ( aObservable, aOldValue, aNewValue ) -> getController( ).performSelectionChange( ) );

			final Label placeholder = new Label( );
			placeholder.setText( getLocalizedString( "noDataAvailable" ) );
			ivTableView.setPlaceholder( placeholder );

			VBox.setVgrow( ivTableView, Priority.ALWAYS );

			{
				ivCountColumn = new TableColumn<>( );
				ivCountColumn.setCellValueFactory( aParam -> new ReadOnlyStringWrapper( Integer.toString( aParam.getValue( ).getCount( ) ).intern( ) ) );
				ivCountColumn.setText( getLocalizedString( "columnCount" ) );
				ivCountColumn.setPrefWidth( 100 );

				ivTableView.getColumns( ).add( ivCountColumn );
			}

			{
				ivHostColumn = new TableColumn<>( );
				ivHostColumn.setCellValueFactory( aParam -> new ReadOnlyObjectWrapper<>( aParam.getValue( ).getHost( ) ) );
				ivHostColumn.setText( getLocalizedString( "columnHost" ) );
				ivHostColumn.setPrefWidth( 100 );

				ivTableView.getColumns( ).add( ivHostColumn );
			}

			{
				ivClassColumn = new TableColumn<>( );
				ivClassColumn.setCellValueFactory( new ClassCellValueFactory( ) );
				ivClassColumn.setText( getLocalizedString( "columnClass" ) );
				ivClassColumn.setPrefWidth( 200 );

				ivTableView.getColumns( ).add( ivClassColumn );
			}

			{
				ivMethodColumn = new TableColumn<>( );
				ivMethodColumn.setCellValueFactory( new MethodCellValueFactory( ) );
				ivMethodColumn.setText( getLocalizedString( "columnMethod" ) );
				ivMethodColumn.setPrefWidth( 400 );

				ivTableView.getColumns( ).add( ivMethodColumn );
			}

			{
				final DurationCellValueFactory cellValueFactory = new DurationCellValueFactory( );
				cellValueFactory.setGetter( AggregatedMethodCall::getMinDuration );

				ivColumnMinDuration = new TableColumn<>( );
				ivColumnMinDuration.setCellValueFactory( cellValueFactory );
				ivColumnMinDuration.setText( getLocalizedString( "columnMinDuration" ) );
				ivColumnMinDuration.setPrefWidth( 150 );

				ivTableView.getColumns( ).add( ivColumnMinDuration );
			}

			{
				final DurationCellValueFactory cellValueFactory = new DurationCellValueFactory( );
				cellValueFactory.setGetter( AggregatedMethodCall::getAvgDuration );

				ivColumnAvgDuration = new TableColumn<>( );
				ivColumnAvgDuration.setCellValueFactory( cellValueFactory );
				ivColumnAvgDuration.setText( getLocalizedString( "columnAvgDuration" ) );
				ivColumnAvgDuration.setPrefWidth( 200 );

				ivTableView.getColumns( ).add( ivColumnAvgDuration );
			}

			{
				final DurationCellValueFactory cellValueFactory = new DurationCellValueFactory( );
				cellValueFactory.setGetter( AggregatedMethodCall::getMedianDuration );

				ivColumnMedianDuration = new TableColumn<>( );
				ivColumnMedianDuration.setCellValueFactory( cellValueFactory );
				ivColumnMedianDuration.setText( getLocalizedString( "columnMedianDuration" ) );
				ivColumnMedianDuration.setPrefWidth( 150 );

				ivTableView.getColumns( ).add( ivColumnMedianDuration );
			}

			{
				final DurationCellValueFactory cellValueFactory = new DurationCellValueFactory( );
				cellValueFactory.setGetter( AggregatedMethodCall::getMaxDuration );

				ivColumnMaxDuration = new TableColumn<>( );
				ivColumnMaxDuration.setCellValueFactory( cellValueFactory );
				ivColumnMaxDuration.setText( getLocalizedString( "columnMaxDuration" ) );
				ivColumnMaxDuration.setPrefWidth( 150 );

				ivTableView.getColumns( ).add( ivColumnMaxDuration );
			}

			{
				final DurationCellValueFactory cellValueFactory = new DurationCellValueFactory( );
				cellValueFactory.setGetter( AggregatedMethodCall::getTotalDuration );

				ivColumnTotalDuration = new TableColumn<>( );
				ivColumnTotalDuration.setCellValueFactory( cellValueFactory );
				ivColumnTotalDuration.setText( getLocalizedString( "columnTotalDuration" ) );
				ivColumnTotalDuration.setPrefWidth( 150 );

				ivTableView.getColumns( ).add( ivColumnTotalDuration );
			}

			// The default sorting is a little bit too slow. Therefore we use a custom sort policy which sorts directly the data.
			ivTableView.setSortPolicy( param -> {
				final ObservableList<TableColumn<AggregatedMethodCall, ?>> sortOrder = param.getSortOrder( );

				if ( sortOrder.size( ) == 1 ) {
					final TableColumn<AggregatedMethodCall, ?> tableColumn = sortOrder.get( 0 );

					if ( tableColumn == ivCountColumn ) {
						final ObservableList<AggregatedMethodCall> items = param.getItems( );
						if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
							items.sort( ( o1, o2 ) -> Long.compare( o1.getCount( ), o2.getCount( ) ) );
						} else {
							items.sort( ( o1, o2 ) -> Long.compare( o2.getCount( ), o1.getCount( ) ) );
						}
					}

					if ( tableColumn == ivHostColumn ) {
						final ObservableList<AggregatedMethodCall> items = param.getItems( );
						if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
							items.sort( ( o1, o2 ) -> o1.getHost( ).compareTo( o2.getHost( ) ) );
						} else {
							items.sort( ( o1, o2 ) -> o2.getHost( ).compareTo( o1.getHost( ) ) );
						}
					}

					if ( tableColumn == ivClassColumn ) {
						final ClassAppearance classAppearance = ivPropertiesService.loadApplicationProperty( ClassAppearanceProperty.class );

						final ObservableList<AggregatedMethodCall> items = param.getItems( );
						if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
							items.sort( ( o1, o2 ) -> classAppearance.convert( o1.getClazz( ) ).compareTo( classAppearance.convert( o2.getClazz( ) ) ) );
						} else {
							items.sort( ( o1, o2 ) -> classAppearance.convert( o2.getClazz( ) ).compareTo( classAppearance.convert( o1.getClazz( ) ) ) );
						}
					}

					if ( tableColumn == ivMethodColumn ) {
						final MethodAppearance methodAppearance = ivPropertiesService.loadApplicationProperty( MethodAppearanceProperty.class );

						final ObservableList<AggregatedMethodCall> items = param.getItems( );
						if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
							items.sort( ( o1, o2 ) -> methodAppearance.convert( o1.getMethod( ) ).compareTo( methodAppearance.convert( o2.getMethod( ) ) ) );
						} else {
							items.sort( ( o1, o2 ) -> methodAppearance.convert( o2.getMethod( ) ).compareTo( methodAppearance.convert( o1.getMethod( ) ) ) );
						}
					}

					if ( tableColumn == ivColumnMinDuration ) {
						final ObservableList<AggregatedMethodCall> items = param.getItems( );
						if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
							items.sort( ( o1, o2 ) -> Long.compare( o1.getMinDuration( ), o2.getMinDuration( ) ) );
						} else {
							items.sort( ( o1, o2 ) -> Long.compare( o2.getMinDuration( ), o1.getMinDuration( ) ) );
						}
					}

					if ( tableColumn == ivColumnAvgDuration ) {
						final ObservableList<AggregatedMethodCall> items = param.getItems( );
						if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
							items.sort( ( o1, o2 ) -> Long.compare( o1.getAvgDuration( ), o2.getAvgDuration( ) ) );
						} else {
							items.sort( ( o1, o2 ) -> Long.compare( o2.getAvgDuration( ), o1.getAvgDuration( ) ) );
						}
					}

					if ( tableColumn == ivColumnMedianDuration ) {
						final ObservableList<AggregatedMethodCall> items = param.getItems( );
						if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
							items.sort( ( o1, o2 ) -> Long.compare( o1.getMedianDuration( ), o2.getMedianDuration( ) ) );
						} else {
							items.sort( ( o1, o2 ) -> Long.compare( o2.getMedianDuration( ), o1.getMedianDuration( ) ) );
						}
					}

					if ( tableColumn == ivColumnMaxDuration ) {
						final ObservableList<AggregatedMethodCall> items = param.getItems( );
						if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
							items.sort( ( o1, o2 ) -> Long.compare( o1.getMaxDuration( ), o2.getMaxDuration( ) ) );
						} else {
							items.sort( ( o1, o2 ) -> Long.compare( o2.getMaxDuration( ), o1.getMaxDuration( ) ) );
						}
					}

					if ( tableColumn == ivColumnTotalDuration ) {
						final ObservableList<AggregatedMethodCall> items = param.getItems( );
						if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
							items.sort( ( o1, o2 ) -> Long.compare( o1.getTotalDuration( ), o2.getTotalDuration( ) ) );
						} else {
							items.sort( ( o1, o2 ) -> Long.compare( o2.getTotalDuration( ), o1.getTotalDuration( ) ) );
						}
					}
				}

				return true;
			} );

			getChildren( ).add( ivTableView );

		}

		// Detail panel
		{
			final TitledPane titledPane = new TitledPane( );
			titledPane.setText( getLocalizedString( "detailTitle" ) );

			{
				final GridPane gridPane = new GridPane( );

				int rowIndex = 0;

				{
					final Label label = new Label( );
					label.setText( getLocalizedString( "labelCount" ) );

					GridPane.setColumnIndex( label, 0 );
					GridPane.setRowIndex( label, rowIndex );

					gridPane.getChildren( ).add( label );
				}
				{
					ivDetailsCount = new TextField( );
					ivDetailsCount.setEditable( false );
					ivDetailsCount.getStyleClass( ).add( "details" );

					GridPane.setColumnIndex( ivDetailsCount, 1 );
					GridPane.setRowIndex( ivDetailsCount, rowIndex++ );
					GridPane.setHgrow( ivDetailsCount, Priority.ALWAYS );

					gridPane.getChildren( ).add( ivDetailsCount );
				}

				{
					final Label label = new Label( );
					label.setText( getLocalizedString( "labelHost" ) );

					GridPane.setColumnIndex( label, 0 );
					GridPane.setRowIndex( label, rowIndex );

					gridPane.getChildren( ).add( label );
				}

				{
					ivDetailsHost = new TextField( );
					ivDetailsHost.setId( "tabAggregatedMethodsDetailHost" );
					ivDetailsHost.setEditable( false );
					ivDetailsHost.getStyleClass( ).add( "details" );

					GridPane.setColumnIndex( ivDetailsHost, 1 );
					GridPane.setRowIndex( ivDetailsHost, rowIndex++ );
					GridPane.setHgrow( ivDetailsHost, Priority.ALWAYS );

					gridPane.getChildren( ).add( ivDetailsHost );
				}

				{
					final Label label = new Label( );
					label.setText( getLocalizedString( "labelClass" ) );

					GridPane.setColumnIndex( label, 0 );
					GridPane.setRowIndex( label, rowIndex );

					gridPane.getChildren( ).add( label );
				}

				{
					ivDetailsClass = new TextField( );
					ivDetailsClass.setEditable( false );
					ivDetailsClass.getStyleClass( ).add( "details" );

					GridPane.setColumnIndex( ivDetailsClass, 1 );
					GridPane.setRowIndex( ivDetailsClass, rowIndex++ );
					GridPane.setHgrow( ivDetailsClass, Priority.ALWAYS );

					gridPane.getChildren( ).add( ivDetailsClass );
				}

				{
					final Label label = new Label( );
					label.setText( getLocalizedString( "labelMethod" ) );

					GridPane.setColumnIndex( label, 0 );
					GridPane.setRowIndex( label, rowIndex );

					gridPane.getChildren( ).add( label );
				}

				{
					ivDetailsMethod = new TextField( );
					ivDetailsMethod.setEditable( false );
					ivDetailsMethod.getStyleClass( ).add( "details" );

					GridPane.setColumnIndex( ivDetailsMethod, 1 );
					GridPane.setRowIndex( ivDetailsMethod, rowIndex++ );
					GridPane.setHgrow( ivDetailsMethod, Priority.ALWAYS );

					gridPane.getChildren( ).add( ivDetailsMethod );
				}

				{
					final Label label = new Label( );
					label.setText( getLocalizedString( "labelException" ) );

					GridPane.setColumnIndex( label, 0 );
					GridPane.setRowIndex( label, rowIndex );

					gridPane.getChildren( ).add( label );
				}

				{
					ivDetailsException = new TextField( );
					ivDetailsException.setEditable( false );
					ivDetailsException.getStyleClass( ).add( "details" );

					GridPane.setColumnIndex( ivDetailsException, 1 );
					GridPane.setRowIndex( ivDetailsException, rowIndex++ );
					GridPane.setHgrow( ivDetailsException, Priority.ALWAYS );

					gridPane.getChildren( ).add( ivDetailsException );
				}

				{
					final Hyperlink hyperlink = new Hyperlink( );
					hyperlink.setText( getLocalizedString( "jumpToMethods" ) );
					hyperlink.setOnAction( e -> getController( ).performJumpToMethods( ) );

					GridPane.setColumnIndex( hyperlink, 0 );
					GridPane.setColumnSpan( hyperlink, 2 );
					GridPane.setRowIndex( hyperlink, rowIndex++ );
					GridPane.setHgrow( hyperlink, Priority.ALWAYS );
					GridPane.setMargin( hyperlink, new Insets( 0, 0, 0, -5 ) );

					gridPane.getChildren( ).add( hyperlink );
				}

				rowIndex = 0;

				{
					final Label label = new Label( );
					label.setText( getLocalizedString( "labelMinDuration" ) );

					GridPane.setColumnIndex( label, 2 );
					GridPane.setRowIndex( label, rowIndex );

					gridPane.getChildren( ).add( label );
				}

				{
					ivDetailsMinDuration = new TextField( );
					ivDetailsMinDuration.setEditable( false );
					ivDetailsMinDuration.getStyleClass( ).add( "details" );

					GridPane.setColumnIndex( ivDetailsMinDuration, 3 );
					GridPane.setRowIndex( ivDetailsMinDuration, rowIndex++ );
					GridPane.setHgrow( ivDetailsMinDuration, Priority.ALWAYS );

					gridPane.getChildren( ).add( ivDetailsMinDuration );
				}

				{
					final Label label = new Label( );
					label.setText( getLocalizedString( "labelAvgDuration" ) );

					GridPane.setColumnIndex( label, 2 );
					GridPane.setRowIndex( label, rowIndex );

					gridPane.getChildren( ).add( label );
				}

				{
					ivDetailsAvgDuration = new TextField( );
					ivDetailsAvgDuration.setEditable( false );
					ivDetailsAvgDuration.getStyleClass( ).add( "details" );

					GridPane.setColumnIndex( ivDetailsAvgDuration, 3 );
					GridPane.setRowIndex( ivDetailsAvgDuration, rowIndex++ );
					GridPane.setHgrow( ivDetailsAvgDuration, Priority.ALWAYS );

					gridPane.getChildren( ).add( ivDetailsAvgDuration );
				}

				{
					final Label label = new Label( );
					label.setText( getLocalizedString( "labelMedianDuration" ) );

					GridPane.setColumnIndex( label, 2 );
					GridPane.setRowIndex( label, rowIndex );

					gridPane.getChildren( ).add( label );
				}

				{
					ivDetailsMedianDuration = new TextField( );
					ivDetailsMedianDuration.setEditable( false );
					ivDetailsMedianDuration.getStyleClass( ).add( "details" );

					GridPane.setColumnIndex( ivDetailsMedianDuration, 3 );
					GridPane.setRowIndex( ivDetailsMedianDuration, rowIndex++ );
					GridPane.setHgrow( ivDetailsMedianDuration, Priority.ALWAYS );

					gridPane.getChildren( ).add( ivDetailsMedianDuration );
				}

				{
					final Label label = new Label( );
					label.setText( getLocalizedString( "labelMaxDuration" ) );

					GridPane.setColumnIndex( label, 2 );
					GridPane.setRowIndex( label, rowIndex );

					gridPane.getChildren( ).add( label );
				}

				{
					ivDetailsMaxDuration = new TextField( );
					ivDetailsMaxDuration.setEditable( false );
					ivDetailsMaxDuration.getStyleClass( ).add( "details" );

					GridPane.setColumnIndex( ivDetailsMaxDuration, 3 );
					GridPane.setRowIndex( ivDetailsMaxDuration, rowIndex++ );
					GridPane.setHgrow( ivDetailsMaxDuration, Priority.ALWAYS );

					gridPane.getChildren( ).add( ivDetailsMaxDuration );
				}

				{
					final Label label = new Label( );
					label.setText( getLocalizedString( "labelTotalDuration" ) );

					GridPane.setColumnIndex( label, 2 );
					GridPane.setRowIndex( label, rowIndex );

					gridPane.getChildren( ).add( label );
				}

				{
					ivDetailsTotalDuration = new TextField( );
					ivDetailsTotalDuration.setEditable( false );
					ivDetailsTotalDuration.getStyleClass( ).add( "details" );

					GridPane.setColumnIndex( ivDetailsTotalDuration, 3 );
					GridPane.setRowIndex( ivDetailsTotalDuration, rowIndex++ );
					GridPane.setHgrow( ivDetailsTotalDuration, Priority.ALWAYS );

					gridPane.getChildren( ).add( ivDetailsTotalDuration );
				}

				titledPane.setContent( gridPane );
			}

			getChildren( ).add( titledPane );
		}

		// Status bar
		{
			final HBox hBox = new HBox( );
			VBox.setMargin( hBox, new Insets( 5 ) );

			{
				ivStatusLabel = new Label( );
				ivStatusLabel.setMaxWidth( Double.POSITIVE_INFINITY );
				HBox.setHgrow( ivStatusLabel, Priority.ALWAYS );
				HBox.setMargin( ivStatusLabel, new Insets( 5, 0, 0, 0 ) );

				hBox.getChildren( ).add( ivStatusLabel );
			}

			{
				final Hyperlink hyperlink = new Hyperlink( );
				hyperlink.setText( getLocalizedString( "exportToCSV" ) );
				hyperlink.setOnAction( e -> getController( ).performExportToCSV( ) );

				hBox.getChildren( ).add( hyperlink );
			}

			getChildren( ).add( hBox );
		}

	}

	TextField getFilterHost( ) {
		return ivFilterHost;
	}

	TextField getFilterClass( ) {
		return ivFilterClass;
	}

	TextField getFilterMethod( ) {
		return ivFilterMethod;
	}

	TextField getFilterException( ) {
		return ivFilterException;
	}

	CheckBox getFilterUseRegExpr( ) {
		return ivFilterUseRegExpr;
	}

	ComboBox<SearchType> getFilterSearchType( ) {
		return ivFilterSearchType;
	}

	public Button getSearchButton( ) {
		return ivSearchButton;
	}

	TableColumn<AggregatedMethodCall, String> getColumnMinDuration( ) {
		return ivColumnMinDuration;
	}

	TableColumn<AggregatedMethodCall, String> getColumnAvgDuration( ) {
		return ivColumnAvgDuration;
	}

	TableColumn<AggregatedMethodCall, String> getColumnMedianDuration( ) {
		return ivColumnMedianDuration;
	}

	TableColumn<AggregatedMethodCall, String> getColumnMaxDuration( ) {
		return ivColumnMaxDuration;
	}

	TableColumn<AggregatedMethodCall, String> getColumnTotalDuration( ) {
		return ivColumnTotalDuration;
	}

	Label getStatusLabel( ) {
		return ivStatusLabel;
	}

	TextField getDetailsCount( ) {
		return ivDetailsCount;
	}

	TextField getDetailsHost( ) {
		return ivDetailsHost;
	}

	TextField getDetailsClass( ) {
		return ivDetailsClass;
	}

	TextField getDetailsMethod( ) {
		return ivDetailsMethod;
	}

	TextField getDetailsException( ) {
		return ivDetailsException;
	}

	TextField getDetailsMinDuration( ) {
		return ivDetailsMinDuration;
	}

	TextField getDetailsAvgDuration( ) {
		return ivDetailsAvgDuration;
	}

	TextField getDetailsMedianDuration( ) {
		return ivDetailsMedianDuration;
	}

	TextField getDetailsMaxDuration( ) {
		return ivDetailsMaxDuration;
	}

	TextField getDetailsTotalDuration( ) {
		return ivDetailsTotalDuration;
	}

	@Override
	public void setParameter( final Object aParameter ) {
		getController( ).performSetParameter( aParameter );
	}

	public void prepareRefresh( ) {
		getController( ).performPrepareRefresh( );
	}

	public void performRefresh( ) {
		getController( ).performRefresh( );
	}

	TableView<AggregatedMethodCall> getTableView( ) {
		return ivTableView;
	}

}
