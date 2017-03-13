package kieker.diagnosis.guitestarchitecture;

import java.util.concurrent.TimeoutException;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.testfx.api.FxToolkit;

@Component
public final class JavaFXInitializer implements ApplicationListener<ContextRefreshedEvent> {

	@Override
	public void onApplicationEvent( final ContextRefreshedEvent aEvent ) {
		final TestApplicationFixture testApplicationFixture = aEvent.getApplicationContext( ).getBean( TestApplicationFixture.class );

		try {
			FxToolkit.registerPrimaryStage( );
			FxToolkit.setupApplication( testApplicationFixture );
		} catch ( final TimeoutException ex ) {
			throw new IllegalStateException( ex );
		}
	}

}