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

package kieker.diagnosis.guitestarchitecture;

import java.util.concurrent.TimeoutException;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.testfx.api.FxToolkit;

/**
 * @author Nils Christian Ehmke
 */
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
