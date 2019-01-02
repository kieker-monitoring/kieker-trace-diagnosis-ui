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

package kieker.diagnosis.backend.base.common;

import java.util.Objects;

/**
 * This util class retrieves the real classes and class names from Guice's proxy classes.
 *
 * @author Nils Christian Ehmke
 */
public final class ClassUtil {

	private ClassUtil( ) {
		// Avoid instantiation
	}

	/**
	 * Retrieves the real class of the given class. If the given class is a proxy class, the super class will be
	 * returned. Otherwise the class itself will be returned.
	 *
	 * @param clazz
	 *            The (potentially proxy) class. Must not be {@code null}.
	 *
	 * @return The real class.
	 *
	 * @throws NullPointerException
	 *             If the given class is {@code null}.
	 */
	public static Class<?> getRealClass( final Class<?> clazz ) {
		Objects.requireNonNull( clazz, "The class must not be null." );

		if ( clazz.getName( ).contains( "$$EnhancerByGuice$$" ) ) {
			return clazz.getSuperclass( );
		} else {
			return clazz;
		}
	}

	/**
	 * Retrieves the real name of the given class. If the given class is a proxy class, the name of the super class will
	 * be returned. Otherwise the name of class itself will be returned.
	 *
	 * @param aClass
	 *            The (potentially proxy) class. Must not be {@code null}.
	 *
	 * @return The real class name.
	 *
	 * @throws NullPointerException
	 *             If the given class is {@code null}.
	 */
	public static String getRealName( final Class<?> clazz ) {
		return getRealClass( clazz ).getName( );
	}

}
