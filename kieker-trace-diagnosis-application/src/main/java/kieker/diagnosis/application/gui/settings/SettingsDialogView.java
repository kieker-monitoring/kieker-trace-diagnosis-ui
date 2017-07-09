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

package kieker.diagnosis.application.gui.settings;

import kieker.diagnosis.application.service.properties.ComponentNames;
import kieker.diagnosis.application.service.properties.MethodCallAggregation;
import kieker.diagnosis.application.service.properties.OperationNames;
import kieker.diagnosis.application.service.properties.TimestampTypes;
import kieker.diagnosis.architecture.gui.AbstractView;
import kieker.diagnosis.architecture.gui.AutowiredElement;

import java.util.concurrent.TimeUnit;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Window;

import org.springframework.stereotype.Component;

/**
 * @author Nils Christian Ehmke
 */
@Component
final class SettingsDialogView extends AbstractView {

	@AutowiredElement
	private ComboBox<OperationNames> ivOperationNames;
	@AutowiredElement
	private ComboBox<ComponentNames> ivComponentNames;
	@AutowiredElement
	private ComboBox<TimeUnit> ivTimeunits;
	@AutowiredElement
	private ComboBox<TimestampTypes> ivTimestamps;
	@AutowiredElement
	private CheckBox ivAdditionalLogChecks;
	@AutowiredElement
	private CheckBox ivActivateRegularExpressions;
	@AutowiredElement
	private CheckBox ivCaseSensitive;
	@AutowiredElement
	private CheckBox ivPercentageCalculation;
	@AutowiredElement
	private CheckBox ivSearchInEntireTrace;
	@AutowiredElement
	private CheckBox ivShowUnmonitoredTime;
	@AutowiredElement
	private ComboBox<MethodCallAggregation> ivTypeOfMethodAggregation;
	@AutowiredElement
	private Label ivMaxNumberOfMethodCallsLabel;
	@AutowiredElement
	private TextField ivMaxNumberOfMethodCallsTextField;
	@AutowiredElement
	private Label ivThresholdLabel;
	@AutowiredElement
	private TextField ivThresholdTextField;
	@AutowiredElement
	private Node ivView;

	public ComboBox<OperationNames> getOperationNames( ) {
		return ivOperationNames;
	}

	public ComboBox<ComponentNames> getComponentNames( ) {
		return ivComponentNames;
	}

	public ComboBox<TimeUnit> getTimeunits( ) {
		return ivTimeunits;
	}

	public ComboBox<TimestampTypes> getTimestamps( ) {
		return ivTimestamps;
	}

	public CheckBox getAdditionalLogChecks( ) {
		return ivAdditionalLogChecks;
	}

	public CheckBox getActivateRegularExpressions( ) {
		return ivActivateRegularExpressions;
	}

	public CheckBox getCaseSensitive( ) {
		return ivCaseSensitive;
	}

	public CheckBox getPercentageCalculation( ) {
		return ivPercentageCalculation;
	}

	public CheckBox getSearchInEntireTrace( ) {
		return ivSearchInEntireTrace;
	}

	public CheckBox getShowUnmonitoredTime( ) {
		return ivShowUnmonitoredTime;
	}

	public ComboBox<MethodCallAggregation> getTypeOfMethodAggregation( ) {
		return ivTypeOfMethodAggregation;
	}

	public Label getMaxNumberOfMethodCallsLabel( ) {
		return ivMaxNumberOfMethodCallsLabel;
	}

	public TextField getMaxNumberOfMethodCallsTextField( ) {
		return ivMaxNumberOfMethodCallsTextField;
	}

	public Label getThresholdLabel( ) {
		return ivThresholdLabel;
	}

	public TextField getThresholdTextField( ) {
		return ivThresholdTextField;
	}

	public Node getView( ) {
		return ivView;
	}

	public Window getStage( ) {
		final Scene scene = ivView.getScene( );
		return scene.getWindow( );
	}

}
