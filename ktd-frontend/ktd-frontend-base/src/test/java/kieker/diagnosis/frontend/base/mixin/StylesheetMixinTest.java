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

import org.junit.Before;
import org.junit.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This is a unit test for {@link StylesheetMixin}.
 *
 * @author Nils Christian Ehmke
 */
public final class StylesheetMixinTest implements StylesheetMixin {

	private final ObservableList<String> stylesheets = FXCollections.observableArrayList( );

	@Before
	public void before( ) {
		stylesheets.clear( );
	}

	@Test
	public void testAddDefaultStylesheet( ) {
		assertThat( stylesheets ).isEmpty( );

		addDefaultStylesheet( );

		assertThat( stylesheets ).hasSize( 1 );
		assertTrue( stylesheets.get( 0 ).contains( "StylesheetMixinTest.css" ) );
	}

	@Override
	public ObservableList<String> getStylesheets( ) {
		return stylesheets;
	}

}
