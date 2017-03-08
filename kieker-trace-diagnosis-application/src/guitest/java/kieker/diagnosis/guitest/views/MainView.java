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

package kieker.diagnosis.guitest.views;

import org.springframework.stereotype.Component;

import kieker.diagnosis.guitest.components.Button;

@Component
public final class MainView extends AbstractView {

	public Button getTracesButton( ) {
		return getComponent( Button.class, "ivTraces" );
	}

	public Button getAggregatedTracesButton( ) {
		return getComponent( Button.class, "ivAggregatedtraces" );
	}

	public Button getCallsButton( ) {
		return getComponent( Button.class, "ivCalls" );
	}

	public Button getAggregatedCallsButton( ) {
		return getComponent( Button.class, "ivAggregatedcalls" );
	}

	public Button getStatisticsButton( ) {
		return getComponent( Button.class, "ivStatistics" );
	}

	public Button getHelpButton( ) {
		return getComponent( Button.class, "ivHelp" );
	}

	public Button getAboutButton( ) {
		return getComponent( Button.class, "ivAbout" );
	}

	public Button getFileButton( ) {
		return getComponent( Button.class, "ivFile" );
	}

	public Button getSettingsButton( ) {
		return getComponent( Button.class, "ivSettings" );
	}

	public Button getOpenMonitoringLogButton( ) {
		return getComponent( Button.class, "ivOpenMonitoringLog" );
	}

}
