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

package kieker.diagnosis.ui.dialogs.about;

import com.google.inject.Singleton;

import kieker.diagnosis.architecture.ui.ControllerBase;

/**
 * The controller of the about dialog.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
class AboutDialogController extends ControllerBase<AboutDialogViewModel> {

	/**
	 * This action is performed, when the user wants to close the about dialog.
	 */
	public void performClose( ) {
		getViewModel( ).close( );
	}

}
