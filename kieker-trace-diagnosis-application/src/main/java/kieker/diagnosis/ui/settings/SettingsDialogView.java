package kieker.diagnosis.ui.settings;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import com.google.inject.Singleton;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import kieker.diagnosis.architecture.ui.EnumStringConverter;
import kieker.diagnosis.architecture.ui.ViewBase;
import kieker.diagnosis.service.settings.ClassAppearance;
import kieker.diagnosis.service.settings.MethodAppearance;
import kieker.diagnosis.service.settings.MethodCallAggregation;
import kieker.diagnosis.service.settings.TimestampAppearance;

@Singleton
public class SettingsDialogView extends ViewBase<SettingsDialogController> {

	private final ComboBox<TimestampAppearance> ivTimestampAppearanceComboBox;
	private final ComboBox<TimeUnit> ivTimeUnitComboBox;
	private final ComboBox<ClassAppearance> ivClassesComboBox;
	private final ComboBox<MethodAppearance> ivMethodsComboBox;
	private final CheckBox ivShowUnmonitoredTime;
	private final ComboBox<MethodCallAggregation> ivMethodCallAggregation;
	private final TextField ivMaxNumberOfCalls;
	private final TextField ivMethodCallThreshold;

	private boolean ivSettingsChanged = false;

	public SettingsDialogView( ) {
		{
			final TitledPane titledPane = new TitledPane( );
			titledPane.setCollapsible( false );
			titledPane.setText( getLocalizedString( "appearance" ) );

			{
				final GridPane gridPane = new GridPane( );
				gridPane.setVgap( 5 );
				gridPane.setHgap( 5 );

				int rowIndex = 0;

				{
					final Label label = new Label( );
					label.setText( getLocalizedString( "timestamp" ) );

					GridPane.setRowIndex( label, rowIndex );
					GridPane.setColumnIndex( label, 1 );

					gridPane.getChildren( ).add( label );
				}

				{
					ivTimestampAppearanceComboBox = new ComboBox<>( );
					ivTimestampAppearanceComboBox.setItems( FXCollections.observableArrayList( TimestampAppearance.values( ) ) );
					ivTimestampAppearanceComboBox.setValue( TimestampAppearance.DATE_AND_TIME );
					ivTimestampAppearanceComboBox.setConverter( new EnumStringConverter<>( TimestampAppearance.class ) );
					ivTimestampAppearanceComboBox.setMaxWidth( Double.POSITIVE_INFINITY );

					GridPane.setRowIndex( ivTimestampAppearanceComboBox, rowIndex++ );
					GridPane.setColumnIndex( ivTimestampAppearanceComboBox, 2 );

					gridPane.getChildren( ).add( ivTimestampAppearanceComboBox );
				}

				{
					final Label label = new Label( );
					label.setText( getLocalizedString( "timeunit" ) );

					GridPane.setRowIndex( label, rowIndex );
					GridPane.setColumnIndex( label, 1 );

					gridPane.getChildren( ).add( label );
				}

				{
					ivTimeUnitComboBox = new ComboBox<>( );
					ivTimeUnitComboBox.setItems( FXCollections.observableArrayList( TimeUnit.values( ) ) );
					ivTimeUnitComboBox.setConverter( new EnumStringConverter<>( TimeUnit.class ) );
					ivTimeUnitComboBox.setMaxWidth( Double.POSITIVE_INFINITY );

					GridPane.setRowIndex( ivTimeUnitComboBox, rowIndex++ );
					GridPane.setColumnIndex( ivTimeUnitComboBox, 2 );

					gridPane.getChildren( ).add( ivTimeUnitComboBox );
				}

				{
					final Label label = new Label( );
					label.setText( getLocalizedString( "classes" ) );

					GridPane.setRowIndex( label, rowIndex );
					GridPane.setColumnIndex( label, 1 );

					gridPane.getChildren( ).add( label );
				}

				{
					ivClassesComboBox = new ComboBox<>( );
					ivClassesComboBox.setItems( FXCollections.observableArrayList( ClassAppearance.values( ) ) );
					ivClassesComboBox.setConverter( new EnumStringConverter<>( ClassAppearance.class ) );
					ivClassesComboBox.setMaxWidth( Double.POSITIVE_INFINITY );

					GridPane.setRowIndex( ivClassesComboBox, rowIndex++ );
					GridPane.setColumnIndex( ivClassesComboBox, 2 );

					gridPane.getChildren( ).add( ivClassesComboBox );
				}

				{
					final Label label = new Label( );
					label.setText( getLocalizedString( "methods" ) );

					GridPane.setRowIndex( label, rowIndex );
					GridPane.setColumnIndex( label, 1 );

					gridPane.getChildren( ).add( label );
				}

				{
					ivMethodsComboBox = new ComboBox<>( );
					ivMethodsComboBox.setItems( FXCollections.observableArrayList( MethodAppearance.values( ) ) );
					ivMethodsComboBox.setConverter( new EnumStringConverter<>( MethodAppearance.class ) );
					ivMethodsComboBox.setMaxWidth( Double.POSITIVE_INFINITY );

					GridPane.setRowIndex( ivMethodsComboBox, rowIndex++ );
					GridPane.setColumnIndex( ivMethodsComboBox, 2 );
					GridPane.setHgrow( ivMethodsComboBox, Priority.ALWAYS );

					gridPane.getChildren( ).add( ivMethodsComboBox );
				}

				{
					final Label label = new Label( );
					label.setText( getLocalizedString( "showUnmonitoredTime" ) );

					GridPane.setRowIndex( label, rowIndex );
					GridPane.setColumnIndex( label, 1 );

					gridPane.getChildren( ).add( label );
				}

				{
					ivShowUnmonitoredTime = new CheckBox( );

					GridPane.setRowIndex( ivShowUnmonitoredTime, rowIndex++ );
					GridPane.setColumnIndex( ivShowUnmonitoredTime, 2 );

					gridPane.getChildren( ).add( ivShowUnmonitoredTime );
				}

				titledPane.setContent( gridPane );
			}

			getChildren( ).add( titledPane );
		}

		{
			final TitledPane titledPane = new TitledPane( );
			titledPane.setCollapsible( false );
			titledPane.setText( getLocalizedString( "aggregateMethodCalls" ) );

			{
				final GridPane gridPane = new GridPane( );
				gridPane.setVgap( 5 );
				gridPane.setHgap( 5 );

				int rowIndex = 0;

				{
					final Label label = new Label( );
					label.setText( getLocalizedString( "methodCallAggregation" ) );

					GridPane.setRowIndex( label, rowIndex );
					GridPane.setColumnIndex( label, 1 );

					gridPane.getChildren( ).add( label );
				}

				{
					ivMethodCallAggregation = new ComboBox<>( );
					ivMethodCallAggregation.setItems( FXCollections.observableArrayList( MethodCallAggregation.values( ) ) );
					ivMethodCallAggregation.setConverter( new EnumStringConverter<>( MethodCallAggregation.class ) );
					ivMethodCallAggregation.setMaxWidth( Double.POSITIVE_INFINITY );

					GridPane.setRowIndex( ivMethodCallAggregation, rowIndex++ );
					GridPane.setColumnIndex( ivMethodCallAggregation, 2 );
					GridPane.setHgrow( ivMethodCallAggregation, Priority.ALWAYS );

					gridPane.getChildren( ).add( ivMethodCallAggregation );
				}

				{
					final Label label = new Label( );
					label.setText( getLocalizedString( "maxNumberOfMethodCalls" ) );

					GridPane.setRowIndex( label, rowIndex );
					GridPane.setColumnIndex( label, 1 );

					gridPane.getChildren( ).add( label );
				}

				{
					ivMaxNumberOfCalls = new TextField( );

					final ReadOnlyObjectProperty<MethodCallAggregation> property = ivMethodCallAggregation.getSelectionModel( ).selectedItemProperty( );
					ivMaxNumberOfCalls.disableProperty( ).bind( property.isEqualTo( MethodCallAggregation.NONE ).or( property.isEqualTo( MethodCallAggregation.BY_THRESHOLD ) ) );

					GridPane.setRowIndex( ivMaxNumberOfCalls, rowIndex++ );
					GridPane.setColumnIndex( ivMaxNumberOfCalls, 2 );

					gridPane.getChildren( ).add( ivMaxNumberOfCalls );
				}

				{
					final Label label = new Label( );
					label.setText( getLocalizedString( "methodCallThreshold" ) );

					GridPane.setRowIndex( label, rowIndex );
					GridPane.setColumnIndex( label, 1 );

					gridPane.getChildren( ).add( label );
				}

				{
					ivMethodCallThreshold = new TextField( );

					final ReadOnlyObjectProperty<MethodCallAggregation> selectedItemProperty = ivMethodCallAggregation.getSelectionModel( ).selectedItemProperty( );
					ivMethodCallThreshold.disableProperty( ).bind( selectedItemProperty.isEqualTo( MethodCallAggregation.BY_THRESHOLD ).not( ) );

					GridPane.setRowIndex( ivMethodCallThreshold, rowIndex++ );
					GridPane.setColumnIndex( ivMethodCallThreshold, 2 );

					gridPane.getChildren( ).add( ivMethodCallThreshold );
				}

				titledPane.setContent( gridPane );
			}

			getChildren( ).add( titledPane );
		}

		{
			final ButtonBar buttonBar = new ButtonBar( );
			VBox.setMargin( buttonBar, new Insets( 10 ) );

			{
				final Button button = new Button( );
				button.setText( getLocalizedString( "cancel" ) );
				button.setCancelButton( true );
				button.setOnAction( e -> getController( ).performClose( ) );

				buttonBar.getButtons( ).add( button );
			}
			{
				final Button button = new Button( );
				button.setText( getLocalizedString( "ok" ) );
				button.setDefaultButton( true );
				button.setOnAction( e -> {
					ivSettingsChanged = true;
					getController( ).performSaveAndClose( );
				} );

				buttonBar.getButtons( ).add( button );
			}

			getChildren( ).add( buttonBar );
		}
	}

