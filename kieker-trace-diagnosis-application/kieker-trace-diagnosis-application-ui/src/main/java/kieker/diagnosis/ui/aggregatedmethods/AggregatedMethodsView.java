/*************************************************************************** 
 * Copyright 2015-2017 Kieker Project (http://kieker-monitoring.net)         
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

package kieker.diagnosis.ui.aggregatedmethods;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import kieker.diagnosis.architecture.ui.EnumStringConverter;
import kieker.diagnosis.architecture.ui.ViewBase;
import kieker.diagnosis.service.aggregatedmethods.SearchType;
import kieker.diagnosis.service.data.AggregatedMethodCall;
import kieker.diagnosis.ui.aggregatedmethods.components.ClassCellValueFactory;
import kieker.diagnosis.ui.aggregatedmethods.components.DurationCellValueFactory;
import kieker.diagnosis.ui.aggregatedmethods.components.MethodCellValueFactory;
import kieker.diagnosis.ui.aggregatedmethods.components.StyledRow;

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

	private final TableColumn<AggregatedMethodCall, Long> ivColumnMinDuration;
	private final TableColumn<AggregatedMethodCall, Long> ivColumnAvgDuration;
	private final TableColumn<AggregatedMethodCall, Long> ivColumnMedianDuration;
	private final TableColumn<AggregatedMethodCall, Long> ivColumnMaxDuration;
	private final TableColumn<AggregatedMethodCall, Long> ivColumnTotalDuration;

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

	public void initialize( ) {
		getController( ).performInitialize( );
	}

	@Inject
	public AggregatedMethodsView( final Injector aInjector ) {

		// Filter
		{
			final TitledPane titledPane = new TitledPane( );
			titledPane.setText( getLocalizedString( "filterTitle" ) );

			{
				final GridPane gridPane = new GridPane( );
				gridPane.setHgap( 5 );
				gridPane.setVgap( 5 );

				int columnIndex = 0;

				{
					ivFilterHost = new TextField( );
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

					GridPane.setColumnIndex( ivFilterSearchType, columnIndex++ );
					GridPane.setRowIndex( ivFilterSearchType, 0 );

					gridPane.getChildren( ).add( ivFilterSearchType );
				}

				{
					ivFilterUseRegExpr = new CheckBox( );
					ivFilterUseRegExpr.setText( getLocalizedString( "filterUseRegExpr" ) );

					GridPane.setColumnIndex( ivFilterUseRegExpr, columnIndex );
					GridPane.setRowIndex( ivFilterUseRegExpr, 0 );

					gridPane.getChildren( ).add( ivFilterUseRegExpr );
				}

				{
					final Hyperlink hyperlink = new Hyperlink( );
					hyperlink.setText( getLocalizedString( "saveAsFavorite" ) );
					hyperlink.setOnAction( e -> getController( ).performSaveAsFavorite( ) );

					GridPane.setColumnIndex( hyperlink, 0 );
					GridPane.setRowIndex( hyperlink, 1 );

					gridPane.getChildren( ).add( hyperlink );
				}

				{
					ivSearchButton = new Button( );
					ivSearchButton.setText( getLocalizedString( "search" ) );
					ivSearchButton.setMinWidth( 140 );
					ivSearchButton.setMaxWidth( Double.POSITIVE_INFINITY );
					ivSearchButton.setOnAction( e -> getController( ).performSearch( ) );

					GridPane.setColumnIndex( ivSearchButton, columnIndex++ );
					GridPane.setRowIndex( ivSearchButton, 1 );

					gridPane.getChildren( ).add( ivSearchButton );
				}

				titledPane.setContent( gridPane );
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
				final TableColumn<AggregatedMethodCall, Integer> column = new TableColumn<>( );
				column.setCellValueFactory( aParam -> new ReadOnlyObjectWrapper<>( aParam.getValue( ).getCount( ) ) );
				column.setText( getLocalizedString( "columnCount" ) );
				column.setPrefWidth( 100 );

				ivTableView.getColumns( ).add( column );
			}

			{
				final TableColumn<AggregatedMethodCall, String> column = new TableColumn<>( );
				column.setCellValueFactory( aParam -> new ReadOnlyObjectWrapper<>( aParam.getValue( ).getHost( ) ) );
				column.setText( getLocalizedString( "columnHost" ) );
				column.setPrefWidth( 100 );

				ivTableView.getColumns( ).add( column );
			}

			{
				final TableColumn<AggregatedMethodCall, String> column = new TableColumn<>( );
				column.setCellValueFactory( aInjector.getInstance( ClassCellValueFactory.class ) );
				column.setText( getLocalizedString( "columnClass" ) );
				column.setPrefWidth( 200 );

				ivTableView.getColumns( ).add( column );
			}

			{
				final TableColumn<AggregatedMethodCall, String> column = new TableColumn<>( );
				column.setCellValueFactory( aInjector.getInstance( MethodCellValueFactory.class ) );
				column.setText( getLocalizedString( "columnMethod" ) );
				column.setPrefWidth( 400 );

				ivTableView.getColumns( ).add( column );
			}

			{
				final DurationCellValueFactory cellValueFactory = aInjector.getInstance( DurationCellValueFactory.class );
				cellValueFactory.setGetter( AggregatedMethodCall::getMinDuration );

				ivColumnMinDuration = new TableColumn<>( );
				ivColumnMinDuration.setCellValueFactory( cellValueFactory );
				ivColumnMinDuration.setText( getLocalizedString( "columnMinDuration" ) );
				ivColumnMinDuration.setPrefWidth( 150 );

				ivTableView.getColumns( ).add( ivColumnMinDuration );
			}

			{
				final DurationCellValueFactory cellValueFactory = aInjector.getInstance( DurationCellValueFactory.class );
				cellValueFactory.setGetter( AggregatedMethodCall::getAvgDuration );

				ivColumnAvgDuration = new TableColumn<>( );
				ivColumnAvgDuration.setCellValueFactory( cellValueFactory );
				ivColumnAvgDuration.setText( getLocalizedString( "columnAvgDuration" ) );
				ivColumnAvgDuration.setPrefWidth( 200 );

				ivTableView.getColumns( ).add( ivColumnAvgDuration );
			}

			{
				final DurationCellValueFactory cellValueFactory = aInjector.getInstance( DurationCellValueFactory.class );
				cellValueFactory.setGetter( AggregatedMethodCall::getMedianDuration );

				ivColumnMedianDuration = new TableColumn<>( );
				ivColumnMedianDuration.setCellValueFactory( cellValueFactory );
				ivColumnMedianDuration.setText( getLocalizedString( "columnMedianDuration" ) );
				ivColumnMedianDuration.setPrefWidth( 150 );

				ivTableView.getColumns( ).add( ivColumnMedianDuration );
			}

			{
				final DurationCellValueFactory cellValueFactory = aInjector.getInstance( DurationCellValueFactory.class );
				cellValueFactory.setGetter( AggregatedMethodCall::getMaxDuration );

				ivColumnMaxDuration = new TableColumn<>( );
				ivColumnMaxDuration.setCellValueFactory( cellValueFactory );
				ivColumnMaxDuration.setText( getLocalizedString( "columnMaxDuration" ) );
				ivColumnMaxDuration.setPrefWidth( 150 );

				ivTableView.getColumns( ).add( ivColumnMaxDuration );
			}

			{
				final DurationCellValueFactory cellValueFactory = aInjector.getInstance( DurationCellValueFactory.class );
				cellValueFactory.setGetter( AggregatedMethodCall::getTotalDuration );

				ivColumnTotalDuration = new TableColumn<>( );
				ivColumnTotalDuration.setCellValueFactory( cellValueFactory );
				ivColumnTotalDuration.setText( getLocalizedString( "columnTotalDuration" ) );
				ivColumnTotalDuration.setPrefWidth( 150 );

				ivTableView.getColumns( ).add( ivColumnTotalDuration );
			}

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

	TableColumn<AggregatedMethodCall, Long> getColumnMinDuration( ) {
		return ivColumnMinDuration;
	}

	TableColumn<AggregatedMethodCall, Long> getColumnAvgDuration( ) {
		return ivColumnAvgDuration;
	}

	TableColumn<AggregatedMethodCall, Long> getColumnMedianDuration( ) {
		return ivColumnMedianDuration;
	}

	TableColumn<AggregatedMethodCall, Long> getColumnMaxDuration( ) {
		return ivColumnMaxDuration;
	}

	TableColumn<AggregatedMethodCall, Long> getColumnTotalDuration( ) {
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
