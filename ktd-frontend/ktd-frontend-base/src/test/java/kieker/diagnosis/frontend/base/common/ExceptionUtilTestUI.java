/*************************************************************************** 
 * Copyright 2015-2018 Kieker Project (http://kieker-monitoring.net)         
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

package kieker.diagnosis.frontend.base.common;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import kieker.diagnosis.backend.base.exception.BusinessException;
import kieker.diagnosis.backend.base.exception.BusinessRuntimeException;
import kieker.diagnosis.frontend.base.common.ExceptionUtil;

/**
 * This is a UI test which checks that the exception handling is working as expected.
 *
 * @author Nils Christian Ehmke
 */
public final class ExceptionUtilTestUI extends ApplicationTest {

	@Override
	public void start( Stage stage ) throws Exception {
		Scene scene = new Scene( new VBox( ) );
		stage.setScene( scene );
		stage.show( );
	}

	@Test
	public void testWithhBusinessException( ) {
		ExceptionUtil.handleException( new BusinessRuntimeException( new BusinessException( "Test Exception" ) ), "test" );
		WaitForAsyncUtils.waitForFxEvents( );

		assertThat( lookup( ".dialog-pane .label" ).queryLabeled( ).getText( ), is( "Ein Fehler ist aufgetreten." ) );
		assertThat( lookup( ".dialog-pane .content.label" ).queryLabeled( ).getText( ), is( "Test Exception" ) );
		clickOn( ".dialog-pane .button" );
	}

	@Test
	public void testWithUnexpectedException( ) {
		ExceptionUtil.handleException( new IllegalArgumentException( "Test Exception" ), "test" );
		WaitForAsyncUtils.waitForFxEvents( );

		assertThat( lookup( ".dialog-pane .label" ).queryLabeled( ).getText( ), is( "Ein Fehler ist aufgetreten." ) );
		assertThat( lookup( ".dialog-pane .content.label" ).queryLabeled( ).getText( ), is( "Test Exception" ) );
		clickOn( ".dialog-pane .button" );
	}

}
