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

package kieker.diagnosis.common;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a simple class replacing a {@link Map} to provide some more readable methods and a fluent API to create a mapping.
 *
 * @author Nils Christian Ehmke
 *
 * @param <I>
 *            The type of the keys.
 * @param <O>
 *            The type of the values.
 */
public final class Mapper<I, O> extends HashMap<I, O> {

	private static final long serialVersionUID = 1L;
	private O ivDefaultValue;

	public To map( final I aKey ) {
		return new To( aKey );
	}

	public To mapPerDefault( ) {
		return new To( );
	}

	public O resolve( final I aKey ) {
		if ( containsKey( aKey ) ) {
			return get( aKey );
		}
		else {
			return ivDefaultValue;
		}
	}

	public I invertedResolve( final O aValue ) {
		return entrySet( ).parallelStream( ).filter( entry -> aValue.equals( entry.getValue( ) ) ).map( Map.Entry::getKey ).findFirst( ).orElse( null );
	}

	/**
	 * @author Nils Christian Ehmke
	 */
	public final class To {

		private final I ivKey;
		private final boolean ivKeyAvailable;

		protected To( final I aKey ) {
			ivKey = aKey;
			ivKeyAvailable = true;
		}

		protected To( ) {
			ivKey = null;
			ivKeyAvailable = false;
		}

		public void to( final O aValue ) {
			if ( ivKeyAvailable ) {
				put( ivKey, aValue );
			}
			else {
				ivDefaultValue = aValue;
			}
		}

	}

}
