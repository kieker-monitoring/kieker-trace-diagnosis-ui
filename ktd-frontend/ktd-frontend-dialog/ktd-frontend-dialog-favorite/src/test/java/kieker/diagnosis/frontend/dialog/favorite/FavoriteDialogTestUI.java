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

package kieker.diagnosis.frontend.dialog.favorite;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This is a UI test which checks that the favorite dialog is working as expected.
 *
 * @author Nils Christian Ehmke
 */
public final class FavoriteDialogTestUI extends ApplicationTest {

	private FavoriteDialog favoriteDialog;
	private FavoriteDialogPage favoriteDialogPage;

	@Override
	public void start( final Stage stage ) throws Exception {
		final Scene scene = new Scene( new VBox( ) );
		stage.setScene( scene );
		stage.show( );

		favoriteDialog = new FavoriteDialog( stage );
		favoriteDialog.show( );

		favoriteDialogPage = new FavoriteDialogPage( this );
	}

	@Test
	public void testCancel( ) {
		favoriteDialogPage.getTextField( ).writeText( "Favorit 1" );
		favoriteDialogPage.getCancel( ).click( );

		assertThat( favoriteDialog.getResult( ) ).isNull( );
	}

	@Test
	public void testOk( ) {
		favoriteDialogPage.getTextField( ).writeText( "Favorit 1" );
		favoriteDialogPage.getOk( ).click( );

		assertThat( favoriteDialog.getResult( ) ).isEqualTo( "Favorit 1" );
	}

}
