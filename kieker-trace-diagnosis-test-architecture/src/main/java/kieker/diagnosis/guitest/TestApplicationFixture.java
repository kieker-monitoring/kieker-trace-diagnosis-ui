/***************************************************************************
 * Copyright 2015-2016 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.guitest;

import javafx.application.Application;
import javafx.stage.Stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.testfx.api.FxRobot;
import org.testfx.toolkit.ApplicationFixture;

/**
 * @author Nils Christian Ehmke
 */
@Component
class TestApplicationFixture extends FxRobot implements ApplicationFixture, JavaFXThreadPerformer {

	@Autowired
	private Application ivApplication;

	@Override
	public void init( ) {
		// No init code necessary
	}

	@Override
	public void start( final Stage aStage ) throws Exception { // NOPMD (The exception is acceptable in this case)
		ivApplication.start( aStage );
	}

	@Override
	public void stop( ) {
		// No cleanup code necessary
	}

	@Override
	public void perform( final Runnable aTask ) {
		interact( aTask );
	}

}
