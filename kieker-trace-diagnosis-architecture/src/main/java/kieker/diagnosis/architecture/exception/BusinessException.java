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

package kieker.diagnosis.architecture.exception;

/**
 * This is a checked exception indicating that something from a business point of view failed (e.g. an incomplete form).
 *
 * @author Nils Christian Ehmke
 *
 * @see TechnicalException
 */
public final class BusinessException extends Exception {

	private static final long serialVersionUID = 1L;

	public BusinessException( final String aMessage ) {
		super( aMessage );
	}

	public BusinessException( final Throwable aCause ) {
		super( aCause );
	}

	public BusinessException( final String aMessage, final Throwable aCause ) {
		super( aMessage, aCause );
	}

}
