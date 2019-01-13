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

package kieker.diagnosis.backend.cache;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation can be used at service methods and makes sure that an entry of a cached method call is invalidated.
 * It is assumed that the method has at least one parameter which provides the key to the cache. Entries can be cached
 * by using the {@link UseCache} annotation.
 *
 * @see UseCache
 *
 * @author Nils Christian Ehmke
 */
@Retention ( RUNTIME )
@Target ( METHOD )
public @interface InvalidateCache {

	/**
	 * The name of the cache.
	 *
	 * @return The name of the cache.
	 */
	String cacheName();

	/**
	 * The key parameter.
	 *
	 * @return Determines which parameter of the method is the key.
	 */
	int keyParameter();

}
