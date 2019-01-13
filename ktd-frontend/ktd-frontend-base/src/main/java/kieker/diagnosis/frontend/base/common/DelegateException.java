/***************************************************************************
 * Copyright 2015-2019 Kieker Project (http://kieker-monitoring.net)
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

/**
 * This is an exception that allows to "convert" a checked exception into a runtime exception. This is only to be used
 * in the frontend. Furthermore, it is only to be used if the developer is aware that a checked exception can occur, but
 * wants to let the general error handling mechanism handle the exception. If it occurs, the error handler will unwrap
 * the exception and show a general error dialog to the user.
 *
 * @author Nils Christian Ehmke
 */
public final class DelegateException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DelegateException( final Exception exception ) {
		super( exception );
	}

}
