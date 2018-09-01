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

import com.google.inject.Inject;
import com.google.inject.Singleton;

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
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.SortType;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import jfxtras.scene.control.LocalTimeTextField;
import kieker.diagnosis.architecture.service.properties.PropertiesService;
import kieker.diagnosis.architecture.ui.EnumStringConverter;
import kieker.diagnosis.architecture.ui.ViewBase;
import kieker.diagnosis.architecture.ui.components.LongTextField;
import kieker.diagnosis.service.data.MethodCall;
import kieker.diagnosis.service.settings.ClassAppearance;
import kieker.diagnosis.service.settings.MethodAppearance;
import kieker.diagnosis.service.settings.properties.ClassAppearanceProperty;
import kieker.diagnosis.service.settings.properties.MethodAppearanceProperty;
import kieker.diagnosis.service.traces.SearchType;
import kieker.diagnosis.ui.tabs.traces.components.ClassCellValueFactory;
import kieker.diagnosis.ui.tabs.traces.components.DurationCellValueFactory;
import kieker.diagnosis.ui.tabs.traces.components.MethodCellValueFactory;
import kieker.diagnosis.ui.tabs.traces.components.StyledRow;
import kieker.diagnosis.ui.tabs.traces.components.TimestampCellValueFactory;

/**
 * The view of the traces tab.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class TracesView extends ViewBase<TracesController> {

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
	private final CheckBox ivFilterSearchWholeTrace;
	private final ComboBox<SearchType> ivFilterSearchType;

	// Table
	private final TreeTableView<MethodCall> ivTreeTableView;
	private TreeTableColumn<MethodCall, String> ivDurationColumn;
	private TreeTableColumn<MethodCall, String> ivHostColumn;
	private TreeTableColumn<MethodCall, String> ivClassColumn;
	private TreeTableColumn<MethodCall, String> ivMethodColumn;
	private TreeTableColumn<MethodCall, String> ivTraceDepthColumn;
	private TreeTableColumn<MethodCall, String> ivTraceSizeColumn;
	private TreeTableColumn<MethodCall, String> ivPercentColumn;
	private TreeTableColumn<MethodCall, String> ivTimestampColumn;
	private TreeTableColumn<MethodCall, String> ivTraceIdColumn;

	// Details
	private final TextField ivDetailsHost;
	private final TextField ivDetailsClass;
	private final TextField ivDetailsMethod;
	private final TextField ivDetailsException;
	private final TextField ivDetailsTraceDepth;
	private final TextField ivDetailsTraceSize;
	private final TextField ivDetailsDuration;
	private final TextField ivDetailsPercent;
	private final TextField ivDetailsTimestamp;
	private final TextField ivDetailsTraceId;

	private final Label ivStatusLabel;

	private final Button ivSearchButton;

	@Inject
	private PropertiesService ivPropertiesService;

	@Inject
	public TracesView( ) {
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
					ivFilterUseRegExpr = new CheckBox( );
					ivFilterUseRegExpr.setText( getLocalizedString( "filterUseRegExpr" ) );

					GridPane.setColumnIndex( ivFilterUseRegExpr, 1 );
					GridPane.setRowIndex( ivFilterUseRegExpr, 0 );
					GridPane.setValignment( ivFilterUseRegExpr, VPos.CENTER );

					outerGridPane.getChildren( ).add( ivFilterUseRegExpr );
				}

				{
					ivFilterSearchWholeTrace = new CheckBox( );
					ivFilterSearchWholeTrace.setText( getLocalizedString( "filterSearchWholeTrace" ) );

					GridPane.setColumnIndex( ivFilterSearchWholeTrace, 1 );
					GridPane.setRowIndex( ivFilterSearchWholeTrace, 1 );
					GridPane.setValignment( ivFilterSearchWholeTrace, VPos.CENTER );

					outerGridPane.getChildren( ).add( ivFilterSearchWholeTrace );
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

		// Tree table
		{
			ivTreeTableView = new TreeTableView<>( );
			ivTreeTableView.setShowRoot( false );
			ivTreeTableView.setTableMenuButtonVisible( true );
			ivTreeTableView.getSelectionModel( ).selectedItemProperty( ).addListener( ( aObservable, aOldValue, aNewValue ) -> getController( ).performSelectionChange( ) );
			ivTreeTableView.setRowFactory( aParam -> new StyledRow( ) );

			final Label placeholder = new Label( );
			placeholder.setText( getLocalizedString( "noDataAvailable" ) );
			ivTreeTableView.setPlaceholder( placeholder );

			VBox.setVgrow( ivTreeTableView, Priority.ALWAYS );

			{
				ivHostColumn = new TreeTableColumn<>( );
				ivHostColumn.setCellValueFactory( aParam -> new ReadOnlyStringWrapper( aParam.getValue( ).getValue( ).getHost( ) ) );
				ivHostColumn.setText( getLocalizedString( "columnHost" ) );
				ivHostColumn.setPrefWidth( 100 );

				ivTreeTableView.getColumns( ).add( ivHostColumn );
			}

			{
				ivClassColumn = new TreeTableColumn<>( );
				ivClassColumn.setCellValueFactory( new ClassCellValueFactory( ) );
				ivClassColumn.setText( getLocalizedString( "columnClass" ) );
				ivClassColumn.setPrefWidth( 200 );

				ivTreeTableView.getColumns( ).add( ivClassColumn );
			}

			{
				ivMethodColumn = new TreeTableColumn<>( );
				ivMethodColumn.setCellValueFactory( new MethodCellValueFactory( ) );
				ivMethodColumn.setText( getLocalizedString( "columnMethod" ) );
				ivMethodColumn.setPrefWidth( 400 );

				ivTreeTableView.getColumns( ).add( ivMethodColumn );
			}

			{
				ivTraceDepthColumn = new TreeTableColumn<>( );
				ivTraceDepthColumn.setCellValueFactory( aParam -> new ReadOnlyStringWrapper( Integer.toString( aParam.getValue( ).getValue( ).getTraceDepth( ) ).intern( ) ) );
				ivTraceDepthColumn.setText( getLocalizedString( "columnTraceDepth" ) );
				ivTraceDepthColumn.setPrefWidth( 100 );

				ivTreeTableView.getColumns( ).add( ivTraceDepthColumn );
			}

			{
				ivTraceSizeColumn = new TreeTableColumn<>( );
				ivTraceSizeColumn.setCellValueFactory( aParam -> new ReadOnlyStringWrapper( Integer.toString( aParam.getValue( ).getValue( ).getTraceSize( ) ).intern( ) ) );
				ivTraceSizeColumn.setText( getLocalizedString( "columnTraceSize" ) );
				ivTraceSizeColumn.setPrefWidth( 100 );

				ivTreeTableView.getColumns( ).add( ivTraceSizeColumn );
			}

			{
				ivPercentColumn = new TreeTableColumn<>( );
				ivPercentColumn.setCellValueFactory( aParam -> new ReadOnlyStringWrapper( Float.toString( aParam.getValue( ).getValue( ).getPercent( ) ).intern( ) ) );
				ivPercentColumn.setText( getLocalizedString( "columnPercent" ) );
				ivPercentColumn.setPrefWidth( 100 );

				ivTreeTableView.getColumns( ).add( ivPercentColumn );
			}

			{
				ivDurationColumn = new TreeTableColumn<>( );
				ivDurationColumn.setCellValueFactory( new DurationCellValueFactory( ) );
				ivDurationColumn.setText( getLocalizedString( "columnDuration" ) );
				ivDurationColumn.setPrefWidth( 150 );

				ivTreeTableView.getColumns( ).add( ivDurationColumn );
			}

			{
				ivTimestampColumn = new TreeTableColumn<>( );
				ivTimestampColumn.setCellValueFactory( new TimestampCellValueFactory( ) );
				ivTimestampColumn.setText( getLocalizedString( "columnTimestamp" ) );
				ivTimestampColumn.setPrefWidth( 150 );

				ivTreeTableView.getColumns( ).add( ivTimestampColumn );
			}

			{
				ivTraceIdColumn = new TreeTableColumn<>( );
				ivTraceIdColumn.setCellValueFactory( aParam -> new ReadOnlyStringWrapper( Long.toString( aParam.getValue( ).getValue( ).getTraceId( ) ).intern( ) ) );
				ivTraceIdColumn.setText( getLocalizedString( "columnTraceId" ) );
				ivTraceIdColumn.setPrefWidth( 150 );

				ivTreeTableView.getColumns( ).add( ivTraceIdColumn );
			}

			// The default sorting is a little bit too slow. Therefore we use a custom sort policy which sorts directly the data.
			ivTreeTableView.setSortPolicy( param -> {
				final ObservableList<TreeTableColumn<MethodCall, ?>> sortOrder = param.getSortOrder( );

				if ( sortOrder.size( ) == 1 ) {
					final TreeTableColumn<MethodCall, ?> tableColumn = sortOrder.get( 0 );

					if ( tableColumn == ivHostColumn ) {
						final ObservableList<TreeItem<MethodCall>> items = param.getRoot( ).getChildren( );
						if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
							items.sort( ( o1, o2 ) -> o1.getValue( ).getHost( ).compareTo( o2.getValue( ).getHost( ) ) );
						} else {
							items.sort( ( o1, o2 ) -> o2.getValue( ).getHost( ).compareTo( o1.getValue( ).getHost( ) ) );
						}
					}

					if ( tableColumn == ivClassColumn ) {
						final ClassAppearance classAppearance = ivPropertiesService.loadApplicationProperty( ClassAppearanceProperty.class );

						final ObservableList<TreeItem<MethodCall>> items = param.getRoot( ).getChildren( );
						if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
							items.sort( ( o1, o2 ) -> classAppearance.convert( o1.getValue( ).getClazz( ) ).compareTo( classAppearance.convert( o2.getValue( ).getClazz( ) ) ) );
						} else {
							items.sort( ( o1, o2 ) -> classAppearance.convert( o2.getValue( ).getClazz( ) ).compareTo( classAppearance.convert( o1.getValue( ).getClazz( ) ) ) );
						}
					}

					if ( tableColumn == ivMethodColumn ) {
						final MethodAppearance methodAppearance = ivPropertiesService.loadApplicationProperty( MethodAppearanceProperty.class );

						final ObservableList<TreeItem<MethodCall>> items = param.getRoot( ).getChildren( );
						if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
							items.sort( ( o1, o2 ) -> methodAppearance.convert( o1.getValue( ).getMethod( ) ).compareTo( methodAppearance.convert( o2.getValue( ).getMethod( ) ) ) );
						} else {
							items.sort( ( o1, o2 ) -> methodAppearance.convert( o2.getValue( ).getMethod( ) ).compareTo( methodAppearance.convert( o1.getValue( ).getMethod( ) ) ) );
						}
					}

					if ( tableColumn == ivDurationColumn ) {
						final ObservableList<TreeItem<MethodCall>> items = param.getRoot( ).getChildren( );
						if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
							items.sort( ( o1, o2 ) -> Long.compare( o1.getValue( ).getDuration( ), o2.getValue( ).getDuration( ) ) );
						} else {
							items.sort( ( o1, o2 ) -> Long.compare( o2.getValue( ).getDuration( ), o1.getValue( ).getDuration( ) ) );
						}
					}

					if ( tableColumn == ivTimestampColumn ) {
						final ObservableList<TreeItem<MethodCall>> items = param.getRoot( ).getChildren( );
						if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
							items.sort( ( o1, o2 ) -> Long.compare( o1.getValue( ).getTimestamp( ), o2.getValue( ).getTimestamp( ) ) );
						} else {
							items.sort( ( o1, o2 ) -> Long.compare( o2.getValue( ).getTimestamp( ), o1.getValue( ).getTimestamp( ) ) );
						}
					}

					if ( tableColumn == ivTraceIdColumn ) {
						final ObservableList<TreeItem<MethodCall>> items = param.getRoot( ).getChildren( );
						if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
							items.sort( ( o1, o2 ) -> Long.compare( o1.getValue( ).getTraceId( ), o2.getValue( ).getTraceId( ) ) );
						} else {
							items.sort( ( o1, o2 ) -> Long.compare( o2.getValue( ).getTraceId( ), o1.getValue( ).getTraceId( ) ) );
						}
					}

					if ( tableColumn == ivTraceDepthColumn ) {
						final ObservableList<TreeItem<MethodCall>> items = param.getRoot( ).getChildren( );
						if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
							items.sort( ( o1, o2 ) -> Long.compare( o1.getValue( ).getTraceDepth( ), o2.getValue( ).getTraceDepth( ) ) );
						} else {
							items.sort( ( o1, o2 ) -> Long.compare( o2.getValue( ).getTraceDepth( ), o1.getValue( ).getTraceDepth( ) ) );
						}
					}

					if ( tableColumn == ivTraceSizeColumn ) {
						final ObservableList<TreeItem<MethodCall>> items = param.getRoot( ).getChildren( );
						if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
							items.sort( ( o1, o2 ) -> Long.compare( o1.getValue( ).getTraceSize( ), o2.getValue( ).getTraceSize( ) ) );
						} else {
							items.sort( ( o1, o2 ) -> Long.compare( o2.getValue( ).getTraceSize( ), o1.getValue( ).getTraceSize( ) ) );
						}
					}

					if ( tableColumn == ivPercentColumn ) {
						final ObservableList<TreeItem<MethodCall>> items = param.getRoot( ).getChildren( );
						if ( tableColumn.getSortType( ) == SortType.ASCENDING ) {
							items.sort( ( o1, o2 ) -> Float.compare( o1.getValue( ).getPercent( ), o2.getValue( ).getPercent( ) ) );
						} else {
							items.sort( ( o1, o2 ) -> Float.compare( o2.getValue( ).getPercent( ), o1.getValue( ).getPercent( ) ) );
						}
					}
				}

				return true;
			} );

			getChildren( ).add( ivTreeTableView );
		}

		// Detail panel
		{
			final TitledPane titledPane = new TitledPane( );
			titledPane.setText( getLocalizedString( "detailTitle" ) );

			{
				final GridPane griPane = new GridPane( );

				int rowIndex = 0;

				{
					final Label label = new Label( );
					label.setText( getLocalizedString( "labelHost" ) );

					GridPane.setColumnIndex( label, 0 );
					GridPane.setRowIndex( label, rowIndex );

					griPane.getChildren( ).add( label );
				}

				{
					ivDetailsHost = new TextField( );
					ivDetailsHost.setEditable( false );
					ivDetailsHost.getStyleClass( ).add( "details" );

					GridPane.setColumnIndex( ivDetailsHost, 1 );
					GridPane.setRowIndex( ivDetailsHost, rowIndex++ );
					GridPane.setHgrow( ivDetailsHost, Priority.ALWAYS );

					griPane.getChildren( ).add( ivDetailsHost );
				}

				{
					final Label label = new Label( );
					label.setText( getLocalizedString( "labelClass" ) );

					GridPane.setColumnIndex( label, 0 );
					GridPane.setRowIndex( label, rowIndex );

					griPane.getChildren( ).add( label );
				}

				{
					ivDetailsClass = new TextField( );
					ivDetailsClass.setEditable( false );
					ivDetailsClass.getStyleClass( ).add( "details" );

					GridPane.setColumnIndex( ivDetailsClass, 1 );
					GridPane.setRowIndex( ivDetailsClass, rowIndex++ );
					GridPane.setHgrow( ivDetailsClass, Priority.ALWAYS );

					griPane.getChildren( ).add( ivDetailsClass );
				}

				{
					final Label label = new Label( );
					label.setText( getLocalizedString( "labelMethod" ) );

					GridPane.setColumnIndex( label, 0 );
					GridPane.setRowIndex( label, rowIndex );

					griPane.getChildren( ).add( label );
				}

				{
					ivDetailsMethod = new TextField( );
					ivDetailsMethod.setEditable( false );
					ivDetailsMethod.getStyleClass( ).add( "details" );

					GridPane.setColumnIndex( ivDetailsMethod, 1 );
					GridPane.setRowIndex( ivDetailsMethod, rowIndex++ );
					GridPane.setHgrow( ivDetailsMethod, Priority.ALWAYS );

					griPane.getChildren( ).add( ivDetailsMethod );
				}

				{
					final Label label = new Label( );
					label.setText( getLocalizedString( "labelException" ) );

					GridPane.setColumnIndex( label, 0 );
					GridPane.setRowIndex( label, rowIndex );

					griPane.getChildren( ).add( label );
				}

				{
					ivDetailsException = new TextField( );
					ivDetailsException.setEditable( false );
					ivDetailsException.getStyleClass( ).add( "details" );

					GridPane.setColumnIndex( ivDetailsException, 1 );
					GridPane.setRowIndex( ivDetailsException, rowIndex++ );
					GridPane.setHgrow( ivDetailsException, Priority.ALWAYS );

					griPane.getChildren( ).add( ivDetailsException );
				}

				{
					final Label label = new Label( );
					label.setText( getLocalizedString( "labelTraceDepth" ) );

					GridPane.setColumnIndex( label, 0 );
					GridPane.setRowIndex( label, rowIndex );

					griPane.getChildren( ).add( label );
				}

				{
					ivDetailsTraceDepth = new TextField( );
					ivDetailsTraceDepth.setEditable( false );
					ivDetailsTraceDepth.getStyleClass( ).add( "details" );

					GridPane.setColumnIndex( ivDetailsTraceDepth, 1 );
					GridPane.setRowIndex( ivDetailsTraceDepth, rowIndex++ );
					GridPane.setHgrow( ivDetailsTraceDepth, Priority.ALWAYS );

					griPane.getChildren( ).add( ivDetailsTraceDepth );
				}

				rowIndex = 0;

				{
					final Label label = new Label( );
					label.setText( getLocalizedString( "labelTraceSize" ) );

					GridPane.setColumnIndex( label, 2 );
					GridPane.setRowIndex( label, rowIndex );

					griPane.getChildren( ).add( label );
				}

				{
					ivDetailsTraceSize = new TextField( );
					ivDetailsTraceSize.setEditable( false );
					ivDetailsTraceSize.getStyleClass( ).add( "details" );

					GridPane.setColumnIndex( ivDetailsTraceSize, 3 );
					GridPane.setRowIndex( ivDetailsTraceSize, rowIndex++ );
					GridPane.setHgrow( ivDetailsTraceSize, Priority.ALWAYS );

					griPane.getChildren( ).add( ivDetailsTraceSize );
				}

				{
					final Label label = new Label( );
					label.setText( getLocalizedString( "labelPercent" ) );

					GridPane.setColumnIndex( label, 2 );
					GridPane.setRowIndex( label, rowIndex );

					griPane.getChildren( ).add( label );
				}

				{
					ivDetailsPercent = new TextField( );
					ivDetailsPercent.setEditable( false );
					ivDetailsPercent.getStyleClass( ).add( "details" );

					GridPane.setColumnIndex( ivDetailsPercent, 3 );
					GridPane.setRowIndex( ivDetailsPercent, rowIndex++ );
					GridPane.setHgrow( ivDetailsPercent, Priority.ALWAYS );

					griPane.getChildren( ).add( ivDetailsPercent );
				}

				{
					final Label label = new Label( );
					label.setText( getLocalizedString( "labelDuration" ) );

					GridPane.setColumnIndex( label, 2 );
					GridPane.setRowIndex( label, rowIndex );

					griPane.getChildren( ).add( label );
				}

				{
					ivDetailsDuration = new TextField( );
					ivDetailsDuration.setEditable( false );
					ivDetailsDuration.getStyleClass( ).add( "details" );

					GridPane.setColumnIndex( ivDetailsDuration, 3 );
					GridPane.setRowIndex( ivDetailsDuration, rowIndex++ );
					GridPane.setHgrow( ivDetailsDuration, Priority.ALWAYS );

					griPane.getChildren( ).add( ivDetailsDuration );
				}

				{
					final Label label = new Label( );
					label.setText( getLocalizedString( "labelTimestamp" ) );

					GridPane.setColumnIndex( label, 2 );
					GridPane.setRowIndex( label, rowIndex );

					griPane.getChildren( ).add( label );
				}

				{
					ivDetailsTimestamp = new TextField( );
					ivDetailsTimestamp.setEditable( false );
					ivDetailsTimestamp.getStyleClass( ).add( "details" );

					GridPane.setColumnIndex( ivDetailsTimestamp, 3 );
					GridPane.setRowIndex( ivDetailsTimestamp, rowIndex++ );
					GridPane.setHgrow( ivDetailsTimestamp, Priority.ALWAYS );

					griPane.getChildren( ).add( ivDetailsTimestamp );
				}

				{
					final Label label = new Label( );
					label.setText( getLocalizedString( "labelTraceId" ) );

					GridPane.setColumnIndex( label, 2 );
					GridPane.setRowIndex( label, rowIndex );

					griPane.getChildren( ).add( label );
				}

				{
					ivDetailsTraceId = new TextField( );
					ivDetailsTraceId.setEditable( false );
					ivDetailsTraceId.getStyleClass( ).add( "details" );

					GridPane.setColumnIndex( ivDetailsTraceId, 3 );
					GridPane.setRowIndex( ivDetailsTraceId, rowIndex++ );
					GridPane.setHgrow( ivDetailsTraceId, Priority.ALWAYS );

					griPane.getChildren( ).add( ivDetailsTraceId );
				}

				titledPane.setContent( griPane );
			}

			getChildren( ).add( titledPane );
		}

		// Status bar
		{
			ivStatusLabel = new Label( );
			VBox.setMargin( ivStatusLabel, new Insets( 5 ) );
			getChildren( ).add( ivStatusLabel );
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

	LongTextField getFilterTraceId( ) {
		return ivFilterTraceId;
	}

	CheckBox getFilterUseRegExpr( ) {
		return ivFilterUseRegExpr;
	}

	CheckBox getFilterSearchWholeTrace( ) {
		return ivFilterSearchWholeTrace;
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

	ComboBox<SearchType> getFilterSearchType( ) {
		return ivFilterSearchType;
	}

	TreeTableView<MethodCall> getTreeTableView( ) {
		return ivTreeTableView;
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

	TextField getDetailsTraceDepth( ) {
		return ivDetailsTraceDepth;
	}

	TextField getDetailsTraceSize( ) {
		return ivDetailsTraceSize;
	}

	TextField getDetailsDuration( ) {
		return ivDetailsDuration;
	}

	TextField getDetailsPercent( ) {
		return ivDetailsPercent;
	}

	TextField getDetailsTimestamp( ) {
		return ivDetailsTimestamp;
	}

	TextField getDetailsTraceId( ) {
		return ivDetailsTraceId;
	}

	Label getStatusLabel( ) {
		return ivStatusLabel;
	}

	TreeTableColumn<MethodCall, String> getDurationColumn( ) {
		return ivDurationColumn;
	}

	@Override
	public void setParameter( final Object aParameter ) {
		getController( ).performSetParameter( aParameter );
	}

	public Button getSearchButton( ) {
		return ivSearchButton;
	}

	public void prepareRefresh( ) {
		getController( ).performPrepareRefresh( );
	}

	public void initialize( ) {
		getController( ).performInitialize( );
	}

	public void performRefresh( ) {
		getController( ).performRefresh( );
	}

}
