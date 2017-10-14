package kieker.diagnosis.ui.monitoring;

import java.io.InputStream;

import com.google.inject.Singleton;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import kieker.diagnosis.architecture.monitoring.Timer;
import kieker.diagnosis.architecture.monitoring.Writer;
import kieker.diagnosis.architecture.ui.EnumStringConverter;
import kieker.diagnosis.architecture.ui.ViewBase;

@Singleton
public class MonitoringDialogView extends ViewBase<MonitoringDialogController> {

	private Label ivStatus;
	private CheckBox ivActive;
	private TextField ivOutputDirectory;
	private ComboBox<Timer> ivTimer;
	private ComboBox<Writer> ivWriter;
	private TextField ivMaxEntriesPerFile;
	private TextField ivQueueSize;
	private TextField ivBuffer;

	public MonitoringDialogView( ) {

		setSpacing( 10 );

		{
			final GridPane gridPane = new GridPane( );
			VBox.setMargin( gridPane, new Insets( 10, 10, 0, 10 ) );
			gridPane.setVgap( 5 );
			gridPane.setHgap( 5 );

			int rowIndex = 0;

			{
				final Label label = new Label( );
				label.setText( getLocalizedString( "status" ) );

				GridPane.setRowIndex( label, rowIndex );
				GridPane.setColumnIndex( label, 1 );

				gridPane.getChildren( ).add( label );
			}

			{
				ivStatus = new Label( );
				ivStatus.setMaxWidth( Double.POSITIVE_INFINITY );
				ivStatus.getStyleClass( ).add( "status" );

				GridPane.setRowIndex( ivStatus, rowIndex++ );
				GridPane.setColumnIndex( ivStatus, 2 );
				GridPane.setHgrow( ivStatus, Priority.ALWAYS );

				gridPane.getChildren( ).add( ivStatus );
			}

			{
				final Label label = new Label( );
				label.setText( getLocalizedString( "active" ) );

				GridPane.setRowIndex( label, rowIndex );
				GridPane.setColumnIndex( label, 1 );

				gridPane.getChildren( ).add( label );
			}

			{
				ivActive = new CheckBox( );
				ivActive.setMaxWidth( Double.POSITIVE_INFINITY );

				GridPane.setRowIndex( ivActive, rowIndex++ );
				GridPane.setColumnIndex( ivActive, 2 );

				gridPane.getChildren( ).add( ivActive );
			}

			{
				final Label label = new Label( );
				label.setText( getLocalizedString( "outputDirectory" ) );

				GridPane.setRowIndex( label, rowIndex );
				GridPane.setColumnIndex( label, 1 );

				gridPane.getChildren( ).add( label );
			}

			{
				ivOutputDirectory = new TextField( );
				ivOutputDirectory.setMaxWidth( Double.POSITIVE_INFINITY );

				GridPane.setRowIndex( ivOutputDirectory, rowIndex++ );
				GridPane.setColumnIndex( ivOutputDirectory, 2 );

				gridPane.getChildren( ).add( ivOutputDirectory );
			}

			{
				final Label label = new Label( );
				label.setText( getLocalizedString( "timer" ) );

				GridPane.setRowIndex( label, rowIndex );
				GridPane.setColumnIndex( label, 1 );

				gridPane.getChildren( ).add( label );
			}

			{
				ivTimer = new ComboBox<>( );
				ivTimer.setMaxWidth( Double.POSITIVE_INFINITY );
				ivTimer.setConverter( new EnumStringConverter<>( Timer.class ) );
				ivTimer.setItems( FXCollections.observableArrayList( Timer.values( ) ) );

				GridPane.setRowIndex( ivTimer, rowIndex++ );
				GridPane.setColumnIndex( ivTimer, 2 );

				gridPane.getChildren( ).add( ivTimer );
			}

			{
				final Label label = new Label( );
				label.setText( getLocalizedString( "writer" ) );

				GridPane.setRowIndex( label, rowIndex );
				GridPane.setColumnIndex( label, 1 );

				gridPane.getChildren( ).add( label );
			}

			{
				ivWriter = new ComboBox<>( );
				ivWriter.setMaxWidth( Double.POSITIVE_INFINITY );
				ivWriter.setConverter( new EnumStringConverter<>( Writer.class ) );
				ivWriter.setItems( FXCollections.observableArrayList( Writer.values( ) ) );

				GridPane.setRowIndex( ivWriter, rowIndex++ );
				GridPane.setColumnIndex( ivWriter, 2 );

				gridPane.getChildren( ).add( ivWriter );
			}

			{
				final Label label = new Label( );
				label.setText( getLocalizedString( "maxEntriesPerFile" ) );

				GridPane.setRowIndex( label, rowIndex );
				GridPane.setColumnIndex( label, 1 );

				gridPane.getChildren( ).add( label );
			}

			{
				ivMaxEntriesPerFile = new TextField( );
				ivMaxEntriesPerFile.setMaxWidth( Double.POSITIVE_INFINITY );

				GridPane.setRowIndex( ivMaxEntriesPerFile, rowIndex++ );
				GridPane.setColumnIndex( ivMaxEntriesPerFile, 2 );

				gridPane.getChildren( ).add( ivMaxEntriesPerFile );
			}

			{
				final Label label = new Label( );
				label.setText( getLocalizedString( "queueSize" ) );

				GridPane.setRowIndex( label, rowIndex );
				GridPane.setColumnIndex( label, 1 );

				gridPane.getChildren( ).add( label );
			}

			{
				ivQueueSize = new TextField( );
				ivQueueSize.setMaxWidth( Double.POSITIVE_INFINITY );

				GridPane.setRowIndex( ivQueueSize, rowIndex++ );
				GridPane.setColumnIndex( ivQueueSize, 2 );

				gridPane.getChildren( ).add( ivQueueSize );
			}

			{
				final Label label = new Label( );
				label.setText( getLocalizedString( "buffer" ) );

				GridPane.setRowIndex( label, rowIndex );
				GridPane.setColumnIndex( label, 1 );

				gridPane.getChildren( ).add( label );
			}

			{
				ivBuffer = new TextField( );
				ivBuffer.setMaxWidth( Double.POSITIVE_INFINITY );

				GridPane.setRowIndex( ivBuffer, rowIndex++ );
				GridPane.setColumnIndex( ivBuffer, 2 );

				gridPane.getChildren( ).add( ivBuffer );
			}

			getChildren( ).add( gridPane );
		}

		{
			final Separator separator = new Separator( );

			getChildren( ).add( separator );
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
					getController( ).performSaveAndClose( );
				} );

				buttonBar.getButtons( ).add( button );
			}

			getChildren( ).add( buttonBar );
		}
	}

	@Override
	public void setParameter( final Object aParameter ) {
	}

	public void open( final Window aParent ) {
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
	}

	Label getStatus( ) {
		return ivStatus;
	}

	CheckBox getActive( ) {
		return ivActive;
	}

	TextField getOutputDirectory( ) {
		return ivOutputDirectory;
	}

	ComboBoxBase<Timer> getTimer( ) {
		return ivTimer;
	}

	ComboBoxBase<Writer> getWriter( ) {
		return ivWriter;
	}

	TextField getMaxEntriesPerFile( ) {
		return ivMaxEntriesPerFile;
	}

	TextField getQueueSize( ) {
		return ivQueueSize;
	}

	TextField getBuffer( ) {
		return ivBuffer;
	}

}