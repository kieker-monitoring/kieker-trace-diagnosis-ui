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

package kieker.diagnosis.frontend.dialog.progress;

import java.util.ResourceBundle;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import kieker.diagnosis.frontend.base.mixin.DialogMixin;
import kieker.diagnosis.frontend.base.mixin.ImageMixin;

/**
 * A progress dialog, which can be displayed during lengthy processes.
 *
 * @author Nils Christian Ehmke
 */
public final class ProgressDialog extends Alert implements DialogMixin, ImageMixin {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( ProgressDialog.class.getName( ) );

	private ProgressIndicator progressIndicator;
	private Label label;

	public ProgressDialog( final Window owner ) {
		super( AlertType.NONE );

		createControl( owner );
	}

	private void createControl( final Window owner ) {
		addDefaultStylesheet( );

		setTitle( RESOURCE_BUNDLE.getString( "title" ) );
		initOwner( owner );
		getDialogPane( ).setContent( createVBox( ) );
	}

	private Node createVBox( ) {
		final VBox vBox = new VBox( );

		vBox.setAlignment( Pos.CENTER );
		vBox.setPadding( new Insets( 5 ) );
		vBox.setSpacing( 10 );

		vBox.setPrefHeight( 100 );
		vBox.setPrefWidth( 250 );

		vBox.getChildren( ).add( createProgressIndicator( ) );
		vBox.getChildren( ).add( createLabel( ) );

		return vBox;
	}

	public void closeDialog( ) {
		getStage( ).close( );
	}

	private Node createProgressIndicator( ) {
		progressIndicator = new ProgressIndicator( );
		return progressIndicator;
	}

	private Node createLabel( ) {
		label = new Label( );

		label.setId( "progressDialogMessage" );
		VBox.setVgrow( label, Priority.ALWAYS );

		return label;
	}

	public void setMessage( final String message ) {
		label.setText( message );
		getDialogPane( ).layout( );
	}

	public void setProgress( final double progress ) {
		progressIndicator.setProgress( progress );
	}

}
