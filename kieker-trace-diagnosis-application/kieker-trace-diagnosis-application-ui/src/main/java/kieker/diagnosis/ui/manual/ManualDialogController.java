/***************************************************************************
 * Copyright 2015-2017 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.ui.manual;

import java.net.URL;
import java.util.Locale;

import com.google.inject.Singleton;

import kieker.diagnosis.architecture.ui.ControllerBase;

/**
 * The controller of the user manual dialog.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class ManualDialogController extends ControllerBase<ManualDialogViewModel> {

	public void performRefresh( ) {
		final Locale locale = Locale.getDefault( );
		final String suffix = locale == Locale.GERMAN || locale == Locale.GERMANY ? "_de" : "";
		final URL documentation = getClass( ).getClassLoader( ).getResource( "kieker/diagnosis/ui/manual/html/manual" + suffix + ".html" );

		getViewModel( ).updatePresentation( documentation );
	}

}
