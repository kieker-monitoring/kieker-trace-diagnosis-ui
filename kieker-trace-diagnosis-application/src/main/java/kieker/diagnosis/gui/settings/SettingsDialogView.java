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

package kieker.diagnosis.gui.settings;

import java.util.concurrent.TimeUnit;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.stage.Window;

import kieker.diagnosis.gui.AbstractView;
import kieker.diagnosis.gui.InjectComponent;
import kieker.diagnosis.service.properties.ComponentNames;
import kieker.diagnosis.service.properties.OperationNames;
import kieker.diagnosis.service.properties.Threshold;
import kieker.diagnosis.service.properties.TimestampTypes;

/**
 * @author Nils Christian Ehmke
 */
public final class SettingsDialogView extends AbstractView {

	@InjectComponent
	private ComboBox<OperationNames> ivOperationNames;
	@InjectComponent
	private ComboBox<ComponentNames> ivComponentNames;
	@InjectComponent
	private ComboBox<Threshold> ivThresholds;
	@InjectComponent
	private ComboBox<TimeUnit> ivTimeunits;
	@InjectComponent
	private ComboBox<TimestampTypes> ivTimestamps;
	@InjectComponent
	private CheckBox ivAdditionalLogChecks;
	@InjectComponent
	private CheckBox ivActivateRegularExpressions;
	@InjectComponent
	private CheckBox ivAggregateMethodCalls;
	@InjectComponent
	private CheckBox ivCaseSensitive;
	@InjectComponent
	private CheckBox ivPercentageCalculation;
	@InjectComponent
	private CheckBox ivCacheViews;
	@InjectComponent
	private CheckBox ivSearchInEntireTrace;
	@InjectComponent
	private CheckBox ivShowUnmonitoredTime;
	@InjectComponent
	private Node ivView;

	public ComboBox<OperationNames> getOperationNames( ) {
		return ivOperationNames;
	}

	public ComboBox<ComponentNames> getComponentNames( ) {
		return ivComponentNames;
	}

	public ComboBox<Threshold> getThresholds( ) {
		return ivThresholds;
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

	public CheckBox getAggregateMethodCalls( ) {
		return ivAggregateMethodCalls;
	}

	public CheckBox getCaseSensitive( ) {
		return ivCaseSensitive;
	}

	public CheckBox getPercentageCalculation( ) {
		return ivPercentageCalculation;
	}

	public CheckBox getCacheViews( ) {
		return ivCacheViews;
	}

	public CheckBox getSearchInEntireTrace( ) {
		return ivSearchInEntireTrace;
	}

	public CheckBox getShowUnmonitoredTime( ) {
		return ivShowUnmonitoredTime;
	}

	public Node getView( ) {
		return ivView;
	}

	public Window getStage( ) {
		final Scene scene = ivView.getScene( );
		return scene.getWindow( );
	}

}
