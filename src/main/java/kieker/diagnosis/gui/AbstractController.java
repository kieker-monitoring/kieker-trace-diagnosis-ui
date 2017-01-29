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

package kieker.diagnosis.gui;

import kieker.diagnosis.util.Context;

public abstract class AbstractController<T extends AbstractView> {

	private final Context ivContext;
	private T ivView;

	public AbstractController( final Context aContext ) {
		ivContext = aContext;
	}

	protected final Context getContext( ) {
		return ivContext;
	}

	protected final T getView( ) {
		return ivView;
	}

	public final void setView( final T aView ) {
		ivView = aView;
	}

	public abstract void doInitialize( );

}
