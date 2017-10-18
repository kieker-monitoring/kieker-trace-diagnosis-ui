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

package kieker.diagnosis.architecture.service.cache;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation can be used at service methods and makes sure that the results of the method call is cached. It is assumed that the method has exactly one
 * parameter which provides the key to the cache. Entries can be invalidated by using the {@link InvalidateCache} annotation.
 *
 * @see InvalidateCache
 *
 * @author Nils Christian Ehmke
 */
@Retention ( RUNTIME )
@Target ( METHOD )
public @interface UseCache {

	/**
	 * @return The name of the cache.
	 */
	String cacheName();

}
