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

package kieker.diagnosis.frontend.base.atom;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This is a actually just a unit test for {@link LongTextField}, but it requires the JavaFX environment.
 *
 * @author Nils Christian Ehmke
 */
public final class LongTextFieldTestUI extends ApplicationTest {

	private LongTextField textField;

	@Override
	public void start( final Stage stage ) throws Exception {
		textField = new LongTextField( );
		final Scene scene = new Scene( textField );
		stage.setScene( scene );
		stage.show( );
	}

	@Test
	public void testValidValue( ) {
		textField.setValue( 42L );

		textField.setText( "50" );
		assertThat( textField.getValue( ), is( 50L ) );
	}

	@Test
	public void testInvalidValues( ) {
		textField.setValue( 42L );

		textField.setText( "abc" );
		assertThat( textField.getValue( ), is( 42L ) );

		textField.setText( "--1" );
		assertThat( textField.getValue( ), is( 42L ) );

		textField.setText( "++1" );
		assertThat( textField.getValue( ), is( 42L ) );

		textField.setText( "." );
		assertThat( textField.getValue( ), is( 42L ) );

		textField.setText( "0.5" );
		assertThat( textField.getValue( ), is( 42L ) );
	}

	@Test
	public void testEmptyValue( ) {
		textField.setText( "" );
		assertThat( textField.getValue( ), is( nullValue( ) ) );
	}

}
