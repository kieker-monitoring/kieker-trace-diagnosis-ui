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

package kieker.diagnosis.ui.tabs.methods;

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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import jfxtras.scene.control.LocalTimeTextField;
import kieker.diagnosis.architecture.service.properties.PropertiesService;
import kieker.diagnosis.architecture.ui.EnumStringConverter;
import kieker.diagnosis.architecture.ui.ViewBase;
import kieker.diagnosis.architecture.ui.components.LongTextField;
import kieker.diagnosis.service.data.MethodCall;
import kieker.diagnosis.service.methods.SearchType;
import kieker.diagnosis.service.settings.ClassAppearance;
import kieker.diagnosis.service.settings.MethodAppearance;
import kieker.diagnosis.service.settings.properties.ClassAppearanceProperty;
import kieker.diagnosis.service.settings.properties.MethodAppearanceProperty;
import kieker.diagnosis.ui.tabs.methods.components.ClassCellValueFactory;
import kieker.diagnosis.ui.tabs.methods.components.DurationCellValueFactory;
import kieker.diagnosis.ui.tabs.methods.components.MethodCellValueFactory;
import kieker.diagnosis.ui.tabs.methods.components.StyledRow;
import kieker.diagnosis.ui.tabs.methods.components.TimestampCellValueFactory;

