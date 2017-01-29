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

package kieker.diagnosis.guitest.mainview;

import org.testfx.framework.junit.ApplicationTest;

import kieker.diagnosis.guitest.components.Button;

public final class MainView {

	private final ApplicationTest applicationTest;

	public MainView( final ApplicationTest applicationTest ) {
		this.applicationTest = applicationTest;
	}

	public Button getTracesButton( ) {
		return new Button( this.applicationTest, "#ivTraces" );
	}

	public Button getAggregatedTracesButton( ) {
		return new Button( this.applicationTest, "#ivAggregatedtraces" );
	}

	public Button getCallsButton( ) {
		return new Button( this.applicationTest, "#ivCalls" );
	}

	public Button getAggregatedCallsButton( ) {
		return new Button( this.applicationTest, "#ivAggregatedcalls" );
	}

	public Button getStatisticsButton( ) {
		return new Button( this.applicationTest, "#ivStatistics" );
	}

	public Button getHelpButton( ) {
		return new Button( this.applicationTest, "#ivHelp" );
	}

	public Button getAboutButton( ) {
		return new Button( this.applicationTest, "#ivAbout" );
	}

	public Button getFileButton( ) {
		return new Button( this.applicationTest, "#ivFile" );
	}

	public Button getSettingsButton( ) {
		return new Button( this.applicationTest, "#ivSettings" );
	}

	public Button getOpenMonitoringLogButton( ) {
		return new Button( this.applicationTest, "#ivOpenMonitoringLog" );
	}

}
