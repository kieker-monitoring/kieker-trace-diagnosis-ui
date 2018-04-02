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

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Command;
import kieker.diagnosis.architecture.ui.ViewModelBase;

/**
 * The view model of the about dialog.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class AboutDialogViewModel extends ViewModelBase<AboutDialogView> implements ViewModel {

	public static final String EVENT_CLOSE_DIALOG = "EVENT_CLOSE_DIALOG";

	private final Command ivCloseDialogCommand = createCommand( this::performClose );

	Command getCloseDialogCommand( ) {
		return ivCloseDialogCommand;
	}

	/**
	 * This action is performed, when the user wants to close the about dialog.
	 */
	private void performClose( ) {
		publish( EVENT_CLOSE_DIALOG );
	}

}
