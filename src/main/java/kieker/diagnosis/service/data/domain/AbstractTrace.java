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

package kieker.diagnosis.service.data.domain;

/**
 * This is an abstract base for classes representing traces (a tree of operation calls) within this application. Technically this class is just a container for
 * a single {@link AbstractOperationCall} instance representing the root call of a whole call tree. Furthermore, this class implements the methods
 * {@link AbstractTrace#equals(Object)} and {@link AbstractTrace#hashCode()}, allowing to put traces for example into a map to aggregate them.
 *
 * @author Nils Christian Ehmke
 *
 * @param <T>
 *            The type of the root node.
 */
public abstract class AbstractTrace<T extends AbstractOperationCall<T>> {

	private final T ivRootOperationCall;

	public AbstractTrace( final T aRootOperationCall ) {
		this.ivRootOperationCall = aRootOperationCall;
	}

	public final T getRootOperationCall( ) {
		return this.ivRootOperationCall;
	}

	public final int calculateHashCode( ) {
		final int prime = 31;
		int result = 1;
		result = ( prime * result ) + ( ( this.ivRootOperationCall == null ) ? 0 : this.ivRootOperationCall.calculateHashCode( ) );
		return result;
	}

	@SuppressWarnings ( "rawtypes" )
	public final boolean isEqualTo( final Object aObj ) {
		if ( this == aObj ) {
			return true;
		}
		if ( aObj == null ) {
			return false;
		}
		if ( this.getClass( ) != aObj.getClass( ) ) {
			return false;
		}
		final AbstractTrace other = (AbstractTrace) aObj;
		if ( this.ivRootOperationCall == null ) {
			if ( other.ivRootOperationCall != null ) {
				return false;
			}
		} else if ( !this.ivRootOperationCall.isEqualTo( other.ivRootOperationCall ) ) {
			return false;
		}
		return true;
	}

}