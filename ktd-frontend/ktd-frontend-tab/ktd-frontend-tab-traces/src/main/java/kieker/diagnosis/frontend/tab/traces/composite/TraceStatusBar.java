/***************************************************************************
 * Copyright 2015-2019 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.frontend.tab.traces.composite;

import java.text.NumberFormat;
import java.util.ResourceBundle;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * This component shows the status of the traces view.
 *
 * @author Nils Christian Ehmke
 */
public final class TraceStatusBar extends HBox {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( TraceStatusBar.class.getName( ) );
	private final Label status;

	public TraceStatusBar( ) {
		status = new Label( );
		status.setMaxWidth( Double.POSITIVE_INFINITY );
		HBox.setHgrow( status, Priority.ALWAYS );
		HBox.setMargin( status, new Insets( 5, 0, 0, 0 ) );

		getChildren( ).add( status );
	}

	/**
	 * Sets the value which is shown in the component.
	 *
	 * @param value
	 *            The new value. Can also be {@code null}.
	 */
	public void setValue( final String value ) {
		status.setText( value );
	}

	/**
	 * Sets the value which is shown in the component.
	 *
	 * @param traces
	 *            The new number of traces.
	 * @param totalTraces
	 *            The new total number of traces.
	 */
	public void setValue( final int traces, final int totalTraces ) {
		final NumberFormat decimalFormat = NumberFormat.getInstance( );
		status.setText( String.format( RESOURCE_BUNDLE.getString( "statusLabel" ), decimalFormat.format( traces ), decimalFormat.format( totalTraces ) ) );
	}
}
