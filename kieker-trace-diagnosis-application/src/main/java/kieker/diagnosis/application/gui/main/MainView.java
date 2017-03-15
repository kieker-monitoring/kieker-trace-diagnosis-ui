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

package kieker.diagnosis.application.gui.main;

import kieker.diagnosis.architecture.gui.AbstractView;
import kieker.diagnosis.architecture.gui.AutowiredElement;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import org.springframework.stereotype.Component;

/**
 * @author Nils Christian Ehmke
 */
@Component
class MainView extends AbstractView {

	@AutowiredElement
	private Node ivView;
	@AutowiredElement
	private AnchorPane ivContent;
	@AutowiredElement
	private VBox ivLeftButtonBox;

	@AutowiredElement
	private Button ivTraces;
	@AutowiredElement
	private Button ivAggregatedtraces;
	@AutowiredElement
	private Button ivCalls;
	@AutowiredElement
	private Button ivAggregatedcalls;
	@AutowiredElement
	private Button ivStatistics;

	public Node getView( ) {
		return ivView;
	}

	public AnchorPane getContent( ) {
		return ivContent;
	}

	public VBox getLeftButtonBox( ) {
		return ivLeftButtonBox;
	}

	public Button getTraces( ) {
		return ivTraces;
	}

	public Button getAggregatedtraces( ) {
		return ivAggregatedtraces;
	}

	public Button getCalls( ) {
		return ivCalls;
	}

	public Button getAggregatedcalls( ) {
		return ivAggregatedcalls;
	}

	public Button getStatistics( ) {
		return ivStatistics;
	}

	public Window getWindow( ) {
		final Scene scene = ivContent.getScene( );
		return scene.getWindow( );
	}
}
