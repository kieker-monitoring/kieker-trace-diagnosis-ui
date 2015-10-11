/***************************************************************************
 * Copyright 2015 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.controller.bugreporting;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import kieker.diagnosis.controller.AbstractDialogController;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.util.ErrorHandling;

/**
 * @author Nils Christian Ehmke
 */
public final class BugReportingDialogViewController extends AbstractDialogController {

	private final PropertiesModel propertiesModel = PropertiesModel.getInstance();

	@ErrorHandling
	public void visitGitLab() throws IOException, URISyntaxException {
		final String gitLabURL = this.propertiesModel.getGitLabURL();
		final Desktop desktop = Desktop.getDesktop();
		desktop.browse(new URI(gitLabURL));
	}

	@ErrorHandling
	public void visitTrac() throws IOException, URISyntaxException {
		final String tracURL = this.propertiesModel.getTracURL();
		final Desktop desktop = Desktop.getDesktop();
		desktop.browse(new URI(tracURL));
	}

}
