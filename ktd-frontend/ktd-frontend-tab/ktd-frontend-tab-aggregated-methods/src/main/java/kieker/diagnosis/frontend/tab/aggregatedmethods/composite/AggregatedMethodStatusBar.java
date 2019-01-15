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

package kieker.diagnosis.frontend.tab.aggregatedmethods.composite;

import java.text.NumberFormat;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * This component shows the status of the aggregated methods view.
 *
 * @author Nils Christian Ehmke
 */
public final class AggregatedMethodStatusBar extends HBox {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( AggregatedMethodStatusBar.class.getName( ) );

	private Label statusField;
	private Hyperlink exportToCsvLink;

	public AggregatedMethodStatusBar( ) {
		createControl( );
	}

	private void createControl( ) {
		getChildren( ).add( createStatusField( ) );
		getChildren( ).add( createExportToCsvLink( ) );
	}

	private Node createStatusField( ) {
		statusField = new Label( );

		statusField.setMaxWidth( Double.POSITIVE_INFINITY );
		HBox.setHgrow( statusField, Priority.ALWAYS );
		HBox.setMargin( statusField, new Insets( 5, 0, 0, 0 ) );

		return statusField;
	}

	private Node createExportToCsvLink( ) {
		exportToCsvLink = new Hyperlink( );

		exportToCsvLink.setText( RESOURCE_BUNDLE.getString( "exportToCSV" ) );

		return exportToCsvLink;
	}

	/**
	 * Sets the action which is performed when the user wants to export the methods to CSV.
	 *
	 * @param value
	 *            The action.
	 */
	public void setOnExportToCsv( final EventHandler<ActionEvent> value ) {
		exportToCsvLink.setOnAction( value );
	}

	/**
	 * Sets the value which is displayed in the component.
	 *
	 * @param methods
	 *            The number of methods.
	 * @param totalMethods
	 *            The total number of methods.
	 */
	public void setValue( final int methods, final int totalMethods ) {
		final NumberFormat decimalFormat = NumberFormat.getInstance( );
		final String text = String.format( RESOURCE_BUNDLE.getString( "statusLabel" ), decimalFormat.format( methods ), decimalFormat.format( totalMethods ) );
		statusField.setText( text );
	}

}