	public boolean open( final Window aParent ) {
		// Create a scene if necessary
		Scene scene = getScene( );
		if ( scene == null ) {
			scene = new Scene( this );
		}

		// Load the icon
		final String iconPath = getLocalizedString( "icon" );
		final InputStream iconStream = getClass( ).getClassLoader( ).getResourceAsStream( iconPath );
		final Image icon = new Image( iconStream );

		// Prepare and show the stage
		final Stage stage = new Stage( );
		stage.setResizable( false );
		stage.initModality( Modality.WINDOW_MODAL );
		stage.initStyle( StageStyle.DECORATED );
		stage.initOwner( aParent );
		stage.getIcons( ).add( icon );
		stage.setTitle( getLocalizedString( "title" ) );
		stage.setScene( scene );

		getController( ).performRefresh( );

		stage.showAndWait( );

		return ivSettingsChanged;
	}

	@Override
	public void setParameter( final Object aParameter ) {

	}

	ComboBox<TimestampAppearance> getTimestampAppearanceComboBox( ) {
		return ivTimestampAppearanceComboBox;
	}

	ComboBox<TimeUnit> getTimeUnitComboBox( ) {
		return ivTimeUnitComboBox;
	}

	ComboBox<ClassAppearance> getClassesComboBox( ) {
		return ivClassesComboBox;
	}

	ComboBox<MethodAppearance> getMethodsComboBox( ) {
		return ivMethodsComboBox;
	}

	CheckBox getShowUnmonitoredTime( ) {
		return ivShowUnmonitoredTime;
	}

	ComboBox<MethodCallAggregation> getMethodCallAggregation( ) {
		return ivMethodCallAggregation;
	}

	TextField getMaxNumberOfMethodCalls( ) {
		return ivMaxNumberOfCalls;
	}

	TextField getMethodCallThreshold( ) {
		return ivMethodCallThreshold;
	}

}
