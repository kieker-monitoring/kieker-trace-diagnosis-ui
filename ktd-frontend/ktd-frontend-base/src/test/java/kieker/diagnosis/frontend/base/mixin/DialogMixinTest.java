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

package kieker.diagnosis.frontend.base.mixin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;

/**
 * This is a unit test for {@link DialogMixin}.
 *
 * @author Nils Christian Ehmke
 */
public final class DialogMixinTest implements DialogMixin {

	private DialogPane dialogPane;

	@Before
	public void before( ) {
		dialogPane = mock( DialogPane.class );
	}

	@Test
	public void testGetStage( ) {
		final Scene scene = mock( Scene.class );
		final Stage stage = mock( Stage.class );
		when( dialogPane.getScene( ) ).thenReturn( scene );
		when( scene.getWindow( ) ).thenReturn( stage );

		assertThat( getStage( ) ).isEqualTo( stage );
	}

	@Test
	public void testAddDefaultStylesheet( ) {
		final ObservableList<String> stylesheets = FXCollections.observableArrayList( );
		when( dialogPane.getStylesheets( ) ).thenReturn( stylesheets );

		addDefaultStylesheet( );

		assertThat( stylesheets ).hasSize( 1 );
		assertTrue( stylesheets.get( 0 ).contains( "DialogMixinTest.css" ) );
	}

	@Override
	public DialogPane getDialogPane( ) {
		return dialogPane;
	}

}
