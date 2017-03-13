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

package kieker.diagnosis.architecture.gui;

import java.util.Optional;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Nils Christian Ehmke
 *
 * @param <V>
 *            The view type controlled by this controller.
 */
public abstract class AbstractController<V extends AbstractView> {

	@Autowired
	private V ivView;

	private String ivStylesheet;
	private boolean ivInitialized;
	private ResourceBundle ivResourceBundle;

	final void setStylesheet( final String aStylesheet ) {
		ivStylesheet = aStylesheet;
	}

	final String getStylesheet( ) {
		return ivStylesheet;
	}

	protected final ResourceBundle getResourceBundle( ) {
		return ivResourceBundle;
	}

	final void setResourceBundle( final ResourceBundle aResourceBundle ) {
		ivResourceBundle = aResourceBundle;
	}

	final void doInitialize( final Optional<?> aParameter ) {
		doInitialize( !ivInitialized, aParameter );
		ivInitialized = true;
	}

	protected final V getView( ) {
		return ivView;
	}

	protected abstract void doInitialize( final boolean aFirstInitialization, final Optional<?> aParameter );

	public abstract void doRefresh( );

}
