/***************************************************************************
 * Copyright 2015-2018 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.backend.base.common;

/**
 * A util class to get the real classes from objects proxied by Guice.
 *
 * @author Nils Christian Ehmke
 */
public final class ClassUtil {

	private ClassUtil( ) {
		// Avoid instantiation
	}

	/**
	 * Delivers the real class of the given class. This means that for a proxy class the super class will be returned and the class itself otherwise.
	 *
	 * @param aClass The (potentially proxy) class.
	 *
	 * @return The real class.
	 */
	public static Class<?> getRealClass( final Class<?> aClass ) {
		if ( aClass.getName( ).contains( "$$EnhancerByGuice$$" ) ) {
			return aClass.getSuperclass( );
		} else {
			return aClass;
		}
	}

	/**
	 * Delivers the real name of the given class. This means that for a proxy class the name of the super class will be returned and the name of the class itself otherwise.
	 *
	 * @param aClass The (potentially proxy) class.
	 *
	 * @return The real class name.
	 */
	public static String getRealName( final Class<?> aClass ) {
		return getRealClass( aClass ).getName( );
	}

}
