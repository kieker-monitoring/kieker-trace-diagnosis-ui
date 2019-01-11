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

package kieker.diagnosis.frontend.tab.methods.composite;

import java.text.NumberFormat;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * This component shows the status of the methods view.
 *
 * @author Nils Christian Ehmke
 */
public final class MethodStatusBar extends HBox {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( MethodStatusBar.class.getName( ) );

	private final Label status;
	private final Hyperlink exportToCsvLink;

	public MethodStatusBar( ) {
		{
			status = new Label( );
			status.setMaxWidth( Double.POSITIVE_INFINITY );
			HBox.setHgrow( status, Priority.ALWAYS );
			HBox.setMargin( status, new Insets( 5, 0, 0, 0 ) );

			getChildren( ).add( status );
		}

		{
			exportToCsvLink = new Hyperlink( );
			exportToCsvLink.setId( "methodCallTabExportToCsv" );
			exportToCsvLink.setText( RESOURCE_BUNDLE.getString( "exportToCSV" ) );

			getChildren( ).add( exportToCsvLink );
		}
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
	 * Sets the value which is shown in the component.
	 *
	 * @param methodCalls
	 *            The new number of method calls.
	 * @param totalMethodCalls
	 *            The new total number of method calls.
	 */
	public void setValue( final int methodCalls, final int totalMethodCalls ) {
		final NumberFormat decimalFormat = NumberFormat.getInstance( );
		final String text = String.format( RESOURCE_BUNDLE.getString( "statusLabel" ), decimalFormat.format( methodCalls ), decimalFormat.format( totalMethodCalls ) );
		status.setText( text );
	}

}
