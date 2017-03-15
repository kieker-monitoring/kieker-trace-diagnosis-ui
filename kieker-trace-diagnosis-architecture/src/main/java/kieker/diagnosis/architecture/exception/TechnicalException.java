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
 * This is a runtime exception indicating that something from a technical point of view failed (e.g. a missing file).
 *
 * @author Nils Christian Ehmke
 *
 * @see BusinessException
 */
public final class TechnicalException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TechnicalException( final String aMessage ) {
		super( aMessage );
	}

	public TechnicalException( final Throwable aCause ) {
		super( aCause );
	}

	public TechnicalException( final String aMessage, final Throwable aCause ) {
		super( aMessage, aCause );
	}

}
