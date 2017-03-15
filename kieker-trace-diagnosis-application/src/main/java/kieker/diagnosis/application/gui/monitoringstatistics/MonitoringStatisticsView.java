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

package kieker.diagnosis.application.gui.monitoringstatistics;

import kieker.diagnosis.architecture.gui.AbstractView;
import kieker.diagnosis.architecture.gui.AutowiredElement;

import javafx.scene.control.TextField;

import org.springframework.stereotype.Component;

/**
 * @author Nils Christian Ehmke
 */
@Component
final class MonitoringStatisticsView extends AbstractView {

	@AutowiredElement
	private TextField ivMonitoringlog;
	@AutowiredElement
	private TextField ivMonitoringsize;
	@AutowiredElement
	private TextField ivAnalysistime;
	@AutowiredElement
	private TextField ivBeginofmonitoring;
	@AutowiredElement
	private TextField ivEndofmonitoring;
	@AutowiredElement
	private TextField ivNumberofcalls;
	@AutowiredElement
	private TextField ivNumberoffailedcalls;
	@AutowiredElement
	private TextField ivNumberofaggcalls;
	@AutowiredElement
	private TextField ivNumberoffailedaggcalls;
	@AutowiredElement
	private TextField ivNumberoftraces;
	@AutowiredElement
	private TextField ivNumberoffailedtraces;
	@AutowiredElement
	private TextField ivNumberoffailuretraces;
	@AutowiredElement
	private TextField ivNumberofaggtraces;
	@AutowiredElement
	private TextField ivNumberofaggfailedtraces;
	@AutowiredElement
	private TextField ivNumberofaggfailuretraces;
	@AutowiredElement
	private TextField ivIncompletetraces;
	@AutowiredElement
	private TextField ivDanglingrecords;
	@AutowiredElement
	private TextField ivIgnoredRecords;

	TextField getMonitoringlog( ) {
		return ivMonitoringlog;
	}

	TextField getMonitoringsize( ) {
		return ivMonitoringsize;
	}

	TextField getAnalysistime( ) {
		return ivAnalysistime;
	}

	TextField getBeginofmonitoring( ) {
		return ivBeginofmonitoring;
	}

	TextField getEndofmonitoring( ) {
		return ivEndofmonitoring;
	}

	TextField getNumberofcalls( ) {
		return ivNumberofcalls;
	}

	TextField getNumberoffailedcalls( ) {
		return ivNumberoffailedcalls;
	}

	TextField getNumberofaggcalls( ) {
		return ivNumberofaggcalls;
	}

	TextField getNumberoffailedaggcalls( ) {
		return ivNumberoffailedaggcalls;
	}

	TextField getNumberoftraces( ) {
		return ivNumberoftraces;
	}

	TextField getNumberoffailedtraces( ) {
		return ivNumberoffailedtraces;
	}

	TextField getNumberoffailuretraces( ) {
		return ivNumberoffailuretraces;
	}

	TextField getNumberofaggtraces( ) {
		return ivNumberofaggtraces;
	}

	TextField getNumberofaggfailedtraces( ) {
		return ivNumberofaggfailedtraces;
	}

	TextField getNumberofaggfailuretraces( ) {
		return ivNumberofaggfailuretraces;
	}

	TextField getIncompletetraces( ) {
		return ivIncompletetraces;
	}

	TextField getDanglingrecords( ) {
		return ivDanglingrecords;
	}

	TextField getIgnoredRecords( ) {
		return ivIgnoredRecords;
	}

}
