/***************************************************************************
 * Copyright 2015-2016 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.gui.bugreporting;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import kieker.diagnosis.gui.AbstractController;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.util.Context;

/**
 * @author Nils Christian Ehmke
 */
public final class BugReportingDialogController extends AbstractController<BugReportingDialogView> implements BugReportingDialogControllerIfc {

	private final PropertiesModel ivPropertiesModel = PropertiesModel.getInstance( );

	public BugReportingDialogController( final Context aContext ) {
		super( aContext );
	}

	@Override
	public void doInitialize( ) {
	}

	@Override
	public void visitGitLab( ) throws IOException, URISyntaxException {
		final String gitLabURL = ivPropertiesModel.getGitLabURL( );
		final Desktop desktop = Desktop.getDesktop( );
		desktop.browse( new URI( gitLabURL ) );
	}

	@Override
	public void visitTrac( ) throws IOException, URISyntaxException {
		final String tracURL = ivPropertiesModel.getTracURL( );
		final Desktop desktop = Desktop.getDesktop( );
		desktop.browse( new URI( tracURL ) );
	}

	@Override
	public void closeDialog( ) {
		getView( ).getStage( ).hide( );
	}

}
