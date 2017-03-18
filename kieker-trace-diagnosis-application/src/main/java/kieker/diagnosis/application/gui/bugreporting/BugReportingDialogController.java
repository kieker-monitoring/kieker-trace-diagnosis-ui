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

package kieker.diagnosis.application.gui.bugreporting;

import kieker.diagnosis.application.service.properties.GitLabURLProperty;
import kieker.diagnosis.application.service.properties.MailingListURLProperty;
import kieker.diagnosis.architecture.gui.AbstractController;
import kieker.diagnosis.architecture.service.properties.PropertiesService;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The controller for the bug reporting dialog.
 *
 * @author Nils Christian Ehmke
 */
@Component
public class BugReportingDialogController extends AbstractController<BugReportingDialogView> {

	@Autowired
	private PropertiesService ivPropertiesService;

	@Override
	protected void doInitialize( final boolean aFirstInitialization, final Optional<?> aParameter ) {
		// Nothing to initialize
	}

	@Override
	public void doRefresh( ) {
		// Nothing to refresh
	}

	public void performVisitGitLab( ) throws IOException, URISyntaxException {
		final String gitLabURL = ivPropertiesService.loadSystemProperty( GitLabURLProperty.class );
		final Desktop desktop = Desktop.getDesktop( );
		desktop.browse( new URI( gitLabURL ) );
	}

	public void performVisitMailingList( ) throws IOException, URISyntaxException {
		final String tracURL = ivPropertiesService.loadSystemProperty( MailingListURLProperty.class );
		final Desktop desktop = Desktop.getDesktop( );
		desktop.browse( new URI( tracURL ) );
	}

	public void performCloseDialog( ) {
		getView( ).getStage( ).hide( );
	}

}
