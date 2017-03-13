package kieker.diagnosis.application;

import kieker.diagnosis.application.gui.main.MainController;
import kieker.diagnosis.application.service.data.DataService;
import kieker.diagnosis.architecture.gui.GuiLoader;

import java.util.List;

import javafx.application.Application;
import javafx.stage.Stage;

import org.springframework.boot.Banner.Mode;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.google.common.collect.Iterables;

@SpringBootApplication
public class Main extends Application {

	private DataService ivDataService;

	public static void main( final String[] args ) {
		Application.launch( args );
	}

	@Override
	public void start( final Stage aPrimaryStage ) throws Exception {
		// Load the Spring context
		final SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder( getClass( ) );
		final ConfigurableApplicationContext context = springApplicationBuilder.bannerMode( Mode.OFF ).logStartupInfo( false ).run( getArguments( ) );

		// Now load the main view
		final GuiLoader guiLoader = context.getBean( GuiLoader.class );
		guiLoader.loadAsMainView( MainController.class, aPrimaryStage );

		// Load the dataservice. This is just for the GUI test as the dialogs can not be handled by test fx
		ivDataService = context.getBean( DataService.class );
	}

	private String[] getArguments( ) {
		final Parameters parameters = getParameters( );
		if ( parameters != null ) {
			final List<String> rawParameters = parameters.getRaw( );
			return Iterables.toArray( rawParameters, String.class );
		} else {
			return new String[0];
		}
	}

	public DataService getDataService( ) {
		return ivDataService;
	}

}