/**
 * The view of the methods tab.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class MethodsView extends ViewBase<MethodsController> {

	// Filter
	private final TextField ivFilterHost;
	private final TextField ivFilterClass;
	private final TextField ivFilterMethod;
	private final TextField ivFilterException;
	private final LongTextField ivFilterTraceId;
	private final CheckBox ivFilterUseRegExpr;

	private final DatePicker ivFilterLowerDate;
	private final LocalTimeTextField ivFilterLowerTime;
	private final DatePicker ivFilterUpperDate;
	private final LocalTimeTextField ivFilterUpperTime;
	private final ComboBox<SearchType> ivFilterSearchType;

	private final Button ivSearchButton;

	// Table
	private final TableView<MethodCall> ivTableView;
	private TableColumn<MethodCall, String> ivHostColumn;
	private TableColumn<MethodCall, String> ivClassColumn;
	private TableColumn<MethodCall, String> ivMethodColumn;
	private final TableColumn<MethodCall, String> ivDurationColumn;
	private TableColumn<MethodCall, String> ivTimestampColumn;
	private TableColumn<MethodCall, String> ivTraceIdColumn;

	// Details
	private final TextField ivDetailsHost;
	private final TextField ivDetailsClass;
	private final TextField ivDetailsMethod;
	private final TextField ivDetailsException;
	private final TextField ivDetailsDuration;
	private final TextField ivDetailsTimestamp;
	private final TextField ivDetailsTraceId;

	// Status bar
	private final Label ivStatusLabel;

	@Inject
	private PropertiesService ivPropertiesService;

	public void initialize( ) {
		getController( ).performInitialize( );
	}

	public MethodsView( ) {

		// Filter
		{
			final TitledPane titledPane = new TitledPane( );
			titledPane.setText( getLocalizedString( "filterTitle" ) );

			{
				final GridPane outerGridPane = new GridPane( );
				outerGridPane.setHgap( 5 );
				outerGridPane.setVgap( 5 );

				for ( int i = 0; i < 3; i++ ) {
					final RowConstraints constraint = new RowConstraints( );
					constraint.setPercentHeight( 100.0 / 3 );
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
					int rowIndex = 0;

					{
						ivFilterHost = new TextField( );
						ivFilterHost.setPromptText( getLocalizedString( "filterByHost" ) );
						GridPane.setColumnIndex( ivFilterHost, columnIndex++ );
						GridPane.setRowIndex( ivFilterHost, rowIndex );
						GridPane.setHgrow( ivFilterHost, Priority.ALWAYS );

						gridPane.getChildren( ).add( ivFilterHost );
					}

					{
						ivFilterClass = new TextField( );
						ivFilterClass.setPromptText( getLocalizedString( "filterByClass" ) );
						GridPane.setColumnIndex( ivFilterClass, columnIndex++ );
						GridPane.setRowIndex( ivFilterClass, rowIndex );
						GridPane.setHgrow( ivFilterClass, Priority.ALWAYS );

						gridPane.getChildren( ).add( ivFilterClass );
					}

					{
						ivFilterMethod = new TextField( );
						ivFilterMethod.setPromptText( getLocalizedString( "filterByMethod" ) );
						GridPane.setColumnIndex( ivFilterMethod, columnIndex++ );
						GridPane.setRowIndex( ivFilterMethod, rowIndex );
						GridPane.setHgrow( ivFilterMethod, Priority.ALWAYS );

						gridPane.getChildren( ).add( ivFilterMethod );
					}

					{
						ivFilterException = new TextField( );
						ivFilterException.setPromptText( getLocalizedString( "filterByException" ) );
						GridPane.setColumnIndex( ivFilterException, columnIndex++ );
						GridPane.setRowIndex( ivFilterException, rowIndex );
						GridPane.setHgrow( ivFilterException, Priority.ALWAYS );

						gridPane.getChildren( ).add( ivFilterException );
					}

					{
						ivFilterTraceId = new LongTextField( );
						ivFilterTraceId.setPromptText( getLocalizedString( "filterByTraceId" ) );
						GridPane.setColumnIndex( ivFilterTraceId, columnIndex++ );
						GridPane.setRowIndex( ivFilterTraceId, rowIndex );
						GridPane.setHgrow( ivFilterTraceId, Priority.ALWAYS );

						gridPane.getChildren( ).add( ivFilterTraceId );
					}

					columnIndex = 0;
					rowIndex++;

					{
						ivFilterLowerDate = new DatePicker( );
						ivFilterLowerDate.setPromptText( getLocalizedString( "filterByLowerDate" ) );
						ivFilterLowerDate.setMaxWidth( Double.POSITIVE_INFINITY );

						GridPane.setColumnIndex( ivFilterLowerDate, columnIndex++ );
						GridPane.setRowIndex( ivFilterLowerDate, rowIndex );
						GridPane.setHgrow( ivFilterLowerDate, Priority.ALWAYS );

						gridPane.getChildren( ).add( ivFilterLowerDate );
					}

					{
						ivFilterLowerTime = new LocalTimeTextField( );
						ivFilterLowerTime.setPromptText( getLocalizedString( "filterByLowerTime" ) );

						// The CalendarTimeTextField doesn't recognize the default button
						ivFilterLowerTime.setOnKeyReleased( e -> {
							if ( e.getCode( ) == KeyCode.ENTER ) {
								getController( ).performSearch( );
							}
						} );

						GridPane.setColumnIndex( ivFilterLowerTime, columnIndex++ );
						GridPane.setRowIndex( ivFilterLowerTime, rowIndex );
						GridPane.setHgrow( ivFilterLowerTime, Priority.ALWAYS );

						gridPane.getChildren( ).add( ivFilterLowerTime );
					}

					{
						ivFilterUpperDate = new DatePicker( );
						ivFilterUpperDate.setPromptText( getLocalizedString( "filterByUpperDate" ) );
						ivFilterUpperDate.setMaxWidth( Double.POSITIVE_INFINITY );

						GridPane.setColumnIndex( ivFilterUpperDate, columnIndex++ );
						GridPane.setRowIndex( ivFilterUpperDate, rowIndex );
						GridPane.setHgrow( ivFilterUpperDate, Priority.ALWAYS );

						gridPane.getChildren( ).add( ivFilterUpperDate );
					}

					{
						ivFilterUpperTime = new LocalTimeTextField( );
						ivFilterUpperTime.setPromptText( getLocalizedString( "filterByUpperTime" ) );

						// The CalendarTimeTextField doesn't recognize the default button
						ivFilterUpperTime.setOnKeyReleased( e -> {
							if ( e.getCode( ) == KeyCode.ENTER ) {
								getController( ).performSearch( );
							}
						} );

						GridPane.setColumnIndex( ivFilterUpperTime, columnIndex++ );
						GridPane.setRowIndex( ivFilterUpperTime, rowIndex );
						GridPane.setHgrow( ivFilterUpperTime, Priority.ALWAYS );

						gridPane.getChildren( ).add( ivFilterUpperTime );
					}

					{
						ivFilterSearchType = new ComboBox<>( );
						ivFilterSearchType.setItems( FXCollections.observableArrayList( SearchType.values( ) ) );
						ivFilterSearchType.setConverter( new EnumStringConverter<>( SearchType.class ) );
						ivFilterSearchType.setMaxWidth( Double.POSITIVE_INFINITY );

						GridPane.setColumnIndex( ivFilterSearchType, columnIndex++ );
						GridPane.setRowIndex( ivFilterSearchType, rowIndex );

						gridPane.getChildren( ).add( ivFilterSearchType );
					}

					GridPane.setColumnIndex( gridPane, 0 );
					GridPane.setRowIndex( gridPane, 0 );
					GridPane.setRowSpan( gridPane, 2 );
					GridPane.setHgrow( gridPane, Priority.ALWAYS );

					outerGridPane.getChildren( ).add( gridPane );
				}

				{
					final Hyperlink hyperlink = new Hyperlink( );
					hyperlink.setText( getLocalizedString( "saveAsFavorite" ) );
					hyperlink.setOnAction( e -> getController( ).performSaveAsFavorite( ) );

					GridPane.setColumnIndex( hyperlink, 0 );
					GridPane.setRowIndex( hyperlink, 2 );

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
					ivSearchButton.setText( getLocalizedString( "search" ) );
					ivSearchButton.setMinWidth( 140 );
					ivSearchButton.setMaxWidth( Double.POSITIVE_INFINITY );
					ivSearchButton.setOnAction( e -> getController( ).performSearch( ) );

					GridPane.setColumnIndex( ivSearchButton, 1 );
					GridPane.setRowIndex( ivSearchButton, 2 );

					outerGridPane.getChildren( ).add( ivSearchButton );
				}

				titledPane.setContent( outerGridPane );
			}

			getChildren( ).add( titledPane );
		}

		// Table view
		{
			ivTableView = new TableView<>( );
			ivTableView.setTableMenuButtonVisible( true );
			ivTableView.setRowFactory( aParam -> new StyledRow( ) );
			ivTableView.getSelectionModel( ).selectedItemProperty( ).addListener( ( aObservable, aOldValue, aNewValue ) -> getController( ).performSelectionChange( ) );

			final Label placeholder = new Label( );
			placeholder.setText( getLocalizedString( "noDataAvailable" ) );
			ivTableView.setPlaceholder( placeholder );

			VBox.setVgrow( ivTableView, Priority.ALWAYS );

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
				ivDurationColumn = new TableColumn<>( );
				ivDurationColumn.setCellValueFactory( new DurationCellValueFactory( ) );
				ivDurationColumn.setText( getLocalizedString( "columnDuration" ) );
				ivDurationColumn.setPrefWidth( 150 );

				ivTableView.getColumns( ).add( ivDurationColumn );
			}

			{
				ivTimestampColumn = new TableColumn<>( );
				ivTimestampColumn.setCellValueFactory( new TimestampCellValueFactory( ) );
				ivTimestampColumn.setText( getLocalizedString( "columnTimestamp" ) );
				ivTimestampColumn.setPrefWidth( 150 );

				ivTableView.getColumns( ).add( ivTimestampColumn );
			}

			{
				ivTraceIdColumn = new TableColumn<>( );
				ivTraceIdColumn.setCellValueFactory( aParam -> new ReadOnlyStringWrapper( Long.toString( aParam.getValue( ).getTraceId( ) ).intern( ) ) );
				ivTraceIdColumn.setText( getLocalizedString( "columnTraceId" ) );
				ivTraceIdColumn.setPrefWidth( 150 );

				ivTableView.getColumns( ).add( ivTraceIdColumn );
			}

			// The default sorting is a little bit too slow. Therefore we use a custom sort policy which sorts directly the data.
			ivTableView.setSortPolicy( param -> {
				final ObservableList<TableColumn<MethodCall, ?>> sortOrder = param.getSortOrder( );

				if ( sortOrder.size( ) == 1 ) {
					final TableColumn<MethodCall, ?> tableColumn = sortOrder.get( 0 );

					if ( tableColumn == ivHostColumn ) {
						final ObservableList<MethodCall> items = param.getItems( );
						if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
							items.sort( ( o1, o2 ) -> o1.getHost( ).compareTo( o2.getHost( ) ) );
						} else {
							items.sort( ( o1, o2 ) -> o2.getHost( ).compareTo( o1.getHost( ) ) );
						}
					}

					if ( tableColumn == ivClassColumn ) {
						final ClassAppearance classAppearance = ivPropertiesService.loadApplicationProperty( ClassAppearanceProperty.class );

						final ObservableList<MethodCall> items = param.getItems( );
						if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
							items.sort( ( o1, o2 ) -> classAppearance.convert( o1.getClazz( ) ).compareTo( classAppearance.convert( o2.getClazz( ) ) ) );
						} else {
							items.sort( ( o1, o2 ) -> classAppearance.convert( o2.getClazz( ) ).compareTo( classAppearance.convert( o1.getClazz( ) ) ) );
						}
					}

					if ( tableColumn == ivMethodColumn ) {
						final MethodAppearance methodAppearance = ivPropertiesService.loadApplicationProperty( MethodAppearanceProperty.class );

						final ObservableList<MethodCall> items = param.getItems( );
						if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
							items.sort( ( o1, o2 ) -> methodAppearance.convert( o1.getMethod( ) ).compareTo( methodAppearance.convert( o2.getMethod( ) ) ) );
						} else {
							items.sort( ( o1, o2 ) -> methodAppearance.convert( o2.getMethod( ) ).compareTo( methodAppearance.convert( o1.getMethod( ) ) ) );
						}
					}

					if ( tableColumn == ivDurationColumn ) {
						final ObservableList<MethodCall> items = param.getItems( );
						if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
							items.sort( ( o1, o2 ) -> Long.compare( o1.getDuration( ), o2.getDuration( ) ) );
						} else {
							items.sort( ( o1, o2 ) -> Long.compare( o2.getDuration( ), o1.getDuration( ) ) );
						}
					}

					if ( tableColumn == ivTimestampColumn ) {
						final ObservableList<MethodCall> items = param.getItems( );
						if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
							items.sort( ( o1, o2 ) -> Long.compare( o1.getTimestamp( ), o2.getTimestamp( ) ) );
						} else {
							items.sort( ( o1, o2 ) -> Long.compare( o2.getTimestamp( ), o1.getTimestamp( ) ) );
						}
					}

					if ( tableColumn == ivTraceIdColumn ) {
						final ObservableList<MethodCall> items = param.getItems( );
						if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
							items.sort( ( o1, o2 ) -> Long.compare( o1.getTraceId( ), o2.getTraceId( ) ) );
						} else {
							items.sort( ( o1, o2 ) -> Long.compare( o2.getTraceId( ), o1.getTraceId( ) ) );
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
					label.setText( getLocalizedString( "labelHost" ) );

					GridPane.setColumnIndex( label, 0 );
					GridPane.setRowIndex( label, rowIndex );

					gridPane.getChildren( ).add( label );
				}

				{
					ivDetailsHost = new TextField( );
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
					hyperlink.setText( getLocalizedString( "jumpToTrace" ) );
					hyperlink.setOnAction( e -> getController( ).performJumpToTrace( ) );

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
					label.setText( getLocalizedString( "labelDuration" ) );

					GridPane.setColumnIndex( label, 2 );
					GridPane.setRowIndex( label, rowIndex );

					gridPane.getChildren( ).add( label );
				}

				{
					ivDetailsDuration = new TextField( );
					ivDetailsDuration.setEditable( false );
					ivDetailsDuration.getStyleClass( ).add( "details" );

					GridPane.setColumnIndex( ivDetailsDuration, 3 );
					GridPane.setRowIndex( ivDetailsDuration, rowIndex++ );
					GridPane.setHgrow( ivDetailsDuration, Priority.ALWAYS );

					gridPane.getChildren( ).add( ivDetailsDuration );
				}

				{
					final Label label = new Label( );
					label.setText( getLocalizedString( "labelTimestamp" ) );

					GridPane.setColumnIndex( label, 2 );
					GridPane.setRowIndex( label, rowIndex );

					gridPane.getChildren( ).add( label );
				}

				{
					ivDetailsTimestamp = new TextField( );
					ivDetailsTimestamp.setEditable( false );
					ivDetailsTimestamp.getStyleClass( ).add( "details" );

					GridPane.setColumnIndex( ivDetailsTimestamp, 3 );
					GridPane.setRowIndex( ivDetailsTimestamp, rowIndex++ );
					GridPane.setHgrow( ivDetailsTimestamp, Priority.ALWAYS );

					gridPane.getChildren( ).add( ivDetailsTimestamp );
				}

				{
					final Label label = new Label( );
					label.setText( getLocalizedString( "labelTraceId" ) );

					GridPane.setColumnIndex( label, 2 );
					GridPane.setRowIndex( label, rowIndex );

					gridPane.getChildren( ).add( label );
				}

				{
					ivDetailsTraceId = new TextField( );
					ivDetailsTraceId.setEditable( false );
					ivDetailsTraceId.getStyleClass( ).add( "details" );

					GridPane.setColumnIndex( ivDetailsTraceId, 3 );
					GridPane.setRowIndex( ivDetailsTraceId, rowIndex++ );
					GridPane.setHgrow( ivDetailsTraceId, Priority.ALWAYS );

					gridPane.getChildren( ).add( ivDetailsTraceId );
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

	public void prepareRefresh( ) {
		getController( ).performPrepareRefresh( );
	}

	TableView<MethodCall> getTableView( ) {
		return ivTableView;
	}

	TableColumn<MethodCall, String> getDurationColumn( ) {
		return ivDurationColumn;
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

	LongTextField getFilterTraceId( ) {
		return ivFilterTraceId;
	}

	CheckBox getFilterUseRegExpr( ) {
		return ivFilterUseRegExpr;
	}

	ComboBox<SearchType> getFilterSearchType( ) {
		return ivFilterSearchType;
	}

	DatePicker getFilterLowerDate( ) {
		return ivFilterLowerDate;
	}

	LocalTimeTextField getFilterLowerTime( ) {
		return ivFilterLowerTime;
	}

	DatePicker getFilterUpperDate( ) {
		return ivFilterUpperDate;
	}

	LocalTimeTextField getFilterUpperTime( ) {
		return ivFilterUpperTime;
	}

	Label getStatusLabel( ) {
		return ivStatusLabel;
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

	TextField getDetailsDuration( ) {
		return ivDetailsDuration;
	}

	TextField getDetailsTimestamp( ) {
		return ivDetailsTimestamp;
	}

	TextField getDetailsTraceId( ) {
		return ivDetailsTraceId;
	}

	@Override
	public void setParameter( final Object aParameter ) {
		getController( ).performSetParameter( aParameter );
	}

	public Button getSearchButton( ) {
		return ivSearchButton;
	}

	public void performRefresh( ) {
		getController( ).performRefresh( );
	}

}
