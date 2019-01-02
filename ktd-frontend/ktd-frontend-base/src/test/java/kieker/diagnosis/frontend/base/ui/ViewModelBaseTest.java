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

package kieker.diagnosis.frontend.base.ui;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import javafx.scene.Scene;
import javafx.stage.Window;
import kieker.diagnosis.backend.monitoring.MonitoringService;
import kieker.diagnosis.frontend.base.FrontendBaseModule;

/**
 * This is a unit test for {@link ViewModelBase}.
 * 
 * @author Nils Christian Ehmke
 */
public final class ViewModelBaseTest {

	private TestViewModel viewModel;
	private TestView view;

	@Before
	public void before( ) {
		final Injector injector = Guice.createInjector( new FrontendBaseModule( ), new Module( ) );
		viewModel = injector.getInstance( TestViewModel.class );
		view = injector.getInstance( TestView.class );
	}

	@Test
	public void testGetLocalizedString( ) {
		assertThat( viewModel.getLocalizedString( "key" ), is( "value" ) );
	}

	@Test
	public void testGetView( ) {
		assertThat( viewModel.getView( ), is( view ) );
	}

	@Test
	public void testGetService( ) {
		assertThat( viewModel.getService( MonitoringService.class ), is( instanceOf( MonitoringService.class ) ) );
	}

	@Test
	public void testClose( ) {
		final Scene scene = mock( Scene.class );
		final Window window = mock( Window.class );
		when( scene.getWindow( ) ).thenReturn( window );
		when( view.getScene( ) ).thenReturn( scene );

		viewModel.close( );

		verify( window ).hide( );
	}

	private static final class Module extends AbstractModule {

		@Override
		protected void configure( ) {
			bind( TestView.class ).toInstance( mock( TestView.class ) );
		}

	}

}
